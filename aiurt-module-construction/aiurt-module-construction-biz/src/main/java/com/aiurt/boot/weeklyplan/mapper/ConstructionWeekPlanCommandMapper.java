package com.aiurt.boot.weeklyplan.mapper;

import com.aiurt.boot.weeklyplan.dto.ConstructionWeekPlanCommandDTO;
import com.aiurt.boot.weeklyplan.entity.ConstructionWeekPlanCommand;
import com.aiurt.boot.weeklyplan.vo.ConstructionWeekPlanCommandVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: construction_week_plan_command
 * @Author: aiurt
 * @Date: 2022-11-22
 * @Version: V1.0
 */
public interface ConstructionWeekPlanCommandMapper extends BaseMapper<ConstructionWeekPlanCommand> {
    /**
     * 施工周计划列表查询
     *
     * @param page
     * @param constructionWeekPlanCommandDTO
     * @return
     */
    IPage<ConstructionWeekPlanCommandVO> queryPageList(@Param("page") Page<ConstructionWeekPlanCommandVO> page, @Param("userId") String userId,
                                                       @Param("condition") ConstructionWeekPlanCommandDTO constructionWeekPlanCommandDTO);

    /**
     *
     * @param page
     * @param id
     * @param constructionWeekPlanCommandDTO
     * @return
     */
    IPage<ConstructionWeekPlanCommandVO> queryWorkToDo(@Param("page") Page<ConstructionWeekPlanCommandVO> page,@Param("userName") String id,
                                                       @Param("condition")  ConstructionWeekPlanCommandDTO constructionWeekPlanCommandDTO);
}
