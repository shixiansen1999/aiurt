package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockInOrderLevel2;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockInOrderLevel2DTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockInOrderLevel2Excel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.StockInOrderLevel2VO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 二级入库单信息
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
public interface IStockInOrderLevel2Service extends IService<StockInOrderLevel2> {

    /**
     * 添加入库单-添加
     * @param stockInOrderLevel2DTO
     * @param req
     * @return
     */
    String addWarehouseIn(StockInOrderLevel2DTO stockInOrderLevel2DTO, HttpServletRequest req);

    /**
     * 二级入库单信息-分页列表查询
     * @param page
     * @param stockInOrderLevel2
     * @param startTime 入库时间范围开始时间
     * @param endTime 入库时间范围结束时间时间
     * @return
     */
    IPage<StockInOrderLevel2VO> queryPageList(Page<StockInOrderLevel2VO> page, StockInOrderLevel2 stockInOrderLevel2, String startTime, String endTime);

    /**
     * 入库列表导出
     * @param selections 选择行的ids
     * @return
     */
    List<StockInOrderLevel2Excel> selectExcelData(List<Integer> selections);
}
