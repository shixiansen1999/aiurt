package com.aiurt.boot.materials.service.impl;


import com.aiurt.boot.materials.dto.PatrolRecordReqDTO;
import com.aiurt.boot.materials.mapper.EmergencyMaterialsInvoicesMapper;
import com.aiurt.boot.materials.service.IEmergencyMaterialsInvoicesService;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoices;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: emergency_materials_invoices
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
@Service
public class EmergencyMaterialsInvoicesServiceImpl extends ServiceImpl<EmergencyMaterialsInvoicesMapper, EmergencyMaterialsInvoices> implements IEmergencyMaterialsInvoicesService {

    @Override
    public List<EmergencyMaterialsInvoices> queryList(Page<EmergencyMaterialsInvoices> pageList, PatrolRecordReqDTO reqDTO) {
        return baseMapper.queryList(pageList, reqDTO);
    }
}
