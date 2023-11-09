package com.aiurt.boot.task.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.task.dto.RepairAbnormalDeviceAddDTO;
import com.aiurt.boot.task.entity.RepairAbnormalDevice;
import com.aiurt.boot.task.mapper.RepairAbnormalDeviceMapper;
import com.aiurt.boot.task.service.IRepairAbnormalDeviceService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sbx
 * @since 2023/10/19
 */
@Service
public class RepairAbnormalDeviceServiceImpl extends ServiceImpl<RepairAbnormalDeviceMapper, RepairAbnormalDevice> implements IRepairAbnormalDeviceService {
    @Override
    public void add(RepairAbnormalDeviceAddDTO repairAbnormalDeviceAddDTO) {
        // 删除之前保存的异常设备
        this.remove(new LambdaQueryWrapper<RepairAbnormalDevice>().eq(RepairAbnormalDevice::getResultId, repairAbnormalDeviceAddDTO.getResultId()));
        List<String> abnormalDeviceCodeList = repairAbnormalDeviceAddDTO.getAbnormalDeviceCodeList();
        if (CollUtil.isEmpty(abnormalDeviceCodeList)) {
            return;
        }
        // 保存异常设备
        ArrayList<RepairAbnormalDevice> list = new ArrayList<>();
        abnormalDeviceCodeList.forEach(d -> {
            RepairAbnormalDevice repairAbnormalDevice = new RepairAbnormalDevice();
            repairAbnormalDevice.setResultId(repairAbnormalDeviceAddDTO.getResultId());
            repairAbnormalDevice.setDeviceCode(d);
            list.add(repairAbnormalDevice);
        });
        this.saveBatch(list);
    }
}
