package com.aiurt.modules.largescream.mapper;


import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.largescream.model.FaultScreenModule;
import org.apache.ibatis.annotations.Param;

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
    List<Fault> queryLargeFaultInformation(@Param("startDate") Date startDate, @Param("endDate") Date endDate,@Param("lineCode") String lineCode);

    /**
     * 故障信息统计详情未解决
     * @param startDate
     * @param endDate
     * @param lineCode
     * @return
     */
    List<Fault> queryLargeFaultInformationUnSo(@Param("startDate") Date startDate, @Param("endDate") Date endDate,@Param("lineCode") String lineCode);

    /**
     * 数据分析-故障数据统计
     * @param lineCode
     * @return
     */
    List<Fault> queryFaultDataInformation(@Param("lineCode") String lineCode);

    /**
     * 故障信息统计当天已解决
     * @param todayStartDate
     * @param todayEndDate
     * @param lineCode
     * @return
     */
    List<Fault> queryLargeFaultInformationTodaySolve(@Param("todayStartDate") Date todayStartDate,@Param("todayEndDate") Date todayEndDate,@Param("lineCode") String lineCode);

    /**
     * 故障数据统计本周已解决
     * @param weekStartDate
     * @param weekEndDate
     * @param lineCode
     * @return
     */
    List<Fault> queryFaultDataInformationWeekSolve(@Param("weekStartDate") Date weekStartDate,@Param("weekEndDate") Date weekEndDate,@Param("lineCode") String lineCode);

    /**
     * 故障信息统计当天新增
     * @param todayStartDate
     * @param todayEndDate
     * @param lineCode
     * @return
     */
    List<Fault> queryLargeFaultInformationTodayAdd(@Param("todayStartDate") Date todayStartDate,@Param("todayEndDate") Date todayEndDate,@Param("lineCode") String lineCode);

    /**
     * 故障数据统计本周新增
     * @param weekStartDate
     * @param weekEndDate
     * @param lineCode
     * @return
     */
    List<Fault> queryFaultDataInformationWeekAdd(@Param("weekStartDate") Date weekStartDate,@Param("weekEndDate") Date weekEndDate,@Param("lineCode") String lineCode);

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
    List<FaultLargeInfoDTO> getLargeFaultInfo(@Param("startDate") Date startDate, @Param("endDate") Date endDate,@Param("lineCode") String lineCode);

    /**
     * 故障数据统计列表
     * @param lineCode
     * @return
     */
    List<FaultDataAnalysisInfoDTO> getLargeFaultDataInfo(@Param("lineCode") String lineCode);


    /**
     * 线路故障列表
     * @param startDate
     * @param endDate
     * @return
     */
    List<Fault> getLargeLineFaultInfo(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    List<FaultSystemTimeDTO> getLargeFaultTime(@Param("month") String month, @Param("lineCode") String lineCode);



    /**
     * 每月故障维修情况统计
     * @param condition
     */
    Integer getYearFault(@Param("condition")FaultDataStatisticsDTO condition);

    /**
     * 所有子系统
     */
    List<String> getAllSystemCode();
}
