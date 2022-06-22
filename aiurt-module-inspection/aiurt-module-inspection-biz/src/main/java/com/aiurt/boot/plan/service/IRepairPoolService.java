package com.aiurt.boot.plan.service;

import com.aiurt.boot.entity.inspection.plan.RepairPool;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;
import java.util.List;

/**
 * @Description: repair_pool
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface IRepairPoolService extends IService<RepairPool> {
    /**
     * 检修计划池列表查询
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return
     */
    List<RepairPool> queryList(Date startTime, Date endTime);

}
