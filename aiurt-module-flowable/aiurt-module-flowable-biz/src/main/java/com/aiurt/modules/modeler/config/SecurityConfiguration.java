package com.aiurt.modules.modeler.config;

import org.flowable.ui.common.security.SecurityConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * 为了绕过授权，需要添加一个自定义授权配置绕过Flowable的默认idm授权，创建一个授权配置类“SecurityConfiguration”,该类在什么位置都可以，不用覆盖原有授权配置类
 */
@Configuration
public class SecurityConfiguration {
    @Configuration(proxyBeanMethods = false)
    //Order配置说明
    // 这个地方相同会报错
    //这个地方如果大于则该配置在FlowableUiSecurityAutoConfiguratio中对应项后加载，不能起到绕过授权作用
    //所以这个地方-1让该配置项在FlowableUiSecurityAutoConfiguratio中对应配置项前加载，以跳过授权
    @Order(SecurityConstants.FORM_LOGIN_SECURITY_ORDER - 1)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    //必须要将csrf设置为disable，不然后面发送POST请求时会报403错误
                    .csrf().disable()
                    //为了简单起见，简单粗暴方式直接放行modeler下面所有请求
                    .authorizeRequests().antMatchers("/modeler/**").permitAll();
        }
    }
}
