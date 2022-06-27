package com.aiurt.boot.plan.service;

import com.aiurt.boot.plan.dto.PatrolPlanDto;
import com.aiurt.boot.plan.entity.PatrolPlan;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: patrol_plan
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface IPatrolPlanService extends IService<PatrolPlan> {

    /**
     * 分页查询
     * @param page
     * @param patrolPlan
     * @return
     */
    IPage<PatrolPlanDto> pageList(Page<PatrolPlanDto> page, PatrolPlanDto patrolPlan);

    /**
     * 添加
     * @param patrolPlanDto
     */
    void add(PatrolPlanDto patrolPlanDto);

    /**
     * 逻辑删除
     * @param id
     */
    void delete(String id);

    /**
     * 查询详情
     * @param id
     * @return
     */
    PatrolPlanDto selectById(String id);
}
