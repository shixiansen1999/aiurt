package com.aiurt.modules.remind.config;


import com.aiurt.modules.remind.handlers.BuildContextHandler;
import com.aiurt.modules.remind.handlers.RemindRecordUpdateHandler;
import com.aiurt.modules.remind.handlers.RemindRuleVerifyHandler;
import com.aiurt.modules.remind.handlers.RemindSendMessageHandler;
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

    @Resource
    private RemindSendMessageHandler remindSendMessageHandler;

    @Resource
    private RemindRuleVerifyHandler remindRuleVerifyHandler;

    @Resource
    private RemindRecordUpdateHandler remindRecordUpdateHandler;

    @Bean
    public RemindHandlerChainPipeline remindPipeline(){
        RemindHandlerChainPipeline filterChainPipeline = new RemindHandlerChainPipeline();
        filterChainPipeline.addFirst("更新催办记录", remindRecordUpdateHandler);
        filterChainPipeline.addFirst("催办通知", remindSendMessageHandler);
        filterChainPipeline.addFirst("催办规则校验", remindRuleVerifyHandler);
        filterChainPipeline.addFirst("构建context", buildContextFilter);
        return filterChainPipeline;
    }
}
