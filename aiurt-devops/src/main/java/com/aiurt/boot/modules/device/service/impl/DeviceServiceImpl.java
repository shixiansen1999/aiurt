package com.aiurt.boot.modules.device.service.impl;

import com.swsc.copsms.modules.device.entity.Device;
import com.swsc.copsms.modules.device.mapper.DeviceMapper;
import com.swsc.copsms.modules.device.service.IDeviceService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 设备
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements IDeviceService {

}
