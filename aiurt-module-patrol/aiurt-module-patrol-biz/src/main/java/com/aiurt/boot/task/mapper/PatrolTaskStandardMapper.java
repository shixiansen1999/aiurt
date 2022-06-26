package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.entity.PatrolTaskStandard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: patrol_task_standard
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface PatrolTaskStandardMapper extends BaseMapper<PatrolTaskStandard> {

    /**
     * app-巡检详情-专业名称
     * @param majorCode
     * @return
     */
    String getMajorName(@Param("majorCode") String majorCode);

    /**
     *app-巡检详情-子系统名称
     * @param subsystemCode
     * @return
     */
    String getSysName(String subsystemCode);
}
