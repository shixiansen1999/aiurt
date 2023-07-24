package com.aiurt.boot.api;

import cn.hutool.core.date.DateTime;
import com.aiurt.boot.dto.UserTeamParameter;
import com.aiurt.boot.dto.UserTeamPatrolDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cgkj0
 */
public interface PatrolApi {
    /**
     * 首页-获取统计日程的巡视完成数
     * @param year
     * @param month
     * @return
     */
    Map<String, Integer> getPatrolFinishNumber(int year, int month, HttpServletRequest request);

    /**
     *  查看当前用户班组的时间范围内的的巡检的工单
     * @param startTime
     * @param endTime
     * @return
     */
    public HashMap<String, String> getUserTask(DateTime startTime, DateTime endTime);

    /**
     * 大屏班组画像巡视工时统计，用户ID:巡视时长 2023-06-12通信6期改为单位秒
     * @param type
     * @param teamId
     * @return
     */
    Map<String, Integer> getPatrolUserHours(int type, String teamId);

    /**
     * 大屏班组画像班组巡视总工时统计，2023-06-12 通信6期改成单位秒
     * @param teamId
     * @return
     */
    Integer getPatrolHours(int type, String teamId);

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

    /**
     * 统计报表-班组维度巡检参数接口返回（按工单数）
     * @param userTeamParameter
     * @return
     */
    Map<String, UserTeamPatrolDTO> getUserTeamParameterDevice(UserTeamParameter userTeamParameter);
    /**
     * 统计报表-人员维度巡检参数接口返回（按工单数）
     * @param userTeamParameter
     * @return
     */
    Map<String, UserTeamPatrolDTO> getUserParameterDevice(UserTeamParameter userTeamParameter);
}
