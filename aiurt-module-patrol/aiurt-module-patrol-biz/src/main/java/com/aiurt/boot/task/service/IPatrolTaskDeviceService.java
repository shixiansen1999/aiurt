package com.aiurt.boot.task.service;

import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.param.PatrolTaskDeviceParam;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: patrol_task_device
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface IPatrolTaskDeviceService extends IService<PatrolTaskDevice> {

    /**
     * PC巡检任务池详情-巡检工单
     * @param patrolTaskDeviceParam
     * @return
     */
    List<PatrolTaskDeviceParam> selectBillInfo(PatrolTaskDeviceParam patrolTaskDeviceParam);
}
