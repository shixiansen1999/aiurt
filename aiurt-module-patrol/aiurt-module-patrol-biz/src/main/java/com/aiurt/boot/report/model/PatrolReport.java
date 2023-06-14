package com.aiurt.boot.report.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.math.BigDecimal;

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
     * 任务id
     */
    private String taskId;
    /**
     * 异常状态：0异常、1正常
     */
    private Integer abnormalState;
    /**
     * 班组名
     */
    @ApiModelProperty("班组名")
    @Excel(name = "班组", width = 15)
    private String orgName;
    /**
     * 班组code
     */

    private String orgId;

    /**
     * 班组code
     */

    private String orgCode;
    /**
     * 人员id
     */

    private String userId;
    /**
     * 巡视工时 2023-06-12通信6期 单位改成秒
     */
    private Integer workHours;
    /**
     * 巡视任务总数
     */
    @ApiModelProperty("巡视任务总数")
    @Excel(name = "巡视任务总数", width = 15)
    private Integer taskTotal;
    /**
     * 已巡视数
     */
    @ApiModelProperty("已巡视数")
    @Excel(name = "已巡视数", width = 15)
    private Integer inspectedNumber;
    /**
     * 未巡视数
     */
    @ApiModelProperty("未巡视数")
    @Excel(name = "未巡视数", width = 15)
    private Integer notInspectedNumber;
    /**
     * 漏巡视数
     */
    @ApiModelProperty("漏巡视数")
    @Excel(name = "漏巡视数", width = 15)
    private float missInspectedNumber;
    /**
     * 平均每周漏巡视数
     */
    @ApiModelProperty("平均每周漏巡视数")
    @Excel(name = "平均每周漏巡视数", width = 15)
    private BigDecimal awmPatrolNumber;
    /**
     * 平均每月漏巡视数
     */
    @ApiModelProperty("平均每月漏巡视数")
    @Excel(name = "平均每月漏巡视数", width = 15)
    private BigDecimal ammPatrolNumber;
    /**
     * 完成率
     */
    @ApiModelProperty("完成率")
    @Excel(name = "完成率", width = 15)
    private BigDecimal completionRate;
    /**
     * 异常数量
     */
    @ApiModelProperty("异常数量")
    @Excel(name = "异常数量", width = 15)
    private Integer abnormalNumber;
    /**
     * 故障数量
     */
    @ApiModelProperty("故障数量")
    @Excel(name = "故障数量", width = 15)
    private Integer faultNumber;
}


