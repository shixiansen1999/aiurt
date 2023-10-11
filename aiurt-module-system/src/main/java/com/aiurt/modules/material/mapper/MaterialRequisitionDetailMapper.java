package com.aiurt.modules.material.mapper;

import com.aiurt.modules.material.entity.MaterialRequisitionDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 领用物资明细的mapper
 *
 * @author 华宜威
 * @date 2023-09-18 16:33:29
 */
public interface MaterialRequisitionDetailMapper extends BaseMapper<MaterialRequisitionDetail> {

    /**
     * 根据申领单id，将申领单的物资清单的入库数量更新为申请数量
     * @param materialRequisitionId 申领单id
     */
    void updateActualNumByMaterialRequisitionId(@Param("materialRequisitionId") String materialRequisitionId);

    /**
     * @param faultRepairRecordId
     * @return
     */
    List<MaterialRequisitionDetail> getList(String faultRepairRecordId);

    /**
     * @param faultRepairRecordId
     * @param isUsed
     */
    void updateIsUsed(@Param("faultRepairRecordId")String faultRepairRecordId, @Param("isUsed")Integer isUsed);

}
