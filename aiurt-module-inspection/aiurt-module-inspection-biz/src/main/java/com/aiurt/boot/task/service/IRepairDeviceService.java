package com.aiurt.boot.task.service;

import com.aiurt.boot.task.dto.RepairDeviceDTO;
import com.aiurt.boot.task.entity.RepairDevice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author sbx
 * @since 2023/10/18
 */
public interface IRepairDeviceService extends IService<RepairDevice> {
    /**
     * 根据任务id和任务标准关联表id查询设备
     * @param taskId 任务id
     * @param taskStandardId 任务标准关联表id
     * @return
     */
    List<RepairDeviceDTO> queryDevices(String taskId, String taskStandardId, String deviceCode);
}
