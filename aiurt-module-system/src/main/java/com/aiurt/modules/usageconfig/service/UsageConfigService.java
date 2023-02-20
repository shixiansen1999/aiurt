package com.aiurt.modules.usageconfig.service;

import com.aiurt.modules.usageconfig.dto.UsageConfigDTO;
import com.aiurt.modules.usageconfig.dto.UsageConfigParamDTO;
import com.aiurt.modules.usageconfig.dto.UsageStatDTO;
import com.aiurt.modules.usageconfig.entity.UsageConfig;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 待办池列表
 * @Author: aiurt
 * @Date:   2022-12-21
 * @Version: V1.0
 */
public interface UsageConfigService extends IService<UsageConfig> {

    Page<UsageConfigDTO> pageList(Page<UsageConfigDTO> pageList, UsageConfigDTO usageConfigDTO);

    List<UsageConfigDTO> tree(String name);
    /**
     * 根据配置的统计项查询统计数量
     * @param usageConfigParamDTO
     * @return
     */
    IPage<UsageStatDTO> getBusinessDataStatistics(UsageConfigParamDTO usageConfigParamDTO);
}
