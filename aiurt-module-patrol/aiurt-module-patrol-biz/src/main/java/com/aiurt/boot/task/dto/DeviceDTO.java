package com.aiurt.boot.task.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/5
 * @desc
 */
@Data
public class DeviceDTO extends DictEntity {
    /**主键id*/
    @TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    private  String  id;
    /**设备名称*/
    @Excel(name = "设备名称", width = 15)
    @ApiModelProperty(value = "设备名称")
    private  String  name;
    /**设备编号*/
    @Excel(name = "设备编号", width = 15)
    @ApiModelProperty(value = "设备编号")
    private  String  code;
    @Excel(name = "设备类型", width = 15)
    @ApiModelProperty(value = "设备类型")
    @TableField(exist = false)
    private  String  deviceType;
    /**位置*/
    @Excel(name = "位置", width = 15)
    @ApiModelProperty(value = "位置")
    private  String  positionCode;
    /**设备位置*/
    @Excel(name = "所属专业名称", width = 15)
    @ApiModelProperty(value = "所属专业名称")
    @TableField(exist = false)
    private  String  majorCodeName;
    /**子系统编号名称*/
    @Excel(name = "子系统编号名称", width = 15)
    @ApiModelProperty(value = "子系统编号名称")
    @TableField(exist = false)
    private  String  systemCodeName;
    /**所属专业*/
    @Excel(name = "所属专业", width = 15)
    @ApiModelProperty(value = "所属专业")
    @Dict(dictTable ="cs_major",dicText = "major_name",dicCode = "major_code")
    private  String  majorCode;
    /**子系统编号*/
    @Excel(name = "子系统编号", width = 15)
    @ApiModelProperty(value = "子系统编号")
    @Dict(dictTable ="cs_subsystem",dicText = "system_name",dicCode = "system_code")
    private  String  systemCode;
    @Excel(name = "设备位置", width = 15)
    @ApiModelProperty(value = "设备位置")
    @TableField(exist = false)
    private  String  positionCodeName;
    /**临时设备(是/否（默认否）1是,0:否)*/
	@Excel(name = "临时设备(是/否（默认否）1是,0:否)", width = 15)
    @ApiModelProperty(value = "临时设备(是/否（默认否）1是,0:否)")
    @Dict(dicCode = "device_temporary")
    private  String  temporary;
    /**状态 0-停用 1-正常*/
	@Excel(name = "状态 0-停用 1-正常", width = 15)
    @ApiModelProperty(value = "状态 0-停用 1-正常")
    @Dict(dicCode = "device_status")
    private  Integer  status;
	@Excel(name = "状态 0-停用 1-正常", width = 15)
    @ApiModelProperty(value = "状态 0-停用 1-正常")
    private  String  statusName;
    /**删除状态 0-未删除 1-已删除*/
}
