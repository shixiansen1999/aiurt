package com.aiurt.boot.report.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.boot.report.model
 * @className: FailureReport
 * @author: life-0
 * @date: 2022/9/19 15:48
 * @description: TODO
 * @version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "FailureReport", description = "统计报表-故障列表-子系统维度")
public class FailureReport {
    /**
     * 班组名称
     */
    @ApiModelProperty(value = "班组名称")
    private String orgName;
    private String orgCode;
    /**
     * 子系统名称
     */
    @ApiModelProperty(value = "子系统名称")
    private String systemName;

    private String code;
    /**
     * 故障总数
     */
    @ApiModelProperty(value = "故障总数")
    private Integer failureNum;
    /**
     * 自检
     */
    @ApiModelProperty(value = "自检")
    private Integer selfInspectionNum;
    /**
     * 报修
     */
    @ApiModelProperty(value = "报修")
    private Integer repairNum;
    private Double lastMonthNum;
    private Double lastYearNum;
    private Double monthNum;
    private Double yearNum;
    @ApiModelProperty(value = "与上个月数据对比增加")
    private String lastMonthStr;
    /**
     * 与上个年数据对比增加
     */
    @ApiModelProperty(value = "与上个年数据对比增加")
    private String lastYearStr;
    /**
     * 已解决数
     */
    @ApiModelProperty(value = "已解决数")
    private Integer resolvedNum;
    /**
     * 待解决数
     */
    @ApiModelProperty(value = "待解决数")
    private Integer unResolvedNum;
    /**
     * 平均响应时间
     */
    @ApiModelProperty(value = "平均响应时间")
    private Integer averageResponse;
    /**
     * 平均解决时间
     */
    @ApiModelProperty(value = "平均解决时间")
    private Integer averageResolution;
}
