package com.aiurt.config.mybatis;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wgp
 * @Title: mybatis-plus数据权限过滤插件
 * @Description: 该插件适用于使用PaginationInnerInterceptor拦截追加limit来进行分页的
 * 参考：https://blog.csdn.net/qq_42445433/article/details/124406475
 * @date 2022/5/2715:55
 */
@Slf4j
public class CustomizeDataPermissionHandler implements DataPermissionHandler {

    /**
     * @param where             为当前sql已有的where条件
     * @param mappedStatementId 为mapper中定义的方法的路径
     * @return
     */
    @Override
    public Expression getSqlSegment(Expression where, String mappedStatementId) {

        // 判断线程内是否有权限信息
        List<PermissionFilter> filters = new ArrayList<>();
        List<PermissionFilter> filtersExtra = PermissionHelper.getFilters();
        if (filtersExtra != null && filtersExtra.size() >= 1) {
            for (PermissionFilter permissionFilter : filtersExtra) {
                filters.add(permissionFilter);
            }
        }

        try {
            log.debug("开始进行权限过滤, where: {}", where);
            if (filters == null && filters.size() <= 0) {
                return where;
            }

            // 追加sql
            for (PermissionFilter filter : filters) {
                Expression newExp = null;
                // 查询where
                if (StrUtil.isNotEmpty(filter.getSql())) {
                    if (where != null) {
                        // 原sql存在where,条件用and拼接
                        newExp = new AndExpression(where, CCJSqlParserUtil.parseCondExpression(filter.getSql()));
                    } else {
                        // 原sql不存在where,加上where后拼接新条件
                        newExp = CCJSqlParserUtil.parseCondExpression(filter.getSql());
                    }
                    where = newExp;
                }
            }

        } catch (Exception e) {
            log.error("CustomizeDataPermissionHandler.error", e);
        } finally {
            // 清除线程中的权限信息
            PermissionHelper.clear();
            log.debug("结束进行权限过滤, where: {}", where);
        }
        return where;
    }
}
