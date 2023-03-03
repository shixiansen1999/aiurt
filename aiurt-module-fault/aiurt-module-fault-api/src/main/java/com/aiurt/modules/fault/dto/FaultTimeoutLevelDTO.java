package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2022-09-08 9:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class FaultTimeoutLevelDTO {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("故障状态")
    @Dict(dicCode = "fault_status")
    private String status;

    @ApiModelProperty("故障编号")
    private String code;

    /**故障级别*/
    @ApiModelProperty(value = "故障级别")
    @Dict(dictTable = "fault_level", dicCode = "code", dicText = "name")
    private String faultLevel;

    /**报修方式*/
    @ApiModelProperty(value = "报修方式",example = "")
    @Dict(dicCode = "fault_mode_code")
    private String faultModeCode;

    @ApiModelProperty("超时时长")
    private String timeoutDuration;

    @ApiModelProperty("app-超时时长")
    private String appTimeoutDuration;

    @ApiModelProperty("超时类型")
    private String timeoutType;

    @ApiModelProperty("班组名称")
    private String teamName;

    @ApiModelProperty("班组负责人")
    private String teamUser;

    @ApiModelProperty("维修负责人")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "username")
    private String appointUserName;

    /**专业子系统编码*/
    @ApiModelProperty(value = "专业子系统编码")
    @Dict(dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    @SystemFilterColumn
    private String subSystemCode;

    /**线路编码*/
    @ApiModelProperty(value = "线路编码", required = true)
    @Dict(dictTable = "cs_line", dicText = "line_name", dicCode = "line_code")
    private String lineCode;

    /**站点*/
    @ApiModelProperty(value = "站点",  required = true)
    @Dict(dictTable = "cs_station", dicText = "station_name", dicCode = "station_code")
    @NotBlank(message = "请选择位置")
    private String stationCode;

    /**设备编码*/
    @ApiModelProperty(value = "设备编码", required = true)
    @TableField(exist = false)
    private String deviceCode;

    @ApiModelProperty(value = "设备名称")
    @TableField(exist = false)
    private String deviceName;

    /**故障现象*/
    @ApiModelProperty(value = "故障现象分类",  required = true)
    @NotBlank(message = "请填写故障现象分类!")
    @Dict(dictTable = "fault_knowledge_base_type", dicCode = "code", dicText = "name")
    private String faultPhenomenon;

    /**故障现象名称*/
    @ApiModelProperty(value = "故障现象分类名称",  required = true)
    private String faultPhenomenonTypeName;

    /**故障发生时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm" )
    @ApiModelProperty(value = "故障发生时间yyyy-MM-dd HH:mm",  required = true)
    @NotNull(message = "请填写故障发生时间")
    private Date happenTime;

}
