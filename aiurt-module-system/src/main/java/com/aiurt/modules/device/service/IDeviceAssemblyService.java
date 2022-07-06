package com.aiurt.modules.device.service;

import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.material.entity.MaterialBase;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface IDeviceAssemblyService extends IService<DeviceAssembly> {
    /**
     *  格式化（物资-设备组件）
     * @param materialBaseList
     * @return 设备组件
     */
    List<DeviceAssembly> fromMaterialToAssembly(List<MaterialBase> materialBaseList);
}
