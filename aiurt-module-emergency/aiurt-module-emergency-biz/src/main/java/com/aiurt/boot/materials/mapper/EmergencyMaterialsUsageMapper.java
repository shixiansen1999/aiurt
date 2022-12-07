package com.aiurt.boot.materials.mapper;


import com.aiurt.boot.materials.dto.MaterialAccountDTO;
import com.aiurt.boot.materials.entity.EmergencyMaterialsUsage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: emergency_materials_usage
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface EmergencyMaterialsUsageMapper extends BaseMapper<EmergencyMaterialsUsage> {

    /**
     * 应急物资使用记录查询
     * @param pageList
     * @param condition
     * @return
     */
    List<EmergencyMaterialsUsage> getUsageRecordList (@Param("pageList") Page<EmergencyMaterialsUsage> pageList, @Param("condition") EmergencyMaterialsUsage condition);

}
