package com.aiurt.modules.usageconfig.mapper;


import com.aiurt.modules.usageconfig.dto.UsageConfigDTO;
import com.aiurt.modules.usageconfig.entity.UsageConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 待办池列表
 * @Author: aiurt
 * @Date: 2022-12-21
 * @Version: V1.0
 */
public interface UsageConfigMapper extends BaseMapper<UsageConfig> {

    Page<UsageConfigDTO> getList(@Param("pageList") Page<UsageConfigDTO> pageList, @Param("usageConfigDTO")UsageConfigDTO usageConfigDTO);

    List<UsageConfigDTO> getAllList();
}
