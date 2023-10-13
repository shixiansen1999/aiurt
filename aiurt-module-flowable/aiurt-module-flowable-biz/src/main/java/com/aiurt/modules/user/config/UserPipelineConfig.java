package com.aiurt.modules.user.config;

import com.aiurt.modules.user.handler.BaseUserHandler;
import com.aiurt.modules.user.handler.CustomVariableUserHandler;
import com.aiurt.modules.user.handler.DefaultNullUserHandler;
import com.aiurt.modules.user.handler.SystemVariableUserHandler;
import com.aiurt.modules.user.pipeline.FilterChainPipeline;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author fgw
 */
@Configuration
public class UserPipelineConfig {

    @Resource
    private BaseUserHandler baseUserHandler;

    @Resource
    private CustomVariableUserHandler customVariableUserHandler;

    @Resource
    private SystemVariableUserHandler systemVariableUserHandler;

    @Resource
    private DefaultNullUserHandler defaultNullUserHandler;

    @Bean
    public FilterChainPipeline chargePipeline(){
        FilterChainPipeline filterChainPipeline = new FilterChainPipeline();
        filterChainPipeline.addFirst("审批人为空", defaultNullUserHandler);
        filterChainPipeline.addFirst("自定义变量", customVariableUserHandler);
        filterChainPipeline.addFirst("系统变量", systemVariableUserHandler);
        filterChainPipeline.addFirst("基础选人", baseUserHandler);
        return filterChainPipeline;
    }

    public static void main(String[] args) {
        FilterChainPipeline filterChainPipeline = new FilterChainPipeline();
        filterChainPipeline.addFirst("自定义变量", new CustomVariableUserHandler());
        filterChainPipeline.addFirst("系统变量", new SystemVariableUserHandler());
        filterChainPipeline.addFirst("基础选人", new BaseUserHandler());
        System.out.println(filterChainPipeline);
    }
}
