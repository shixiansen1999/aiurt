package com.aiurt.config.datafilter.interceptor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.base.BaseEntity;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ParameterUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.DialectModel;
import com.baomidou.mybatisplus.extension.plugins.pagination.dialects.IDialect;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.OrderByElement;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

public class PaginationExtInnerInterceptor extends PaginationInnerInterceptor {

	public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
		IPage<?> page = (IPage)ParameterUtils.findPage(parameter).orElse(null);
		if (null != page) {
			boolean addOrdered = false;
			String buildSql = boundSql.getSql();
			List<OrderItem> orders = page.orders();
			if (CollectionUtils.isNotEmpty(orders)) {
				addOrdered = true;
				buildSql = this.concatOrderBy(buildSql, orders);
			}

			List<OrderItem> orderFileds = findOrderFiled(parameter);
			if (CollectionUtils.isNotEmpty(orderFileds)){//用户排序字段不为空
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
	 * @param parameterObject
	 * @return
	 */
	private List<OrderItem> findOrderFiled(Object parameterObject){
		if (parameterObject instanceof Map) {
			List<OrderItem> orders = new ArrayList<>();
			Map<?, ?> parameterMap = (Map)parameterObject;
			Object condition = parameterMap.get("condition");
			if (condition instanceof BaseEntity){
				BaseEntity baseEntity = (BaseEntity) condition;
				if (Objects.nonNull(baseEntity.getColumn())&&!baseEntity.getColumn().contains(",")){
					String column = baseEntity.getColumn();
					String order = baseEntity.getOrder();
					OrderItem orderItem = new OrderItem();
					orderItem.setColumn(StrUtil.toUnderlineCase(column));
					orderItem.setAsc("desc".equalsIgnoreCase(order)?false:true);
					orders.add(orderItem);
				}

			}
			return orders;
		}
		return Collections.emptyList();
	}

}
