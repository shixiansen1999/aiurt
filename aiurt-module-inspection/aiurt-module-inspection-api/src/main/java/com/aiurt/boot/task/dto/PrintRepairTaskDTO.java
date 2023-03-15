package com.aiurt.boot.task.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author lkj
 */
@Data

public class PrintRepairTaskDTO {

    @ApiModelProperty(value = "检修班组")
    private String orgName;
    @ApiModelProperty(value = "检修日期")
    private String repairTime;
    @ApiModelProperty(value = "检修地点")
    private String stationName;
    @ApiModelProperty(value = "计划检修时间")
    private String planRepairTime;
    @ApiModelProperty(value = "检修人员")
    private String repairPeople;
    @ApiModelProperty(value = "检修开始时间")
    private String startRepairTime;
    @ApiModelProperty(value = "检修周期类型，0周检、1月检、2双月检、3季检、4半年检、5年检")
    private String type;
    @ApiModelProperty(value = "检修内容")
    private List<String> content;
    @ApiModelProperty(value = "检修记录")
    private String repairRecord;
    @ApiModelProperty(value = "处理结果")
    private String repairResult;
    @ApiModelProperty(value = "备件更换")
    private String changeDevices;
    @ApiModelProperty(value = "附件信息")
    private String rel;
    @ApiModelProperty(value = "作业类型（A1不用计划令,A2,A3,B1,B2,B3）")
    private String workType;
    @ApiModelProperty(value = "计划令编码")
    private String planOrderCode;
    @ApiModelProperty(value = "计划令图片")
    private String planOrderCodeUrl;
    @ApiModelProperty(value = "提交人")
    private String submitUserName;
    @ApiModelProperty(value = "提交时间，精确到秒")
    private String submitTime;
}
