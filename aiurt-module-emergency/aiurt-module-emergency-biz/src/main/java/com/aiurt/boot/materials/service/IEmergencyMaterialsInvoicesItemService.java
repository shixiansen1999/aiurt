package com.aiurt.boot.materials.service;

import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoicesItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: emergency_materials_invoices_item
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyMaterialsInvoicesItemService extends IService<EmergencyMaterialsInvoicesItem> {

    List<EmergencyMaterialsInvoicesItem> getPatrolRecord(String materialsCode,String startTime, String endTime);
}
