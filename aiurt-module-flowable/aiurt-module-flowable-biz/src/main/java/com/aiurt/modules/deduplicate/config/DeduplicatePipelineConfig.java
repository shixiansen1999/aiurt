package com.aiurt.modules.deduplicate.config;

import com.aiurt.modules.deduplicate.handler.*;
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

    @Resource
    private BackNodeRuleVerifyHandler backNodeRuleVerifyHandler;

    @Resource
    private ApprovalRequirementRuleVerifyHandler approvalRequirementRuleVerifyHandler;

    @Resource
    private DuplicateRuleVerifyHandler duplicateRuleVerifyHandler;

    @Resource
    private AutoCompleteHandler autoCompleteHandler;

    @Resource
    private DuplicateBeforeVerifyHandler duplicateBeforeVerifyHandler;


    @Bean
    public DeduplicateHandlerChainPipeline deduplicatePipeline(){
        DeduplicateHandlerChainPipeline filterChainPipeline = new DeduplicateHandlerChainPipeline();
        filterChainPipeline.addFirst("提交", autoCompleteHandler);
        filterChainPipeline.addFirst("规则", duplicateRuleVerifyHandler);
        filterChainPipeline.addFirst("审批意见必填", backNodeRuleVerifyHandler);
        filterChainPipeline.addFirst("加签规则", multiInstanceRuleVerifyHandler);
        filterChainPipeline.addFirst("审批意见必填", approvalRequirementRuleVerifyHandler);
        filterChainPipeline.addFirst("去重前规则校验", duplicateBeforeVerifyHandler);
        filterChainPipeline.addFirst("构建context", deduplicateContextHandler);
        return filterChainPipeline;
    }
}
