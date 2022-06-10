package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartStock;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.SparePartStockDTO;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.SpareMaterialVO;

import java.util.List;

/**
 * @Description: 备件库存
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface ISparePartStockService extends IService<SparePartStock> {

    IPage<SparePartStockDTO> queryPageList(IPage<SparePartStockDTO> page, SparePartStockDTO sparePartStockDTO);

    List<SpareMaterialVO> queryMaterialByWarehouse(String warehouseCode);
}
