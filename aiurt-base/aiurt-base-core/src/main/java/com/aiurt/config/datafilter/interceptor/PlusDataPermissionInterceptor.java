package com.aiurt.config.datafilter.interceptor;

import com.aiurt.config.datafilter.handler.PlusDataPermissionHandler;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.parser.JsqlParserSupport;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SetOperationList;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2023/2/610:57
 */
public class PlusDataPermissionInterceptor extends JsqlParserSupport implements InnerInterceptor {
    private final PlusDataPermissionHandler dataPermissionHandler = new PlusDataPermissionHandler();

    @Override
    public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) {
        // 解析 sql 分配对应方法
        PluginUtils.MPBoundSql mpBs = PluginUtils.mpBoundSql(boundSql);
        mpBs.sql(parserSingle(mpBs.sql(), ms.getId()));
    }

//    @Override
//    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
//        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
//        MappedStatement ms = mpSh.mappedStatement();
//        SqlCommandType sct = ms.getSqlCommandType();
//        if (sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
//            if (InterceptorIgnoreHelper.willIgnoreDataPermission(ms.getId())) {
//                return;
//            }
//            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
//            mpBs.sql(parserMulti(mpBs.sql(), ms.getId()));
//        }
//    }

    @Override
    protected void processSelect(Select select, int index, String sql, Object obj) {
        SelectBody selectBody = select.getSelectBody();
        if (selectBody instanceof PlainSelect) {
            // 普通查询
            this.setWhere((PlainSelect) selectBody, (String) obj);
        } else if (selectBody instanceof SetOperationList) {
            // INTERSECT、EXCEPT、MINUS、UNION语句交并集查询
            SetOperationList setOperationList = (SetOperationList) selectBody;
            List<SelectBody> selectBodyList = setOperationList.getSelects();
            selectBodyList.forEach(s -> this.setWhere((PlainSelect) s, (String) obj));
        }
    }

    /**
     * 设置 where 条件
     *
     * @param plainSelect       查询对象
     * @param mappedStatementId 执行方法id
     */
    protected void setWhere(PlainSelect plainSelect, String mappedStatementId) {
        Expression sqlSegment = dataPermissionHandler.getSqlSegment(plainSelect.getWhere(), mappedStatementId);
        if (null != sqlSegment) {
            plainSelect.setWhere(sqlSegment);
        }
    }
}
