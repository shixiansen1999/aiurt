package com.aiurt.boot.modules.device.service.impl;

import com.swsc.copsms.modules.device.entity.DeviceType;
import com.swsc.copsms.modules.device.mapper.DeviceMapper;
import com.swsc.copsms.modules.device.mapper.DeviceTypeMapper;
import com.swsc.copsms.modules.device.service.IDeviceTypeService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 设备分类
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class DeviceTypeServiceImpl extends ServiceImpl<DeviceTypeMapper, DeviceType> implements IDeviceTypeService {

}
