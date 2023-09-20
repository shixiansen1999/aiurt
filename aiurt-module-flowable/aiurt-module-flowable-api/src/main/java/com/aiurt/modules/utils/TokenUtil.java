package com.aiurt.modules.utils;

import com.aiurt.common.constant.CommonConstant;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author fgw
 */
public class TokenUtil {

    public static String getToken() {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();
        if (Objects.isNull(request)) {
            return "";
        }
        //获取token
        String token = request.getHeader(CommonConstant.X_ACCESS_TOKEN);
        if (token == null) {
            token = request.getParameter("token");
        }
        return token;
    }
}
