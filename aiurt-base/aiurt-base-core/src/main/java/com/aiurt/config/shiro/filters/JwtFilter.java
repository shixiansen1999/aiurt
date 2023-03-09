package com.aiurt.config.shiro.filters;

import cn.hutool.core.util.StrUtil;
import com.aiurt.common.api.CommonAPI;
import com.aiurt.common.util.RedisUtil;
import com.aiurt.common.util.oConvertUtils;
import com.aiurt.config.mybatis.TenantContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.util.AntPathMatcher;
import org.apache.shiro.util.PatternMatcher;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.system.util.JwtUtil;
import com.aiurt.config.shiro.JwtToken;
import org.apache.shiro.web.util.WebUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Description: 鉴权登录拦截器
 * @Author: Scott
 * @Date: 2018/10/7
 **/
@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {

    /**
     * 默认开启跨域设置（使用单体）
     * 微服务情况下，此属性设置为false
     */
    private boolean allowOrigin = true;

    private Set<String> bigScreenUrlSet;


    protected PatternMatcher pathMatcher = new AntPathMatcher();

    public JwtFilter(){}
    public JwtFilter(boolean allowOrigin){
        this.allowOrigin = allowOrigin;
    }

    /**
     * 执行登录认证
     *
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        try {
            executeLogin(request, response);
            return true;
        } catch (Exception e) {
            JwtUtil.responseError(response,401,CommonConstant.TOKEN_IS_INVALID_MSG);
            return false;
            //throw new AuthenticationException("Token失效，请重新登录", e);
        }
    }

    /**
     *
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        AtomicReference<String> token = new AtomicReference<>(httpServletRequest.getHeader(CommonConstant.X_ACCESS_TOKEN));
        // update-begin--Author:lvdandan Date:20210105 for：JT-355 OA聊天添加token验证，获取token参数
        if (oConvertUtils.isEmpty(token)) {
            token.set(httpServletRequest.getParameter("token"));
        }
        // update-end--Author:lvdandan Date:20210105 for：JT-355 OA聊天添加token验证，获取token参数
        //
        if (StrUtil.isBlank(token.get())) {
            String requestURI = WebUtils.getPathWithinApplication(WebUtils.toHttp(request));
            RedisUtil redisUtil = SpringContextUtils.getBean(RedisUtil.class);

            CommonAPI commonApi = SpringContextUtils.getBean(CommonAPI.class);
            // 判断是否是大屏的数据, 外部范围给的是
            bigScreenUrlSet.forEach(path->{
                if (pathsMatch(requestURI, path)) {
                    String redisToken = redisUtil.getStr(CommonConstant.PREFIX_USER_TOKEN + "bigScreen");
                    if (StrUtil.isBlank(redisToken)) {
                        // 默认登录
                        try {
                            LoginUser loginUser = commonApi.getUserByName("admin");
                            redisToken  = JwtUtil.sign(loginUser.getUsername(), loginUser.getPassword());

                            // 保存到redis
                            redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + "bigScreen", redisToken);
                            redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + "bigScreen", JwtUtil.EXPIRE_TIME * 2/1000);
                            redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + redisToken, redisToken);
                            redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + redisToken, JwtUtil.EXPIRE_TIME *2 / 1000);
                        } catch (Exception e) {
                          log.error(e.getMessage(), e);
                        }
                    }
                    token.set(redisToken);
                    return;
                }
            });
        }

        JwtToken jwtToken = new JwtToken(token.get());
        // 提交给realm进行登入，如果错误他会抛出异常并被捕获
        getSubject(request, response).login(jwtToken);
        // 如果没有抛出异常则代表登入成功，返回true
        return true;
    }

    /**
     * 对跨域提供支持
     */
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        if(allowOrigin){
            httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, httpServletRequest.getHeader(HttpHeaders.ORIGIN));
            // 允许客户端请求方法
            httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,OPTIONS,PUT,DELETE");
            // 允许客户端提交的Header
            String requestHeaders = httpServletRequest.getHeader(HttpHeaders.ACCESS_CONTROL_REQUEST_HEADERS);
            if (StringUtils.isNotEmpty(requestHeaders)) {
                httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, requestHeaders);
            }
            // 允许客户端携带凭证信息(是否允许发送Cookie)
            httpServletResponse.setHeader(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        }
        // 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
        if (RequestMethod.OPTIONS.name().equalsIgnoreCase(httpServletRequest.getMethod())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        //update-begin-author:taoyan date:20200708 for:多租户用到
        String tenantId = httpServletRequest.getHeader(CommonConstant.TENANT_ID);
        TenantContext.setTenant(tenantId);
        //update-end-author:taoyan date:20200708 for:多租户用到

        return super.preHandle(request, response);
    }

    public void setBigScreenUrlSet(Set<String> bigScreenUrlSet) {
        this.bigScreenUrlSet = bigScreenUrlSet;
    }

}
