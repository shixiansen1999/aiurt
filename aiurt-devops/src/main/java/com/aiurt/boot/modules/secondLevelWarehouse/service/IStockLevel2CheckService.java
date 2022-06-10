package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2Check;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckDTO;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2CheckExcel;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.Stock2CheckVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Description: 二级库盘点列表
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface IStockLevel2CheckService extends IService<StockLevel2Check> {

    /**
     * 二级库盘点列表-添加
     * @param stockLevel2Check
     * @param req
     */
    void addCheck(StockLevel2Check stockLevel2Check, HttpServletRequest req);

    /**
     * 二级库盘点列表-分页列表查询
     * @param page
     * @param stockLevel2CheckDTO
     * @return
     */
    IPage<Stock2CheckVO> queryPageList( IPage<Stock2CheckVO> page, StockLevel2CheckDTO stockLevel2CheckDTO);


    /**
     * 二级库盘点导出
     * @param stockLevel2CheckDTO
     * @return
     */
    List<StockLevel2CheckExcel> exportXls(StockLevel2CheckDTO stockLevel2CheckDTO);
}
