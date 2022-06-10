package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockInOrderLevel2Detail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.StockInDetailVO;

import java.util.List;

/**
 * @Description: 二级入库单详细信息
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface IStockInOrderLevel2DetailService extends IService<StockInOrderLevel2Detail> {

    /**
     * 根据入库单号查询入库备件列表
     * @param page
     * @param applyCode
     * @return
     */
    IPage<StockInDetailVO> queryPageList(IPage<StockInDetailVO> page, String applyCode);

    /**
     * 根据id添加数量
     * @param dto
     * @return
     */
    Result addNumById(List<StockDTO> dto);

}
