package com.aiurt.boot.report.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

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
    private String id;

    /**
     * 子系统名称
     */
    @ApiModelProperty(value = "子系统名称")
    private String systemName;

    /**名称*/
    @Excel(name = "子系统分类", width = 15)
    @ApiModelProperty(value = "简称")
    private String shortenedForm;

    private String code;
    /**
     * 故障总数
     */
    @ApiModelProperty(value = "故障总数")
    @Excel(name = "故障总数", width = 15,groupName = "故障数")
    private Integer failureNum;
    /**
     * 自检
     */
    @ApiModelProperty(value = "自检")
    @Excel(name = "自检", width = 15,groupName = "故障数")
    private Integer selfInspectionNum;
    /**
     * 报修
     */
    @ApiModelProperty(value = "报修")
    @Excel(name = "报修", width = 15,groupName = "故障数")
    private Double repairNum;
    private Double lastMonthNum;
    private Double lastYearNum;
    private Double lastWeekNum;
    private Double thisWeekNum;
    private Double monthNum;
    private Double yearNum;
    @ApiModelProperty(value = "与上个月数据对比增加")
/*    @Excel(name = "与上个月数据对比增加", width = 15,groupName = "故障数")*/
    private String lastMonthStr;
    /**
     * 与上个年数据对比增加
     */
    @ApiModelProperty(value = "与上个年数据对比增加")
    /*@Excel(name = "与上个年数据对比增加", width = 15,groupName = "故障数")*/
    private String lastYearStr;

    @Excel(name = "与上个周对比(%)", width = 15)
    private String lastWeekStr;

    /**
     * 已解决数
     */
    @ApiModelProperty(value = "已解决数")
    @Excel(name = "已解决数", width = 15,groupName = "解决数")
    private Integer resolvedNum;
    /**
     * 待解决数
     */
    @ApiModelProperty(value = "待解决数")
    @Excel(name = "待解决数", width = 15,groupName = "解决数")
    private Integer unResolvedNum;
    /**
     * 平均响应时间
     */
    @ApiModelProperty(value = "平均响应时间")
    @Excel(name = "故障平均响应时间(秒)", width = 15,groupName = "平均时长")
    private Integer averageResponse;
    /**
     * 平均解决时间
     */
    @ApiModelProperty(value = "平均解决时间")
    @Excel(name = "故障平均解决时间（秒）", width = 15,groupName = "平均时长")
    private Integer averageResolution;
}
