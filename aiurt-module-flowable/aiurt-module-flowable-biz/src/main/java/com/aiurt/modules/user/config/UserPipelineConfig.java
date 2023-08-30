package com.aiurt.modules.user.config;

import com.aiurt.modules.user.filters.BaseUserFilter;
import com.aiurt.modules.user.filters.CustomVariableUserFilter;
import com.aiurt.modules.user.filters.SystemVariableUserFilter;
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
    private BaseUserFilter baseUserFilter;

    @Autowired
    private CustomVariableUserFilter customVariableUserFilter;

    @Autowired
    private SystemVariableUserFilter systemVariableUserFilter;

    @Bean
    public FilterChainPipeline chargePipeline(){
        FilterChainPipeline filterChainPipeline = new FilterChainPipeline();
        filterChainPipeline.addFirst("车辆信息判断", customVariableUserFilter);
        filterChainPipeline.addFirst("车辆信息查询", systemVariableUserFilter);
        filterChainPipeline.addFirst("基础选人", baseUserFilter);
        return filterChainPipeline;
    }
}
