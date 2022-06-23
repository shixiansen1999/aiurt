package com.aiurt.modules.device.service;

import com.aiurt.modules.device.entity.Device;
import com.aiurt.modules.device.entity.DeviceAssembly;
import com.aiurt.modules.material.entity.MaterialBase;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.util.List;
import java.util.Map;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface IDeviceAssemblyService extends IService<DeviceAssembly> {
    List<DeviceAssembly> fromMaterialToAssembly(List<MaterialBase> materialBaseList);
}
