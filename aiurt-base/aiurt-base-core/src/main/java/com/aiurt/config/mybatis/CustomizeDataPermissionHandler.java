package com.aiurt.config.mybatis;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.aspect.annotation.DataScope;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.config.mybatis.constant.DataPermRuleType;
import com.aiurt.config.mybatis.filter.DataScopeParam;
import com.aiurt.config.mybatis.filter.PermissionFilter;
import com.baomidou.mybatisplus.extension.plugins.handler.DataPermissionHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jeecg.common.system.vo.*;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wgp
 * @Title: mybatis-plus数据权限过滤插件
 * @Description: 该插件适用于使用PaginationInnerInterceptor拦截追加limit来进行分页的
 * 参考：https://blog.csdn.net/qq_42445433/article/details/124406475
 * @date 2022/5/2715:55
 */
@Aspect
@Slf4j
@Component
public class CustomizeDataPermissionHandler implements DataPermissionHandler {

    @Resource
    private CommonAPI commonAPI;

    /**
     * 清空当前线程上次保存的权限信息
     */
    @After("dataScopePointCut()")
    public void clearThreadLocal() {
        PermissionHelper.clear();
        log.debug("threadLocal.clear()");
    }

    /**
     * 配置织入点
     */
    @Pointcut("@annotation(com.aiurt.common.aspect.annotation.DataScope)")
    public void dataScopePointCut() {
    }

    @Before("dataScopePointCut()")
    public void doBefore(JoinPoint point) {
        // 获得注解
        DataScope controllerDataScope = getAnnotationLog(point);
        if (controllerDataScope != null) {
            // 获取当前的用户及相关属性，需提前获取和保存数据权限对应的部门ID集合
            LoginUser sysUser = this.getLoginUser();
            // todo 如果你是admin，不需要过滤
            if (ObjectUtil.isNotEmpty(sysUser)) {

            }
            // 组装过滤器
            List<DataScopeParam> dataScopeParam = this.dataScopeFilter(controllerDataScope);
            // 将过滤器添加到线程中
            if (CollUtil.isNotEmpty(dataScopeParam)) {
                dataScopeParam.forEach(dataScope -> {
                    PermissionHelper.addFilter(dataScope);
                });
            }
        }
    }


    /**
     * 是否存在注解，如果存在就获取
     */
    private DataScope getAnnotationLog(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method = methodSignature.getMethod();
        if (method != null) {
            return method.getAnnotation(DataScope.class);
        }
        return null;
    }

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

    /**
     * 组装数据权限过滤器
     *
     * @param controllerDataScope
     * @return
     */
    private List<DataScopeParam> dataScopeFilter(DataScope controllerDataScope) {
        List<DataScopeParam> dataScopeParam = new ArrayList<>();
        // 当前登录用户
        LoginUser loginUser = getLoginUser();
        String id = loginUser.getId();

        // 管理的部门
        List<CsUserDepartModel> departByUserId = commonAPI.getDepartByUserId(id);

        // 管理的站点
        List<CsUserStationModel> stationByUserId = commonAPI.getStationByUserId(id);

        // 管理的专业
        List<CsUserMajorModel> majorByUserId = commonAPI.getMajorByUserId(id);

        // 管理的子系统
        List<CsUserSubsystemModel> subsystemByUserId = commonAPI.getSubsystemByUserId(id);

        // 从request域里面获取过滤规则
        List<SysPermissionDataRuleModel> list = null;

        // 遍历，如果是DataPermRuleType里面的类型就需要拼接
        if (CollUtil.isNotEmpty(list)) {
            for (SysPermissionDataRuleModel per : list) {
                String ruleConditions = per.getRuleConditions();
                if (DataPermRuleType.isValid(ruleConditions)) {

                    // 规则字段不为空时才处理
                    if (StrUtil.isNotEmpty(per.getRuleColumn())) {

                        if (DataPermRuleType.TYPE_ALL.equals(ruleConditions)) {
                            // 全部权限
                            break;
                        } else if (DataPermRuleType.TYPE_USER_ONLY.equals(ruleConditions)) {
                            // 仅看自己的数据,拼接的sql
                            StringBuilder sql = new StringBuilder(64);
                            sql.append("(");
                            if (StrUtil.isNotEmpty(controllerDataScope.userAlias())) {
                                sql.append(controllerDataScope.userAlias()).append(".");
                            }
                            sql.append(String.format("%s = '%s')", per.getRuleColumn(), loginUser.getUsername()));
                            if (ObjectUtil.isNotEmpty(sql)) {
                                dataScopeParam.add(DataScopeParam.builder().sql(sql.toString()).build());
                            }
                        } else if (DataPermRuleType.TYPE_DEPT_ONLY.equals(ruleConditions)) {
                            // 当前部门的
                            StringBuilder sql = new StringBuilder(64);
                            if (StrUtil.isNotEmpty(controllerDataScope.deptAlias())) {
                                sql.append(controllerDataScope.deptAlias()).append(".");
                            }
                            sql.append(String.format("%s = '%s'", per.getRuleColumn(), loginUser.getOrgCode()));
                            if (ObjectUtil.isNotEmpty(sql)) {
                                dataScopeParam.add(DataScopeParam.builder().sql(sql.toString()).build());
                            }
                        } else if (DataPermRuleType.TYPE_DEPT_MANAGED.equals(ruleConditions)) {
                            // 当前管理的部门
                            StringBuilder sql = new StringBuilder(64);
                            if (StrUtil.isNotEmpty(controllerDataScope.deptAlias())) {
                                sql.append(controllerDataScope.deptAlias()).append(".");
                            }
                            if(CollUtil.isNotEmpty(departByUserId)){
                                sql.append(String.format("%s in (%s)",per.getRuleColumn(),departByUserId.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.joining(","))));
                                if (ObjectUtil.isNotEmpty(sql)) {
                                    dataScopeParam.add(DataScopeParam.builder().sql(sql.toString()).build());
                                }
                            }
                        }else if(DataPermRuleType.TYPE_STATION_MANAGED.equals(ruleConditions)){
                            // 当前管理的站点
                            StringBuilder sql = new StringBuilder(64);
                            if (StrUtil.isNotEmpty(controllerDataScope.stationAlias())) {
                                sql.append(controllerDataScope.stationAlias()).append(".");
                            }
                            if(CollUtil.isNotEmpty(stationByUserId)){
                                sql.append(String.format("%s in (%s)",per.getRuleColumn(),stationByUserId.stream().map(CsUserStationModel::getStationCode).collect(Collectors.joining(","))));
                                if (ObjectUtil.isNotEmpty(sql)) {
                                    dataScopeParam.add(DataScopeParam.builder().sql(sql.toString()).build());
                                }
                            }
                        }else if(DataPermRuleType.TYPE_MAJOR_MANAGED.equals(ruleConditions)){
                            // 当前管理的专业
                            StringBuilder sql = new StringBuilder(64);
                            if (StrUtil.isNotEmpty(controllerDataScope.majorAlias())) {
                                sql.append(controllerDataScope.majorAlias()).append(".");
                            }
                            if(CollUtil.isNotEmpty(majorByUserId)){
                                sql.append(String.format("%s in (%s)",per.getRuleColumn(),majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.joining(","))));
                                if (ObjectUtil.isNotEmpty(sql)) {
                                    dataScopeParam.add(DataScopeParam.builder().sql(sql.toString()).build());
                                }
                            }
                        }else if(DataPermRuleType.TYPE_SUBSYSTEM_MANAGED.equals(ruleConditions)){
                            // 当前管理的子系统
                            StringBuilder sql = new StringBuilder(64);
                            if (StrUtil.isNotEmpty(controllerDataScope.subsystemAlias())) {
                                sql.append(controllerDataScope.subsystemAlias()).append(".");
                            }
                            if(CollUtil.isNotEmpty(subsystemByUserId)){
                                sql.append(String.format("%s in (%s)",per.getRuleColumn(),subsystemByUserId.stream().map(CsUserSubsystemModel::getSystemCode).collect(Collectors.joining(","))));
                                if (ObjectUtil.isNotEmpty(sql)) {
                                    dataScopeParam.add(DataScopeParam.builder().sql(sql.toString()).build());
                                }
                            }
                        }
                    }
                }
            }
        }

        return dataScopeParam;
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
        if (ObjectUtil.isEmpty(sysUser)) {
            throw new AiurtBootException("非法操作");
        }
        return sysUser;
    }
}
