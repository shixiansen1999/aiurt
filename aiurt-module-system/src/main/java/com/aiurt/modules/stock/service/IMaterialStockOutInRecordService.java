package com.aiurt.modules.stock.service;

import com.aiurt.modules.stock.dto.req.MaterialStockOutInRecordReqDTO;
import com.aiurt.modules.stock.dto.resp.MaterialStockOutInRecordRespDTO;
import com.aiurt.modules.stock.entity.MaterialStockOutInRecord;
import com.aiurt.modules.stock.entity.StockInOrderLevel2;
import com.aiurt.modules.stock.entity.StockIncomingMaterials;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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

    /**
     * 根据二级库入库单id，生成对应的入库记录到出入库记录表
     * @param id 二级库入库单id
     */
    void addInRecordFormLevel2(String id);

    /**
     * 根据二级库入库单信息以及入库物资清单，生成对应的入库记录到出入库记录表
     * @param stockInOrderLevel2 二级库入库单信息
     * @param stockIncomingMaterialsList 二级库入库单物资清单信息，这个清单信息要是上面入库单的物资清单
     */
    void addInRecordFormLevel2(StockInOrderLevel2 stockInOrderLevel2, List<StockIncomingMaterials> stockIncomingMaterialsList);
}
