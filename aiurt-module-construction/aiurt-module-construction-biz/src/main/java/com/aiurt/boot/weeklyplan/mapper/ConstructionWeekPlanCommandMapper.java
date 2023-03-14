package com.aiurt.boot.weeklyplan.mapper;

import com.aiurt.boot.weeklyplan.dto.ConstructionWeekPlanCommandDTO;
import com.aiurt.boot.weeklyplan.dto.ConstructionWeekPlanExportDTO;
import com.aiurt.boot.weeklyplan.entity.ConstructionWeekPlanCommand;
import com.aiurt.boot.weeklyplan.vo.ConstructionWeekPlanCommandVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

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
    IPage<ConstructionWeekPlanCommandVO> queryPageList(@Param("page") Page<ConstructionWeekPlanCommandVO> page,
                                                       @Param("condition") ConstructionWeekPlanCommandDTO constructionWeekPlanCommandDTO);

   ConstructionWeekPlanCommandVO queryById(String id);

    /**
     * 待办审核
     *
     * @param page
     * @param id
     * @param constructionWeekPlanCommandDTO
     * @return
     */
    IPage<ConstructionWeekPlanCommandVO> queryWorkToDo(@Param("page") Page<ConstructionWeekPlanCommandVO> page,@Param("userName") String id,
                                                       @Param("condition")  ConstructionWeekPlanCommandDTO constructionWeekPlanCommandDTO);

    /**
     * 查询userId
     * @param name
     * @param phone
     * @return
     */
    String selectUserId(@Param("name") String name,@Param("phone") String phone);

    /**
     * 查询
     * @param permitCode
     * @param name
     * @return
     */
    String selectUserIdByPermitCode(@Param("permitCode") String permitCode,@Param("name") String name);

    /**
     * 获取周计划导出的数据
     *
     * @param startDate
     * @param endDate
     * @param formStatus
     * @return
     */
    List<ConstructionWeekPlanExportDTO> getExportData(@Param("lineCode") String lineCode,
                                                      @Param("startDate") Date startDate,
                                                      @Param("endDate") Date endDate,
                                                      @Param("formStatus") Integer formStatus);

}
