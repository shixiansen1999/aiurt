package com.aiurt.boot.modules.secondLevelWarehouse.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SpareApplyMaterialDTO;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartStockDTO;
import org.apache.ibatis.annotations.Param;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartApplyMaterial;
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
