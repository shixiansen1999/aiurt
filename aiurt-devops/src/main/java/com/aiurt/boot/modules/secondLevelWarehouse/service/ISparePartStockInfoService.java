package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.SparePartStockVO;

import java.util.List;

/**
 * @Description: 备件仓库信息
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface ISparePartStockInfoService extends IService<SparePartStockVO> {

    /**
     * 去cs_stock_spare_part查基础数据
     * @return
     */
    List<SparePartStockVO> queryList();
}
