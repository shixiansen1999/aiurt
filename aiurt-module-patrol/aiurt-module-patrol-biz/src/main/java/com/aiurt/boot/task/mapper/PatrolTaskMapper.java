package com.aiurt.boot.task.mapper;

import com.aiurt.boot.dto.UserTeamPatrolDTO;
import com.aiurt.boot.report.model.FailureOrgReport;
import com.aiurt.boot.report.model.FailureReport;
import com.aiurt.boot.report.model.PatrolReport;
import com.aiurt.boot.report.model.PatrolReportModel;
import com.aiurt.boot.report.model.dto.LineOrStationDTO;
import com.aiurt.boot.report.model.dto.MonthDTO;
import com.aiurt.boot.screen.model.ScreenModule;
import com.aiurt.boot.screen.model.ScreenStatisticsGraph;
import com.aiurt.boot.screen.model.ScreenStatisticsTask;
import com.aiurt.boot.screen.model.ScreenTran;
import com.aiurt.boot.standard.dto.StationDTO;
import com.aiurt.boot.statistics.dto.IndexScheduleDTO;
import com.aiurt.boot.statistics.dto.IndexTaskDTO;
import com.aiurt.boot.statistics.model.IndexTaskInfo;
import com.aiurt.boot.statistics.model.PatrolCondition;
import com.aiurt.boot.statistics.model.PatrolIndexTask;
import com.aiurt.boot.statistics.model.ScheduleTask;
import com.aiurt.boot.task.dto.GeneralReturn;
import com.aiurt.boot.task.dto.PatrolTaskDTO;
import com.aiurt.boot.task.dto.PatrolTaskUserContentDTO;
import com.aiurt.boot.task.dto.SubsystemDTO;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskOrganization;
import com.aiurt.boot.task.entity.PatrolTaskStandard;
import com.aiurt.boot.task.param.PatrolTaskParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.CsUserDepartModel;

import java.util.Date;
import java.util.List;

/**
 * @Description: patrol_task
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface PatrolTaskMapper extends BaseMapper<PatrolTask> {
    /**
     * app-巡检任务池列表
     *
     * @param pageList
     * @param patrolTaskDTO
     * @return
     */
    List<PatrolTaskDTO> getPatrolTaskPoolList(@Param("pageList") Page<PatrolTaskDTO> pageList, @Param("patrolTaskDTO") PatrolTaskDTO patrolTaskDTO);

    /**
     *  app-巡检任务列表
     * @param pageList
     * @param patrolTaskDTO
     * @return
     */
    List<PatrolTaskDTO> getPatrolTaskList(@Param("pageList") Page<PatrolTaskDTO> pageList, @Param("patrolTaskDTO") PatrolTaskDTO patrolTaskDTO);

    /**
     * app-获取组织机构名称
     *
     * @param taskCode
     * @return author hlq
     */
    List<String> getOrganizationName(@Param("taskCode") String taskCode);

    /**
     * app-获取站点名称
     *
     * @param code
     * @return author hlq
     */
    List<StationDTO> getStationName(String code);

    /**
     * 查询巡检任务列表
     *
     * @param page
     * @param patrolTaskParam
     * @return
     */
    IPage<PatrolTaskParam> getTaskList(Page<PatrolTaskParam> page, @Param("patrolTask") PatrolTaskParam patrolTaskParam, @Param("taskCodes") List<String> taskCodes);

    /**
     * app-获取退回人的名称
     *
     * @param patrolReturnUserId
     * @return
     */
    String getUserName(String patrolReturnUserId);

    /**
     * app-获取部门code
     *
     * @param taskCode
     * @return
     */
    List<String> getOrgCode(String taskCode);

    /**
     * app-获取指派人员信息
     *
     * @param code
     * @return
     */
    List<PatrolTaskUserContentDTO> getUser(@Param("code") String code);

    /**
     * app-获取组织机构名称
     *
     * @param code
     * @return
     */
    String getOrgName(String code);

    /**
     * PC巡检任务池详情-基本信息
     *
     * @param patrolTaskParam
     * @return
     */
    PatrolTaskParam selectBasicInfo(@Param("patrolTaskParam") PatrolTaskParam patrolTaskParam);

    /**
     * PC-手工下方列表
     *
     * @param pageList
     * @param patrolTaskDTO
     * @return
     */
    List<PatrolTaskDTO> getPatrolTaskManualList(@Param("pageList") Page<PatrolTaskDTO> pageList, @Param("patrolTaskDTO") PatrolTaskDTO patrolTaskDTO);

    /**
     * 根据专业编码获取专业名称
     *
     * @param code
     * @return
     */
    String getMajorNameByMajorCode(String code);

    /**
     * 根据子系统编码获取子系统名称
     *
     * @param code
     * @return
     */
    String getSubsystemNameBySystemCode(String code);

    /**
     * 根据用户ID获取用户名
     *
     * @param userId
     * @return
     */
    String getUsername(String userId);

    /**
     * 获取专业下的子系统信息
     *
     * @param majorCode
     * @param subsystem
     * @return
     */
    List<SubsystemDTO> getMajorSubsystemGanged(@Param("majorCode") String majorCode, @Param("subsystemList") List<SubsystemDTO> subsystem);

    /**
     * 获取stationCodeList
     *
     * @param code
     * @return
     */
    List<String> getStationCode(String code);

    /**
     * 根据站点编号获取线路编号
     *
     * @param stationCode
     * @return
     */
    String getLineCode(String stationCode);

    /**
     * 获取首页巡检的巡视列表
     * @param page
     * @param condition
     * @param regexp
     * @param departList
     * @param standard
     * @return
     */
    IPage<PatrolIndexTask> getIndexPatrolList(Page<PatrolIndexTask> page, @Param("condition") PatrolCondition condition, @Param("regexp") String regexp, @Param("departList") List<PatrolTaskOrganization> departList,@Param("standard")List<PatrolTaskStandard> standard);

    /**
     * 获取首页巡视列表下的任务列表
     * @param page
     * @param condition
     * @param departList
     * @param standard
     * @return
     */
    IPage<IndexTaskInfo> getIndexTaskList(Page<IndexTaskInfo> page, @Param("condition") IndexTaskDTO condition, @Param("departList") List<PatrolTaskOrganization> departList, @Param("standard")List<PatrolTaskStandard> standard);

    /**
     * 获取首页的日程的巡检列表
     * @param page
     * @param indexScheduleDTO
     * @param departList
     * @return
     */
    IPage<ScheduleTask> getScheduleList(Page<ScheduleTask> page, @Param("condition") IndexScheduleDTO indexScheduleDTO, @Param("departList") List<CsUserDepartModel> departList);

    /**
     * 获取首页指定日期范围的任务列表
     * @param startDate
     * @param endDate
     * @param departList
     * @param taskIds
     * @return
     */
    List<PatrolTask> getOverviewInfo(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("departList") List<PatrolTaskOrganization> departList, @Param("taskIds") List<PatrolTaskStandard> taskIds);

    /**
     * 查看当前用户，当天的巡检任务
     *
     * @param userId
     * @param date
     * @return
     */
    List<PatrolTask> getUserTask(@Param("userId") String userId, @Param("date") Date date);

    /**
     * 大屏巡视模块-巡视数据统计任务列表
     *
     * @param screenTran
     * @return
     */
    List<ScreenStatisticsTask> getScreenTask(@Param("condition") ScreenTran screenTran);

    /**
     * 大屏巡视模块-巡视任务完成情况
     *
     * @param screenTran
     * @return
     */
    List<ScreenStatisticsGraph> getScreenGraph(@Param("condition") ScreenTran screenTran);

    /**
     * 大屏巡视模块-巡视数据统计详情列表
     *
     * @param page
     * @param moduleType
     * @return
     */
    IPage<ScreenStatisticsTask> getStatisticsDataList(Page<ScreenStatisticsTask> page, @Param("condition") ScreenModule moduleType);

    /**
     * 大屏巡视模块-数据统计和重要数据展示任务查询
     *
     * @param module
     * @return
     */
    List<PatrolTask> getScreenDataCount(@Param("condition") ScreenModule module);
    /**
     * 报表统计-巡检任务查询
     * @param pageList
     * @param orgIdList
     * @return
     */

    List<PatrolReport> getReportTaskList(@Param("pageList")Page<PatrolReport> pageList, @Param("orgIdList")List<String> orgIdList);

    /**
     * 报表统计-漏检数查询
     * @param omitModel
     * @return
     */
    /**
     *解决分页问题
     * @param condition
     * @param orgCode
     * @return
     */
    PatrolReport getTasks(@Param("orgCode")String orgCode,@Param("condition") PatrolReportModel condition);

    /**
     * 获取漏巡列表
     * @param omitModel
     * @return
     */
    List<PatrolReport> getReportOmitList(@Param("condition")PatrolReportModel omitModel);

    /**
     * 报表统计-故障列表
     * @param page
     * @param id
     * @param lineCode
     * @param stationCode
     * @param startTime
     * @param endTime
     * @return
     */
    IPage<FailureReport> getFailureReport(Page<FailureReport>page,@Param("id")String id,@Param("lineCode") String lineCode,@Param("stationCode") List<String> stationCode,@Param("startTime") String startTime, @Param("endTime")String endTime);

    /**
     * 查询数量
     * @param code
     * @param orgCode
     * @param lineCode
     * @param stationCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<Integer> selectNum(@Param("code") String code,@Param("orgCode")String orgCode,@Param("lineCode") String lineCode,@Param("stationCode") List<String> stationCode,@Param("startTime") String startTime, @Param("endTime")String endTime);

    /**
     * 查询时间
     * @param code
     * @param orgCode
     * @param lineCode
     * @param stationCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<Integer> selectNum1(@Param("code") String code,@Param("orgCode")String orgCode,@Param("lineCode") String lineCode,@Param("stationCode") List<String> stationCode,@Param("startTime") String startTime, @Param("endTime")String endTime);

    /**
     * 查当年总数
     * @param id
     * @param lineCode
     * @param stationCode
     * @return
     */
    List<MonthDTO> selectMonth(@Param("id")String id,@Param("lineCode") String lineCode,@Param("stationCode") List<String> stationCode);

    /**
     * 查询班组报表
     * @param page
     * @param ids
     * @param lineCode
     * @param stationCode
     * @param startTime
     * @param endTime
     * @param systemCode
     * @return
     */
    IPage<FailureOrgReport> getOrgReport(Page<FailureOrgReport> page, @Param("ids")List<String> ids, @Param("lineCode") String lineCode, @Param("stationCode") List<String> stationCode, @Param("startTime") String startTime, @Param("endTime")String endTime, @Param("systemCode")  List<String> systemCode);

    /**
     * 查询班组时间
     * @param orgCodes
     * @param lineCode
     * @param stationCode
     * @param systemCode
     * @return
     */
    List<MonthDTO> selectMonthOrg(@Param("orgCodes") List<String> orgCodes,@Param("lineCode") String lineCode,@Param("stationCode") List<String> stationCode,@Param("systemCode") List<String> systemCode);

    /**
     * 权限查询
     * @param id
     * @return
     */
    List<LineOrStationDTO> selectLine(@Param("id") String id);

    /**
     * 权限查询
     * @param id
     * @param lineCode
     * @return
     */
    List<LineOrStationDTO> selectStation(@Param("id") String id,@Param("lineCode") String lineCode);

    /**
     * 权限查询
     * @param id
     * @return
     */
    List<LineOrStationDTO> selectSystem(@Param("id") String id);

    /**
     * 班组权限查询
     * @param id
     * @return
     */
    List<LineOrStationDTO> selectDepart(@Param("id") String id);

    /**
     * 获取自己及管辖的班组
     * @param orgCode
     * @return
     */
    List<LineOrStationDTO> getUserOrgCategory(String orgCode);

    /**
     * 计算指派的计划数
     * @param orgId
     * @param startDate
     * @param endDate
     * @return
     */
    UserTeamPatrolDTO getUserPlanNumber(@Param("orgId")String orgId, @Param("startDate")String startDate, @Param("endDate")String endDate);
    /**
     * 计算指派的巡检实际完成数
     * @param useIds
     * @param startDate
     * @param endDate
     * @return
     */
    List<UserTeamPatrolDTO> getUserNowNumber(@Param("useIds")List<String> useIds, @Param("startDate")String startDate, @Param("endDate")String endDate);

    /**
     * 计算同行人的巡检实际完成数
     * @param useIds
     * @param startDate
     * @param endDate
     * @return
     */
    List<UserTeamPatrolDTO> getPeopleNowNumber(@Param("useIds")List<String> useIds, @Param("startDate")String startDate, @Param("endDate")String endDate);

    /**
     * 计算指派的漏检数
     * @param useIds
     * @param startDate
     * @param endDate
     * @return
     */
    List<UserTeamPatrolDTO> getUserOmitTasksNumber(@Param("useIds")List<String> useIds, @Param("startDate")String startDate, @Param("endDate")String endDate);

    /**
     * 计算同行人的漏检数
     * @param useIds
     * @param startDate
     * @param endDate
     * @return
     */
    List<UserTeamPatrolDTO> getPeopleOmitTasksNumber(@Param("useIds")List<String> useIds, @Param("startDate")String startDate, @Param("endDate")String endDate);

    /**
     * app任务池-查看详情
     * @param id
     * @return
     */
    PatrolTaskDTO getDetail(@Param("id")String id);

    /**
     * 前端返回通用格式-组织机构
     * @param orgCodes
     * @return
     */
    List<GeneralReturn> getOrgCodeName(@Param("orgCodes")List<String> orgCodes);
}
