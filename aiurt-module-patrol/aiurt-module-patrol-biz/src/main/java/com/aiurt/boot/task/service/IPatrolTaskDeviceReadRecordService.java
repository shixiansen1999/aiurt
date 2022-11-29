package com.aiurt.boot.task.service;

import com.aiurt.boot.task.entity.PatrolTaskDeviceReadRecord;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/11/29
 * @desc
 */
public interface IPatrolTaskDeviceReadRecordService extends IService<PatrolTaskDeviceReadRecord> {
    /**
     * 判断当前用户是否已经阅读过安全事项
     * @param taskDeviceId
     * @param majorCode
     * @param subsystemCode
     * @param taskId
     * @return
     */
    boolean getPatrolTaskDeviceList(String taskDeviceId, String majorCode, String subsystemCode, String taskId);
}
