package com.aiurt.modules.common.api;

import com.aiurt.modules.fault.dto.FaultReportDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public interface DailyFaultApi {
    /**
     * 日待办故障数
     * @param year
     * @param month
     * @return
     */
    Map<String, Integer> getDailyFaultNum(Integer year, Integer month);

    /**
     * 获取当前用户，当天的故障维修单
     * @return
     */
    String getFaultTask();

    /**
     * 大屏班组画像维修工时统计，用户ID:维修时长
     */
    Map<String, BigDecimal> getFaultUserHours(int type, String teamId);

    /**
     * 大屏班组画像维修总工时统计
     * @param type
     * @param teamId
     * @return
     */
    BigDecimal getFaultHours(int type, String teamId);

    /**
     * 报表班组工时统计
     * @param teamId
     * @param startTime
     * @param endTime
     * @return
     */
    Map<String, FaultReportDTO> getFaultOrgReport(List<String> teamId,String startTime,String endTime);

    /**
     * 报表人员工时统计
     * @param teamId
     * @param startTime
     * @param endTime
     * @param userId
     * @return
     */
    Map<String, FaultReportDTO> getFaultUserReport(List<String> teamId,String startTime,String endTime,String userId);
}
