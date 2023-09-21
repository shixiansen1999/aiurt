package com.aiurt.modules.stock.service;

import com.aiurt.modules.stock.dto.req.StockLevel2RequisitionAddReqDTO;

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
}
