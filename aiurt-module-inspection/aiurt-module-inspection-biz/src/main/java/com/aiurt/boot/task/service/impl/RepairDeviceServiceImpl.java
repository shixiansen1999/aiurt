package com.aiurt.boot.task.service.impl;

import com.aiurt.boot.task.entity.RepairDevice;
import com.aiurt.boot.task.mapper.RepairDeviceMapper;
import com.aiurt.boot.task.service.IRepairDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author sbx
 * @since 2023/10/18
 */
@Service
public class RepairDeviceServiceImpl extends ServiceImpl<RepairDeviceMapper, RepairDevice> implements IRepairDeviceService {
}
