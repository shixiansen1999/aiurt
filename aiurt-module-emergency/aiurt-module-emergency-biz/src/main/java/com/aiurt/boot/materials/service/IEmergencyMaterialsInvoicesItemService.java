package com.aiurt.boot.materials.service;

import com.aiurt.boot.materials.dto.PatrolRecordReqDTO;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoicesItem;
import com.aiurt.common.system.base.entity.DynamicTableEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @Description: emergency_materials_invoices_item
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyMaterialsInvoicesItemService extends IService<EmergencyMaterialsInvoicesItem> {

    Page<EmergencyMaterialsInvoicesItem> getPatrolRecord(Page<EmergencyMaterialsInvoicesItem> pageList,
                                                         String materialsCode,
                                                         String startTime,
                                                         String endTime,
                                                         String standardCode,
                                                         String lineCode,
                                                         String stationCode,
                                                         String positionCode);

    /**
     * 查询物资的巡检记录
     * @param recordReqDTO
     * @return
     */
    DynamicTableEntity getPatrolRecord(PatrolRecordReqDTO recordReqDTO);
}
