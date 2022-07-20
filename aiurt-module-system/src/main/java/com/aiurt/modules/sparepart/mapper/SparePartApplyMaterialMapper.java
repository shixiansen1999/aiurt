package com.aiurt.modules.sparepart.mapper;

import com.aiurt.modules.sparepart.entity.SparePartApplyMaterial;
import com.aiurt.modules.sparepart.entity.dto.SpareApplyMaterialDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 备件申领物资
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface SparePartApplyMaterialMapper extends BaseMapper<SparePartApplyMaterial> {

    IPage<SpareApplyMaterialDTO> queryPageList(IPage<SpareApplyMaterialDTO> page, @Param("applyCode") String applyCode);
}
