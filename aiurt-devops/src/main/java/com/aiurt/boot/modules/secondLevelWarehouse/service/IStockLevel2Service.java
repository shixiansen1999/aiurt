package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.dto.StockLevel2Query;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.vo.StockLevel2VO;

import java.util.List;

/**
 * @Description: 二级库库存信息
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
public interface IStockLevel2Service extends IService<StockLevel2> {

    /**
     * 二级库库存信息
     * @param page
     * @param stockLevel2Query
     * @return
     */
    IPage<StockLevel2VO> queryPageList(Page<StockLevel2VO> page, StockLevel2Query stockLevel2Query);

    /**
     * 导出excel
     * @param stockLevel2Query
     * @return
     */
    List<StockLevel2VO> exportXls(StockLevel2Query stockLevel2Query);

    /**
     * 填写备注
     * @param id
     * @param remark
     * @return
     */
    Result addRemark (Integer id,String remark);
}
