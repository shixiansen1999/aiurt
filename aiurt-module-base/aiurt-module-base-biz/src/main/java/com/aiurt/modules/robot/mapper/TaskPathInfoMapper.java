package com.aiurt.modules.robot.mapper;

import com.aiurt.modules.robot.dto.TaskPathInfoDTO;
import com.aiurt.modules.robot.entity.TaskPathInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: task_path_info
 * @Author: aiurt
 * @Date: 2022-09-26
 * @Version: V1.0
 */
public interface TaskPathInfoMapper extends BaseMapper<TaskPathInfo> {
    /**
     * 任务模板列表分页查询
     *
     * @param page         分页参数
     * @param taskPathInfo 查询条件
     * @return
     */
    List<TaskPathInfoDTO> queryPageList(@Param("page") Page<TaskPathInfoDTO> page, @Param("taskPathInfo") TaskPathInfo taskPathInfo);

    /**
     * 根据任务模板id查找点位对应的机器人
     *
     * @param taskPathId 任务模板id
     * @return
     */
    List<String> queryRobotIpByTaskPathId(String taskPathId);
}
