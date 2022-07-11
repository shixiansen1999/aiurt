package com.aiurt.modules.modeler.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;


@Configuration

//@ComponentScan(value = { "org.flowable.ui.modeler.rest.app"

//        // 不加载 rest，因为 getAccount 接口需要我们自己实现

//        //,"org.flowable.ui.common.rest"

//},excludeFilters = {

//        // 移除 EditorUsersResource 与 EditorGroupsResource，因为不使用 IDM 部分

//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = EditorUsersResource.class),

//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = EditorGroupsResource.class),

//        // 配置文件用自己的

//        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = StencilSetResource.class),

//})

@EnableAsync
public class ApiDispatcherServletConfiguration extends WebMvcConfigurationSupport {

    @Autowired

    protected ObjectMapper objectMapper;



    @Bean

    public SessionLocaleResolver localeResolver() {

        return new SessionLocaleResolver();

    }



    /**

     * RequestMappingHandlerMapping和RequestMappingHandlerAdapter是为Spring MVC分发请求所必须的

     *RequestMappingHandlerMapping：主要是将Contoller的带RequestMapping方法，添加到处理方法映射器和路径方法解析器中

     *RequestMappingHandlerAdapter：主要是解决请求的，会话，请求头部处理，数据的绑定等，然后从容器中，获取handlerMethod，处理业务逻辑，获取数据，并渲染视图，返回。

     *注入RequestMappingHandlerMapping和RequestMappingHandlerAdapter Bean就相当于配置<context:annotation-config/>启用注解

     *

     * @return

     */

    @Override

    protected RequestMappingHandlerMapping createRequestMappingHandlerMapping() {

        RequestMappingHandlerMapping requestMappingHandlerMapping = new RequestMappingHandlerMapping();

        requestMappingHandlerMapping.setUseSuffixPatternMatch(false);

        requestMappingHandlerMapping.setRemoveSemicolonContent(false);

        return requestMappingHandlerMapping;

    }



    @Override

    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {

        addDefaultHttpMessageConverters(converters);

        for (HttpMessageConverter<?> converter : converters) {

            if (converter instanceof MappingJackson2HttpMessageConverter) {

                MappingJackson2HttpMessageConverter jackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter) converter;

                jackson2HttpMessageConverter.setObjectMapper(objectMapper);

                break;

            }

        }

    }
}
