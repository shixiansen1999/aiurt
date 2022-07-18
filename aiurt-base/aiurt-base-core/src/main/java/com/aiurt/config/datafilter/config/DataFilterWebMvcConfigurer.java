package com.aiurt.config.datafilter.config;


import com.aiurt.config.datafilter.interceptor.DataFilterInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 添加数据过滤相关的拦截器。
 *
 * @author aiurt
 * @date 2022-07-18
 */
@Configuration
public class DataFilterWebMvcConfigurer implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DataFilterInterceptor()).addPathPatterns("/**");
    }
}
