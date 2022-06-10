package com.aiurt.boot.modules.shiro.authc.aop;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.modules.shiro.authc.JwtToken;
import com.aiurt.boot.modules.shiro.vo.DefContants;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * @Description: 鉴权登录拦截器
 * @Author: swsc
 * @Date: 2018/10/7
 **/
@Slf4j
public class JwtFilter extends BasicHttpAuthenticationFilter {

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
			return executeLogin(request, response);
		} catch (Exception e) {
			throw new AuthenticationException("Token失效，请重新登录", e);
		}
	}

	/**
	 *
	 */
	@Override
	protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		String token = httpServletRequest.getHeader(DefContants.X_ACCESS_TOKEN);

		JwtToken jwtToken = new JwtToken(token);
		// 提交给realm进行登入，如果错误他会抛出异常并被捕获
		//9.27 qian修改添加鉴权
		try {
			getSubject(request, response).login(jwtToken);
		} catch (AuthenticationException e) {
            responseError(response, CommonConstant.SC_JEECG_NO_AUTHZ, e.getMessage());
			return false;
		}
		// 如果没有抛出异常则代表登入成功，返回true
		return true;
	}

	/**
	 * 将非法请求跳转到 /filterError/**中
	 */
	private void responseError(ServletResponse response, int code,String message) {
		try {
			HttpServletResponse httpServletResponse = (HttpServletResponse) response;
			//设置编码，否则中文字符在重定向时会变为空字符串
			message = URLEncoder.encode(message, "UTF-8");
			//如果有项目名称路径记得加上
			httpServletResponse.sendRedirect("/filterError/" + code + "/" + message);
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	/**
	 * 对跨域提供支持
	 */
	@Override
	protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;
		HttpServletResponse httpServletResponse = (HttpServletResponse) response;
		httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE");
		httpServletResponse.setHeader("Access-Control-Allow-Headers", httpServletRequest.getHeader("Access-Control-Request-Headers"));
		// 跨域时会首先发送一个option请求，这里我们给option请求直接返回正常状态
		if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
			httpServletResponse.setStatus(HttpStatus.OK.value());
			return false;
		}
		return super.preHandle(request, response);
	}
}
