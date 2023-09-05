package com.aiurt.modules.user.config;

import com.aiurt.modules.user.filters.BaseUserHandler;
import com.aiurt.modules.user.filters.CustomVariableUserHandler;
import com.aiurt.modules.user.filters.SystemVariableUserHandler;
import com.aiurt.modules.user.pipeline.FilterChainPipeline;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fgw
 */
@Configuration
public class UserPipelineConfig {

    @Autowired
    private BaseUserHandler baseUserFilter;

    @Autowired
    private CustomVariableUserHandler customVariableUserFilter;

    @Autowired
    private SystemVariableUserHandler systemVariableUserFilter;

    @Bean
    public FilterChainPipeline chargePipeline(){
        FilterChainPipeline filterChainPipeline = new FilterChainPipeline();
        filterChainPipeline.addFirst("自定义变量", customVariableUserFilter);
        filterChainPipeline.addFirst("系统变量", systemVariableUserFilter);
        filterChainPipeline.addFirst("基础选人", baseUserFilter);
        return filterChainPipeline;
    }
}
