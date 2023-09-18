package com.aiurt.modules.recall.config;

import com.aiurt.modules.recall.handler.BuildRecallContextHandler;
import com.aiurt.modules.recall.pipeline.RecallHandlerChainPipeline;
import com.aiurt.modules.remind.pipeline.RemindHandlerChainPipeline;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author fgw
 */
@Configuration
public class RecallChainPipelineconfig {

    @Resource
    BuildRecallContextHandler buildRecallContextHandler;

    @Bean
    public RecallHandlerChainPipeline recallPipeline(){
        RecallHandlerChainPipeline filterChainPipeline = new RecallHandlerChainPipeline();
        filterChainPipeline.addFirst("构建context", buildRecallContextHandler);
        return filterChainPipeline;
    }
}
