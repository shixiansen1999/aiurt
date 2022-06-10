package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.CsStockLevelTwoVO;

import java.util.List;

/**
 * @Description: 二级库仓库信息
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
public interface IStockLevel2InfoService extends IService<CsStockLevelTwoVO> {

    /**
     * cs_stock_level_two查询库存基础信息
     * @return
     */
    List<CsStockLevelTwoVO> selectStockList();
}
