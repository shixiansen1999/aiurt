package com.aiurt.boot.task.service;

import com.aiurt.boot.task.entity.RepairTask;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: repair_task
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface IRepairTaskService extends IService<RepairTask> {

    /**
     * 检修任务列表查询
     * @param pageList
     * @param condition
     * @return
     */
    Page<RepairTask> selectables(Page<RepairTask> pageList, RepairTask condition);

    /**
     * 检修任务清单查询
     * @param pageList
     * @param condition
     * @return
     */
    Page<RepairTask> selectTasklet(Page<RepairTask> pageList, RepairTask condition);
}
