package com.aiurt.modules.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: flowable配置,
 * @Author: fgw
 * @Since:18:44 2022/07/13
 */
@Configuration
public class FlowBpmnConfig implements EngineConfigurationConfigurer<SpringProcessEngineConfiguration> {

    // 删除， 全局控制，导致其他序列化
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
        //设置自定义的uuid生成策略
        configuration.setIdGenerator(uuidGenerator());
        // 字体
        configuration.setActivityFontName("宋体");
        configuration.setLabelFontName("宋体");
        configuration.setAnnotationFontName("宋体");
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
