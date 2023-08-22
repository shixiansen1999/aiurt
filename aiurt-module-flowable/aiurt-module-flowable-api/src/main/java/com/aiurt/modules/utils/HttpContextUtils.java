package com.aiurt.modules.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * HttpServletRequest转换工具类
 *
 * <p>detailed comment
 * @author leaf 2018年6月5日  https://www.leaf17.com
 * @email 421434393@qq.com
 * @see
 * @since 1.0
 */
public class HttpContextUtils {
    private static Logger logger = LoggerFactory.getLogger(HttpContextUtils.class);

    private HttpContextUtils() {

    }

    public static HttpServletRequest getHttpServletRequest() {
        try {
        	ServletRequestAttributes servletRequestAttributes=(ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        	if(null!=servletRequestAttributes) {
        		return servletRequestAttributes.getRequest();
        	}
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

}
