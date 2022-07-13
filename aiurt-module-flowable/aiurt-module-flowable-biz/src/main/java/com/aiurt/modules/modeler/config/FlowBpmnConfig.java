package com.aiurt.modules.modeler.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: flowable配置
 * @Author: fgw
 * @Since:18:44 2022/07/13
 */
@Configuration
public class FlowBpmnConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public UuidGenerator uuidGenerator() {
        return new UuidGenerator();
    }

    @Override
    public void configure(SpringProcessEngineConfiguration configuration) {
        //设置自定义的uuid生成策略
        configuration.setIdGenerator(uuidGenerator());
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
