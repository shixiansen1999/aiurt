package com.aiurt.modules.common.api;

import cn.hutool.core.date.DateTime;
import com.aiurt.modules.fault.dto.FaultReportDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 日待办故障数
 *
 * @author: qkx
 * @date: 2022-09-09 15:11
 */
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
     * 获取当前用户在时间范围内的故障维修单
     * @param startTime
     * @param endTime
     * @return
     */
    HashMap<String, String> getFaultTask(DateTime startTime, DateTime endTime);

    /**
     * 大屏班组画像维修工时统计，用户ID:维修时长
     * @param type
     * @param teamId
     * @return
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
    Map<String, FaultReportDTO> getFaultUserReport(List<String> teamId,String startTime,String endTime,String userId,List<String> userIds);
}
