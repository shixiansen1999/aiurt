package com.aiurt.boot.materials.mapper;

import com.aiurt.boot.materials.entity.EmergencyMaterialsCategory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: emergency_materials_category
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface EmergencyMaterialsCategoryMapper extends BaseMapper<EmergencyMaterialsCategory> {
    /**
     * 根据编码查询物资分类信息
     * @param code
     * @return
     */
    EmergencyMaterialsCategory getOne(@Param("code") String code);
}
