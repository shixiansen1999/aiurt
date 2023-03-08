package com.aiurt.config.datafilter.interceptor;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.base.BaseEntity;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.alibaba.druid.util.JdbcConstants;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ParameterUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

/**
 * @author zc
 */
public class PaginationExtInnerInterceptor extends PaginationInnerInterceptor {

	@Override
	public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
		IPage<?> page = (IPage)ParameterUtils.findPage(parameter).orElse(null);
		if (null != page) {
			boolean addOrdered = false;
			String buildSql = boundSql.getSql();

			List<OrderItem> orders = page.orders();
			if (CollectionUtils.isNotEmpty(orders)) {
				addOrdered = true;
				// 删除page 排序
				//buildSql = this.concatOrderBy(buildSql, orders);
			}

			List<OrderItem> orderFileds = findOrderFiled(parameter);
			if (CollectionUtils.isNotEmpty(orderFileds)){
				//用户排序字段不为空
				buildSql = "select * from ( "+ buildSql + " ) page ";
				buildSql = this.concatOrderBy(buildSql, orderFileds);
			}

			Long _limit = page.maxLimit() != null ? page.maxLimit() : this.maxLimit;
			if (page.getSize() < 0L && null == _limit) {
				if (addOrdered) {
					PluginUtils.mpBoundSql(boundSql).sql(buildSql);
				}

			} else {
				this.handlerLimit(page, _limit);
				IDialect dialect = this.findIDialect(executor);
				Configuration configuration = ms.getConfiguration();
				DialectModel model = dialect.buildPaginationSql(buildSql, page.offset(), page.getSize());
				PluginUtils.MPBoundSql mpBoundSql = PluginUtils.mpBoundSql(boundSql);
				List<ParameterMapping> mappings = mpBoundSql.parameterMappings();
				Map<String, Object> additionalParameter = mpBoundSql.additionalParameters();
				model.consumers(mappings, configuration, additionalParameter);
				mpBoundSql.sql(model.getDialectSql());
				mpBoundSql.parameterMappings(mappings);
			}
		}
	}

	/**
	 * 从条件参数中查找动态排序字段
	 * @param parameterObject 查询参数
	 * @return
	 */
	private List<OrderItem> findOrderFiled(Object parameterObject){
		if (parameterObject instanceof Map) {
			List<OrderItem> orders = new ArrayList<>();
			Map<?, ?> parameterMap = (Map)parameterObject;

			// page 排序, PageOrderGenerator initPage方式
			Object page = null;
			try {
				page = parameterMap.getOrDefault("page", null);
			} catch (Exception e) {
				try {
					page = parameterMap.getOrDefault("pageList", null);
				} catch (Exception exception) {
					// do nothing
				}
			}
			if (Objects.nonNull(page) && page instanceof Page) {
				Page p = (Page) page;
				List orderList = p.orders();
				if (CollectionUtils.isNotEmpty(orderList)) {
					p.setOrders(new ArrayList<>());
					return orderList;
				}
			}

			// 手写sql, 参数是condition方式
			Object condition = null;
			try {
				condition = parameterMap.get("condition");
			} catch (Exception e) {
				// do nothing
			}
			if (Objects.isNull(condition)) {
				return Collections.emptyList();
			}
			// 缓存sql
			if (condition instanceof BaseEntity){
				BaseEntity baseEntity = (BaseEntity) condition;
				if (Objects.nonNull(baseEntity.getColumn())&&!baseEntity.getColumn().contains(",")){
					String column = baseEntity.getColumn();
					String order = baseEntity.getOrder();
					if (StrUtil.isNotBlank(column) || StrUtil.isNotBlank(order)) {
						// 字典翻译处理
						if(column.endsWith(CommonConstant.DICT_TEXT_SUFFIX)) {
							column = column.substring(0, column.lastIndexOf(CommonConstant.DICT_TEXT_SUFFIX));
						}
						OrderItem orderItem = new OrderItem();
						orderItem.setColumn(StrUtil.toUnderlineCase(column));
						orderItem.setAsc("desc".equalsIgnoreCase(order)?false:true);
						orders.add(orderItem);
					}
				}
			}
			return orders;
		}
		return Collections.emptyList();
	}

}
