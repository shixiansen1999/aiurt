//package com.aiurt.config.datafilter.interceptor;
//
//import cn.hutool.core.annotation.AnnotationUtil;
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.util.ArrayUtil;
//import cn.hutool.core.util.ClassUtil;
//import cn.hutool.core.util.ObjectUtil;
//import cn.hutool.core.util.ReflectUtil;
//import com.aiurt.common.aspect.annotation.DataColumn;
//import com.aiurt.common.aspect.annotation.DataPermission;
//import com.aiurt.common.constant.CommonConstant;
//import com.aiurt.config.datafilter.constant.enums.DataScopeType;
//import com.aiurt.common.util.SpringUtils;
//import com.aiurt.config.datafilter.config.DataFilterProperties;
//import com.aiurt.config.datafilter.constant.DataPermRuleType;
//import com.aiurt.config.datafilter.entity.ModelDataPermInfo;
//import com.aiurt.config.datafilter.object.GlobalThreadLocal;
//import com.aiurt.config.datafilter.utils.ContextUtil;
//import lombok.extern.slf4j.Slf4j;
//import net.sf.jsqlparser.JSQLParserException;
//import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
//import net.sf.jsqlparser.parser.CCJSqlParserUtil;
//import net.sf.jsqlparser.statement.Statement;
//import net.sf.jsqlparser.statement.select.FromItem;
//import net.sf.jsqlparser.statement.select.PlainSelect;
//import net.sf.jsqlparser.statement.select.Select;
//import net.sf.jsqlparser.statement.select.SubSelect;
//import org.apache.commons.collections4.CollectionUtils;
//import org.apache.commons.collections4.MapUtils;
//import org.apache.commons.lang3.StringUtils;
//import org.apache.ibatis.executor.statement.RoutingStatementHandler;
//import org.apache.ibatis.executor.statement.StatementHandler;
//import org.apache.ibatis.mapping.BoundSql;
//import org.apache.ibatis.mapping.MappedStatement;
//import org.apache.ibatis.mapping.SqlCommandType;
//import org.apache.ibatis.plugin.Interceptor;
//import org.apache.ibatis.plugin.Invocation;
//import org.apache.ibatis.plugin.Plugin;
//import org.apache.shiro.SecurityUtils;
//import org.jeecg.common.system.vo.LoginUser;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.expression.BeanFactoryResolver;
//import org.springframework.expression.BeanResolver;
//import org.springframework.expression.ExpressionParser;
//import org.springframework.expression.ParserContext;
//import org.springframework.expression.common.TemplateParserContext;
//import org.springframework.expression.spel.standard.SpelExpressionParser;
//import org.springframework.expression.spel.support.StandardEvaluationContext;
//
//import java.lang.reflect.Method;
//import java.util.*;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.stream.Collectors;
//
///**
// * Mybatis拦截器。目前用于数据权限的统一拦截和注入处理。
// *
// * @author wgp
// * @date 2022-07-17
// */
////@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
//@Slf4j
////@Component
//public class MybatisDataFilterInterceptor implements Interceptor {
//
//    @Autowired
//    private DataFilterProperties properties;
//
//    /**
//     * 方法名称 与 注解的映射关系缓存
//     */
//    private final Map<String, DataPermission> dataPermissionCacheMap = new ConcurrentHashMap<>();
//
//    /**
//     * 对象缓存。由于Set是排序后的，因此在查找排除方法名称时效率更高。
//     * 在应用服务启动的监听器中(LoadDataFilterInfoListener)，会调用当前对象的(loadInfoWithDataFilter)方法，加载缓存。
//     */
//    private final Map<String, ModelDataPermInfo> cachedDataPermMap = new HashMap<>();
//
//    /**
//     * spel 解析器
//     */
//    private final ExpressionParser parser = new SpelExpressionParser();
//    private final ParserContext parserContext = new TemplateParserContext();
//    /**
//     * bean解析器 用于处理 spel 表达式中对 bean 的调用
//     */
//    private final BeanResolver beanResolver = new BeanFactoryResolver(SpringUtils.getBeanFactory());
//
//
//    @Override
//    public Object intercept(Invocation invocation) throws Throwable {
//        // 判断当前线程本地存储中，业务操作是否禁用了数据权限过滤，如果禁用，则不进行后续的数据过滤处理了。
//        if (!GlobalThreadLocal.enabledDataFilter()) {
//            return invocation.proceed();
//        }
//
//        // 只有在HttpServletRequest场景下，该拦截器才起作用，对于系统级别的预加载数据不会应用数据权限。
//        if (!ContextUtil.hasRequestContext()) {
//            return invocation.proceed();
//        }
//
//        // 通常对于无需登录的白名单url，也无需过滤了。
//        // 另外就是登录接口中，获取菜单列表的接口，由于尚未登录，没有TokenData，所以这个接口我们手动加入了该条件。
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        if (sysUser == null) {
//            return invocation.proceed();
//        }
//
//        RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
//        StatementHandler delegate = (StatementHandler) ReflectUtil.getFieldValue(handler, "delegate");
//
//        // 通过反射获取delegate父类BaseStatementHandler的mappedStatement属性
//        MappedStatement mappedStatement = (MappedStatement) ReflectUtil.getFieldValue(delegate, "mappedStatement");
//        SqlCommandType commandType = mappedStatement.getSqlCommandType();
//
//        // 对于INSERT、UPDATE、DELETE语句，我们不进行任何数据过滤。
//        if (commandType == SqlCommandType.INSERT || commandType == SqlCommandType.UPDATE || commandType == SqlCommandType.DELETE) {
//            return invocation.proceed();
//        }
//
//        // org.jeecg.modules.system.mapper.SysCategoryMapper.queryIdByCode
//        String sqlId = mappedStatement.getId();
//        int pos = StringUtils.lastIndexOf(sqlId, ".");
//        // 类名 org.jeecg.modules.system.mapper.SysCategoryMapper
//        String className = StringUtils.substring(sqlId, 0, pos);
//        // 方法名 queryIdByCode
//        String methodName = StringUtils.substring(sqlId, pos + 1);
//
//        Statement statement = null;
//
//        // 处理数据权限过滤
//        if (properties.getEnabledDataPermFilter()) {
//            this.processDataPermFilter(className, methodName, delegate.getBoundSql(), commandType, statement, sqlId);
//        }
//        return invocation.proceed();
//    }
//
//    /**
//     * @param className
//     * @param methodName
//     * @param boundSql    它是建立 SQL 和参数的地方。它有3个常用的属性：SQL、parameterObject 和 parameterMappings。
//     * @param commandType
//     * @param statement   它保存映射器的一个节点（select | insert | delete | update）。包括许多我们配置的 SQL、SQL 的 id、缓存信息、resultMap、parameterType、resultType 和 languageDriver 等重要的内容。
//     * @param sqlId
//     * @throws JSQLParserException
//     */
//    private void processDataPermFilter(String className,
//                                       String methodName,
//                                       BoundSql boundSql,
//                                       SqlCommandType commandType,
//                                       Statement statement,
//                                       String sqlId)
//            throws JSQLParserException {
//
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        if (sysUser == null) {
//            return;
//        }
//
//        // 如果是管理员则不参与数据权限的数据过滤，显示全部数据。
//        if (sysUser.getRoleCodes().contains(CommonConstant.ADMIN)) {
//            return;
//        }
//
//        // 判断当前线程本地存储中，业务操作是否禁用了数据权限过滤，如果禁用，则不进行后续的数据过滤处理了。
//        ModelDataPermInfo info = cachedDataPermMap.get(className);
//        // 再次查找当前方法是否为排除方法，如果不是，就参与数据权限注入过滤。
//        if (info == null || CollUtil.contains(info.getExcludeMethodNameSet(), methodName)) {
//            return;
//        }
//
//        // 查找当前用户的数据权限，从request里面获取
//        // dataPermMap的key是规则类型(TYPE_MANAGE_LINE_ONLY)，value是对应的数组，比如我管理的线路（1,2,3,4,8）
//        Map<String, String> dataPermMap = ContextUtil.loadDataSearchConditon();
//        if (MapUtils.isEmpty(dataPermMap)) {
//            return;
//        }
//
//        // 包含所有权限，不需要过滤
//        if (dataPermMap.containsKey(DataPermRuleType.TYPE_ALL)) {
//            return;
//        }
//
//        // 如果当前过滤注解中mustIncludeUserRule参数为true，同时当前用户的数据权限中，不包含TYPE_USER_ONLY，
//        // 这里就需要自动添加该数据权限。
//        if (info.getMustIncludeUserRule()
//                && !dataPermMap.containsKey(DataPermRuleType.TYPE_USER_ONLY)) {
//            dataPermMap.put(DataPermRuleType.TYPE_USER_ONLY, null);
//        }
//
//        this.processDataPerm(info, dataPermMap, boundSql, commandType, statement, sqlId);
//    }
//
//    /**
//     *
//     * @param info
//     * @param dataPermMap 用户数据权限集合
//     * @param boundSql
//     * @param commandType
//     * @param statement
//     * @param sqlId
//     * @throws JSQLParserException
//     */
//    private void processDataPerm(
//            ModelDataPermInfo info,
//            Map<String, String> dataPermMap,
//            BoundSql boundSql,
//            SqlCommandType commandType,
//            Statement statement,
//            String sqlId) throws JSQLParserException {
//        // 处理好的sql集合
//        List<String> criteriaList = new LinkedList<>();
//
//        for (Map.Entry<String, String> entry : dataPermMap.entrySet()) {
//            String filterClause = processDataPermRule(info, entry.getKey(), entry.getValue(), sqlId);
//            if (StringUtils.isNotBlank(filterClause)) {
//                criteriaList.add(filterClause);
//            }
//        }
//        if (CollectionUtils.isEmpty(criteriaList)) {
//            return;
//        }
//
//        StringBuilder filterBuilder = new StringBuilder(128);
//        filterBuilder.append("(");
//        filterBuilder.append(StringUtils.join(criteriaList, " AND "));
//        filterBuilder.append(")");
//        String dataFilter = filterBuilder.toString();
//        if (statement == null) {
//            String sql = boundSql.getSql();
//            statement = CCJSqlParserUtil.parse(sql);
//        }
//
//        // 只处理select的语句
//        if (commandType == SqlCommandType.SELECT) {
//            Select select = (Select) statement;
//            PlainSelect selectBody = (PlainSelect) select.getSelectBody();
//            FromItem fromItem = selectBody.getFromItem();
//            PlainSelect subSelect = null;
//            if (fromItem != null) {
//                if (fromItem instanceof SubSelect) {
//                    subSelect = (PlainSelect) ((SubSelect) fromItem).getSelectBody();
//                }
//                if (subSelect != null) {
//                    buildWhereClause(subSelect, dataFilter);
//                } else {
//                    buildWhereClause(selectBody, dataFilter);
//                }
//            }
//        }
//        log.info("DataPerm Filter Where Clause [{}]", dataFilter);
//        ReflectUtil.setFieldValue(boundSql, "sql", statement.toString());
//    }
//
//    private String processDataPermRule(ModelDataPermInfo info, String ruleType, String ruleValue, String sqlId) {
//
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        StringBuilder filter = new StringBuilder(128);
//
//        // 处理mapper上标记有@DataPermission
//        DataColumn[] dataColumns = findAnnotation(sqlId);
//        if (ArrayUtil.isNotEmpty(dataColumns)) {
//            filter = handleDataPermission(dataColumns, ruleType, ruleValue,sysUser);
//            if (ObjectUtil.isNotEmpty(filter)) {
//                return filter.toString();
//            }
//        }
//
//        // 处理标记@StaionFilterColumn、@SystemFilterColumn、@MajorFilterColumn、@DeptFilterColumn、@UserFilterColumn
//        handleFilterColumn(info, ruleType, ruleValue, sysUser, filter);
//        return filter.toString();
//    }
//
//    private void handleFilterColumn(ModelDataPermInfo info, String ruleType, String ruleValue, LoginUser sysUser, StringBuilder filter) {
//        if (DataPermRuleType.TYPE_USER_ONLY.equals(ruleType)) {
//            if (StringUtils.isNotBlank(info.getUserFilterColumn())) {
//                if (properties.getAddTableNamePrefix()) {
//                    filter.append(info.getMainTableName()).append(".");
//                }
//                filter.append(info.getUserFilterColumn())
//                        .append(" = ")
//                        .append("'" + sysUser.getUsername() + "'");
//            }
//        } else if (DataPermRuleType.TYPE_MANAGE_DEPT.equals(ruleType)) {
//            if (StringUtils.isNotBlank(info.getDeptFilterColumn())) {
//                if (properties.getAddTableNamePrefix()) {
//                    filter.append(info.getMainTableName()).append(".");
//                }
//                filter.append(info.getDeptFilterColumn())
//                        .append(" IN (")
//                        .append(ruleValue)
//                        .append(") ");
//            }
//        } else if (DataPermRuleType.TYPE_DEPT_ONLY.equals(ruleType)) {
//            if (StringUtils.isNotBlank(info.getDeptFilterColumn())) {
//                if (properties.getAddTableNamePrefix()) {
//                    filter.append(info.getMainTableName()).append(".");
//                }
//                filter.append(info.getDeptFilterColumn())
//                        .append(" = ")
//                        .append("'" + sysUser.getOrgCode() + "'");
//            }
//        } else if (DataPermRuleType.TYPE_MANAGE_LINE_ONLY.equals(ruleType)) {
//            if (StringUtils.isNotBlank(info.getLineFilterColumn())) {
//                if (properties.getAddTableNamePrefix()) {
//                    filter.append(info.getMainTableName()).append(".");
//                }
//                filter.append(info.getLineFilterColumn())
//                        .append(" IN (")
//                        .append(ruleValue)
//                        .append(") ");
//            }
//        } else if (DataPermRuleType.TYPE_MANAGE_STATION_ONLY.equals(ruleType)) {
//            if (StringUtils.isNotBlank(info.getStationFilterColumn())) {
//                if (properties.getAddTableNamePrefix()) {
//                    filter.append(info.getMainTableName()).append(".");
//                }
//                filter.append(info.getStationFilterColumn())
//                        .append(" IN (")
//                        .append(ruleValue)
//                        .append(") ");
//            }
//        } else if (DataPermRuleType.TYPE_MANAGE_MAJOR_ONLY.equals(ruleType)) {
//            if (StringUtils.isNotBlank(info.getMajorFilterColumn())) {
//                if (properties.getAddTableNamePrefix()) {
//                    filter.append(info.getMainTableName()).append(".");
//                }
//                filter.append(info.getMajorFilterColumn())
//                        .append(" IN (")
//                        .append(ruleValue)
//                        .append(") ");
//            }
//        } else if (DataPermRuleType.TYPE_MANAGE_SYSTEM_ONLY.equals(ruleType)) {
//            if (StringUtils.isNotBlank(info.getSystemFilterColumn())) {
//                if (properties.getAddTableNamePrefix()) {
//                    filter.append(info.getMainTableName()).append(".");
//                }
//                filter.append(info.getSystemFilterColumn())
//                        .append(" IN (")
//                        .append(ruleValue)
//                        .append(") ");
//            }
//        }
//    }
//
//    private DataColumn[] findAnnotation(String sqlId) {
//        StringBuilder sb = new StringBuilder(sqlId);
//        int index = sb.lastIndexOf(".");
//        String clazzName = sb.substring(0, index);
//        String methodName = sb.substring(index + 1, sb.length());
//        Class<?> clazz = ClassUtil.loadClass(clazzName);
//        List<Method> methods = Arrays.stream(ClassUtil.getDeclaredMethods(clazz))
//                .filter(method -> method.getName().equals(methodName)).collect(Collectors.toList());
//        DataPermission dataPermission;
//        // 获取方法注解
//        for (Method method : methods) {
//            dataPermission = dataPermissionCacheMap.get(sqlId);
//            if (ObjectUtil.isNotNull(dataPermission)) {
//                return dataPermission.value();
//            }
//            if (null != AnnotationUtil.getAnnotation(method, DataPermission.class)) {
//                dataPermission = AnnotationUtil.getAnnotation(method, DataPermission.class);
//                dataPermissionCacheMap.put(sqlId, dataPermission);
//                return dataPermission.value();
//            }
//        }
//        dataPermission = dataPermissionCacheMap.get(clazz.getName());
//        if (ObjectUtil.isNotNull(dataPermission)) {
//            return dataPermission.value();
//        }
//        // 获取类注解
//        if (null != AnnotationUtil.getAnnotation(clazz, DataPermission.class)) {
//            dataPermission = AnnotationUtil.getAnnotation(clazz, DataPermission.class);
//            dataPermissionCacheMap.put(clazz.getName(), dataPermission);
//            return dataPermission.value();
//        }
//        return null;
//    }
//
//    private StringBuilder handleDataPermission(DataColumn[] dataColumns, String ruleType, String ruleValue,LoginUser sysUser) {
//        StandardEvaluationContext context = new StandardEvaluationContext();
//        DataScopeType type = DataScopeType.findCode(ruleType);
//
//
//        // 添加登录用户信息
//        context.setVariable("sysUser",sysUser);
//        context.setBeanResolver(beanResolver);
//        StringBuilder str = new StringBuilder();
//        boolean isSuccess = false;
//        for (DataColumn dataColumn : dataColumns) {
//
//            // 不包含 key 变量 则不处理
//            if (!StringUtils.containsAny(type.getSqlTemplate(),
//                    Arrays.stream(dataColumn.key()).map(key -> "#" + key).toArray(String[]::new)
//            )) {
//                continue;
//            }
//            // 设置注解变量 key 为表达式变量 value 为变量值
//            for (int i = 0; i < dataColumn.key().length; i++) {
//                context.setVariable(dataColumn.key()[i], dataColumn.value()[i]);
//            }
//
//            // 解析sql模板并填充
//            String sql = parser.parseExpression(type.getSqlTemplate(), parserContext).getValue(context, String.class);
//            str.append(sql);
//            isSuccess = true;
//        }
//
//        // 未处理成功则填充兜底方案
//        if (!isSuccess && StringUtils.isNotBlank(type.getElseSql())) {
//            str.append(type.getElseSql());
//        }
//
//        return str;
//    }
//
//    private void buildWhereClause(PlainSelect select, String dataFilter) throws JSQLParserException {
//        if (select.getWhere() == null) {
//            select.setWhere(CCJSqlParserUtil.parseCondExpression(dataFilter));
//        } else {
//            AndExpression and = new AndExpression(
//                    CCJSqlParserUtil.parseCondExpression(dataFilter), select.getWhere());
//            select.setWhere(and);
//        }
//    }
//
//    @Override
//    public Object plugin(Object target) {
//        return Plugin.wrap(target, this);
//    }
//
//    @Override
//    public void setProperties(Properties properties) {
//        // 这里需要空注解，否则sonar会不happy。
//    }
//
//
//}
