package com.aiurt.modules.modeler.config;

import org.flowable.ui.modeler.service.FlowableModelQueryService;
import org.flowable.ui.modeler.service.ModelImageService;
import org.flowable.ui.modeler.service.ModelServiceImpl;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @program: flow
 * @description: 流程设计器serviceconfig
 * @author: fgw
 * @create: 2022-07-11 09:01
 **/
@Configuration
public class ModelerBeanConfig {


    @Bean
    public ModelService createModelService() {
        return new ModelServiceImpl();
    }

    @Bean
    public ModelImageService createModelImageService() {
        return new ModelImageService();
    }

    @Bean
    public FlowableModelQueryService createFlowableModelQueryService() {
        return new FlowableModelQueryService();
    }

    /*@Bean
    @ConditionalOnMissingBean
    public RememberMeServices flowableUiRememberMeService(FlowableCommonAppProperties properties, UserDetailsService userDetailsService,
                                                          PersistentTokenService persistentTokenService) {
        CustomPersistentRememberMeServices customPersistentRememberMeServices = new CustomPersistentRememberMeServices(properties, userDetailsService, persistentTokenService);
        customPersistentRememberMeServices.setCookieName("DRAGON_FLOW_REMEMBER_ME");
        return customPersistentRememberMeServices;
    }*/
}
