package com.aiurt.config.datafilter.listener;

import com.aiurt.config.datafilter.interceptor.PlusLoadDataPerm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 应用服务启动监听器。
 * 目前主要功能是调用MybatisDataFilterInterceptor中的loadInfoWithDataFilter方法，
 * 将标记有过滤注解的数据加载到缓存，以提升系统运行时效率。
 *
 * @author aiurt
 * @date 2022-07-18
 */
@Slf4j
@Component
public class LoadDataFilterInfoListener implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        PlusLoadDataPerm interceptor =
                applicationReadyEvent.getApplicationContext().getBean(PlusLoadDataPerm.class);
        interceptor.loadInfoWithDataFilter();
    }
}
