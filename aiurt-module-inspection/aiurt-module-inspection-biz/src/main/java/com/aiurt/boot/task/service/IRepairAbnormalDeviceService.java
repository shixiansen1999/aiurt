package com.aiurt.boot.task.service;

import com.aiurt.boot.task.dto.RepairAbnormalDeviceAddDTO;
import com.aiurt.boot.task.entity.RepairAbnormalDevice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author sbx
 * @since 2023/10/19
 */
public interface IRepairAbnormalDeviceService extends IService<RepairAbnormalDevice> {
    /**
     * 保存异常设备
     * @param repairAbnormalDeviceAddDTO 请求保存异常设备实体
     */
    void add(RepairAbnormalDeviceAddDTO repairAbnormalDeviceAddDTO);
}
