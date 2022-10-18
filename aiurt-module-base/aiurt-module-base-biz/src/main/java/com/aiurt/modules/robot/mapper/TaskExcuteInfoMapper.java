package com.aiurt.modules.robot.mapper;

import com.aiurt.modules.robot.dto.TaskPatrolDTO;
import com.aiurt.modules.robot.entity.TaskExcuteInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: task_excute_info
 * @Author: aiurt
 * @Date:   2022-09-29
 * @Version: V1.0
 */
public interface TaskExcuteInfoMapper extends BaseMapper<TaskExcuteInfo> {
    IPage<TaskPatrolDTO> getPatrolListPage(Page<TaskPatrolDTO> page, @Param("taskId") String taskId, @Param("device") String device, @Param("excuteState") String excuteState);
}
