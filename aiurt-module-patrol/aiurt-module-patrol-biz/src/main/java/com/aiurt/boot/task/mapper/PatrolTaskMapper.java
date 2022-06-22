package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.entity.PatrolTask;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: patrol_task
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface PatrolTaskMapper extends BaseMapper<PatrolTask> {

    /**
     * 查询巡检任务列表
     *
     * @param page
     * @param patrolTask
     * @return
     */
    IPage<PatrolTask> getTaskList(Page<PatrolTask> page, @Param("patrolTask") PatrolTask patrolTask);
}
