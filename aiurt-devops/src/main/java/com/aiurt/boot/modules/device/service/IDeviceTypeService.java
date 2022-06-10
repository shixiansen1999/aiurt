package com.aiurt.boot.modules.device.service;

import com.aiurt.boot.modules.device.entity.DeviceType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 设备分类
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface IDeviceTypeService extends IService<DeviceType> {

    Integer existCode(String code);

    List<DeviceType> getDeviceTypeBySystemCode(String systemCode);
}
