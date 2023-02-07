package com.aiurt.boot.materials.mapper;


import com.aiurt.boot.materials.dto.PatrolRecordReqDTO;
import com.aiurt.boot.materials.entity.EmergencyMaterialsInvoices;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: emergency_materials_invoices
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface EmergencyMaterialsInvoicesMapper extends BaseMapper<EmergencyMaterialsInvoices> {

    /**
     * 查询巡检记录
     * @param pageList
     * @param reqDTO
     * @return
     */
    List<EmergencyMaterialsInvoices> queryList(@Param("pageList") Page<EmergencyMaterialsInvoices> pageList, @Param("condition") PatrolRecordReqDTO reqDTO);

}
