package com.aiurt.modules.stock.service;

import com.aiurt.modules.stock.dto.req.StockLevel2RequisitionAddReqDTO;
import com.aiurt.modules.stock.dto.req.StockLevel2RequisitionListReqDTO;
import com.aiurt.modules.stock.dto.resp.StockLevel2RequisitionListRespDTO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 二级库申领的service，因为用到的实体类是领料单，因此不继承IService
 * 因为二级库申领走流程，所以和其他的申领分开
 *
 * @author 华宜威
 * @date 2023-09-21 09:48:07
 */
public interface StockLevel2RequisitionService {


    /**
     * 二级库申领-添加一条申领数据
     * @param stockLevel2RequisitionAddReqDTO 二级库申领的添加、编辑等请求DTO
     */
    void add(StockLevel2RequisitionAddReqDTO stockLevel2RequisitionAddReqDTO);

    /**
     * 二级库申领-编辑
     * @param stockLevel2RequisitionAddReqDTO 二级库申领的添加、编辑等请求DTO
     */
    void edit(StockLevel2RequisitionAddReqDTO stockLevel2RequisitionAddReqDTO);

    /**
     * 二级库管理-分页列表查询
     * @param stockLevel2RequisitionListReqDTO 二级库申领分页列表查询的请求DTO
     * @return Page<StockLevel2RequisitionListRespDTO> 返回分页列表查询结果
     */
    Page<StockLevel2RequisitionListRespDTO> pageList(StockLevel2RequisitionListReqDTO stockLevel2RequisitionListReqDTO);
}
