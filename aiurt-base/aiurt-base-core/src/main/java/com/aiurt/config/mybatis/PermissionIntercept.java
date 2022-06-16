package com.aiurt.config.mybatis;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author wgp
 * @Title: JSqlParser是一个SQL语句解析器
 * @Description:Permission Filter Mybatis Plugin （权限过滤Mybatis插件）
 * @date 2022/5/2614:20 该插件使用于使用 PageHelper.startPage(pageNo, pageSize);
 */
@Slf4j
//@Component
//@Intercepts({@Signature(
//        type = Executor.class,
//        method = "query",
//        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
//), @Signature(
//        type = Executor.class,
//        method = "query",
//        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}
//)})
public class PermissionIntercept implements Interceptor {
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        try {
            Object[] args = invocation.getArgs();
            MappedStatement ms = (MappedStatement) args[0];
            Object parameter = args[1];
            RowBounds rowBounds = (RowBounds) args[2];
            ResultHandler resultHandler = (ResultHandler) args[3];
            Executor executor = (Executor) invocation.getTarget();
            CacheKey cacheKey;
            BoundSql boundSql;
            //由于逻辑关系，只会进入一次
            if (args.length == 4) {
                //4 个参数时
                boundSql = ms.getBoundSql(parameter);
                cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
            } else {
                //6 个参数时
                cacheKey = (CacheKey) args[4];
                boundSql = (BoundSql) args[5];
            }
            //TODO 自己要进行的各种处理
            String sql = boundSql.getSql();
            log.debug("原始SQL： {}", sql);

            // 判断线程内是否有权限信息
            List<PermissionFilter> filters = new ArrayList<>();
            List<PermissionFilter> filtersExtra = PermissionHelper.getFilters();
            if (filtersExtra != null && filtersExtra.size() >= 1) {
                for (PermissionFilter permissionFilter : filtersExtra) {
                    if (checkIsNeedFilter(permissionFilter, boundSql)) {
                        filters.add(permissionFilter);
                    }
                }
            }

            // 存在权限信息才处理
            if (filters != null && filters.size() >= 1) {
                // 增强sql
                Statement stmt = CCJSqlParserUtil.parse(sql);
                Select selectStatement = (Select) stmt;

                SelectBody selectBody = selectStatement.getSelectBody();
                if (selectBody instanceof PlainSelect) {
                    this.setWhere((PlainSelect) selectBody, filters);
                }
                else if (selectBody instanceof SetOperationList) {
                    SetOperationList setOperationList = (SetOperationList) selectBody;
                    List<SelectBody> selectBodyList = setOperationList.getSelects();
                    selectBodyList.forEach((s) -> {
                        this.setWhere((PlainSelect) s, filters);
                    });
                }
                String dataPermissionSql = selectStatement.toString();
                log.debug("增强SQL： {}", dataPermissionSql);
                // 重新new一个查询语句对象
                BoundSql dataPermissionBoundSql = new BoundSql(ms.getConfiguration(), dataPermissionSql, boundSql.getParameterMappings(), parameter);
                return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, dataPermissionBoundSql);
            }
            //注：下面的方法可以根据自己的逻辑调用多次，在分页插件中，count 和 page 各调用了一次
            return executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
        } finally {
            //清除线程中权限参数
            PermissionHelper.clear();
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        // TODO Auto-generated method stub
    }

    private boolean checkIsNeedFilter(PermissionFilter permissionFilter, BoundSql boundSql) {
        String selectSql = boundSql.getSql();
        String checkChar = permissionFilter.checkChar();
        if (StrUtil.isNotEmpty(checkChar)) {
            if (!selectSql.contains(checkChar)) {
                return false;
            }
        }
        return true;
    }

    protected void setWhere(PlainSelect plainSelect, List<PermissionFilter> filters) {
        for (PermissionFilter filter : filters) {
            Expression newExp = null;
            // 查询where
            Expression exp = plainSelect.getWhere();
            if (StrUtil.isNotEmpty(filter.getSql())) {
                if (exp != null) {
                    // 原sql存在where,条件用and拼接
                    try {
                        newExp = new AndExpression(plainSelect.getWhere(),
                                CCJSqlParserUtil.parseCondExpression(filter.getSql()));
                    } catch (JSQLParserException e) {
                        e.printStackTrace();
                    }
                } else {
                    // 原sql不存在where,加上where后拼接新条件
                    try {
                        newExp = CCJSqlParserUtil.parseCondExpression(filter.getSql());
                    } catch (JSQLParserException e) {
                        e.printStackTrace();
                    }
                }
                // 设置where条件
                plainSelect.setWhere(newExp);
            }
        }
    }

}
