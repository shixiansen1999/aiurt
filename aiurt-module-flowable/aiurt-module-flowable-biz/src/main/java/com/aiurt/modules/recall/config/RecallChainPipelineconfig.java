package com.aiurt.modules.recall.config;

import com.aiurt.modules.recall.handler.BuildRecallContextHandler;
import com.aiurt.modules.recall.handler.ChangeTaskStatusHandler;
import com.aiurt.modules.recall.handler.RecallRuleVerifyHandler;
import com.aiurt.modules.recall.handler.RecallSendMessageHandler;
import com.aiurt.modules.recall.pipeline.RecallHandlerChainPipeline;
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
    @Resource
    RecallRuleVerifyHandler recallRuleVerifyHandler;
    @Resource
    ChangeTaskStatusHandler changeTaskStatusHandler;
    @Resource
    RecallSendMessageHandler recallSendMessageHandler;

    @Bean
    public RecallHandlerChainPipeline recallPipeline(){
        RecallHandlerChainPipeline filterChainPipeline = new RecallHandlerChainPipeline();
        filterChainPipeline.addFirst("撤回任务到发起节点", changeTaskStatusHandler);
        filterChainPipeline.addFirst("撤回通知", recallSendMessageHandler);
        filterChainPipeline.addFirst("撤回规则校验", recallRuleVerifyHandler);
        filterChainPipeline.addFirst("构建context", buildRecallContextHandler);
        return filterChainPipeline;
    }
}
