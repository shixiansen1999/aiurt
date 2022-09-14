package com.aiurt.boot.screen.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "ScreenStatisticsTask", description = "大屏巡视模块-巡视数据统计任务列表对象")
public class ScreenStatisticsTask {
    /**
     * 任务编号
     */
    @ApiModelProperty(value = "任务编号")
    private String code;
    /**
     * 任务名称
     */
    @ApiModelProperty(value = "任务名称")
    private String name;
    /**
     * 巡检结果提交时间(yyyy-MM-dd HH:mm:ss)
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "巡检结果提交时间,格式yyyy-MM-dd HH:mm:ss")
    private java.util.Date submitTime;
    /**
     * 是否需要审核：0否、1是
     */
    @ApiModelProperty(value = "是否需要审核：0否、1是")
    private java.lang.Integer auditor;
    /**
     * 审核时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "审核时间(yyyy-MM-dd HH:mm:ss)")
    private java.util.Date auditorTime;
    /**
     * 任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成
     */
    @ApiModelProperty(value = "任务状态：0待指派、1待确认、2待执行、3已退回、4执行中、5已驳回、6待审核、7已完成")
    private Integer status;
    /**
     * 任务状态字典名
     */
    @ApiModelProperty(value = "任务状态字典名")
    private String statusName;
    /**
     * 异常状态：0异常、1正常
     */
    @ApiModelProperty(value = "异常状态：0异常、1正常")
    private Integer abnormalState;
    /**
     * 异常状态字典名
     */
    @ApiModelProperty(value = "异常状态字典名")
    private String abnormalStateName;
    /**
     * 漏检状态:0未漏检，1已漏检
     */
    @ApiModelProperty(value = "漏检状态:0未漏检，1已漏检")
    private Integer omitStatus;
    /**
     * 漏检状态字典名
     */
    @ApiModelProperty(value = "漏检状态字典名")
    private String omitStatusName;
    /**
     * 站点信息
     */
    @ApiModelProperty(value = "站点信息")
    private String stationInfo;
    /**
     * 巡视人员信息
     */
    @ApiModelProperty(value = "巡视人员信息")
    private String userInfo;
}
