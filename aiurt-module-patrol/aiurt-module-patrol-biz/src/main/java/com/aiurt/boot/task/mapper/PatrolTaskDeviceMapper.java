package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.param.PatrolTaskDeviceParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: patrol_task_device
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface PatrolTaskDeviceMapper extends BaseMapper<PatrolTaskDevice> {

    List<PatrolTaskDeviceParam> selectBillInfo(PatrolTaskDeviceParam patrolTaskDeviceParam);
}
