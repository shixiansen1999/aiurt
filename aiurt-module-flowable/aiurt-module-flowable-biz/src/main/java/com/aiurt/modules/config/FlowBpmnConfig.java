package com.aiurt.modules.config;

import com.aiurt.modules.editor.language.json.converter.CustomBpmnJsonConverter;
import com.aiurt.modules.el.funtion.CustomVariableContainsAnyExpressionFunction;
import com.aiurt.modules.listener.*;
import com.aiurt.modules.remind.job.TimeOutRemindJobHandler;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.common.engine.api.delegate.FlowableFunctionDelegate;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: flowable配置, 全局控制，导致其他序列化失败问题不能初始化ObjectMapper
 * @Author: fgw
 * @Since:18:44 2022/07/13
 */
@Slf4j
@Configuration
public class FlowBpmnConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    @Bean
    public UuidGenerator uuidGenerator() {
        return new UuidGenerator();
    }

    @Override
    public void configure(SpringProcessEngineConfiguration configuration) {
        log.info("flowable 全局配置初始化........");
        //设置自定义的uuid生成策略
        configuration.setIdGenerator(uuidGenerator());

        Map<String, List<FlowableEventListener>> typedEventListeners = new HashMap<>(16);
        typedEventListeners.put(FlowableEngineEventType.SEQUENCEFLOW_TAKEN.name(), Arrays.asList(new SequenceFlowTakenListener()));
        typedEventListeners.put(FlowableEngineEventType.TASK_CREATED.name(), Arrays.asList(new TaskCreateListener()));
        typedEventListeners.put(FlowableEngineEventType.TASK_COMPLETED.name(), Arrays.asList(new TaskCompletedListener()));
        typedEventListeners.put(FlowableEngineEventType.PROCESS_STARTED.name(), Arrays.asList(new ProcessStartListener()));
        typedEventListeners.put(FlowableEngineEventType.PROCESS_COMPLETED.name(), Arrays.asList(new ProcessCompletedListener()));
        typedEventListeners.put(FlowableEngineEventType.TASK_ASSIGNED.name(), Arrays.asList(new TaskAssignedListener()));
        typedEventListeners.put(FlowableEngineEventType.ENTITY_DELETED.name(), Arrays.asList(new EntityDeletedListener()));
        configuration.setTypedEventListeners(typedEventListeners);

        // 自定义el表达式
        ProcessEngineConfigurationImpl processEngineConfiguration = configuration;
        processEngineConfiguration.initFunctionDelegates();
        List<FlowableFunctionDelegate> flowableFunctionDelegates = processEngineConfiguration.getFlowableFunctionDelegates();
        flowableFunctionDelegates.add(new CustomVariableContainsAnyExpressionFunction());
        configuration.addCustomJobHandler(new TimeOutRemindJobHandler());
        //设置字体
        configuration.setActivityFontName("宋体");
        configuration.setLabelFontName("宋体");
        configuration.setAnnotationFontName("宋体");
        log.info("flowable 全局配置初始化结束.");
    }

    /**
     * BpmnXMLConverter
     *
     * @return BpmnXMLConverter
     */
    @Bean
    public BpmnXMLConverter createBpmnXMLConverter() {
        return new BpmnXMLConverter();
    }

    @Bean
    public BpmnJsonConverter createBpmnJsonConverter() {
        return new CustomBpmnJsonConverter();
    }
}
