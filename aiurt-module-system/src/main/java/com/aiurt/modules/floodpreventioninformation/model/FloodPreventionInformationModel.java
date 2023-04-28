package com.aiurt.modules.floodpreventioninformation.model;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import cn.afterturn.easypoi.excel.annotation.Excel;

/**
 * @Description: flood_prevention_information
 * @Author: zwl
 * @Date:   2023-04-25
 * @Version: V1.0
 */
@Data
public class FloodPreventionInformationModel {

    /**主键id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "主键id")
    private String id;

    /**站点编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "站点编码")
    private String stationCode;


    /**站点名称*/
    @Excel(name = "站点名称")
    @TableField(exist = false)
    @ApiModelProperty(value = "站点名称")
    private String stationName;


    /**部门编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "部门编码")
    private String orgCode;

    /**部门名称*/
    @Excel(name = "所属部门")
    @ApiModelProperty(value = "所属部门")
    @TableField(exist = false)
    private String orgName;


    /**站长*/
    @Excel(name = "站长")
    @TableField(exist = false)
    @ApiModelProperty(value = "站长")
    private String masterName;


    /**包保负责人*/
    @Excel(name = "包保负责人")
    @TableField(exist = false)
    @ApiModelProperty(value = "包保负责人")
    private String insuranceName;


    /**应急队伍人数*/
    @Excel(name = "应急队伍人数")
    @TableField(exist = false)
    @ApiModelProperty(value = "应急队伍人数")
    private String emergencyPersonnel;


    /**应急队伍负责人Id*/
    @ApiModelProperty(value = "应急队伍负责人Id")
    @TableField(exist = false)
    private String emergencyPeopleId;


    /**应急队伍负责人*/
    @Excel(name = "应急队伍负责人")
    @ApiModelProperty(value = "应急队伍负责人")
    @TableField(exist = false)
    private String emergencyPeopleName;


    /**后备人数*/
    @Excel(name = "后备人数")
    @ApiModelProperty(value = "后备人数")
    @TableField(exist = false)
    private String reservePersonnel;


    /**周边是否存在排水不畅*/
    @Excel(name = "周边是否存在排水不畅")
    @ApiModelProperty(value = "周边是否存在排水不畅")
    @TableField(exist = false)
    private String peripheryWaterName;

    /**周边是否存在排水不畅*/
    @ApiModelProperty(value = "周边是否存在排水不畅（1是，0否）")
    @TableField(exist = false)
    private Long peripheryWater;

    /**周边是否存在工地*/
    @Excel(name = "周边是否存在工地")
    @ApiModelProperty(value = "周边是否存在工地")
    @TableField(exist = false)
    private String peripheryGroundsName;

    /**周边是否存在工地*/
    @ApiModelProperty(value = "周边是否存在工地（1是，0否）")
    @TableField(exist = false)
    private Long peripheryGrounds;


    /**防汛物资配备所在位置*/
    @Excel(name = "防汛物资配备所在位置")
    @ApiModelProperty(value = "防汛物资配备所在位置")
    @TableField(exist = false)
    private String materialLocation;


    /**防汛出入口*/
    @Excel(name = "防汛出入口")
    @ApiModelProperty(value = "防汛出入口")
    @TableField(exist = false)
    private String entrance;


    /**防汛等级*/
    @ApiModelProperty(value = "防汛等级")
    @TableField(exist = false)
    private Long grade;


    /**防汛等级名称*/
    @Excel(name = "防汛等级")
    @TableField(exist = false)
    @ApiModelProperty(value = "防汛等级")
    private String gradeName;


    /**工号*/
    @Excel(name = "工号")
    @ApiModelProperty(value = "工号")
    @TableField(exist = false)
    private String workNo;

    /**错误原因*/
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private  String  mistake;

}
