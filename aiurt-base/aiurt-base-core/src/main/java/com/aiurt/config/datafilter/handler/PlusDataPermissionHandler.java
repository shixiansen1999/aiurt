package com.aiurt.config.datafilter.handler;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.DataColumn;
import com.aiurt.common.aspect.annotation.DataPermission;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.config.datafilter.constant.enums.DataScopeType;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.SpringUtils;
import com.aiurt.config.datafilter.config.DataFilterProperties;
import com.aiurt.config.datafilter.constant.DataPermRuleType;
import com.aiurt.config.datafilter.entity.ModelDataPermInfo;
import com.aiurt.config.datafilter.interceptor.PlusLoadDataPerm;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.config.datafilter.utils.ContextUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.BeanResolver;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author wgp
 * @Title:
 * @Description: 数据权限过滤助手
 * @date 2023/2/614:11
 */
@Slf4j
public class PlusDataPermissionHandler {

    /**
     * 方法名称 与 注解的映射关系缓存
     */
    private final Map<String, DataPermission> dataPermissionCacheMap = new ConcurrentHashMap<>();
    /**
     * spel 解析器
     */
    private final ExpressionParser parser = new SpelExpressionParser();
    private final ParserContext parserContext = new TemplateParserContext();
    /**
     * bean解析器 用于处理 spel 表达式中对 bean 的调用
     */
    private final BeanResolver beanResolver = new BeanFactoryResolver(SpringUtils.getBeanFactory());
    /**
     * 用户信息标记
     */
    private final String SYS_USER = "sysUser";

    public Expression getSqlSegment(Expression where, String mappedStatementId) {
        // 判断当前线程本地存储中，业务操作是否禁用了数据权限过滤，如果禁用，则不进行后续的数据过滤处理了。
        if (!GlobalThreadLocal.enabledDataFilter()) {
            return where;
        }

        // 只有在HttpServletRequest场景下，该拦截器才起作用，对于系统级别的预加载数据不会应用数据权限。
        if (!ContextUtil.hasRequestContext()) {
            return where;
        }

        // 通常对于无需登录的白名单url，也无需过滤了。
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(sysUser)) {
            return where;
        }

        // 默认对管理员角色不进行数据过滤
        if (sysUser.getRoleCodes().contains(CommonConstant.ADMIN)) {
            return where;
        }

        // org.jeecg.modules.system.mapper.SysCategoryMapper.queryIdByCode
        int pos = StringUtils.lastIndexOf(mappedStatementId, ".");
        // 类名 org.jeecg.modules.system.mapper.SysCategoryMapper
        String className = StringUtils.substring(mappedStatementId, 0, pos);
        // 方法名 queryIdByCode
        String methodName = StringUtils.substring(mappedStatementId, pos + 1);

        // 判断当前线程本地存储中，业务操作是否禁用了数据权限过滤，如果禁用，则不进行后续的数据过滤处理了。
        PlusLoadDataPerm plusLoadDataPerm = SpringUtils.getBean(PlusLoadDataPerm.class);
        if (ObjectUtil.isEmpty(plusLoadDataPerm)) {
            return where;
        }
        ModelDataPermInfo info = plusLoadDataPerm.getCachedDataPermMap(className);
        // 再次查找当前方法是否为排除方法，如果不是，就参与数据权限注入过滤。
        if (info == null || CollUtil.contains(info.getExcludeMethodNameSet(), methodName)) {
            return where;
        }

        // 用户数据权限规则
        Map<String, String> dataPermMap = ContextUtil.loadDataSearchConditon();
        if (MapUtils.isEmpty(dataPermMap)) {
            return where;
        }

        // 包含所有权限，不需要过滤
        if (dataPermMap.containsKey(DataPermRuleType.TYPE_ALL)) {
            return where;
        }

        // 是否启动数据权限过滤
        DataFilterProperties dataFilterProperties = SpringUtils.getBean(DataFilterProperties.class);
        if (ObjectUtil.isNotEmpty(dataFilterProperties) && dataFilterProperties.getEnabledDataPermFilter()) {
            String dataFilterSql = this.buildDataFilter(info, dataPermMap, mappedStatementId);
            if (StrUtil.isEmpty(dataFilterSql)) {
                return where;
            }

            try {
                Expression expression = CCJSqlParserUtil.parseExpression(dataFilterSql);
                return ObjectUtil.isNotNull(where) ? new AndExpression(where, expression) : expression;
            } catch (JSQLParserException e) {
                throw new AiurtBootException("数据权限解析异常 => " + e.getMessage());
            }
        }
        return where;
    }

    /**
     * 构建sql
     *
     * @param info
     * @param dataPermMap       用户数据规则信息
     * @param mappedStatementId
     * @return
     */
    private String buildDataFilter(
            ModelDataPermInfo info,
            Map<String, String> dataPermMap,
            String mappedStatementId) {
        // 处理好的sql集合
        List<String> sqlList = new LinkedList<>();

        for (Map.Entry<String, String> entry : dataPermMap.entrySet()) {
            String filterClause = doBuildDataFilter(info, entry.getKey(), entry.getValue(), mappedStatementId);
            if (StringUtils.isNotBlank(filterClause)) {
                sqlList.add(filterClause);
            }
        }

        if (CollUtil.isEmpty(sqlList)) {
            return "";
        }

        StringBuilder filterBuilder = new StringBuilder(128);
        filterBuilder.append("(");
        filterBuilder.append(StringUtils.join(sqlList, " AND "));
        filterBuilder.append(")");
        String dataFilterSql = StrUtil.isNotEmpty(filterBuilder.toString()) ? filterBuilder.toString() : "";
        return dataFilterSql;
    }

    /**
     * 构造数据过滤sql
     */
    private String doBuildDataFilter(ModelDataPermInfo info, String ruleType, String ruleValue, String mappedStatementId) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        StringBuilder filterClause = new StringBuilder(128);

        // 处理mapper上标记有@DataPermission
        DataColumn[] dataColumns = findAnnotation(mappedStatementId);
        if (ArrayUtil.isNotEmpty(dataColumns)) {
            filterClause = handleDataPermission(dataColumns, ruleType, sysUser);
            if (ObjectUtil.isNotEmpty(filterClause)) {
                return filterClause.toString();
            }
        }

        // 兼容之前的数据权限过滤方式
        // 处理标记@StaionFilterColumn、@SystemFilterColumn、@MajorFilterColumn、@DeptFilterColumn、@UserFilterColumn
        handleFilterColumn(info, ruleType, ruleValue, sysUser, filterClause);
        return filterClause.toString();
    }

    private StringBuilder handleDataPermission(DataColumn[] dataColumns, String ruleType, LoginUser sysUser) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        DataScopeType type = DataScopeType.findCode(ruleType);
        // 添加登录用户信息
        context.setVariable(SYS_USER, sysUser);
        // 添加bean解析器
        context.setBeanResolver(beanResolver);

        StringBuilder filterClause = new StringBuilder();
        boolean isSuccess = false;
        for (DataColumn dataColumn : dataColumns) {
            // 不包含 key 变量 则不处理
            if (!StringUtils.containsAny(type.getSqlTemplate(),
                    Arrays.stream(dataColumn.key()).map(key -> "#" + key).toArray(String[]::new)
            )) {
                continue;
            }

            // 设置注解变量 key 为表达式变量 value 为变量值
            for (int i = 0; i < dataColumn.key().length; i++) {
                context.setVariable(dataColumn.key()[i], dataColumn.value()[i]);
            }

            // 解析sql模板并填充
            String sql = parser.parseExpression(type.getSqlTemplate(), parserContext).getValue(context, String.class);
            filterClause.append(sql);
            isSuccess = true;
        }

        // 未处理成功则填充兜底方案
        if (!isSuccess && StringUtils.isNotBlank(type.getElseSql())) {
            filterClause.append(type.getElseSql());
        }

        return filterClause;
    }

    private void handleFilterColumn(ModelDataPermInfo info, String ruleType, String ruleValue, LoginUser sysUser, StringBuilder filter) {
        DataFilterProperties dataFilterProperties = SpringUtils.getBean(DataFilterProperties.class);

        if (DataPermRuleType.TYPE_USER_ONLY.equals(ruleType)) {
            if (StringUtils.isNotBlank(info.getUserFilterColumn())) {
                if (ObjectUtil.isNotEmpty(dataFilterProperties) && dataFilterProperties.getAddTableNamePrefix()) {
                    filter.append(info.getMainTableName()).append(".");
                }
                filter.append(info.getUserFilterColumn())
                        .append(" = ")
                        .append("'" + sysUser.getUsername() + "'");
            }
        } else if (DataPermRuleType.TYPE_MANAGE_DEPT.equals(ruleType)) {
            if (StringUtils.isNotBlank(info.getDeptFilterColumn())) {
                if (ObjectUtil.isNotEmpty(dataFilterProperties) && dataFilterProperties.getAddTableNamePrefix()) {
                    filter.append(info.getMainTableName()).append(".");
                }
                filter.append(info.getDeptFilterColumn())
                        .append(" IN (")
                        .append(ruleValue)
                        .append(") ");
            }
        } else if (DataPermRuleType.TYPE_DEPT_ONLY.equals(ruleType)) {
            if (StringUtils.isNotBlank(info.getDeptFilterColumn())) {
                if (ObjectUtil.isNotEmpty(dataFilterProperties) && dataFilterProperties.getAddTableNamePrefix()) {
                    filter.append(info.getMainTableName()).append(".");
                }
                filter.append(info.getDeptFilterColumn())
                        .append(" = ")
                        .append("'" + sysUser.getOrgCode() + "'");
            }
        } else if (DataPermRuleType.TYPE_MANAGE_LINE_ONLY.equals(ruleType)) {
            if (StringUtils.isNotBlank(info.getLineFilterColumn())) {
                if (ObjectUtil.isNotEmpty(dataFilterProperties) && dataFilterProperties.getAddTableNamePrefix()) {
                    filter.append(info.getMainTableName()).append(".");
                }
                filter.append(info.getLineFilterColumn())
                        .append(" IN (")
                        .append(ruleValue)
                        .append(") ");
            }
        } else if (DataPermRuleType.TYPE_MANAGE_STATION_ONLY.equals(ruleType)) {
            if (StringUtils.isNotBlank(info.getStationFilterColumn())) {
                if (ObjectUtil.isNotEmpty(dataFilterProperties) && dataFilterProperties.getAddTableNamePrefix()) {
                    filter.append(info.getMainTableName()).append(".");
                }
                filter.append(info.getStationFilterColumn())
                        .append(" IN (")
                        .append(ruleValue)
                        .append(") ");
            }
        } else if (DataPermRuleType.TYPE_MANAGE_MAJOR_ONLY.equals(ruleType)) {
            if (StringUtils.isNotBlank(info.getMajorFilterColumn())) {
                if (ObjectUtil.isNotEmpty(dataFilterProperties) && dataFilterProperties.getAddTableNamePrefix()) {
                    filter.append(info.getMainTableName()).append(".");
                }
                filter.append(info.getMajorFilterColumn())
                        .append(" IN (")
                        .append(ruleValue)
                        .append(") ");
            }
        } else if (DataPermRuleType.TYPE_MANAGE_SYSTEM_ONLY.equals(ruleType)) {
            if (StringUtils.isNotBlank(info.getSystemFilterColumn())) {
                if (ObjectUtil.isNotEmpty(dataFilterProperties) && dataFilterProperties.getAddTableNamePrefix()) {
                    filter.append(info.getMainTableName()).append(".");
                }
                filter.append(info.getSystemFilterColumn())
                        .append(" IN (")
                        .append(ruleValue)
                        .append(") ");
            }
        }
    }

    /**
     * 根据sqlId查找对应的@DataColumn
     *
     * @param sqlId
     * @return
     */
    private DataColumn[] findAnnotation(String sqlId) {
        StringBuilder sb = new StringBuilder(sqlId);
        int index = sb.lastIndexOf(".");
        String clazzName = sb.substring(0, index);
        String methodName = sb.substring(index + 1, sb.length());
        Class<?> clazz = ClassUtil.loadClass(clazzName);
        List<Method> methods = Arrays.stream(ClassUtil.getDeclaredMethods(clazz))
                .filter(method -> method.getName().equals(methodName)).collect(Collectors.toList());
        DataPermission dataPermission;
        // 获取方法注解
        for (Method method : methods) {
            dataPermission = dataPermissionCacheMap.get(sqlId);
            if (ObjectUtil.isNotNull(dataPermission)) {
                return dataPermission.value();
            }
            if (null != AnnotationUtil.getAnnotation(method, DataPermission.class)) {
                dataPermission = AnnotationUtil.getAnnotation(method, DataPermission.class);
                dataPermissionCacheMap.put(sqlId, dataPermission);
                return dataPermission.value();
            }
        }
        dataPermission = dataPermissionCacheMap.get(clazz.getName());
        if (ObjectUtil.isNotNull(dataPermission)) {
            return dataPermission.value();
        }
        // 获取类注解
        if (null != AnnotationUtil.getAnnotation(clazz, DataPermission.class)) {
            dataPermission = AnnotationUtil.getAnnotation(clazz, DataPermission.class);
            dataPermissionCacheMap.put(clazz.getName(), dataPermission);
            return dataPermission.value();
        }
        return null;
    }
}
