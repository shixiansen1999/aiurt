package com.aiurt.common.aspect;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.aspect.annotation.PermissionData;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.SymbolConstant;
import com.aiurt.common.system.util.JeecgDataAutorUtils;
import com.aiurt.common.system.util.JwtUtil;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.config.datafilter.constant.DataPermRuleType;
import com.aiurt.config.datafilter.utils.ContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.system.vo.*;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据权限切面处理类
 * 当被请求的方法有注解PermissionData时,会在往当前request中写入数据权限信息
 *
 * @Date 2019年4月10日
 * @Version: 1.0
 * @author: jeecg-boot
 */
@Aspect
@Component
@Slf4j
public class PermissionDataAspect {
    @Lazy
    @Autowired
    private CommonAPI commonApi;

    @Pointcut("@annotation(com.aiurt.common.aspect.annotation.PermissionData)")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object arround(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        PermissionData pd = method.getAnnotation(PermissionData.class);
        String component = pd.pageComponent();
        String requestMethod = request.getMethod();
        String requestPath = request.getRequestURI().substring(request.getContextPath().length());
        requestPath = filterUrl(requestPath);
        //update-begin-author:taoyan date:20211027 for:JTC-132【online报表权限】online报表带参数的菜单配置数据权限无效
        //先判断是否online报表请求
        // TODO 参数顺序调整有隐患
        if (requestPath.indexOf(UrlMatchEnum.CGREPORT_DATA.getMatchUrl()) >= 0) {
            // 获取地址栏参数
            String urlParamString = request.getParameter(CommonConstant.ONL_REP_URL_PARAM_STR);
            if (oConvertUtils.isNotEmpty(urlParamString)) {
                requestPath += "?" + urlParamString;
            }
        }
        //update-end-author:taoyan date:20211027 for:JTC-132【online报表权限】online报表带参数的菜单配置数据权限无效
        log.info("拦截请求 >> {} ; 请求类型 >> {} . ", requestPath, requestMethod);
        String username = JwtUtil.getUserNameByToken(request);

        // 查询数据权限信息
        // 微服务情况下也得支持缓存机制
        List<SysPermissionDataRuleModel> dataRules = commonApi.queryPermissionDataRule(component, requestPath, username);
        if (dataRules != null && dataRules.size() > 0) {
            // 处理自定义的权限信息
            List<SysPermissionDataRuleModel> customPermissions = dataRules.stream().filter(rule -> DataPermRuleType.isValid(rule.getRuleConditions())).collect(Collectors.toList());
            Map<String, String> customPerMap = handleRuleValue(customPermissions);
            if (MapUtils.isNotEmpty(customPerMap)) {
                ContextUtil.installDataSearchConditon(request, customPerMap);
            }

            // 处理自带的权限信息
            List<SysPermissionDataRuleModel> aiurtPermissions = dataRules.stream().filter(rule -> !DataPermRuleType.isValid(rule.getRuleConditions())).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(aiurtPermissions)) {
                JeecgDataAutorUtils.installDataSearchConditon(request, aiurtPermissions);
            }

            // 微服务情况下也得支持缓存机制
            SysUserCacheInfo userinfo = commonApi.getCacheUser(username);
            JeecgDataAutorUtils.installUserInfo(request, userinfo);
        }
        return point.proceed();
    }


    private String filterUrl(String requestPath) {
        String url = "";
        if (oConvertUtils.isNotEmpty(requestPath)) {
            url = requestPath.replace("\\", "/");
            url = url.replace("//", "/");
            if (url.indexOf(SymbolConstant.DOUBLE_SLASH) >= 0) {
                url = filterUrl(url);
            }
        }
        return url;
    }

    /**
     * 获取请求地址
     *
     * @param request
     * @return
     */
    @Deprecated
    private String getJgAuthRequsetPath(HttpServletRequest request) {
        String queryString = request.getQueryString();
        String requestPath = request.getRequestURI();
        if (oConvertUtils.isNotEmpty(queryString)) {
            requestPath += "?" + queryString;
        }
        // 去掉其他参数(保留一个参数) 例如：loginController.do?login
        if (requestPath.indexOf(SymbolConstant.AND) > -1) {
            requestPath = requestPath.substring(0, requestPath.indexOf("&"));
        }
        if (requestPath.indexOf(QueryRuleEnum.EQ.getValue()) != -1) {
            if (requestPath.indexOf(CommonConstant.SPOT_DO) != -1) {
                requestPath = requestPath.substring(0, requestPath.indexOf(".do") + 3);
            } else {
                requestPath = requestPath.substring(0, requestPath.indexOf("?"));
            }
        }
        // 去掉项目路径
        requestPath = requestPath.substring(request.getContextPath().length() + 1);
        return filterUrl(requestPath);
    }

    @Deprecated
    private boolean moHuContain(List<String> list, String key) {
        for (String str : list) {
            if (key.contains(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 处理自定义权限的规则值
     * (K,V)：K是DataPermRuleType里的规则类型，V是对应的规则值
     *
     * @param customPermissions
     */
    private Map<String, String> handleRuleValue(List<SysPermissionDataRuleModel> customPermissions) {
        Map<String, String> dataPermMap = new HashMap<>(8);

        // 用户信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String userId = ObjectUtil.isNotEmpty(sysUser) ? sysUser.getId() : null;

        if (CollUtil.isNotEmpty(customPermissions)) {
            for (SysPermissionDataRuleModel customPermission : customPermissions) {
                if (ObjectUtil.isNotEmpty(customPermission)) {
                    if (DataPermRuleType.TYPE_MANAGE_DEPT.equals(customPermission.getRuleConditions())) {
                        List<CsUserDepartModel> departByUserId = commonApi.getDepartByUserId(userId);
                        if (CollUtil.isNotEmpty(departByUserId) && !dataPermMap.containsKey(customPermission.getRuleConditions())) {
                            dataPermMap.put(customPermission.getRuleConditions(), departByUserId.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.joining(",")));
                        }
                    } else if (DataPermRuleType.TYPE_DEPT_ONLY.equals(customPermission.getRuleConditions())) {
                        if (!dataPermMap.containsKey(customPermission.getRuleConditions())) {
                            dataPermMap.put(customPermission.getRuleConditions(), "null");
                        }
                    } else if (DataPermRuleType.TYPE_MANAGE_LINE_ONLY.equals(customPermission.getRuleConditions())) {
                        // todo 待处理
                    } else if (DataPermRuleType.TYPE_MANAGE_STATION_ONLY.equals(customPermission.getRuleConditions())) {
                        List<CsUserStationModel> stationByUserId = commonApi.getStationByUserId(userId);
                        if (CollUtil.isNotEmpty(stationByUserId) && !dataPermMap.containsKey(customPermission.getRuleConditions())) {
                            dataPermMap.put(customPermission.getRuleConditions(), stationByUserId.stream().map(CsUserStationModel::getStationCode).collect(Collectors.joining(",")));
                        }
                    } else if (DataPermRuleType.TYPE_MANAGE_MAJOR_ONLY.equals(customPermission.getRuleConditions())) {
                        List<CsUserMajorModel> majorByUserId = commonApi.getMajorByUserId(userId);
                        if (CollUtil.isNotEmpty(majorByUserId) && !dataPermMap.containsKey(customPermission.getRuleConditions())) {
                            dataPermMap.put(customPermission.getRuleConditions(), majorByUserId.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.joining(",")));
                        }
                    } else if (DataPermRuleType.TYPE_MANAGE_SYSTEM_ONLY.equals(customPermission.getRuleConditions())) {
                        List<CsUserSubsystemModel> subsystemByUserId = commonApi.getSubsystemByUserId(userId);
                        if (CollUtil.isNotEmpty(subsystemByUserId) && !dataPermMap.containsKey(customPermission.getRuleConditions())) {
                            dataPermMap.put(customPermission.getRuleConditions(), subsystemByUserId.stream().map(CsUserSubsystemModel::getSystemCode).collect(Collectors.joining(",")));
                        }
                    } else if (DataPermRuleType.TYPE_ALL.equals(customPermission.getRuleConditions())) {
                        dataPermMap.put(customPermission.getRuleConditions(), "null");
                    }
                }
            }
        }
        return dataPermMap;
    }

}
