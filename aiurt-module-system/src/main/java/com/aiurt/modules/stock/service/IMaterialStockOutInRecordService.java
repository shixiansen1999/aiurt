package com.aiurt.modules.stock.service;

import com.aiurt.modules.stock.dto.req.MaterialStockOutInRecordReqDTO;
import com.aiurt.modules.stock.dto.resp.MaterialStockOutInRecordRespDTO;
import com.aiurt.modules.stock.entity.MaterialStockOutInRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 出入库记录表的service
 *
 * @author 华宜威
 * @date 2023-09-18 16:53:57
 */
public interface IMaterialStockOutInRecordService extends IService<MaterialStockOutInRecord> {


    /**
     * 出入库记录分页列表查询
     *
     * @param materialStockOutInRecordReqDTO 出入库记录查询的请求DTO
     * @return IPage<MaterialStockOutInRecordRespDTO> 返回出入库记录查询的响应DTO的Page对象
     */
    IPage<MaterialStockOutInRecordRespDTO> pageList(MaterialStockOutInRecordReqDTO materialStockOutInRecordReqDTO);
}
