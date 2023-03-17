package com.aiurt.config.mybatis;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.RowBounds;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Component;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * mybatis拦截器，拦截删除的sql(delete删除和 update del_flag=1的伪删除)
 * delete删除：1、拦截 delete删除的 sql
 * 2、先查询要删除的数据（把 delete from 改成 select * from就能查询到）
 * 3、把查询到的数据和一些相关信息保存到 sys_recycle表中
 * 4、执行原来的 delete 语句
 * update伪删除：1、拦截 update 的sql,看是否是把 del_flag=1,不是的话直接放行
 * 2、先查询数据（select * from tableName + update的 where部分）
 * 3、把查询到的数据和一些相关信息保存到 sys_recycle表中
 * 4、执行原来的 delete 语句
 *
 * @Author
 * @Date 2023-03-14
 */
@Slf4j
@Component
@Intercepts({@Signature(method = "prepare", type = StatementHandler.class, args = {Connection.class, Integer.class})})
public class RecycleMybatisInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
        //通过反射获取到当前RoutingStatementHandler对象的delegate属性
        StatementHandler delegate = (StatementHandler) ReflectUtil.getFieldValue(handler, "delegate");
        //获取到当前StatementHandler的 boundSql，这里不管是调用handler.getBoundSql()还是直接调用delegate.getBoundSql()结果是一样的，
        // 因为RoutingStatementHandler实现的所有StatementHandler接口方法里面都是调用的delegate对应的方法。
        BoundSql boundSql = delegate.getBoundSql();

        //通过反射获取delegate父类BaseStatementHandler的mappedStatement属性
        MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(delegate, "mappedStatement");

        // 如果是delete语句，目前仅考虑简单的delete from xxx where... 语句(不分大小写)，不是这个格式的会报错
        if (mappedStatement.getSqlCommandType() == SqlCommandType.DELETE) {
            try {
                Object parameter = boundSql.getParameterObject();
                Executor executor = (Executor) ReflectUtil.getFieldValue(delegate, "executor");
                // 原始的删除sql，就是 delete from xxx where ..., 不能直接delete from xxx删除全部，mybatis plus会格式化sql
                String originSql = boundSql.getSql();
                // 获取表名
                Pattern pattern = Pattern.compile("DELETE FROM ([a-z_\\d]+) WHERE[\\s\\S]*", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(originSql);
                boolean matches = matcher.matches();
                if (!matches) {
                    // 不是 delete from xxx where... 的格式
                    return invocation.proceed();
                }
                String tableName = matcher.group(1);
                // 不拦截sys_recycle表
                if ("sys_recycle".equals(tableName)) {
                    return invocation.proceed();
                }
                // 获取查询的queryBoundSql
                String querySql = originSql.replaceAll("(?i)DELETE FROM", "SELECT * FROM");
                // 如果querySql里面不是select开头，说明没替换到。
                if (!(querySql.indexOf("SELECT") == 0)) {
                    log.info("未能替换成select语句的delete的sql语句是" + boundSql.getSql());
                    throw new AiurtBootException("delete删除语句没能替换成select语句");
                }

//                BoundSql queryBoundSql = new BoundSql(mappedStatement.getConfiguration(), querySql, boundSql.getParameterMappings(), parameter);
                // 下面几行是mybatis plus里面的
                PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
                BoundSql queryBoundSql = new BoundSql(mappedStatement.getConfiguration(), querySql, mpBoundSql.parameterMappings(), parameter);
                PluginUtils.setAdditionalParameter(queryBoundSql, mpBoundSql.additionalParameters());

                // 查询
                List<Map<String, Object>> result = executorQuery(executor, mappedStatement, parameter, queryBoundSql);

                // 查询没有数据，不用存
                if (result.size() == 0) {
                    log.info(queryBoundSql.getSql() + "->查询没有数据");
                    return invocation.proceed();
                }
                List<String> billIdList = new ArrayList<>();
                // 数据存成json形式的，方便还原
                List<String> resultJson = new ArrayList<>();
                for (Map<String, Object> r : result) {
                    billIdList.add((String) r.get("id"));
                    resultJson.add(JSONObject.toJSONString(new JSONObject(r), SerializerFeature.WriteMapNullValue));
                }
                String resultString = resultJson.toString(); // 将结果转成字符串

//			String moduleName = mappedStatement.getId();
                String moduleName = null;  // 这个数据库的module_name先置空

                Connection connection = (Connection) invocation.getArgs()[0];
                boolean saveOk = saveToRecycle(connection, tableName, resultString, moduleName, billIdList.toString(), 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            log.info("删除完毕，已存入还原库");
        } else if (mappedStatement.getSqlCommandType() == SqlCommandType.UPDATE) {
            try {
                // 如果是update语句，看看是不是 update xxx set ... del_flag ... where ... 的格式
                String originSql = boundSql.getSql();
                Pattern pattern = Pattern.compile("UPDATE\\s+([a-z_\\d]+)\\s+SET[\\s\\S]+?del_flag[\\s\\S]+?(WHERE[\\s\\S]*)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(originSql);
                boolean matches = matcher.matches();
                if (!matches) {
                    // 不是 update xxx set ... del_flag ... where ... 的格式
                    return invocation.proceed();
                }

                Object delFlag = null;
                // 直接写sql的话，参数会直接在((ParamMap) boundSql.getParameterObject())的ParamMap里面
                ParamMap parameterObject = (ParamMap) boundSql.getParameterObject();
                if (parameterObject.containsKey("delFlag")) {
                    delFlag = ((ParamMap) boundSql.getParameterObject()).get("delFlag");
                }
                if (delFlag == null) {
                    // mybatis-plus的BaseMapper源码里面的更新语句：
                    // int updateById(@Param("et") T entity);
                    // int update(@Param("et") T entity, @Param("ew") Wrapper<T> updateWrapper);
                    // 所以取 "et" 或者 "ew", "ew"的是Wrapper对象，比较麻烦
                    if (parameterObject.containsKey("et") && parameterObject.get("et") != null) {
                        Object obj = parameterObject.get("et");
                        if (obj != null) {
                            delFlag = ReflectUtil.getFieldValue(obj, "delFlag");
                        }
                    } else if (parameterObject.containsKey("ew") && parameterObject.get("ew") != null) {
                        Object object = parameterObject.get("ew");
                        List sqlSet = (List) ReflectUtil.getFieldValue(object, "sqlSet");
                        Map paramMap = (Map) ReflectUtil.getFieldValue(object, "paramNameValuePairs");
                        for (Object o : sqlSet) {
                            String periodSql = (String) o;  // periodSql是类似del_flag=#{ew.paramNameValuePairs.MPGENVAL1}的片段
                            if (periodSql.indexOf("del_flag") == 0) {
                                String key = periodSql.replace("del_flag=#{ew.paramNameValuePairs.", "").replace("}", "");
                                delFlag = paramMap.get(key);
                                break;
                            }
                        }
                    }
                }

                if (!CommonConstant.DEL_FLAG_1.equals(delFlag)) {
                    // delFlag 不等于 1，不是删除
                    return invocation.proceed();
                }

                String tableName = matcher.group(1);
                // 不拦截sys_recycle表
                if ("sys_recycle".equals(tableName)) {
                    return invocation.proceed();
                }
                String querySql = "select * from " + tableName + " " + matcher.group(2);

                // 要找到where后面的 xxx = ? ，把where前面的去掉，使用newParameterMappingList填充queryBoundSql，不然会报错
                List<ParameterMapping> newParameterMappingList = new ArrayList<>();
                List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings(); // 预编译里所有要填充的?参数

                parameterMappingList.forEach(parameterMapping -> {
                    String property = parameterMapping.getProperty();  // property大概是et.id,et.delFlag这种格式的
                    String fieldName = property.split("\\.")[1];  // fieldName就是去掉property前面的et.
                    // fieldName在要查询的sql语句中存在，才填充。
                    if (Pattern.matches("[\\s\\S]*[^a-z]" + fieldName + "[^a-z][\\s\\S]*", StrUtil.toCamelCase(querySql))) {
                        newParameterMappingList.add(parameterMapping);
                    }
                });

                Object parameter = boundSql.getParameterObject();
//                BoundSql queryBoundSql = new BoundSql(mappedStatement.getConfiguration(), querySql, newParameterMappingList, parameter);
                PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
                BoundSql queryBoundSql = new BoundSql(mappedStatement.getConfiguration(), querySql, mpBoundSql.parameterMappings(), parameter);
                PluginUtils.setAdditionalParameter(queryBoundSql, mpBoundSql.additionalParameters());

                Executor executor = (Executor) ReflectUtil.getFieldValue(delegate, "executor");

                // 查询
                List<Map<String, Object>> result = executorQuery(executor, mappedStatement, parameter, queryBoundSql);

                // 查询没有数据，不用存
                if (result.size() == 0) {
                    log.info(queryBoundSql.getSql() + "->查询没有数据");
                    return invocation.proceed();
                }
                List<String> billIdList = new ArrayList<>();
                // 数据存成json形式的，方便还原
                List<String> resultJson = new ArrayList<>();
                for (Map<String, Object> r : result) {
                    billIdList.add((String) r.get("id"));
                    resultJson.add(JSONObject.toJSONString(new JSONObject(r), SerializerFeature.WriteMapNullValue));
                }
                String resultString = resultJson.toString(); // 将结果转成字符串

                // String moduleName = mappedStatement.getId();
                String moduleName = null;  // 这个数据库的module_name先置空
                Connection connection = (Connection) invocation.getArgs()[0];
                boolean saveOk = saveToRecycle(connection, tableName, resultString, moduleName, billIdList.toString(), 1);
                log.info("伪删除完毕，已存入还原库");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // TODO Auto-generated method stub
    }

    /**
     * 使用jdbc将数据保存到sys_recycle表
     *
     * @param connection
     * @param billTableNm
     * @param billValue
     * @param moduleName
     * @param billId
     * @param delSign
     * @return
     * @throws Throwable
     */
    private boolean saveToRecycle(Connection connection, String billTableNm, String billValue, String moduleName, String billId, Integer delSign) throws Throwable {

        //		connection.setAutoCommit(false);

        LoginUser sysUser = this.getLoginUser();
        String username = null; // 创建人
        String orgCode = null; // 所属部门
        if (sysUser != null) {
            username = sysUser.getUsername();
            orgCode = sysUser.getOrgCode();
        }

        // 预编译
        String insertSql = "insert into sys_recycle(id,create_by,create_time,sys_org_code,bill_tablenm, bill_value," +
                "physicaldel_id, dphysical_del,state,bill_id, module_name, del_sign) values (?,?,?,?,?,?,?,?,?,?,?,?)";

        PreparedStatement preparedStatement = connection.prepareStatement(insertSql);
        preparedStatement.setString(1, IdWorker.getIdStr(billValue));   // id，雪花算法
        preparedStatement.setString(2, username); // 创建人
        preparedStatement.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // 创建时间
        preparedStatement.setString(4, orgCode); // 所属部门
        preparedStatement.setString(5, billTableNm);  // 单据表名
        preparedStatement.setString(6, billValue); // 单据值
        preparedStatement.setString(7, username); // 删除人，就是当前的登录人
        preparedStatement.setTimestamp(8, new Timestamp(System.currentTimeMillis())); // 删除时间
        preparedStatement.setString(9, "1"); // 状态（1正常 2还原 3删除（指从回收站删除））,这里固定为1
        preparedStatement.setString(10, billId); // 单据id
        preparedStatement.setString(11, moduleName); // 模块名称
        preparedStatement.setInt(12, delSign); // 是否逻辑删除 1是 0否

        int i = preparedStatement.executeUpdate();
        preparedStatement.close();
        return i > 0;

        // 可以把下面事务提交回滚变成一个方法
        // 可以通过spring的aop动态代理拦截有@Transactional注解得controller层方法的结果，如果返回正常，则执行事务提交，否则执行回滚
//		try {
//			// 事务提交
//			connection.commit();
//			return true;
//		} catch (SQLException e) {
//			// 事务回滚
//			connection.rollback();
//			e.printStackTrace();
//			return false;
//		}
    }

    /**
     * 获取登录用户
     *
     * @return
     */
    private LoginUser getLoginUser() {
        LoginUser sysUser = null;
        try {
            sysUser = SecurityUtils.getSubject().getPrincipal() != null ? (LoginUser) SecurityUtils.getSubject().getPrincipal() : null;
        } catch (Exception e) {
            sysUser = null;
        }
        return sysUser;
    }

    /**
     * 执行查询
     */
    private List<Map<String, Object>> executorQuery(Executor executor, MappedStatement mappedStatement, Object parameter, BoundSql queryBoundSql) throws SQLException {
        // 新建一个 query 的 mappedStatement
        Map<String, MappedStatement> queryMsCache = new ConcurrentHashMap();
        String queryId = mappedStatement.getId() + "_recycle";
        MappedStatement queryMs = CollectionUtils.computeIfAbsent(queryMsCache, queryId, (key) -> {
            MappedStatement.Builder builder = new MappedStatement.Builder(mappedStatement.getConfiguration(), key, mappedStatement.getSqlSource(), SqlCommandType.SELECT);
            builder.resource(mappedStatement.getResource());
            builder.fetchSize(mappedStatement.getFetchSize());
            builder.statementType(mappedStatement.getStatementType());
            builder.timeout(mappedStatement.getTimeout());
            builder.parameterMap(mappedStatement.getParameterMap());
            builder.resultMaps(Collections.singletonList((new ResultMap.Builder(mappedStatement.getConfiguration(), "sys_recycle", Map.class, Collections.emptyList())).build()));
            builder.resultSetType(mappedStatement.getResultSetType());
            builder.cache(mappedStatement.getCache());
            builder.flushCacheRequired(mappedStatement.isFlushCacheRequired());
            builder.useCache(mappedStatement.isUseCache());
            return builder.build();
        });
        RowBounds rowBounds = new RowBounds();

        // 发起sql进行查询
        CacheKey cacheKey = executor.createCacheKey(queryMs, parameter, rowBounds, queryBoundSql);
        return executor.query(queryMs, parameter, rowBounds, null, cacheKey, queryBoundSql);
    }
}
