package com.aiurt.boot.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/9/19
 * @desc
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class PatrolReport {

    /**
     * 班组
     */
    private String orgName;
    /**
     * 班组
     */
    private String orgCode;
    /**
     * 巡视任务总数
     */
    private Integer taskTotal;
    /**
     * 已巡视数
     */
    private Integer inspectedNumber;
    /**
     * 未巡视数
     */
    private Integer notInspectedNumber;
    /**
     * 漏巡视数
     */
    private Integer missInspectedNumber;
    /**
     * 平均每周漏巡视数
     */
    private Integer awmPatrolNumber;
    /**
     * 平均每月漏巡视数
     */
    private Integer ammPatrolNumber;
    /**
     * 完成率
     */
    private String completionRate;
    /**
     * 异常数量
     */
    private Integer abnormalNumber;
    /**
     * 故障数量
     */
    private Integer faultNumber;
}


