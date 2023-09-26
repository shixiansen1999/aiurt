package com.aiurt.modules.material.mapper;

import com.aiurt.modules.material.entity.MaterialRequisitionDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * 领用物资明细的mapper
 *
 * @author 华宜威
 * @date 2023-09-18 16:33:29
 */
public interface MaterialRequisitionDetailMapper extends BaseMapper<MaterialRequisitionDetail> {

    /**
     * 根据申领单id，将申领单的物资清单的入库数量更新为申请数量
     * @param materialRequisitionId 入库单id
     */
    void updateActualNumByMaterialRequisitionId(@Param("materialRequisitionId") String materialRequisitionId);

}
