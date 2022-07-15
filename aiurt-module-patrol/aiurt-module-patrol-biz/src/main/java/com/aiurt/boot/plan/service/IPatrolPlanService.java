package com.aiurt.boot.plan.service;

import com.aiurt.boot.plan.dto.PatrolPlanDto;
import com.aiurt.boot.plan.dto.QuerySiteDto;
import com.aiurt.boot.plan.dto.StandardDTO;
import com.aiurt.boot.plan.entity.PatrolPlan;
import com.aiurt.boot.task.dto.MajorDTO;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

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
     *
     * @param code
     * @param id
     * @return
     */
    PatrolPlanDto selectId( String id,String code);

    /**
     * 查询站点
     * @return
     */
    List<QuerySiteDto> querySited();

    /**
     * 编辑
     * @param patrolPlanDto
     */
    void updateId(PatrolPlanDto patrolPlanDto);

    /**
     * 查询指定设备
     * @param standardCode
     * @return
     */
    IPage<Device> viewDetails(Page<Device> page,String standardCode, String planId);

    /**
     * 查询专业，专业子系统的信息
     *
     * @param id
     * @return
     */
    List<MajorDTO> selectMajorCodeList(String id);
    /**
     * 查询对应标准表下拉框
     * @param PlanId
     * @param majorCode
     * @param subsystemCode
     * @return
     */
    List<StandardDTO> selectPlanStandard(String PlanId, String majorCode, String subsystemCode);

}
