package com.aiurt.boot.modules.fault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.modules.fault.entity.DeviceChangeSparePart;
import com.aiurt.boot.modules.fault.mapper.DeviceChangeSparePartMapper;
import com.aiurt.boot.modules.fault.service.IDeviceChangeSparePartService;
import org.springframework.stereotype.Service;

/**
 * @author Mr.zhao
 * @date 2022/1/15 16:05
 */
@Service
public class DeviceChangeSparePartServiceImpl extends ServiceImpl<DeviceChangeSparePartMapper, DeviceChangeSparePart> implements IDeviceChangeSparePartService {
}
