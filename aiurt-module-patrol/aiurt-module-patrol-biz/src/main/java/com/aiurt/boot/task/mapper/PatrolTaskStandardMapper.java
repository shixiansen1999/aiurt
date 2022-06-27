package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.dto.PatrolTaskStandardDTO;
import com.aiurt.boot.task.entity.PatrolTaskStandard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: patrol_task_standard
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface PatrolTaskStandardMapper extends BaseMapper<PatrolTaskStandard> {
    /**
     * app-巡检详情-专业和子系统名称
     * @param id
     * @return
     */
    List<PatrolTaskStandardDTO> getMajorSystemName(String id);
}
