package com.aiurt.modules.deduplicate.config;

import com.aiurt.modules.deduplicate.handler.BuildDeduplicateContextHandler;
import com.aiurt.modules.deduplicate.handler.MultiInstanceRuleVerifyHandler;
import com.aiurt.modules.deduplicate.pipeline.DeduplicateHandlerChainPipeline;
import com.aiurt.modules.remind.pipeline.RemindHandlerChainPipeline;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author fgw
 */
@Configuration
public class DeduplicatePipelineConfig {

    @Resource
    private BuildDeduplicateContextHandler deduplicateContextHandler;

    @Resource
    private MultiInstanceRuleVerifyHandler multiInstanceRuleVerifyHandler;


    @Bean
    public DeduplicateHandlerChainPipeline deduplicatePipeline(){
        DeduplicateHandlerChainPipeline filterChainPipeline = new DeduplicateHandlerChainPipeline();
        //filterChainPipeline.addFirst("构建context", buildContextFilter);
        filterChainPipeline.addFirst("加签规则", multiInstanceRuleVerifyHandler);
        filterChainPipeline.addFirst("构建context", deduplicateContextHandler);
        return filterChainPipeline;
    }
}
