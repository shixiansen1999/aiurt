package com.aiurt.boot.modules.statistical.service;

import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.patrol.param.PatrolTaskDetailParam;
import com.aiurt.boot.modules.patrol.vo.PatrolTaskVO;
import com.aiurt.boot.modules.statistical.vo.StatisticsPatrolVO;
import com.aiurt.boot.modules.statistical.vo.StatisticsRepairVO;

import com.aiurt.boot.modules.statistical.vo.PageVo;
import com.aiurt.boot.modules.statistical.vo.StatisticsResultVO;
import com.aiurt.boot.modules.statistical.vo.StatisticsVO;
import com.aiurt.boot.modules.statistical.vo.*;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.vo.SysDepartScheduleVo;


import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author qian
 * @version 1.0
 * @date 2021/11/20 11:24
 */
public interface StatisticalService {
    Result<List<StatisticsResultVO>> getCount(StatisticsVO statisticsVO);

    /**
     * 查询各班组巡检数
     *
     * @return
     */
    Result<List<StatisticsPatrolVO>> getPatrolCountGroupByOrg(String lineId);

    /**
     * 查询各班组检修数
     *
     * @return
     */
    Result<List<StatisticsRepairVO>> getRepairCountGroupByOrg(String lineId);


    PageVo getWeeklyFaultStatistics(String lineCode, Integer pageNo, Integer pageSize, DateTime startTime, DateTime endTime);

    PageVo getRepairStatisticByLineCodeAndTime(String lineCode, Integer pageNo, Integer pageSize, DateTime startTime, DateTime endTime);

    SysDepartScheduleVo getSysDepartSchedulePageVo(String lineCode, Integer pageNo, Integer pageSize, DateTime now);

    PageVo getWeeklyPatrolStatisticPageVo(Integer pageNo, Integer pageSize, List<Integer> stationIds,String lineCode, DateTime now);

    PageVo getPatrolStatisticPageVo(Integer pageNo, Integer pageSize, String lineCode, DateTime now);

    /**
     * 故障报修方式对比
     *
     * @param lineCode
     * @return
     */
    Result<List<StatisticsFaultWayVO>> getFaultCountGroupByWay(String lineCode);

    /**
     * 故障完成情况对比
     *
     * @param lineCode
     * @return
     */
    Result<List<StatisticsFaultStatusVO>> getFaultCountGroupByStatus(String lineCode);

    /**
     * 故障一级、二级、三级统计
     *
     * @param lineCode
     * @return
     */
    Result<Map<String, List<StatisticsFaultLevelVO>>> getFaultGroupByLevel(String lineCode);

    /**
     * 年度维修情况统计
     * @param lineCode
     * @return
     */
    Result<List<StatisticsFaultMonthVO>> getFaultCountGroupByMonth(String lineCode);

    /**
     * 各子系统故障数据统计
     *
     * @param lineCode
     * @return
     */
    Result<List<StatisticsFaultSystemVO>> getFaultCountGroupBySystem(String lineCode, Integer month);


    PageVo getRepairStatistic(String lineId, Integer pageNo, Integer pageSize, DateTime now);
    /**
     * 故障数据统计
     *
     * @param lineCode
     * @return
     */
    Result<StatisticsFaultCountVO> getFaultCountAndDetails(String lineCode);

    List<RepairTaskVo> getCompletedRepair(String lineCode, DateTime startTime, DateTime endTime);

    /**
     *  获取当日当班人员
     */
    List<SysUser> getDutyUsers(String lineCode, String orgId, DateTime endTime);

    List<FaultStatisticsModal> getUncompletedFault(String lineCode, DateTime startTime, DateTime endTime);

    List<RepairTaskVo> getWeeklyIgnoreRepair(String lineCode, DateTime now);

    List<RepairTaskVo> getTodayRepair(String lineCode, DateTime now);

    PatrolStatisticVo getWeeklyPatrolStatistic(String lineCode, DateTime now);

    Result<?> detail(HttpServletRequest req, PatrolTaskDetailParam param);

    List<PatrolTaskVO> getCompletedPatrol(String lineCode, String departId,DateTime now);

    List<PatrolTaskVO> getIgnoredPatrol(String lineCode, String departId, DateTime now);

    List<PatrolTaskVO> getExceptionPatrol(String lineCode, DateTime now);

    List<PatrolTaskVO> getTodayPatrol(String lineCode, DateTime now);

    List<PatrolTaskVO> getTodayCompletedPatrol(String lineCode, DateTime now);

    List<PatrolTaskVO> getPlanPatrol(String lineCode, String departId, DateTime now);

    List<RepairTaskVo> getWeeklyPlanRepair(String lineCode, DateTime now);

    IPage getFaultTotalDetail(String lineCode, Integer pageNo, Integer pageSize, Date startTime, Date endTime);

    List<FaultStatisticsModal> getCompletedFault(String lineCode, DateTime startTime, DateTime endTime);
}
