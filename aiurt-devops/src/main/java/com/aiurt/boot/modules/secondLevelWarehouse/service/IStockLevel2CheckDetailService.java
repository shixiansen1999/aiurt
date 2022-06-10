package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockLevel2CheckDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDetailDTO;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDetailEditDTO;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDetailExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.StockLevel2CheckDetailVO;

import java.util.List;

/**
 * @Description: 二级库盘点列表记录
 * @Author: swsc
 * @Date:   2021-09-18
 * @Version: V1.0
 */
public interface IStockLevel2CheckDetailService extends IService<StockLevel2CheckDetail> {

    IPage<StockLevel2CheckDetailVO> queryPageList(Page<StockLevel2CheckDetailVO> page, StockLevel2CheckDetailDTO stockLevel2CheckDetailDTO);

    void updateDetail(List<StockLevel2CheckDetailEditDTO> checkDetailList);

    IPage<StockLevel2CheckDetailVO> queryNewestStockList(Page<StockLevel2CheckDetailVO> page, String warehouseCode);

    List<StockLevel2CheckDetailExcel> exportNewestStockXls(String warehouseCode);
}
