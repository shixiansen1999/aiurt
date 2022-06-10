package com.aiurt.boot.modules.statistical.vo;

import com.aiurt.boot.modules.patrol.vo.PatrolTaskVO;
import lombok.Data;

import java.util.List;

/**
 * @author: niuzeyu
 * @date: 2022年01月20日 13:45
 */
@Data
public class PatrolStatisticVo {
    //周计划巡检数
    private Integer weeklyPatrolNum;
    //周巡视完成数
    private Integer weeklyCompleteNum;
    //周漏检数
    private Integer weeklyIgnoreNum;
    //周巡视异常数
    private Integer exceptionPatrolNum;
    //今日巡视数
    private Integer intradayPatrolNum;
    //今日巡视完成数
    private Integer intradayCompleteNum;

    List<PatrolTaskVO> patrolTaskVoList;

    List<PatrolTaskStatisticVo> patrolTaskStatisticVoList;

}
