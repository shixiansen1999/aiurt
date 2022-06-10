package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockInOrderLevel2;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockInOrderLevel2DTO;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockInOrderLevel2Excel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.StockInOrderLevel2VO;

import java.util.List;

/**
 * @Description: 二级入库单信息
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
public interface IStockInOrderLevel2Service extends IService<StockInOrderLevel2> {

    void addWarehouseIn(StockInOrderLevel2DTO stockInOrderLevel2DTO);

    IPage<StockInOrderLevel2VO> queryPageList(Page<StockInOrderLevel2VO> page, StockInOrderLevel2 stockInOrderLevel2, String startTime, String endTime);

    List<StockInOrderLevel2Excel> selectExcelData(List<Integer> ids);
}
