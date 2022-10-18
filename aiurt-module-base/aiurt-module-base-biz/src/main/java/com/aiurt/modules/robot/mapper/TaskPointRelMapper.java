package com.aiurt.modules.robot.mapper;


import com.aiurt.modules.robot.dto.AreaPointDTO;
import com.aiurt.modules.robot.entity.TaskPointRel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: task_point_rel
 * @Author: aiurt
 * @Date:   2022-09-27
 * @Version: V1.0
 */
public interface TaskPointRelMapper extends BaseMapper<TaskPointRel> {

    /**
     * 查询任务模板id对应的巡检点位
     * @param taskPathId 任务模板id
     * @return
     */
    List<AreaPointDTO> queryPointByTaskPathId(String taskPathId);
}
