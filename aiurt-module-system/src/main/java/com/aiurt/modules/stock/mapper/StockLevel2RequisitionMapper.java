package com.aiurt.modules.stock.mapper;

import com.aiurt.modules.flow.dto.TaskCompleteDTO;

/**
 * 二级库申领的mapper，因为实体类使用的是领料单，就不继承BaseMapper
 *
 * @author 华宜威
 * @date 2023-09-21 09:56:40
 */
public interface StockLevel2RequisitionMapper {

    /**
     * 根据申领单id查询对应的流程信息。其中申领单id就是act_hi_procinst表的BUSINESS_KEY_
     * @param id 申领单id
     * @return TaskCompleteDTO 流程信息对象
     */
    TaskCompleteDTO getFlowDataById(String id);
}
