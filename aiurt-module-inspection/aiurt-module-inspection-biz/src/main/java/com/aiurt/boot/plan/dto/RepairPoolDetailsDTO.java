package com.aiurt.boot.plan.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title: 检修计划详情DTO
 * @Description:
 * @date 2022/6/239:01
 */
@Data
public class RepairPoolDetailsDTO {

    @ApiModelProperty(value = "检修计划名称")
    private java.lang.String name;

    @ApiModelProperty(value = "检修计划单号")
    private java.lang.String code;

    @ApiModelProperty(value = "所属周")
    private java.lang.Integer weeks;

    @ApiModelProperty(value = "适用站点")
    private String stationName;

    @ApiModelProperty(value = "组织结构名称")
    private String orgName;

    @ApiModelProperty(value = "检修周期类型名称")
    private String typeName;

    @ApiModelProperty(value = "适用专业名称")
    private String majorName;

    @ApiModelProperty(value = "适用专业子系统名称")
    private String subsystemName;

    @ApiModelProperty(value = "状态名称")
    private String statusName;

    @ApiModelProperty(value = "年份")
    private Integer year;

    @ApiModelProperty(value = "所属策略")
    private java.lang.String strategy;

    @ApiModelProperty(value = "是否委外")
    private String isManual;

    @ApiModelProperty(value = "是否审核")
    private String isConfirm;

    @ApiModelProperty(value = "是否验收")
    private String isReceipt;

    @ApiModelProperty(value = "作业类型（A1不用计划令,A2,A3,B1,B2,B3）")
    private String workType;

    @ApiModelProperty(value = "退回理由")
    private String remark;

}
