package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2CheckDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDetailDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDetailEditDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDetailExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.StockLevel2CheckDetailVO;

import java.util.List;

/**
 * @Description: 二级库盘点列表记录
 * @Author: swsc
 * @Date:   2021-09-18
 * @Version: V1.0
 */
public interface IStockLevel2CheckDetailService extends IService<StockLevel2CheckDetail> {

    /**
     * 填写盘点结果以及查看盘点结果的列表-分页列表查询
     * @param page
     * @param stockLevel2CheckDetailDTO
     * @return
     */
    IPage<StockLevel2CheckDetailVO> queryPageList(Page<StockLevel2CheckDetailVO> page, StockLevel2CheckDetailDTO stockLevel2CheckDetailDTO);

    /**
     * 填写盘点结果-批量编辑
     * @param checkDetailList
     */
    void updateDetail(List<StockLevel2CheckDetailEditDTO> checkDetailList);

    /**
     * 最新库存数据-分页列表查询
     * @param page
     * @param warehouseCode 仓库编号
     * @return
     */
    IPage<StockLevel2CheckDetailVO> queryNewestStockList(Page<StockLevel2CheckDetailVO> page,String warehouseCode);

    /**
     * 最新库存数据导出
     * @param warehouseCode 仓库编号
     * @return
     */
    List<StockLevel2CheckDetailExcel> exportNewestStockXls(String warehouseCode);
}
