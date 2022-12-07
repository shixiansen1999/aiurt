package com.aiurt.boot.materials.service;


import com.aiurt.boot.materials.entity.EmergencyMaterialsUsage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;


/**
 * @Description: emergency_materials_usage
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyMaterialsUsageService extends IService<EmergencyMaterialsUsage> {

    /**
     * 应急物资使用记录查询
     * @param pageList
     * @param condition
     * @return
     */
    Page<EmergencyMaterialsUsage> getUsageRecordList (Page<EmergencyMaterialsUsage> pageList,EmergencyMaterialsUsage condition);

}
