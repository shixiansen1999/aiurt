package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.util.PageLimitUtil;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockLevel2Check;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDTO;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckExcel;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.vo.Stock2CheckVO;

import java.util.Date;
import java.util.List;

/**
 * @Description: 二级库盘点列表
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface IStockLevel2CheckService extends IService<StockLevel2Check> {

    void addCheck(StockLevel2Check stockLevel2Check);

    IPage<Stock2CheckVO> queryPageList(IPage<Stock2CheckVO> page, StockLevel2CheckDTO stockLevel2CheckDTO);

    List<StockLevel2CheckExcel> exportXls(List<Integer> ids);
}
