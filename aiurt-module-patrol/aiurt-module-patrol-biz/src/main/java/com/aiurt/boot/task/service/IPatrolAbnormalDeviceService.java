package com.aiurt.boot.task.service;

import com.aiurt.boot.task.dto.PatrolAbnormalDeviceAddDTO;
import com.aiurt.boot.task.entity.PatrolAbnormalDevice;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author sbx
 * @since 2023/10/17
 */
public interface IPatrolAbnormalDeviceService extends IService<PatrolAbnormalDevice> {

    void add(PatrolAbnormalDeviceAddDTO patrolAbnormalDeviceAddDTO);
}
