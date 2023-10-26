package com.aiurt.boot.task.service;

import com.aiurt.boot.task.dto.DeviceDTO;
import com.aiurt.boot.task.dto.PatrolDeviceDTO;
import com.aiurt.boot.task.entity.PatrolDevice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author sbx
 * @since 2023/10/18
 */
public interface IPatrolDeviceService extends IService<PatrolDevice> {
    /**
     * 根据任务id、任务标准关联表id和工单的设备code查询设备
     * 工单的设备code不为空时只返回该设备
     * @param taskId 任务id
     * @param taskStandardId 任务标准关联表id
     * @param deviceCode 工单的设备code
     * @return
     */
    List<PatrolDeviceDTO> queryDevices(String taskId, String taskStandardId, String deviceCode);

    /**
     * 根据任务id和任务标准关联表id查询设备详情
     * @param taskId 任务id
     * @param taskStandardId 任务标准关联表id
     * @return
     */
    List<DeviceDTO> queryDevicesDetail(String taskId, String taskStandardId);
}
