package com.aiurt.boot.task.service.impl;

import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.task.dto.RepairDeviceDTO;
import com.aiurt.boot.task.entity.RepairDevice;
import com.aiurt.boot.task.mapper.RepairDeviceMapper;
import com.aiurt.boot.task.service.IRepairDeviceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sbx
 * @since 2023/10/18
 */
@Service
public class RepairDeviceServiceImpl extends ServiceImpl<RepairDeviceMapper, RepairDevice> implements IRepairDeviceService {

    @Autowired
    private RepairDeviceMapper repairDeviceMapper;

    @Override
    public List<RepairDeviceDTO> queryDevices(String taskId, String taskStandardId, String deviceCode) {
        List<RepairDeviceDTO> repairDeviceDTOList = repairDeviceMapper.queryDevices(taskId, taskStandardId);
        // 当标准与设备类型相关且不合并工单时，此时是按照设备来生成工单的，只返回该工单的设备
        if (StrUtil.isNotBlank(deviceCode)) {
            repairDeviceDTOList = repairDeviceDTOList.stream().filter(rd -> deviceCode.equals(rd.getDeviceCode())).collect(Collectors.toList());
        }
        return repairDeviceDTOList;
    }
}
