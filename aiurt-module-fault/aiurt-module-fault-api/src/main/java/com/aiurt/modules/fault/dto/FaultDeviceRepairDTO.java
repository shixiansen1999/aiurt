package com.aiurt.modules.fault.dto;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023/3/16
 * @time: 14:32
 */

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

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

    /**送修状态*/
    @ApiModelProperty(value = "送修状态:1.待返修，2.已返修，3.已验收")
    @Dict(dicCode = "device_repair_status")
    private String repairStatus;

    /**送修序列号*/
    @ApiModelProperty(value = "送修序列号")
    private String repairSerialNumber;

    /**送修时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "送修时间")
    private Date repairSendTime;

    /**送修返回时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "送修返回时间")
    private Date repairBackTime;


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

    @Excel(name = "位置层级", width = 15,needMerge = true)
    @ApiModelProperty(value = "位置层级")
    @TableField(exist = false)
    private  String  positionCodeCc;
    @Excel(name = "位置层级名称", width = 15,needMerge = true)
    @ApiModelProperty(value = "位置层级名称")
    @TableField(exist = false)
    private  String  positionCodeCcName;

    /**专业编码*/
    @Excel(name = "专业编码", width = 15)
    @ApiModelProperty(value = "专业编码", required = true)
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    private String majorCode;

    /**子系统编号*/
    @Excel(name = "子系统编号", width = 15,needMerge = true)
    @ApiModelProperty(value = "子系统编号")
    @Dict(dictTable ="cs_subsystem",dicText = "system_name",dicCode = "system_code")
    private  String  systemCode;
    /**子系统编号名称*/
    @Excel(name = "子系统编号名称", width = 15,needMerge = true)
    @ApiModelProperty(value = "子系统编号名称")
    @TableField(exist = false)
    private  String  systemCodeName;

    @ApiModelProperty(value = "故障现象")
    private String symptoms;

    /**故障现象*/
    @Excel(name = "故障现象分类", width = 15)
    @ApiModelProperty(value = "故障现象分类",  required = true)
    @NotBlank(message = "故障现象分类!")
    @Dict(dictTable = "fault_knowledge_base_type", dicCode = "code", dicText = "name")
    private String faultPhenomenon;


    /**负责人*/
    @Excel(name = "负责人", width = 15)
    @ApiModelProperty(value = "负责人")
    private List<String>  chargeUserName;

    /**负责人名称*/
    @Excel(name = "负责人名称", width = 15)
    @ApiModelProperty(value = "负责人名称")
    private List<String>  chargeRealName;

    /**送修经办人*/
    @Excel(name = "送修经办人", width = 15)
    @ApiModelProperty(value = "送修经办人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
    private String repairUserName;

    @ApiModelProperty(value = "故障接报人")
    private String receiveUserName;



}
