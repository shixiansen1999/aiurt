package com.aiurt.modules.device.service;

import com.aiurt.modules.device.entity.DeviceType;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: device_type
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface IDeviceTypeService extends IService<DeviceType> {
    String getCcStr(DeviceType deviceType);
}
