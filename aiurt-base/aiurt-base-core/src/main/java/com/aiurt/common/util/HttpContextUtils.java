package com.aiurt.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author:wgp
 * @create: 2023-08-15 16:15
 * @Description:
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
