package com.aiurt.config.datafilter.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.aiurt.common.aspect.annotation.*;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.config.datafilter.config.DataFilterProperties;
import com.aiurt.config.datafilter.constant.DataPermRuleType;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.config.datafilter.utils.ApplicationContextHolder;
import com.aiurt.config.datafilter.utils.ContextUtil;
import com.aiurt.config.datafilter.utils.MyModelUtil;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SubSelect;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.Connection;
import java.util.*;

/**
 * Mybatis拦截器。目前用于数据权限的统一拦截和注入处理。
 *
 * @author wgp
 * @date 2022-07-17
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
@Slf4j
@Component
public class MybatisDataFilterInterceptor implements Interceptor {

    @Autowired
    private DataFilterProperties properties;

    /**
     * 对象缓存。由于Set是排序后的，因此在查找排除方法名称时效率更高。
     * 在应用服务启动的监听器中(LoadDataFilterInfoListener)，会调用当前对象的(loadInfoWithDataFilter)方法，加载缓存。
     */
    private final Map<String, ModelDataPermInfo> cachedDataPermMap = new HashMap<>();

    /**
     * 预先加载与数据过滤相关的数据到缓存，该函数会在(LoadDataFilterInfoListener)监听器中调用。
     */
    public void loadInfoWithDataFilter() {
        // 获取一个接口下所有实现类 执行方法或者获取实现类对象等
        Map<String, BaseMapper> mapperMap =
                ApplicationContextHolder.getApplicationContext().getBeansOfType(BaseMapper.class);

        for (BaseMapper<?> mapperProxy : mapperMap.values()) {
            // 优先处理jdk的代理
            Object proxy = ReflectUtil.getFieldValue(mapperProxy, "h");
            // 如果不是jdk的代理，再看看cjlib的代理。
            if (proxy == null) {
                proxy = ReflectUtil.getFieldValue(mapperProxy, "CGLIB$CALLBACK_0");
            }
            Class<?> mapperClass = (Class<?>) ReflectUtil.getFieldValue(proxy, "mapperInterface");
            if (properties.getEnabledDataPermFilter()) {
                if(mapperClass!=null){
                    EnableDataPerm rule = mapperClass.getAnnotation(EnableDataPerm.class);
                    if (rule != null) {
                        loadDataPermFilterRules(mapperClass, rule);
                    }
                }
            }
        }
    }

    private void loadDataPermFilterRules(Class<?> mapperClass, EnableDataPerm rule) {
        String sysPermissionMapper = "SysPermissionMapper";
        // 由于给数据权限Mapper添加@EnableDataPerm，将会导致无限递归，因此这里检测到之后，
        // 会在系统启动加载监听器的时候，及时抛出异常,SysPermissionMapper类上的@EnableDataPerm进行移除即可
        if (StringUtils.equals(sysPermissionMapper, mapperClass.getSimpleName())) {
            throw new IllegalStateException("Add @EnableDataPerm annotation to SysPermissionMapper is ILLEGAL!");
        }

        // 这里开始获取当前Mapper已经声明的的SqlId中，有哪些是需要排除在外的。
        // 排除在外的将不进行数据过滤。
        Set<String> excludeMethodNameSet = null;
        String[] excludes = rule.excluseMethodName();
        if (excludes.length > 0) {
            excludeMethodNameSet = new HashSet<>();
            for (String excludeName : excludes) {
                excludeMethodNameSet.add(excludeName);
                // 这里是给pagehelper中，分页查询先获取数据总量的查询。
                excludeMethodNameSet.add(excludeName + "_COUNT");
            }
        }

        // 获取Mapper关联的主表信息，包括表名，user、dept、line、station、major、system等过滤字段名。
        // clazz.getGenericSuperclass(); 获取父类的类型
        // p.getActualTypeArguments()[0]; 获取第一个参数
        Class<?> modelClazz = (Class<?>)
                ((ParameterizedType) mapperClass.getGenericInterfaces()[0]).getActualTypeArguments()[0];
        Field[] fields = ReflectUtil.getFields(modelClazz);
        Field userFilterField = null;
        Field deptFilterField = null;
        Field lineFilterField = null;
        Field stationFilterField = null;
        Field majorFilterField = null;
        Field systemFilterField = null;
        for (Field field : fields) {
            if (null != field.getAnnotation(UserFilterColumn.class)) {
                userFilterField = field;
            }
            if (null != field.getAnnotation(DeptFilterColumn.class)) {
                deptFilterField = field;
            }
            if (null != field.getAnnotation(LineFilterColumn.class)) {
                lineFilterField = field;
            }
            if (null != field.getAnnotation(StaionFilterColumn.class)) {
                stationFilterField = field;
            }
            if (null != field.getAnnotation(MajorFilterColumn.class)) {
                majorFilterField = field;
            }
            if (null != field.getAnnotation(SystemFilterColumn.class)) {
                systemFilterField = field;
            }
            if (userFilterField != null
                    && deptFilterField != null
                    && lineFilterField != null
                    && stationFilterField != null
                    && majorFilterField != null
                    && systemFilterField != null) {
                break;
            }
        }

        // 通过注解解析与Mapper关联的Model，并获取与数据权限关联的信息，并将结果缓存。
        ModelDataPermInfo info = new ModelDataPermInfo();
        info.setMainTableName(MyModelUtil.mapToTableName(modelClazz));
        info.setMustIncludeUserRule(rule.mustIncludeUserRule());
        info.setExcludeMethodNameSet(excludeMethodNameSet);
        if (userFilterField != null) {
            info.setUserFilterColumn(MyModelUtil.mapToColumnName(userFilterField, modelClazz));
        }
        if (deptFilterField != null) {
            info.setDeptFilterColumn(MyModelUtil.mapToColumnName(deptFilterField, modelClazz));
        }
        if (lineFilterField != null) {
            info.setLineFilterColumn(MyModelUtil.mapToColumnName(lineFilterField, modelClazz));
        }
        if (stationFilterField != null) {
            info.setStationFilterColumn(MyModelUtil.mapToColumnName(stationFilterField, modelClazz));
        }
        if (majorFilterField != null) {
            info.setMajorFilterColumn(MyModelUtil.mapToColumnName(majorFilterField, modelClazz));
        }
        if (systemFilterField != null) {
            info.setSystemFilterColumn(MyModelUtil.mapToColumnName(systemFilterField, modelClazz));
        }
        cachedDataPermMap.put(mapperClass.getName(), info);
    }

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 判断当前线程本地存储中，业务操作是否禁用了数据权限过滤，如果禁用，则不进行后续的数据过滤处理了。
        if (!GlobalThreadLocal.enabledDataFilter()) {
            return invocation.proceed();
        }

        // 只有在HttpServletRequest场景下，该拦截器才起作用，对于系统级别的预加载数据不会应用数据权限。
        if (!ContextUtil.hasRequestContext()) {
            return invocation.proceed();
        }

        // 通常对于无需登录的白名单url，也无需过滤了。
        // 另外就是登录接口中，获取菜单列表的接口，由于尚未登录，没有TokenData，所以这个接口我们手动加入了该条件。
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (sysUser == null) {
            return invocation.proceed();
        }

        RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
        StatementHandler delegate =
                (StatementHandler) ReflectUtil.getFieldValue(handler, "delegate");

        // 通过反射获取delegate父类BaseStatementHandler的mappedStatement属性
        MappedStatement mappedStatement =
                (MappedStatement) ReflectUtil.getFieldValue(delegate, "mappedStatement");
        SqlCommandType commandType = mappedStatement.getSqlCommandType();

        // 对于INSERT、UPDATE、DELETE语句，我们不进行任何数据过滤。
        if (commandType == SqlCommandType.INSERT || commandType == SqlCommandType.UPDATE || commandType == SqlCommandType.DELETE) {
            return invocation.proceed();
        }

        // org.jeecg.modules.system.mapper.SysCategoryMapper.queryIdByCode
        String sqlId = mappedStatement.getId();
        int pos = StringUtils.lastIndexOf(sqlId, ".");
        // 类名 org.jeecg.modules.system.mapper.SysCategoryMapper
        String className = StringUtils.substring(sqlId, 0, pos);
        // 方法名 queryIdByCode
        String methodName = StringUtils.substring(sqlId, pos + 1);

        Statement statement = null;

        // 处理数据权限过滤
        if (properties.getEnabledDataPermFilter()) {
            this.processDataPermFilter(className, methodName, delegate.getBoundSql(), commandType, statement, sqlId);
        }
        return invocation.proceed();
    }

    /**
     * @param className
     * @param methodName
     * @param boundSql    它是建立 SQL 和参数的地方。它有3个常用的属性：SQL、parameterObject 和 parameterMappings。
     * @param commandType
     * @param statement   它保存映射器的一个节点（select | insert | delete | update）。包括许多我们配置的 SQL、SQL 的 id、缓存信息、resultMap、parameterType、resultType 和 languageDriver 等重要的内容。
     * @param sqlId
     * @throws JSQLParserException
     */
    private void processDataPermFilter(
            String className, String methodName, BoundSql boundSql, SqlCommandType commandType, Statement statement, String sqlId)
            throws JSQLParserException {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (sysUser == null) {
            return;
        }

        // 如果是管理员则不参与数据权限的数据过滤，显示全部数据。
        if (sysUser.getRoleCodes().contains(CommonConstant.ADMIN)) {
            return;
        }

        // 判断当前线程本地存储中，业务操作是否禁用了数据权限过滤，如果禁用，则不进行后续的数据过滤处理了。
        ModelDataPermInfo info = cachedDataPermMap.get(className);
        // 再次查找当前方法是否为排除方法，如果不是，就参与数据权限注入过滤。
        if (info == null || CollUtil.contains(info.getExcludeMethodNameSet(), methodName)) {
            return;
        }

        // 查找当前用户的数据权限，从request里面获取
        // dataPermMap的key是规则类型(TYPE_MANAGE_LINE_ONLY)，value是对应的数组，比如我管理的线路（1,2,3,4,8）
        Map<String, String> dataPermMap =  ContextUtil.loadDataSearchConditon();
        if (MapUtils.isEmpty(dataPermMap)) {
            return;
        }

        // 包含所有权限，不需要过滤
        if (dataPermMap.containsKey(DataPermRuleType.TYPE_ALL)) {
            return;
        }

        // 如果当前过滤注解中mustIncludeUserRule参数为true，同时当前用户的数据权限中，不包含TYPE_USER_ONLY，
        // 这里就需要自动添加该数据权限。
        if (info.getMustIncludeUserRule()
                && !dataPermMap.containsKey(DataPermRuleType.TYPE_USER_ONLY)) {
            dataPermMap.put(DataPermRuleType.TYPE_USER_ONLY, null);
        }

        this.processDataPerm(info, dataPermMap, boundSql, commandType, statement);
    }

    private void processDataPerm(
            ModelDataPermInfo info,
            Map<String, String> dataPermMap,
            BoundSql boundSql,
            SqlCommandType commandType,
            Statement statement) throws JSQLParserException {
        List<String> criteriaList = new LinkedList<>();

        for (Map.Entry<String, String> entry : dataPermMap.entrySet()) {
            String filterClause = processDataPermRule(info, entry.getKey(), entry.getValue());
            if (StringUtils.isNotBlank(filterClause)) {
                criteriaList.add(filterClause);
            }
        }
        if (CollectionUtils.isEmpty(criteriaList)) {
            return;
        }
        StringBuilder filterBuilder = new StringBuilder(128);
        filterBuilder.append("(");
        filterBuilder.append(StringUtils.join(criteriaList, " AND "));
        filterBuilder.append(")");
        String dataFilter = filterBuilder.toString();
        if (statement == null) {
            String sql = boundSql.getSql();
            statement = CCJSqlParserUtil.parse(sql);
        }

        // 只处理select的语句
        if (commandType == SqlCommandType.SELECT) {
            Select select = (Select) statement;
            PlainSelect selectBody = (PlainSelect) select.getSelectBody();
            FromItem fromItem = selectBody.getFromItem();
            PlainSelect subSelect = null;
            if (fromItem != null) {
                if (fromItem instanceof SubSelect) {
                    subSelect = (PlainSelect) ((SubSelect) fromItem).getSelectBody();
                }
                if (subSelect != null) {
                    buildWhereClause(subSelect, dataFilter);
                } else {
                    buildWhereClause(selectBody, dataFilter);
                }
            }
        }
        log.info("DataPerm Filter Where Clause [{}]", dataFilter);
        ReflectUtil.setFieldValue(boundSql, "sql", statement.toString());
    }

    private String processDataPermRule(ModelDataPermInfo info, String ruleType, String ruleValue) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        StringBuilder filter = new StringBuilder(128);
        if (DataPermRuleType.TYPE_USER_ONLY.equals(ruleType)) {
            if (StringUtils.isNotBlank(info.getUserFilterColumn())) {
                if (properties.getAddTableNamePrefix()) {
                    filter.append(info.getMainTableName()).append(".");
                }
                filter.append(info.getUserFilterColumn())
                        .append(" = ")
                        .append("'" + sysUser.getUsername() + "'");
            }
        } else if (DataPermRuleType.TYPE_MANAGE_DEPT.equals(ruleType)) {
            if (StringUtils.isNotBlank(info.getDeptFilterColumn())) {
                if (properties.getAddTableNamePrefix()) {
                    filter.append(info.getMainTableName()).append(".");
                }
                filter.append(info.getDeptFilterColumn())
                        .append(" IN (")
                        .append(ruleValue)
                        .append(") ");
            }
        } else if (DataPermRuleType.TYPE_DEPT_ONLY.equals(ruleType)) {
            if (StringUtils.isNotBlank(info.getDeptFilterColumn())) {
                if (properties.getAddTableNamePrefix()) {
                    filter.append(info.getMainTableName()).append(".");
                }
                filter.append(info.getDeptFilterColumn())
                        .append(" = ")
                        .append("'"+sysUser.getOrgCode()+"'");
            }
        } else if (DataPermRuleType.TYPE_MANAGE_LINE_ONLY.equals(ruleType)) {
            if (StringUtils.isNotBlank(info.getLineFilterColumn())) {
                if (properties.getAddTableNamePrefix()) {
                    filter.append(info.getMainTableName()).append(".");
                }
                filter.append(info.getLineFilterColumn())
                        .append(" IN (")
                        .append(ruleValue)
                        .append(") ");
            }
        } else if (DataPermRuleType.TYPE_MANAGE_STATION_ONLY.equals(ruleType)) {
            if (StringUtils.isNotBlank(info.getStationFilterColumn())) {
                if (properties.getAddTableNamePrefix()) {
                    filter.append(info.getMainTableName()).append(".");
                }
                filter.append(info.getStationFilterColumn())
                        .append(" IN (")
                        .append(ruleValue)
                        .append(") ");
            }
        } else if (DataPermRuleType.TYPE_MANAGE_MAJOR_ONLY.equals(ruleType)) {
            if (StringUtils.isNotBlank(info.getMajorFilterColumn())) {
                if (properties.getAddTableNamePrefix()) {
                    filter.append(info.getMainTableName()).append(".");
                }
                filter.append(info.getMajorFilterColumn())
                        .append(" IN (")
                        .append(ruleValue)
                        .append(") ");
            }
        } else if (DataPermRuleType.TYPE_MANAGE_SYSTEM_ONLY.equals(ruleType)) {
            if (StringUtils.isNotBlank(info.getSystemFilterColumn())) {
                if (properties.getAddTableNamePrefix()) {
                    filter.append(info.getMainTableName()).append(".");
                }
                filter.append(info.getSystemFilterColumn())
                        .append(" IN (")
                        .append(ruleValue)
                        .append(") ");
            }
        }
        return filter.toString();
    }

    private void buildWhereClause(PlainSelect select, String dataFilter) throws JSQLParserException {
        if (select.getWhere() == null) {
            select.setWhere(CCJSqlParserUtil.parseCondExpression(dataFilter));
        } else {
            AndExpression and = new AndExpression(
                    CCJSqlParserUtil.parseCondExpression(dataFilter), select.getWhere());
            select.setWhere(and);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // 这里需要空注解，否则sonar会不happy。
    }

    @Data
    private static final class ModelDataPermInfo {
        private Set<String> excludeMethodNameSet;
        private String userFilterColumn;
        private String deptFilterColumn;
        private String lineFilterColumn;
        private String stationFilterColumn;
        private String majorFilterColumn;
        private String systemFilterColumn;
        private String mainTableName;
        private Boolean mustIncludeUserRule;
    }

}
