package com.aiurt.modules.deduplicate.config;

import com.aiurt.modules.deduplicate.pipeline.DeduplicateHandlerChainPipeline;
import com.aiurt.modules.remind.pipeline.RemindHandlerChainPipeline;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fgw
 */
@Configuration
public class DeduplicatePipelineConfig {


    @Bean
    public DeduplicateHandlerChainPipeline deduplicatePipeline(){
        DeduplicateHandlerChainPipeline filterChainPipeline = new DeduplicateHandlerChainPipeline();
        //filterChainPipeline.addFirst("构建context", buildContextFilter);
        return filterChainPipeline;
    }
}
