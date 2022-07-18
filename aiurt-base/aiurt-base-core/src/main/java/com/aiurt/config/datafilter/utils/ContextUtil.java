package com.aiurt.config.datafilter.utils;

import org.apache.commons.collections4.MapUtils;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * 获取Servlet HttpRequest和HttpResponse的工具类。
 *
 * @author aiurt
 * @date 2022-07-18
 */
public class ContextUtil {

    public static final String FILTER_DATA_AUTHOR_RULES = "FILTER_DATA_AUTHOR_RULES";

    /**
     * 判断当前是否处于HttpServletRequest上下文环境。
     *
     * @return 是返回true，否则false。
     */
    public static boolean hasRequestContext() {
        return RequestContextHolder.getRequestAttributes() != null;
    }

    /**
     * 获取Servlet请求上下文的HttpRequest对象。
     *
     * @return 请求上下文中的HttpRequest对象。
     */
    public static HttpServletRequest getHttpRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取Servlet请求上下文的HttpResponse对象。
     *
     * @return 请求上下文中的HttpResponse对象。
     */
    public static HttpServletResponse getHttpResponse() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
    }

    /**
     * 往链接请求里面，传入数据查询条件
     *
     * @param request
     * @param customPerMap
     */
    public static synchronized void installDataSearchConditon(HttpServletRequest request, Map<String, String> customPerMap) {

        // 1.先从request获取FILTER_DATA_AUTHOR_RULES，如果存在则获取到Map
        Map<String, String> dataRules = loadDataSearchConditon();

        // 2.如果不存在，则new一个HashMap
        if (MapUtils.isEmpty(dataRules)) {
            dataRules = new HashMap<>(8);
        }

        // 3. 添加过滤器
        if (MapUtils.isNotEmpty(customPerMap)) {
            dataRules.putAll(customPerMap);
        }

        // 4.拦截器过滤的数据权限
        request.setAttribute(FILTER_DATA_AUTHOR_RULES, dataRules);
    }

    /**
     * 获取请求对应的数据权限规则
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public static synchronized Map<String, String> loadDataSearchConditon() {
        return (Map<String, String>) SpringContextUtils.getHttpServletRequest().getAttribute(FILTER_DATA_AUTHOR_RULES);
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private ContextUtil() {
    }
}
