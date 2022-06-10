package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartApplyMaterial;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.SpareApplyMaterialDTO;

/**
 * @Description: 备件申领物资
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface ISparePartApplyMaterialService extends IService<SparePartApplyMaterial> {

    /**
     * 备件申领物资详情/出库详情/出库确认的列表-分页列表查询
     * @param page
     * @param applyCode 申领编号
     * @return
     */
    IPage<SpareApplyMaterialDTO> queryPageList(IPage<SpareApplyMaterialDTO> page, String applyCode);
}
