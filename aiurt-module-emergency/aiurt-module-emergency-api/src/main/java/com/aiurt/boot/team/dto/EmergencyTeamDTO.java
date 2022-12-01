package com.aiurt.boot.team.dto;
/**
 * @Description: emergency_team
 * @Author: LKJ
 * @Date:   2022-11-30
 * @Version: V1.0
 */

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
public class EmergencyTeamDTO {

    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    /**所属专业*/
    @Excel(name = "所属专业", width = 15)
    @ApiModelProperty(value = "所属专业")
    private String majorCode;
    @ApiModelProperty(value = "所属专业名称")
    @TableField(exist = false)
    private String majorName;
    /**所属部门*/
    @Excel(name = "所属部门", width = 15)
    @ApiModelProperty(value = "所属部门")
    private String orgCode;
    @ApiModelProperty(value = "所属部门名称")
    @TableField(exist = false)
    private String orgName;
    /**应急队伍名称*/
    @Excel(name = "应急队伍名称", width = 15)
    @ApiModelProperty(value = "应急队伍名称")
    private String emergencyTeamname;
    /**应急队伍编号*/
    @Excel(name = "应急队伍编号", width = 15)
    @ApiModelProperty(value = "应急队伍编号")
    private String emergencyTeamcode;
    /**队伍人数*/
    @Excel(name = "队伍人数", width = 15)
    @ApiModelProperty(value = "队伍人数")
    private Integer peopleNum;
    /**当班人数*/
    @Excel(name = "当班人数", width = 15)
    @ApiModelProperty(value = "当班人数")
    private Integer ondutyNum;
    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;
    /**线路编码*/
    @Excel(name = "线路编码", width = 15)
    @ApiModelProperty(value = "线路编码")
    private String lineCode;
    @ApiModelProperty(value = "线路名称")
    @TableField(exist = false)
    private String lineName;
    /**站点编码*/
    @Excel(name = "站点编码", width = 15)
    @ApiModelProperty(value = "站点编码")
    private String stationCode;
    @ApiModelProperty(value = "站点名称")
    @TableField(exist = false)
    private String stationName;
    /**驻扎地编码*/
    @Excel(name = "驻扎地编码", width = 15)
    @ApiModelProperty(value = "驻扎地编码")
    private String positionCode;
    @ApiModelProperty(value = "驻扎地名称")
    @TableField(exist = false)
    private String positionName;
    /**工区编码*/
    @Excel(name = "工区编码", width = 15)
    @ApiModelProperty(value = "工区编码")
    private String workareaCode;
    @ApiModelProperty(value = "工区名称")
    @TableField(exist = false)
    private String workareaName;
    /**负责人id*/
    @Excel(name = "负责人id", width = 15)
    @ApiModelProperty(value = "负责人id")
    private String managerId;
    @ApiModelProperty(value = "负责人姓名")
    @TableField(exist = false)
    private String managerName;
    /**联系电话*/
    @Excel(name = "联系电话", width = 15)
    @ApiModelProperty(value = "联系电话")
    private String managerPhone;
    /**删除状态(0-正常,1-已删除)*/
    @Excel(name = "删除状态(0-正常,1-已删除)", width = 15)
    @ApiModelProperty(value = "删除状态(0-正常,1-已删除)")
    private Integer delFlag;
}
