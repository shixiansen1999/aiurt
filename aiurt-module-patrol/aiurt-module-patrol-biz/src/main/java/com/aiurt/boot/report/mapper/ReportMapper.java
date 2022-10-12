package com.aiurt.boot.report.mapper;

import com.aiurt.boot.report.model.PatrolReport;
import com.aiurt.boot.report.model.PatrolReportModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/10/11
 * @desc
 */
public interface ReportMapper extends BaseMapper<PatrolReport> {
    /**
     * 计算班组下的指派巡检已完成任务
     * @param useIds
     * @param condition
     * @return
     */
    List<PatrolReport> getUserNowNumber(@Param("useIds") List<String> useIds, @Param("condition")PatrolReportModel condition);

    /**
     * 计算班组下的同行人巡检已完成任务
     * @param useIds
     * @param condition
     * @return
     */
    List<PatrolReport> getPeopleNowNumber(@Param("useIds") List<String> useIds, @Param("condition")PatrolReportModel condition);

    /**
     * 计算班组下的指派漏检任务
     * @param useIds
     * @param condition
     * @return
     */
    List<PatrolReport> getUserOmitTasksNumber(@Param("useIds") List<String> useIds, @Param("condition")PatrolReportModel condition);

    /**
     * 计算班组下的同行人漏检任务
     * @param useIds
     * @param condition
     * @return
     */
    List<PatrolReport> getPeopleOmitTasksNumber(@Param("useIds") List<String> useIds, @Param("condition")PatrolReportModel condition);

    /**
     * 计算指派全部的任务数
     * @param useIds
     * @param condition
     * @return
     */
    List<PatrolReport> getAllUserTask(@Param("useIds") List<String> useIds, @Param("condition")PatrolReportModel condition);

    /**
     * 计算同行人的全部的任务数
     * @param useIds
     * @param condition
     * @return
     */
    List<PatrolReport> getAllPeopleTask(@Param("useIds") List<String> useIds, @Param("condition")PatrolReportModel condition);
}
