package com.aiurt.modules.faultanalysisreport.entity.dto;

import com.aiurt.common.aspect.annotation.Dict;
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

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@ApiModel("故障分析")
public class FaultDTO {
    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    /**故障报修编码*/
    @Excel(name = "故障报修编码", width = 15)
    @ApiModelProperty(value = "故障报修编码")
    private String code;

    /**专业编码*/
    @Excel(name = "专业编码", width = 15)
    @ApiModelProperty(value = "专业编码", required = true)
    @NotBlank(message = "所属专业不能为空")
    private String majorCode;

    /**专业子系统编码*/
    @Excel(name = "专业子系统编码", width = 15)
    @ApiModelProperty(value = "专业子系统编码")
    private String subSystemCode;

    /**专业子系统名称*/
    @Excel(name = "专业子系统名称", width = 15)
    @ApiModelProperty(value = "专业子系统名称")
    private String subSystemName;

    /**报修人*/
    @Excel(name = "报修人", width = 15)
    @ApiModelProperty(value = "报修人")
    @Dict(dictTable = "sys_user", dicCode = "username", dicText = "realname")
    private String faultApplicant;

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
    private String stationCode;

    /**位置*/
    @Excel(name = "故障位置-位置编码", width = 15)
    @ApiModelProperty(value = "位置")
    private String stationPositionCode;

    /**站点名称*/
    @Excel(name = "故障位置-站所名称", width = 15)
    @ApiModelProperty(value = "站点名称",  required = true)
    @TableField(exist = false)
    private String stationName;

    /**位置名称*/
    @Excel(name = "故障位置-位置名称", width = 15)
    @ApiModelProperty(value = "位置名称")
    private String stationPositionName;

    /**故障发生时间*/
    @Excel(name = "故障发生时间", width = 15, format = "yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm" )
    @ApiModelProperty(value = "故障发生时间yyyy-MM-dd HH:mm",  required = true)
    @NotNull(message = "请填写故障发生时间")
    private Date happenTime;

    /**故障现象*/
    @Excel(name = "故障现象", width = 15)
    @ApiModelProperty(value = "故障现象",  required = true)
    @NotBlank(message = "请填写故障现象!")
    @Length(max = 255, message = "故障现象长度不能超过255")
    private String faultPhenomenon;

    @ApiModelProperty(value = "故障分析",  required = true)
    private FaultAnalysisReport faultAnalysisReport;

    @ApiModelProperty(value = "故障知识库",  required = true)
    private FaultKnowledgeBase faultKnowledgeBase;

}
