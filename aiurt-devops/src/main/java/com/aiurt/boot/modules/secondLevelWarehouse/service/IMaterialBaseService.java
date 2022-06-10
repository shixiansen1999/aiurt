package com.aiurt.boot.modules.secondLevelWarehouse.service;

import com.swsc.copsms.modules.secondLevelWarehouse.entity.MaterialBase;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 物资基础信息
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface IMaterialBaseService extends IService<MaterialBase> {

    Integer getTypeByMaterialCode(String code);
}
