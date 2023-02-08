package com.aiurt.boot.materials.service;

import com.aiurt.boot.materials.dto.PatrolRecordReqDTO;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoices;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import java.util.List;

/**
 * @Description: emergency_materials_invoices
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyMaterialsInvoicesService extends IService<EmergencyMaterialsInvoices> {

    /**
     * 查询
     * @param pageList
     * @param reqDTO
     * @return
     */
    List<EmergencyMaterialsInvoices> queryList(Page<EmergencyMaterialsInvoices> pageList, PatrolRecordReqDTO reqDTO);

}
