package com.aiurt.modules.largescream.mapper;


import com.aiurt.modules.fault.dto.FaultDataStatisticsDTO;
import com.aiurt.modules.fault.dto.FaultLargeInfoDTO;
import com.aiurt.modules.fault.dto.FaultLargeLineInfoDTO;
import com.aiurt.modules.fault.dto.FaultSystemTimeDTO;
import com.aiurt.modules.fault.entity.Fault;
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
     * 故障信息统计当天已解决
     * @param todayStartDate
     * @param todayEndDate
     * @param lineCode
     * @return
     */
    List<Fault> queryLargeFaultInformationTodaySolve(@Param("todayStartDate") Date todayStartDate,@Param("todayEndDate") Date todayEndDate,@Param("lineCode") String lineCode);

    /**
     * 故障信息统计当天新增
     * @param todayStartDate
     * @param todayEndDate
     * @param lineCode
     * @return
     */
    List<Fault> queryLargeFaultInformationTodayAdd(@Param("todayStartDate") Date todayStartDate,@Param("todayEndDate") Date todayEndDate,@Param("lineCode") String lineCode);

    /**
     * 故障信息统计列表
     * @param startDate
     * @param endDate
     * @param lineCode
     * @return
     */
    List<FaultLargeInfoDTO> getLargeFaultInfo(@Param("startDate") Date startDate, @Param("endDate") Date endDate,@Param("lineCode") String lineCode);


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
