package com.aiurt.modules.floodpreventioninformation.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: flood_prevention_information
 * @Author: zwl
 * @Date:   2023-04-24
 * @Version: V1.0
 */
@Data
@TableName("flood_prevention_information")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="flood_prevention_information对象", description="flood_prevention_information")
public class FloodPreventionInformation implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;

    /**线路编码*/
    @Excel(name = "线路编码")
    @ApiModelProperty(value = "线路编码")
    private String lineCode;

    /**站点编码*/
    @Excel(name = "站点编码")
    @ApiModelProperty(value = "站点编码")
    private String stationCode;

    /**站点名称*/
    @Excel(name = "站点名称")
    @ApiModelProperty(value = "站点名称")
    private String stationName;

    /**部门编码*/
    @Excel(name = "部门编码")
    @ApiModelProperty(value = "部门编码")
    private String orgCode;

    /**部门名称*/
    @Excel(name = "部门名称")
    @ApiModelProperty(value = "部门名称")
    @TableField(exist = false)
    private String orgName;

    /**应急队伍负责人ID*/
    @Excel(name = "应急队伍负责人ID")
    @ApiModelProperty(value = "应急队伍负责人ID")
    private String emergencyPeople;

    /**应急队伍负责人名称*/
    @Excel(name = "应急队伍负责人名称")
    @ApiModelProperty(value = "应急队伍负责人名称")
    @TableField(exist = false)
    private String emergencyPeopleName;

    /**应急队伍人数*/
    @Excel(name = "应急队伍人数")
    @ApiModelProperty(value = "应急队伍人数")
    private Long emergencyPersonnel;

    /**后备人数*/
    @Excel(name = "后备人数")
    @ApiModelProperty(value = "后备人数")
    private Long reservePersonnel;

    /**周边是否存在排水不畅*/
    @Excel(name = "周边是否存在排水不畅")
    @ApiModelProperty(value = "周边是否存在排水不畅（1是，0否）")
    private Long peripheryWater;

    /**周边是否存在排水不畅名称*/
    @Excel(name = "周边是否存在排水不畅名称")
    @ApiModelProperty(value = "周边是否存在排水不畅名称")
    @TableField(exist = false)
    private String peripheryWaterName;

    /**周边是否存在工地*/
    @Excel(name = "周边是否存在工地")
    @ApiModelProperty(value = "周边是否存在工地（1是，0否）")
    private Long peripheryGrounds;

    /**周边是否存在工地名称*/
    @Excel(name = "周边是否存在工地名称")
    @ApiModelProperty(value = "周边是否存在工地名称")
    @TableField(exist = false)
    private String peripheryGroundsName;

    /**防汛物资配备所在位置*/
    @Excel(name = "防汛物资配备所在位置")
    @ApiModelProperty(value = "防汛物资配备所在位置")
    private String materialLocation;

    /**站长名称*/
    @Excel(name = "站长名称")
    @ApiModelProperty(value = "站长名称")
    private String masterName;

    /**包保负责人名称*/
    @Excel(name = "包保负责人名称")
    @ApiModelProperty(value = "包保负责人名称")
    private String insuranceName;

    /**防汛出入口*/
    @Excel(name = "防汛出入口")
    @ApiModelProperty(value = "防汛出入口")
    private String entrance;

    /**防汛等级*/
    @Excel(name = "防汛等级")
    @ApiModelProperty(value = "防汛等级")
    private String grade;

    /**删除状态 0-未删除 1-已删除*/
    @ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
    @TableLogic
    private Integer delFlag;

    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**修改人*/
    @ApiModelProperty(value = "修改人")
    private String updateBy;

    /**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**修改时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private Date updateTime;

}
