package com.aiurt.modules.config;

import com.aiurt.modules.listener.ProcessStartListener;
import com.aiurt.modules.listener.SequenceflowTakenListener;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: flowable配置,
 * @Author: fgw
 * @Since:18:44 2022/07/13
 */
@Slf4j
@Configuration
public class FlowBpmnConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    // 删除， 全局控制，导致其他序列化失败问题
    /*@Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(Long.class, ToStringSerializer.instance);
        simpleModule.addSerializer(Long.TYPE, ToStringSerializer.instance);
        objectMapper.registerModule(simpleModule);
        // 既然
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return new ObjectMapper();
    }*/

    @Bean
    public UuidGenerator uuidGenerator() {
        return new UuidGenerator();
    }

    @Override
    public void configure(SpringProcessEngineConfiguration configuration) {
        log.info("flowable 全局配置初始化........");
        //设置自定义的uuid生成策略
        configuration.setIdGenerator(uuidGenerator());

        Map<String, List<FlowableEventListener>> typedEventListeners = new HashMap<>();
        typedEventListeners.put(FlowableEngineEventType.SEQUENCEFLOW_TAKEN.name(), Arrays.asList(new SequenceflowTakenListener()));
        typedEventListeners.put(FlowableEngineEventType.PROCESS_STARTED.name(), Arrays.asList(new ProcessStartListener()));
        configuration.setTypedEventListeners(typedEventListeners);

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
        return new BpmnJsonConverter();
    }
}
