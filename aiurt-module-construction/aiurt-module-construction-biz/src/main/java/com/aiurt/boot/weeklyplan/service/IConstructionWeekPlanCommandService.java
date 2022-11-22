package com.aiurt.boot.weeklyplan.service;

import com.aiurt.boot.weeklyplan.dto.ConstructionWeekPlanCommandDTO;
import com.aiurt.boot.weeklyplan.entity.ConstructionWeekPlanCommand;
import com.aiurt.boot.weeklyplan.vo.ConstructionWeekPlanCommandVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: construction_week_plan_command
 * @Author: aiurt
 * @Date: 2022-11-22
 * @Version: V1.0
 */
public interface IConstructionWeekPlanCommandService extends IService<ConstructionWeekPlanCommand> {
    /**
     * 施工周计划列表查询
     *
     * @param page
     * @param constructionWeekPlanCommandDTO
     * @return
     */
    IPage<ConstructionWeekPlanCommandVO> queryPageList(Page<ConstructionWeekPlanCommandVO> page, ConstructionWeekPlanCommandDTO constructionWeekPlanCommandDTO);

    /**
     * 施工周计划申报
     *
     * @param constructionWeekPlanCommand
     */
    String declaration(ConstructionWeekPlanCommand constructionWeekPlanCommand);

    /**
     * 施工周计划-编辑
     *
     * @param constructionWeekPlanCommand
     */
    void edit(ConstructionWeekPlanCommand constructionWeekPlanCommand);

    /**
     * 施工周计划-取消计划
     *
     * @param id
     * @param reason
     */
    void cancel(String id, String reason);

    /**
     * 施工周计划-计划提审
     *
     * @param id
     */
    void submit(String id);

    /**
     * 施工周计划-计划审核
     *
     * @param id
     */
    void audit(String id);
}
