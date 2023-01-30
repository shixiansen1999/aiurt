package com.aiurt.modules.faultanalysisreport.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.aiurt.modules.faultanalysisreport.entity.FaultAnalysisReport;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @author lkj
 */
@Data
@ApiModel("故障分析")
public class FaultDTO extends DictEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    /**故障主键id*/
    @ApiModelProperty(value = "故障主键id")
    @TableField(exist = false)
    private String faultId;

    /**故障报修编码*/
    @Excel(name = "故障报修编码", width = 15)
    @ApiModelProperty(value = "故障报修编码")
    private String code;

    /**专业编码*/
    @Excel(name = "专业编码", width = 15)
    @ApiModelProperty(value = "专业编码", required = true)
    @Dict(dictTable = "cs_major", dicText = "major_name", dicCode = "major_code")
    private String majorCode;


    /**专业子系统编码*/
    @Excel(name = "专业子系统编码", width = 15)
    @ApiModelProperty(value = "专业子系统编码")
    @Dict(dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    private String subSystemCode;

    /**报修方式*/
    @Excel(name = "报修方式", width = 15)
    @ApiModelProperty(value = "报修方式",example = "")
    @Dict(dicCode = "fault_mode_code")
    private String faultModeCode;

    /**报修人*/
    @Excel(name = "报修人", width = 15)
    @ApiModelProperty(value = "报修人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
    private String faultApplicant;

    @Excel(name = "被指派人/领取人/责任人", width = 15)
    @ApiModelProperty("被指派人/领取人/责任人")
    @Dict(dicCode = "username", dicText = "realname", dictTable = "sys_user")
    private String appointUserName;

    /**线路编码*/
    @Excel(name = "故障位置-线路编码", width = 15)
    @ApiModelProperty(value = "线路编码", required = true)
    @Dict(dictTable = "cs_line", dicText = "line_name", dicCode = "line_code")
    private String lineCode;

    /**设备编码*/
    @Excel(name = "设备编码", width = 15)
    @ApiModelProperty(value = "设备编码", required = true)
    private String deviceCode;

    /**设备编码*/
    @Excel(name = "设备名称", width = 15)
    @ApiModelProperty(value = "设备名称", required = true)
    private String deviceName;

    /**站点*/
    @Excel(name = "故障位置-站所编码", width = 15)
    @ApiModelProperty(value = "站点",  required = true)
    @Dict(dictTable = "cs_station", dicText = "station_name", dicCode = "station_code")
    private String stationCode;

    /**位置*/
    @Excel(name = "故障位置-位置编码", width = 15)
    @ApiModelProperty(value = "位置")
    @Dict(dictTable = "cs_station_position", dicText = "position_name", dicCode = "position_code")
    private String stationPositionCode;

    /**故障发生时间*/
    @Excel(name = "故障发生时间", width = 15, format = "yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm" )
    @ApiModelProperty(value = "故障发生时间yyyy-MM-dd HH:mm",  required = true)
    private Date happenTime;

    /**故障现象分类*/
    @Excel(name = "故障现象分类", width = 15)
    @ApiModelProperty(value = "故障现象分类",  required = true)
    @Length(max = 255, message = "故障现象长度不能超过255")
    @Dict(dictTable = "fault_knowledge_base_type", dicCode = "code", dicText = "name")
    private String faultPhenomenon;

    /**故障分析*/
    @ApiModelProperty(value = "故障分析")
    private String faultAnalysis;

    /**设备类型*/
    @Excel(name = "设备类型", width = 15)
    @ApiModelProperty(value = "设备类型")
    @Dict(dictTable ="device_Type",dicText = "name",dicCode = "code")
    private String deviceTypeCode;

    /**设备组件*/
    @Excel(name = "设备组件", width = 15)
    @ApiModelProperty(value = "设备组件")
    @Dict(dictTable ="device_assembly",dicText = "material_name",dicCode = "material_code")
    private String materialCode;

    /**故障措施/解决方案*/
    @cn.afterturn.easypoi.excel.annotation.Excel(name = "解决方案", width = 15)
    @ApiModelProperty(value = "故障措施/解决方案")
    private String solution;

    /**维修完成时间*/
    @Excel(name = "维修完成时间", width = 15, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "维修完成时间")
    private Date endTime;

    @ApiModelProperty("挂起原因")
    private String hangUpReason;

    @ApiModelProperty(value = "故障分析",  required = true)
    private FaultAnalysisReport faultAnalysisReport;

    @ApiModelProperty(value = "故障知识库",  required = true)
    private FaultKnowledgeBase faultKnowledgeBase;

    @ApiModelProperty(value = "故障现象")
    private String symptoms;

}
