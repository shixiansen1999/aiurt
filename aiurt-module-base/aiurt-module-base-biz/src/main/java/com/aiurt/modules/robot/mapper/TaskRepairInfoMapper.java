package com.aiurt.modules.robot.mapper;

import com.aiurt.modules.robot.entity.TaskRepairInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: task_repair_info
 * @Author: jeecg-boot
 * @Date:   2022-10-08
 * @Version: V1.0
 */
public interface TaskRepairInfoMapper extends BaseMapper<TaskRepairInfo> {

    List<TaskRepairInfo> queryByTaskId(String taskId);

}
