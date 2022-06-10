package com.aiurt.boot.modules.statistical.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description: 本周检修统计
 * @author: niuzeyu
 * @date: 2022年01月21日 13:36
 */
@Data
public class RepairStatisticVo {
    //计划检修数
    private Integer repairNum;
    //修完成数
    private Integer completeNum;
    //本周漏检数
    private Integer weeklyIgnoreNum;
    //今日检测数
    private Integer intradayRepairNum;

    List<RepairTaskVo> repairTaskList;


}
