package com.aiurt.modules.remind.config;


import com.aiurt.modules.remind.handlers.BuildContextHandler;
import com.aiurt.modules.remind.pipeline.RemindFilterChainPipeline;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author fgw
 */
@Configuration
public class RemindPipelineConfig {

    @Resource
    private BuildContextHandler buildContextFilter;

    @Bean
    public RemindFilterChainPipeline remindPipeline(){
        RemindFilterChainPipeline filterChainPipeline = new RemindFilterChainPipeline();
        filterChainPipeline.addFirst("构建context", buildContextFilter);
        return filterChainPipeline;
    }
}
