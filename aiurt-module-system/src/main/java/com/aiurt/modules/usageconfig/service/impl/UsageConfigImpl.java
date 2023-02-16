package com.aiurt.modules.usageconfig.service.impl;

import com.aiurt.modules.usageconfig.entity.UsageConfig;
import com.aiurt.modules.usageconfig.mapper.UsageConfigMapper;
import com.aiurt.modules.usageconfig.service.UsageConfigService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description: 待办池列表
 * @Author: aiurt
 * @Date: 2022-12-21
 * @Version: V1.0
 */
@Service
public class UsageConfigImpl extends ServiceImpl<UsageConfigMapper, UsageConfig> implements UsageConfigService {

    @Autowired
    private UsageConfigMapper usageConfigMapper;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;


}
