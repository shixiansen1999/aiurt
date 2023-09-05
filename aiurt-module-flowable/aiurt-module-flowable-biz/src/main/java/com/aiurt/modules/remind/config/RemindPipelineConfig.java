package com.aiurt.modules.remind.config;


import com.aiurt.modules.remind.handlers.BuildContextHandler;
import com.aiurt.modules.remind.pipeline.RemindHandlerChainPipeline;
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
    public RemindHandlerChainPipeline remindPipeline(){
        RemindHandlerChainPipeline filterChainPipeline = new RemindHandlerChainPipeline();
        filterChainPipeline.addFirst("构建context", buildContextFilter);
        return filterChainPipeline;
    }
}
