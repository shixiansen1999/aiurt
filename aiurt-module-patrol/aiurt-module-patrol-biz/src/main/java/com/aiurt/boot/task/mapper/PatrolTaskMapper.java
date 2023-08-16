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
import com.aiurt.boot.statistics.dto.IndexCountDTO;
import com.aiurt.boot.statistics.dto.IndexScheduleDTO;
import com.aiurt.boot.statistics.dto.IndexTaskDTO;
import com.aiurt.boot.statistics.model.*;
import com.aiurt.boot.task.dto.*;
import com.aiurt.boot.task.entity.*;
import com.aiurt.boot.task.param.PatrolTaskParam;
import com.aiurt.boot.task.param.TemHumParam;
import com.aiurt.common.aspect.annotation.DataColumn;
import com.aiurt.common.aspect.annotation.DataPermission;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description: patrol_task
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
@EnableDataPerm(excluseMethodName = {"getIndexPatrolList"})
public interface PatrolTaskMapper extends BaseMapper<PatrolTask> {
    /**
     * app-巡检任务池列表
     *
     * @param pageList
     * @param patrolTaskDTO
     * @return
     */
    @DataPermission({@DataColumn(key = "deptName",value = "pto.org_code")})
    List<PatrolTaskDTO> getPatrolTaskPoolList(@Param("pageList") Page<PatrolTaskDTO> pageList, @Param("patrolTaskDTO") PatrolTaskDTO patrolTaskDTO, @Param("b") boolean b);

    /**
     *  app-巡检任务列表
     * @param pageList
     * @param patrolTaskDTO
     * @return
     */
    @DataPermission({@DataColumn(key = "deptName",value = "pto.org_code")})
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
    @DataPermission({
            @DataColumn(key = "deptName", value = "pto.org_code"),
            @DataColumn(key = "majorName", value = "ptsd.major_code"),
            @DataColumn(key = "systemName", value = "ptsd.system_code"),
            @DataColumn(key = "lineName", value = "pts.line_code"),
            @DataColumn(key = "stationName", value = "pts.station_code")
    })
    IPage<PatrolTaskParam> getTaskList(Page<PatrolTaskParam> page, @Param("patrolTask") PatrolTaskParam patrolTaskParam);

    /**
     * 根据巡视任务id列表查询
     * @param patrolTaskIdList 巡视任务id列表
     * @return
     */
    List<PatrolTaskParam> getTaskListByIds(@Param("patrolTaskIdList") List<String> patrolTaskIdList);

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
    @DataPermission({
            @DataColumn(key = "deptName", value = "pto.org_code"),
            @DataColumn(key = "majorName", value = "ptsd.major_code"),
            @DataColumn(key = "systemName", value = "ptsd.system_code"),
            @DataColumn(key = "lineName", value = "pts.line_code"),
            @DataColumn(key = "stationName", value = "pts.station_code")
    })
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
     * @return
     */
//    @DataPermission({
//            @DataColumn(key = "deptName",value = "t2.org_code"),
//            @DataColumn(key = "majorName",value = "t3.major_code"),
//            @DataColumn(key = "systemName",value = "t3.system_code"),
//            @DataColumn(key = "lineName",value = "t4.line_code"),
//            @DataColumn(key = "stationName",value = "t4.station_code")
//    })
    IPage<PatrolIndexTask> getIndexPatrolList(Page<PatrolIndexTask> page, @Param("condition") PatrolCondition condition, @Param("regexp") String regexp);

    /**
     * 获取首页巡视列表下的任务列表
     * @param page
     * @param condition
     * @return
     */
    @DataPermission({
            @DataColumn(key = "deptName", value = "pto.org_code"),
            @DataColumn(key = "majorName", value = "ptsd.major_code"),
            @DataColumn(key = "systemName", value = "ptsd.system_code"),
            @DataColumn(key = "lineName", value = "pts.line_code"),
            @DataColumn(key = "stationName", value = "pts.station_code")
    })
    IPage<IndexTaskInfo> getIndexTaskList(Page<IndexTaskInfo> page, @Param("condition") IndexTaskDTO condition);

    /**
     * 获取首页的日程的巡检列表
     * @param page
     * @param indexScheduleDTO
     * @param patrolTaskOrganizations
     * @return
     */
//    @DataPermission({
//            @DataColumn(key = "deptName",value = "pto.org_code"),
//            @DataColumn(key = "majorName",value = "pts2.major_code"),
//            @DataColumn(key = "systemName",value = "pts2.system_code"),
//            @DataColumn(key = "lineName",value = "pts.station_code"),
//            @DataColumn(key = "stationName",value = "pts.station_code")
//    })
//    IPage<ScheduleTask> getScheduleList(Page<ScheduleTask> page, @Param("condition") IndexScheduleDTO indexScheduleDTO, @Param("patrolTaskOrganizations") List<PatrolTaskOrganization> patrolTaskOrganizations);
    @DataPermission({
            @DataColumn(key = "deptName", value = "pto.org_code"),
            @DataColumn(key = "majorName", value = "ptsd.major_code"),
            @DataColumn(key = "systemName", value = "ptsd.system_code"),
            @DataColumn(key = "lineName", value = "pts.line_code"),
            @DataColumn(key = "stationName", value = "pts.station_code")
    })
    IPage<ScheduleTask> getScheduleList(Page<ScheduleTask> page, @Param("condition") IndexScheduleDTO indexScheduleDTO);

    /**
     * 获取首页指定日期范围的任务列表
     *
     * @param startDate
     * @param endDate
     * @param filterConditions
     * @return
     */
    @DataPermission({
            @DataColumn(key = "deptName", value = "pto.org_code"),
            @DataColumn(key = "majorName", value = "ptsd.major_code"),
            @DataColumn(key = "systemName", value = "ptsd.system_code"),
            @DataColumn(key = "lineName", value = "pts.line_code"),
            @DataColumn(key = "stationName", value = "pts.station_code")
    })
    List<PatrolTask> getOverviewInfo(@Param("startDate") Date startDate, @Param("endDate") Date endDate/*, @Param("jointSQL") String filterConditions*/);

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
     * @param page
     * @param screenTran
     * @return
     */
    IPage<ScreenStatisticsTask> getScreenTask(Page<ScreenStatisticsTask> page, @Param("condition") ScreenTran screenTran);

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
    IPage<FailureReport> getFailureReport(Page<FailureReport>page,@Param("id")String id,@Param("lineCode") String lineCode,@Param("stationCode") List<String> stationCode,@Param("startTime") String startTime, @Param("endTime")String endTime,@Param("systemCode")List<String> systemCode);

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
    List<MonthDTO> selectMonth(@Param("id")String id,@Param("lineCode") String lineCode,@Param("stationCode") List<String> stationCode,@Param("systemCode")List<String> systemCode,@Param("startTime") String startTime, @Param("endTime")String endTime);

    /**
     * 查询班组报表
     * @param page
     * @param orgCodes
     * @param lineCode
     * @param stationCode
     * @param startTime
     * @param endTime
     * @param systemCode
     * @return
     */
    IPage<FailureOrgReport> getOrgReport(Page<FailureOrgReport> page, @Param("orgCodes")List<String> orgCodes, @Param("lineCode") String lineCode, @Param("stationCode") List<String> stationCode, @Param("startTime") String startTime, @Param("endTime")String endTime, @Param("systemCode")  List<String> systemCode);

    /**
     * 查询班组时间
     * @param orgCodes
     * @param lineCode
     * @param stationCode
     * @param systemCode
     * @return
     */
    List<MonthDTO> selectMonthOrg(@Param("orgCodes") List<String> orgCodes,@Param("lineCode") String lineCode,@Param("stationCode") List<String> stationCode,@Param("systemCode") List<String> systemCode, @Param("startTime") String startTime, @Param("endTime")String endTime);

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
     * @param workLogOrgCategory 实施配置里面组织机构是班组的编码
     * @return
     */
    List<LineOrStationDTO> getUserOrgCategory(@Param("orgCode") String orgCode, @Param("workLogOrgCategory") String workLogOrgCategory);

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

    List<TemperatureHumidityDTO> getTemAndHum(@Param("condition")TemHumParam temHumParam);

    @DataPermission({
            @DataColumn(key = "deptName",value = "t2.org_code"),
            @DataColumn(key = "majorName",value = "t3.major_code"),
            @DataColumn(key = "systemName",value = "t3.system_code"),
            @DataColumn(key = "lineName",value = "t4.line_code"),
            @DataColumn(key = "stationName",value = "t4.station_code")
    })
    List<PatrolTask> selectpatrolTaskList(@Param("firstDay")Date firstDay,@Param("lastDay") Date lastDay, @Param("status")Integer status);

    /**
     * 首页统计巡视数量(巡视总数、已巡视、未巡视、异常数)
     */
//    PatrolSituation getOverviewInfoCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("jointSQL") String filterConditions);
    PatrolSituation getOverviewInfoCount(@Param("condition") IndexCountDTO indexCountDTO);

    /**
     * 首页统计巡视任务下的工单数量(巡视总数、已巡视、未巡视、异常数)
     */
    PatrolSituation getTaskDeviceOverviewInfoCount(@Param("condition") IndexCountDTO indexCountDTO);

    /**
     * 首页统计巡视数量(巡视总数、已巡视、未巡视、异常数)
     */
    List<PatrolSituation>  getCountONMonth(@Param("condition") IndexCountDTO indexCountDTO);

    /**
     * 首页统计巡视任务下的工单数量(巡视总数、已巡视、未巡视、异常数)
     */
    List<PatrolSituation>  getTaskDeviceCountONMonth(@Param("condition") IndexCountDTO indexCountDTO);

    /**
     * 大屏统计巡视任务下的工单数量(巡视总数、已巡视、未巡视、异常数)
     */
    PatrolSituation getTaskDeviceCount(@Param("condition") ScreenModule module);
    /**
     * 统计报表中巡视任务下的工单数量(巡视总数、已巡视、未巡视、异常数)
     */
    List<PatrolReport> getReportTaskDeviceCount(@Param("condition") PatrolReportModel report);
    /**
     * 统计报表中巡视任务下的故障数
     */
    List<PatrolReport> getFaultList(@Param("condition") PatrolReportModel report);

    /**
     * 统计报表-故障-统计各个子系统故障已解决数（过滤挂起的）
     * @param page
     * @param id
     * @param lineCode
     * @param stationCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<FailureReport> getFilterFailureReport(Page<FailureReport>page,@Param("id")String id,@Param("lineCode") String lineCode,@Param("stationCode") List<String> stationCode,@Param("startTime") String startTime, @Param("endTime")String endTime,@Param("systemCode")List<String> systemCode);

    /**
     * 统计报表-故障-统计子系统发生故障时间（过滤挂起的）
     * @param code
     * @param orgCode
     * @param lineCode
     * @param stationCode
     * @param startTime
     * @param endTime
     * @return
     */
    List<Integer> selectFaultWorkTime(@Param("code") String code,@Param("orgCode")String orgCode,@Param("lineCode") String lineCode,@Param("stationCode") List<String> stationCode,@Param("startTime") String startTime, @Param("endTime")String endTime);

    /**
     * 统计报表-故障-统计各个部门故障已解决数（过滤挂起的）
     * @param page
     * @param orgCodes
     * @param lineCode
     * @param stationCode
     * @param startTime
     * @param endTime
     * @param systemCode
     * @return
     */
    List<FailureOrgReport> getFilterOrgReport(Page<FailureOrgReport> page, @Param("orgCodes")List<String> orgCodes, @Param("lineCode") String lineCode, @Param("stationCode") List<String> stationCode, @Param("startTime") String startTime, @Param("endTime")String endTime, @Param("systemCode")  List<String> systemCode);
    /**
     * 统计报表中巡视任务下的维修人工单数量(人员维度)
     */
    List<PatrolReport> getReportTaskUserCount(@Param("condition") PatrolReportModel report);
    /**
     * 统计报表中巡视任务下的同行人工单数量(人员维度)
     */
    List<PatrolReport> getReportTaskAccompanyCount(@Param("condition") PatrolReportModel report);

    /**
     * 获取首页巡视列表下的任务列表
     * @param page
     * @param condition
     * @return
     */
    IPage<IndexTaskInfo> getIndexTaskDeviceList(Page<IndexTaskInfo> page, @Param("condition") IndexTaskDTO condition);

    IPage<ScheduleTask> getScheduleDeviceList(Page<ScheduleTask> page, @Param("condition")IndexScheduleDTO indexScheduleDTO);

    IPage<ScreenStatisticsTask> getStatisticsDataDeviceList(Page<ScreenStatisticsTask> page, @Param("condition") ScreenModule moduleType);

    /**
     * 根据巡视任务id，查询出要导出excel的数据
     * @param patrolTaskIdList 巡视任务id列表
     * @return 返回PatrolTaskExportExcelDTO对象的列表
     */
    List<PatrolTaskExportExcelDTO> queryPatrolTaskExportExcelDTOByIds(@Param("patrolTaskIdList") List<String> patrolTaskIdList);

    /**
     * 根据巡视任务id，获取巡视任务时长，巡视任务时长获取方法是该巡视任务对应的巡视工单的时长之和
     * @param patrolTaskId 巡视任务id
     * @return 返回巡视任务时长
     */
    Integer getTaskDurationBySumDevice(String patrolTaskId);
}
