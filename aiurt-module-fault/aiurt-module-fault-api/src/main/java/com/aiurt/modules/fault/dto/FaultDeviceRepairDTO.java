package com.aiurt.modules.fault.dto;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023/3/16
 * @time: 14:32
 */

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 设备送修
 *
 * @author: qkx
 * @date: 2023-03-16 14:32
 */
@Data
public class FaultDeviceRepairDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键id*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;
    /**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;

    /**创建日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;

    /**更新日期*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;

    /**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;

    /**所属部门*/
    @ApiModelProperty(value = "送修状态")
    @Dict(dicCode = "device_repair_status")
    private String repairStatus;

    /**所属部门*/
    @ApiModelProperty(value = "送修序列号")
    private String repairSerialNumber;

    /**所属部门*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "送修时间")
    private String repairSendTime;

    /**所属部门*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "送修返回时间")
    private String repairBackTime;


    /**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;

    /**删除标志*/
    @Excel(name = "删除标志", width = 15)
    @ApiModelProperty(value = "删除标志")
    private Integer delFlag;

    /**故障单编号*/
    @Excel(name = "故障单编号", width = 15)
    @ApiModelProperty(value = "故障单编号")
    private String faultCode;

    /**设备id*/
    @Excel(name = "设备id", width = 15)
    @ApiModelProperty(value = "设备id")
    private String deviceId;

    /**设备编码*/
    @Excel(name = "设备编码", width = 15)
    @ApiModelProperty(value = "设备编码")
    private String deviceCode;

    @ApiModelProperty(value = "设备名称")
    @TableField(exist = false)
    private String deviceName;


    @ApiModelProperty("设备类型名称")
    @TableField(exist = false)
    private String deviceTypeName;

    @ApiModelProperty("设备类编码")
    @TableField(exist = false)
    private String deviceTypeCode;

    @ApiModelProperty("线路编码")
    @TableField(exist = false)
    private String lineCode;

    @ApiModelProperty("线路编码")
    @TableField(exist = false)
    private String lineName;

    @ApiModelProperty("站点名称")
    @TableField(exist = false)
    private String stationName;

    @ApiModelProperty("站点编码")
    @TableField(exist = false)
    private String stationCode;

    @ApiModelProperty("位置编码")
    @TableField(exist = false)
    private String positionCode;

    @ApiModelProperty("位置名称")
    @TableField(exist = false)
    private String positionName;

    /**专业子系统编码*/
    @Excel(name = "专业子系统编码", width = 15)
    @ApiModelProperty(value = "专业子系统编码")
    @Dict(dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    @SystemFilterColumn
    private String subSystemCode;

    @ApiModelProperty(value = "故障现象")
    private String symptoms;


    /**负责人*/
    @Excel(name = "负责人", width = 15)
    @ApiModelProperty(value = "负责人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
    private String chargeUserName;

    /**送修经办人*/
    @Excel(name = "送修经办人", width = 15)
    @ApiModelProperty(value = "送修经办人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
    private String repairUserName;

}
