package com.aiurt.boot.weeklyplan.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2021/4/2611:18
 */
@Data
public class TeamByIdDTO {
    @ApiModelProperty(value = "班组id")
    private String id;
    @ApiModelProperty(value = "专业id")
    private String deptId;
    @ApiModelProperty(value = "父节点id")
    private String parentId;
    @ApiModelProperty(value = "班组名称")
    private String name;
    @ApiModelProperty(value = "备注")
    private String remark;
    @ApiModelProperty(value = "类型(1组2段3线4其他)")
    private String type;
    @ApiModelProperty(value = "父节点名称")
    private String parentName;
    @ApiModelProperty(value = "所属线路id")
    private String lineId;
    @ApiModelProperty(value = "子系统")
    private String subsystem;
    @ApiModelProperty(value = "考勤地点")
    private String attendancePlace;
    @ApiModelProperty(value = "打卡方式")
    private String clockWay;
    @ApiModelProperty(value = "班组排序")
    private Integer scheduleNumber;

}
