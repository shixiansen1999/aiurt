package com.aiurt.modules.largescream.mapper;


import com.aiurt.modules.fault.dto.FaultLargeCountDTO;
import com.aiurt.modules.fault.dto.FaultLargeInfoDTO;
import com.aiurt.modules.fault.dto.FaultLargeLineInfoDTO;
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
    List<Fault> queryLargeFaultInformation(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("lineCode") String lineCode);

    /**
     * 故障信息统计当天已解决
     * @param todayStartDate
     * @param todayEndDate
     * @param lineCode
     * @return
     */
    List<Fault> queryLargeFaultInformationTodaySolve(@Param("todayStartDate") String todayStartDate,@Param("todayEndDate") String todayEndDate,@Param("lineCode") String lineCode);

    /**
     * 故障信息统计当天新增
     * @param todayStartDate
     * @param todayEndDate
     * @param lineCode
     * @return
     */
    List<Fault> queryLargeFaultInformationTodayAdd(@Param("todayStartDate") String todayStartDate,@Param("todayEndDate") String todayEndDate,@Param("lineCode") String lineCode);

    /**
     * 故障信息统计列表
     * @param startDate
     * @param endDate
     * @param lineCode
     * @return
     */
    List<FaultLargeInfoDTO> getLargeFaultInfo(@Param("startDate") String startDate, @Param("endDate") String endDate,@Param("lineCode") String lineCode);


    /**
     * 线路故障列表
     * @param startDate
     * @param endDate
     * @return
     */
    List<Fault> getLargeLineFaultInfo(@Param("startDate") String startDate, @Param("endDate") String endDate);
}
