package com.aiurt.modules.config;

import com.aiurt.modules.editor.language.json.converter.CustomBpmnJsonConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author fgw
 */
@Configuration
public class FlowExtensionConfig {

    @Bean
    public CustomBpmnJsonConverter createCustomBpmnJsonConverter() {
        return new CustomBpmnJsonConverter();
    }
}
