package com.aiurt.boot.plan.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

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
    private java.lang.String weeks;

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

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "状态名称")
    private String statusName;

    @ApiModelProperty(value = "年份")
    private Integer year;

    @ApiModelProperty(value = "所属策略")
    private java.lang.String strategy;

//    @ApiModelProperty(value = "是否委外")
//    private String isManual;

    @ApiModelProperty(value = "是否委外")
    private String isOutsource;

    @ApiModelProperty(value = "是否审核")
    private String isConfirm;

    @ApiModelProperty(value = "是否验收")
    private String isReceipt;

    @ApiModelProperty(value = "作业类型")
    private Integer workType;

    @ApiModelProperty(value = "作业类型名称")
    private String workTypeName;

    @ApiModelProperty(value = "退回理由")
    private String remark;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "开始时间")
    private java.util.Date startTime;

    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "结束时间")
    private java.util.Date endTime;
}
