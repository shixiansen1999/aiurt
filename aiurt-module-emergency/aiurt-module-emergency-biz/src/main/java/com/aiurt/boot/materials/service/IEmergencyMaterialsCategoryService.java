package com.aiurt.boot.materials.service;


import com.aiurt.boot.materials.entity.EmergencyMaterialsCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: emergency_materials_category
 * @Author: aiurt
 * @Date:   2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyMaterialsCategoryService extends IService<EmergencyMaterialsCategory> {

    List<EmergencyMaterialsCategory> selectTreeList();

}
