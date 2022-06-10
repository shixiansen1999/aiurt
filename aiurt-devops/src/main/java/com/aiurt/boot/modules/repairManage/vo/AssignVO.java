package com.aiurt.boot.modules.repairManage.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author qian
 * @version 1.0
 * @date 2021/11/28 14:06
 */
@Data
public class AssignVO {
    @NotNull
    @ApiModelProperty(value = "检修池ids",required = true)
    private String ids;
    @NotNull
    @ApiModelProperty(value = "指派人员ids",required = true)
    private String userIds;
//    @NotNull
//    @ApiModelProperty(value = "指派人员names",required = true)
//    private String userNames;
    @NotNull
    @ApiModelProperty(value = "站点id",required = true)
    private String stationId;
    @NotNull
    @ApiModelProperty(value = "计划检修日期",required = true)
    private String repairTime;
    @NotNull
    @ApiModelProperty(value = "作业类型",required = true)
    private String workType;
    @ApiModelProperty(value = "计划令编码")
    private String planOrderCode;
    @ApiModelProperty(value = "计划令图片")
    private String planOrderCodeUrl;

}
