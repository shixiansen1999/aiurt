package com.aiurt.modules.config;

import org.flowable.common.engine.impl.persistence.StrongUuidGenerator;

/**
 * @Description: flowable配置
 * @Author: fgw
 * @Since:18:44 2022/07/13
 */
public class UuidGenerator extends StrongUuidGenerator {


    @Override
    public String getNextId() {
        String uuid = super.getNextId();
        uuid = uuid.replaceAll("-", "");
        return uuid;
    }
}
