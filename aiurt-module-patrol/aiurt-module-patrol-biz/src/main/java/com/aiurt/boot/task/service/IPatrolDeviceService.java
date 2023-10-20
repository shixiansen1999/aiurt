package com.aiurt.boot.task.service;

import com.aiurt.boot.task.dto.PatrolDeviceDTO;
import com.aiurt.boot.task.entity.PatrolDevice;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author sbx
 * @since 2023/10/18
 */
public interface IPatrolDeviceService extends IService<PatrolDevice> {
    List<PatrolDeviceDTO> queryDevices(String taskId, String taskStandardId);
}
