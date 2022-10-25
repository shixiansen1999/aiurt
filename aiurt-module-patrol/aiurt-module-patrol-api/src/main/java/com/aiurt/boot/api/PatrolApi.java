package com.aiurt.boot.api;

import com.aiurt.boot.dto.UserTeamParameter;
import com.aiurt.boot.dto.UserTeamPatrolDTO;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author cgkj0
 */ //@FeignClient(value = "jeecg-patrol", fallbackFactory = PatrolFallback.class)
public interface PatrolApi {
    /**
     * 首页-获取统计日程的巡视完成数
     */
    Map<String, Integer> getPatrolFinishNumber(int year, int month);

    /**
     * 查看当前用户，当天的巡检的工单
     */
    public String getUserTask();

    /**
     * 大屏班组画像巡视工时统计，用户ID:巡视时长
     */
    Map<String, BigDecimal> getPatrolUserHours(int type, String teamId);

    /**
     * 大屏班组画像班组巡视总工时统计
     */
    BigDecimal getPatrolHours(int type, String teamId);

    /**
     * 统计报表-人员班组巡检参数接口返回
     * @param userTeamParameter
     * @return
     */
    Map<String, UserTeamPatrolDTO> getUserParameter(UserTeamParameter userTeamParameter);
    /**
     * 统计报表-班组维度巡检参数接口返回
     * @param userTeamParameter
     * @return
     */
    Map<String, UserTeamPatrolDTO> getUserTeamParameter(UserTeamParameter userTeamParameter);
}
