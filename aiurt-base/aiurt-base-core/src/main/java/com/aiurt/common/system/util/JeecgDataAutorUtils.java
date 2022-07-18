package com.aiurt.common.system.util;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.config.mybatis.constant.DataPermRuleType;
import org.jeecg.common.system.vo.SysPermissionDataRuleModel;
import org.jeecg.common.system.vo.SysUserCacheInfo;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: JeecgDataAutorUtils
 * @Description: 数据权限查询规则容器工具类
 * @Author: 张代浩
 * @Date: 2012-12-15 下午11:27:39
 */
public class JeecgDataAutorUtils {

    public static final String MENU_DATA_AUTHOR_RULES = "MENU_DATA_AUTHOR_RULES";

    public static final String FILTER_DATA_AUTHOR_RULES = "FILTER_DATA_AUTHOR_RULES";

    public static final String DATA_AUTHOR_RULES = "DATA_AUTHOR_RULES";

    public static final String MENU_DATA_AUTHOR_RULE_SQL = "MENU_DATA_AUTHOR_RULE_SQL";

    public static final String SYS_USER_INFO = "SYS_USER_INFO";

    /**
     * 往链接请求里面，传入数据查询条件
     *
     * @param request
     * @param dataRules
     */
    public static synchronized void installDataSearchConditon(HttpServletRequest request, List<SysPermissionDataRuleModel> dataRules) {
        @SuppressWarnings("unchecked")
        // 1.先从request获取MENU_DATA_AUTHOR_RULES，如果存则获取到LIST
                List<SysPermissionDataRuleModel> list = (List<SysPermissionDataRuleModel>) loadDataSearchConditon();

        List<SysPermissionDataRuleModel> filterRules = (List<SysPermissionDataRuleModel>) loadDataSearchConditon(FILTER_DATA_AUTHOR_RULES);
        if (list == null) {
            // 2.如果不存在，则new一个list
            list = new ArrayList<SysPermissionDataRuleModel>();
        }
        if (filterRules == null) {
            // 3.如果不存在，则一样new一个list
            filterRules = new ArrayList<SysPermissionDataRuleModel>();
        }

        // 4. 添加过滤器
        for (SysPermissionDataRuleModel tsDataRule : dataRules) {
            if (ObjectUtil.isNotEmpty(tsDataRule)) {
                if (DataPermRuleType.isValid(tsDataRule.getRuleConditions())) {
                    filterRules.add(tsDataRule);
                } else {
                    list.add(tsDataRule);
                }
            }
        }

        // 5.往list里面增量存指
        request.setAttribute(MENU_DATA_AUTHOR_RULES, list);
        // 6.拦截器过滤的数据权限
        request.setAttribute(FILTER_DATA_AUTHOR_RULES, filterRules);
    }

    /**
     * 获取请求对应的数据权限规则
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static synchronized List<SysPermissionDataRuleModel> loadDataSearchConditon() {
        return (List<SysPermissionDataRuleModel>) SpringContextUtils.getHttpServletRequest().getAttribute(MENU_DATA_AUTHOR_RULES);

    }

    /**
     * 获取请求对应的数据权限规则
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static synchronized List<SysPermissionDataRuleModel> loadDataSearchConditon(String requestName) {
        return (List<SysPermissionDataRuleModel>) SpringContextUtils.getHttpServletRequest().getAttribute(requestName);
    }

    /**
     * 获取请求对应的数据权限SQL
     *
     * @return
     */
    public static synchronized String loadDataSearchConditonSqlString() {
        return (String) SpringContextUtils.getHttpServletRequest().getAttribute(MENU_DATA_AUTHOR_RULE_SQL);
    }

    /**
     * 往链接请求里面，传入数据查询条件
     *
     * @param request
     * @param sql
     */
    public static synchronized void installDataSearchConditon(HttpServletRequest request, String sql) {
        String ruleSql = (String) loadDataSearchConditonSqlString();
        if (!StringUtils.hasText(ruleSql)) {
            request.setAttribute(MENU_DATA_AUTHOR_RULE_SQL, sql);
        }
    }

    /**
     * 将用户信息存到request
     *
     * @param request
     * @param userinfo
     */
    public static synchronized void installUserInfo(HttpServletRequest request, SysUserCacheInfo userinfo) {
        request.setAttribute(SYS_USER_INFO, userinfo);
    }

    /**
     * 将用户信息存到request
     *
     * @param userinfo
     */
    public static synchronized void installUserInfo(SysUserCacheInfo userinfo) {
        SpringContextUtils.getHttpServletRequest().setAttribute(SYS_USER_INFO, userinfo);
    }

    /**
     * 从request获取用户信息
     *
     * @return
     */
    public static synchronized SysUserCacheInfo loadUserInfo() {
        return (SysUserCacheInfo) SpringContextUtils.getHttpServletRequest().getAttribute(SYS_USER_INFO);

    }
}
