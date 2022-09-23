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
 * @className: FailureOrgReport
 * @author: life-0
 * @date: 2022/9/23 10:55
 * @description: TODO
 * @version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ApiModel(value = "FailureOrgReport", description = "统计报表-故障列表-班组维度")
public class FailureOrgReport {
    private String id;
    /**
     * 班组名称
     */
    @ApiModelProperty(value = "班组名称")
    @Excel(name = "班组", width = 15)
    private String orgName;
    private String orgCode;


    private String code;
    /**
     * 故障总数
     */
    @ApiModelProperty(value = "故障总数")
    @Excel(name = "故障总数", width = 15)
    private Integer failureNum;
    /**
     * 自检
     */
    @ApiModelProperty(value = "自检")
    @Excel(name = "自检", width = 15)
    private Integer selfInspectionNum;
    /**
     * 报修
     */
    @ApiModelProperty(value = "报修")
    @Excel(name = "报修", width = 15)
    private Integer repairNum;
    private Double lastMonthNum;
    private Double lastYearNum;
    private Double monthNum;
    private Double yearNum;
    @ApiModelProperty(value = "与上个月数据对比增加")
    @Excel(name = "与上个月数据对比增加", width = 15)
    private String lastMonthStr;
    /**
     * 与上个年数据对比增加
     */
    @ApiModelProperty(value = "与上个年数据对比增加")
    @Excel(name = "与上个年数据对比增加", width = 15)
    private String lastYearStr;
    /**
     * 已解决数
     */
    @ApiModelProperty(value = "已解决数")
    @Excel(name = "已解决数", width = 15)
    private Integer resolvedNum;
    /**
     * 待解决数
     */
    @ApiModelProperty(value = "待解决数")
    @Excel(name = "待解决数", width = 15)
    private Integer unResolvedNum;
    /**
     * 平均响应时间
     */
    @ApiModelProperty(value = "平均响应时间")
    @Excel(name = "平均响应时间", width = 15)
    private Integer averageResponse;
    /**
     * 平均解决时间
     */
    @ApiModelProperty(value = "平均解决时间")
    @Excel(name = "平均解决时间", width = 15)
    private Integer averageResolution;
}
