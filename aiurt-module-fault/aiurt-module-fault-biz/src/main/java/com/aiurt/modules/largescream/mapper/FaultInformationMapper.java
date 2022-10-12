package com.aiurt.modules.largescream.mapper;


import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.largescream.model.FaultDurationTask;
import com.aiurt.modules.largescream.model.FaultScreenModule;
import org.apache.ibatis.annotations.Param;
import org.jeecg.common.system.vo.LoginUser;

import java.util.Date;
import java.util.List;


public interface FaultInformationMapper {

    /**
     * 故障信息统计
     * @param startDate
     * @param endDate
     * @param lineCode
     * @return
     */
    List<Fault> queryLargeFaultInformation(@Param("startDate") Date startDate, @Param("endDate") Date endDate,@Param("lineCode") String lineCode,@Param("majors") List<String> majors);

    /**
     * 故障信息统计详情未解决
     * @param startDate
     * @param endDate
     * @param lineCode
     * @return
     */
    List<Fault> queryLargeFaultInformationUnSo(@Param("startDate") Date startDate, @Param("endDate") Date endDate,@Param("lineCode") String lineCode);

    /**
     * 数据分析-故障数据统计总数和未解决
     * @param lineCode
     * @return
     */
    List<Fault> queryFaultDataInformation(@Param("lineCode") String lineCode,@Param("majors") List<String> majors);

    /**
     * 故障信息统计当天已解决
     * @param todayStartDate
     * @param todayEndDate
     * @param lineCode
     * @return
     */
    List<Fault> queryLargeFaultInformationTodaySolve(@Param("todayStartDate") Date todayStartDate,@Param("todayEndDate") Date todayEndDate,@Param("lineCode") String lineCode,@Param("majors") List<String> majors);

    /**
     * 故障数据统计本周已解决
     * @param weekStartDate
     * @param weekEndDate
     * @param lineCode
     * @return
     */
    List<Fault> queryFaultDataInformationWeekSolve(@Param("weekStartDate") Date weekStartDate,@Param("weekEndDate") Date weekEndDate,@Param("lineCode") String lineCode,@Param("majors") List<String> majors);

    /**
     * 故障信息统计当天新增
     * @param todayStartDate
     * @param todayEndDate
     * @param lineCode
     * @return
     */
    List<Fault> queryLargeFaultInformationTodayAdd(@Param("todayStartDate") Date todayStartDate,@Param("todayEndDate") Date todayEndDate,@Param("lineCode") String lineCode,@Param("majors") List<String> majors);

    /**
     * 故障数据统计本周新增
     * @param weekStartDate
     * @param weekEndDate
     * @param lineCode
     * @return
     */
    List<Fault> queryFaultDataInformationWeekAdd(@Param("weekStartDate") Date weekStartDate,@Param("weekEndDate") Date weekEndDate,@Param("lineCode") String lineCode,@Param("majors") List<String> majors);

    /**
     * 故障统计详情
     * @param faultScreenModule
     * @return
     */
    List<FaultLargeInfoDTO> getLargeFaultDatails(@Param("condition") FaultScreenModule faultScreenModule);

    /**
     * 故障数据统计详情
     * @param faultScreenModule
     * @return
     */
    List<FaultLargeInfoDTO> getLargeFaultDataDatails(@Param("condition") FaultScreenModule faultScreenModule);


    /**
     * 故障信息统计列表
     * @param startDate
     * @param endDate
     * @param lineCode
     * @return
     */
    List<FaultLargeInfoDTO> getLargeFaultInfo(@Param("startDate") Date startDate, @Param("endDate") Date endDate,@Param("lineCode") String lineCode,@Param("majors") List<String> majors);

    /**
     * 故障数据统计列表
     * @param lineCode
     * @return
     */
    List<FaultDataAnalysisInfoDTO> getLargeFaultDataInfo(@Param("startDate") Date startDate, @Param("endDate") Date endDate,@Param("lineCode") String lineCode,@Param("majors") List<String> majors);


    /**
     * 线路故障列表
     * @param startDate
     * @param endDate
     * @return
     */
    List<Fault> getLargeLineFaultInfo(@Param("startDate") Date startDate, @Param("endDate") Date endDate,@Param("majors") List<String> majors);

    /**
     * 获取子系统下故障维修时长
     * @param month
     * @param lineCode
     * @return
     */
    List<FaultSystemTimeDTO> getLargeFaultTime(@Param("month") String month, @Param("lineCode") String lineCode,@Param("majors") List<String> majors);

    /**
     * 按系统分类获取子系统下故障维修时长总数
     * @param startDate
     * @param endDate
     * @return
     */
    List<FaultSystemTimesDTO> getSystemFaultSum(@Param("startDate") Date startDate, @Param("endDate") Date endDate,@Param("majors") List<String> majors);


    /**
     * 按系统分类获取专业下设备数
     * @return
     */
    List<FaultSystemDeviceSumDTO> getSystemDeviceSum(@Param("majors") List<String> majors);


    /**
     *故障超时等级详情
     * @param level
     * @param startDate
     * @param endDate
     * @param lineCode
     * @return
     */
    List<FaultTimeoutLevelDTO> getFaultData(@Param("level") Integer level,@Param("startDate") Date startDate, @Param("endDate") Date endDate ,@Param("lineCode") String lineCode,@Param("majors") List<String> majors);


    /**
     * 每月故障维修情况统计
     * @param condition
     */
    Integer getYearFault(@Param("condition")FaultDataStatisticsDTO condition);

    /**
     * 拥有的专业下的所有子系统
     */
    List<FaultDataStatisticsDTO> getAllSystemCode(@Param("majorCodes") List<String> majorCodes);

    /**
     * 班组画像维修工时
     * @param startTime
     * @param endTime
     * @return
     */
    List<FaultDurationTask> getFaultUserDuration(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 参与人维修工时
     * @param startTime
     * @param endTime
     * @return
     */
    List<FaultDurationTask> getFaultParticipantsDuration(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 指派人维修工时按任务id计算
     * @param startTime
     * @param endTime
     * @param userList
     * @return
     */
    List<FaultDurationTask> getFaultByIdDuration(@Param("startTime")Date startTime, @Param("endTime")Date endTime,@Param("userList")List<LoginUser> userList );

    List<FaultDurationTask> getParticipantsDuration(@Param("startTime")Date startTime, @Param("endTime")Date endTime,@Param("userList")List<LoginUser> userList );




    /**
     * 班组报表工时
     * @param orgCodes
     * @param orgIds
     * @param teamId
     * @param startTime
     * @param endTime
     * @return
     */
    List<FaultReportDTO> getFaultOrgReport(@Param("teamId") List<String> teamId,@Param("startTime") String startTime,@Param("endTime") String endTime, @Param("orgCodes")List<String> orgCodes,@Param("orgIds") List<String> orgIds);

    /**
     * 查询配合工时
     * @param orgId
     * @param startTime
     * @param endTime
     * @return
     */
    List<String> getConstructionHours(@Param("orgId")String orgId, @Param("startTime") String startTime,@Param("endTime") String endTime);

    /**
     * 人员报表工时
     * @param teamId
     * @param startTime
     * @param endTime
     * @param orgCodes
     * @return
     */
    List<FaultReportDTO> getFaultUserReport(@Param("teamId") List<String> teamId,@Param("startTime") String startTime,@Param("endTime") String endTime, @Param("orgCodes")List<String> orgCodes,@Param("userId") String userId);

    /**
     * 查询配合工时
     * @param userId
     * @param startTime
     * @param endTime
     * @return
     */
    List<String> getUserConstructionHours(@Param("userId") String userId,@Param("startTime") String startTime,@Param("endTime")String endTime);

    /**
     *
     * @param userId
     * @param startTime
     * @param endTime
     * @return
     */
    FaultReportDTO getUserConstructorsNum(@Param("userId") String userId,@Param("startTime") String startTime,@Param("endTime")String endTime);

    /**
     * 指派人
     * @param orgId
     * @param startTime
     * @param endTime
     * @return
     */
    List<UserTimeDTO> getUserTime(@Param("orgId") String orgId,@Param("startTime") String startTime,@Param("endTime")String endTime);

    /**
     * 参与人
     * @param orgId
     * @param startTime
     * @param endTime
     * @return
     */
    List<UserTimeDTO> getAccompanyTime(@Param("orgId") String orgId,@Param("startTime") String startTime,@Param("endTime") String endTime);

    /**
     *
     * @param userId
     * @param startTime
     * @param endTime
     * @return
     */
    Long getUserTimes(@Param("userId") String userId,@Param("startTime") String startTime,@Param("endTime") String endTime);
}
