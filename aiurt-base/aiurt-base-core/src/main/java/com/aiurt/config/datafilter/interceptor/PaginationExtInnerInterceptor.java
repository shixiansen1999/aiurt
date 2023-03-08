package com.aiurt.config.datafilter.interceptor;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.base.BaseEntity;
import com.aiurt.modules.base.PageOrderGenerator;
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
import org.jetbrains.annotations.Nullable;

/**
 * @author zc
 */
public class PaginationExtInnerInterceptor extends PaginationInnerInterceptor {

	@Override
	public void beforeQuery(Executor executor, MappedStatement ms, Object parameter, RowBounds rowBounds, ResultHandler resultHandler, BoundSql boundSql) throws SQLException {
		IPage<?> page = (IPage) ParameterUtils.findPage(parameter).orElse(null);
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
			if (CollectionUtils.isNotEmpty(orderFileds)) {
				//用户排序字段不为空
				buildSql = "select * from ( " + buildSql + " ) page ";
				buildSql = this.concatOrderBy(buildSql, orderFileds);
			}

			Long _limit = page.maxLimit() != null ? page.maxLimit() : this.maxLimit;
			if (page.getSize() < 0L && null == _limit) {
				if (addOrdered) {
					PluginUtils.mpBoundSql(boundSql).sql(buildSql);
				}

			}
			else {
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
	private List<OrderItem> findOrderFiled(Object parameterObject) {
		if (parameterObject != null) {
			if (parameterObject instanceof Map) {
				Map parameterMap = (Map) parameterObject;
				return findOrderFiledBySql(parameterMap);

			} else if (parameterObject instanceof IPage) {
				IPage page = (IPage)parameterObject;
				return findOrderFieldByPage(page);
			}
		}
		return Collections.emptyList();
	}

	/**
	 * initPage方式
	 * @param page
	 * @return
	 */
	@Nullable
	private List<OrderItem> findOrderFieldByPage(IPage page) {
		// page 排序, PageOrderGenerator initPage方式
		List<OrderItem> orderItems = null;
		if (Objects.nonNull(page)){
			orderItems = page.orders();
		}

		return orderItems;
	}

	/**
	 * 自定义sql方式
	 * @param parameterMap
	 * @return
	 */
	private List<OrderItem> findOrderFiledBySql(Map parameterMap) {

		List<OrderItem> orders = new ArrayList<>();
		// 手写sql, 参数是condition方式

		Object condition = null;
		try {
			condition = parameterMap.get("condition");
		}
		catch (Exception e) {
			// do nothing
		}
		if (Objects.isNull(condition)) {
			return orders;
		}
		// 缓存sql
		if (condition instanceof BaseEntity) {
			BaseEntity baseEntity = (BaseEntity) condition;
			if (Objects.nonNull(baseEntity) && Objects.nonNull(baseEntity.getColumn())) {
				String column = baseEntity.getColumn();
				String order = baseEntity.getOrder();
				List<OrderItem> orderItemList = new ArrayList<>();
				if (StrUtil.isNotBlank(column) || StrUtil.isNotBlank(order)) {
					List<String> columnList = StrUtil.split(column, ',');
					List<String> orderList = StrUtil.split(order, ',');
					// 多字段排序，column 以及order 大小必须相等， 一一对应
					if (CollectionUtils.isNotEmpty(columnList) && CollectionUtils.isNotEmpty(orderList)
							&& (orderList.size() == columnList.size())) {
						for (int i = 0; i < columnList.size(); i++) {
							String v = columnList.get(i);
							// 字典翻译处理
							if (v.endsWith(CommonConstant.DICT_TEXT_SUFFIX)) {
								v = v.substring(0, v.lastIndexOf(CommonConstant.DICT_TEXT_SUFFIX));
							}
							//将字段的驼峰转换成下划线
							v = StrUtil.toUnderlineCase(v);

							OrderItem item = new OrderItem();
							item.setColumn(v);
							if (StrUtil.indexOfIgnoreCase(orderList.get(i), PageOrderGenerator.ORDER_TYPE_ASC) >= 0) {
								item.setAsc(true);
							} else {
								item.setAsc(false);
							}
							orderItemList.add(item);
						}
						orders.addAll(orderItemList);
					}
				}
			}
		}
		return orders;
	}
}
