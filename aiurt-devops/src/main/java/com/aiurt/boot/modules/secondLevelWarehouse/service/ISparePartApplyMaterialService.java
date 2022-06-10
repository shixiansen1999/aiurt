package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartApplyMaterial;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SpareApplyMaterialDTO;

/**
 * @Description: 备件申领物资
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface ISparePartApplyMaterialService extends IService<SparePartApplyMaterial> {

    IPage<SpareApplyMaterialDTO> queryPageList(IPage<SpareApplyMaterialDTO> page, String applyCode);
}
