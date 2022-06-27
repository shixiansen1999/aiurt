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
    List<DeviceAssembly> fromMaterialToAssembly(List<MaterialBase> materialBaseList);
}
