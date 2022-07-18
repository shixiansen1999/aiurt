package com.aiurt.config.mybatis;

import com.aiurt.config.mybatis.filter.PermissionFilter;
import org.jeecg.common.system.vo.SysPermissionDataRuleModel;
import org.jeecg.common.util.SpringContextUtils;

import java.util.ArrayList;
import java.util.List;

import static com.aiurt.common.system.util.JeecgDataAutorUtils.MENU_DATA_AUTHOR_RULES;

/**
 * @author wgp
 * @Title:
 * @Description:  Permission Helper (权限过滤器助手)
 * @date 2022/5/2315:50
 */
public class PermissionHelper {
    protected static final ThreadLocal<List<PermissionFilter>> LOCAL_PERMISSION = new ThreadLocal<List<PermissionFilter>>();

    /**
     * 添加q权限过滤
     */
    public static void addFilter(PermissionFilter filter) {

        List<PermissionFilter> filters = LOCAL_PERMISSION.get();

        if (filters == null) {
            filters = new ArrayList<PermissionFilter>();
            LOCAL_PERMISSION.set(filters);
        }

        filters.add(filter);
    }

    public static void addFilterList(List<PermissionFilter> filter) {

        if (filter != null && filter.size() >= 1) {
            LOCAL_PERMISSION.set(filter);
        }
    }


    public static List<PermissionFilter> getFilters() {
        return LOCAL_PERMISSION.get();
    }

    /**
     * 移除本地变量
     */
    public static void clear() {
        LOCAL_PERMISSION.remove();
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

}
