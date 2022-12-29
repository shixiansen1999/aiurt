package com.aiurt.boot.team.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author lkj
 */
@Data
public class TeamModel {

    @Excel(name = "序号", width = 15)
    private String sort;

    /**所属专业*/
    @Excel(name = "所属专业", width = 15)
    @ApiModelProperty(value = "所属专业")
    private String majorName;
    /**所属部门*/
    @Excel(name = "所属部门", width = 15)
    @ApiModelProperty(value = "所属部门名称")
    private String orgName;
    /**应急队伍名称*/
    @Excel(name = "应急队伍名称", width = 15)
    @ApiModelProperty(value = "应急队伍名称")
    private String emergencyTeamname;
    /**应急队伍编号*/
    @Excel(name = "应急队伍编号", width = 15)
    @ApiModelProperty(value = "应急队伍编号")
    private String emergencyTeamcode;

    /**负责人姓名*/
    @Excel(name = "负责人", width = 15)
    @ApiModelProperty(value = "负责人姓名")
    private String managerName;
    /**负责人工号*/
    @ApiModelProperty(value = "负责人工号")
    private String managerWorkNo;
    /**联系电话*/
    @Excel(name = "联系电话", width = 15)
    @ApiModelProperty(value = "联系电话")
    private String managerPhone;
    /**线路名称*/
    @Excel(name = "线路", width = 15)
    @ApiModelProperty(value = "线路名称")
    private String lineName;
    /**站点名称*/
    @Excel(name = "站点", width = 15)
    @ApiModelProperty(value = "站点名称")
    private String stationName;
    /**驻扎地名称*/
    @Excel(name = "驻扎地", width = 15)
    @ApiModelProperty(value = "驻扎地名称")
    private String positionName;
    /**工区名称*/
    @Excel(name = "工区", width = 15)
    @ApiModelProperty(value = "工区名称")
    private String workAreaName;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;

    private String mistake;

}
