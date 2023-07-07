package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.SystemFilterColumn;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

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
public class FaultDataAnalysisInfoDTO extends DictEntity {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    /**故障报修编码*/
    @ApiModelProperty(value = "故障报修编码")
    private String code;


    /**专业子系统编码*/
    @ApiModelProperty(value = "专业子系统编码")
    @SystemFilterColumn
    private String subSystemCode;

    @ApiModelProperty(value = "专业子系统编码")
    private String systemName;

    @ApiModelProperty(value = "简称")
    private String shortenedForm;

    /**站点*/
    @ApiModelProperty(value = "站点",  required = true)
    private String stationCode;

    @ApiModelProperty(value = "站点",  required = true)
    private String stationName;

    /**报修方式*/
    @ApiModelProperty(value = "报修方式",example = "")
    private String faultModeCode;

    @ApiModelProperty("报修方式名称")
    private String faultModeName;

    /**故障发生时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "MM月dd日 HH:mm")
    @DateTimeFormat(pattern="MM月dd日 HH:mm" )
    @ApiModelProperty(value = "故障发生时间MM月dd日 HH:mm",  required = true)
    private Date happenTime;

    @ApiModelProperty("维修负责人")
    private String appointUserName;

    @ApiModelProperty("真实姓名")
    private String realName;

    @ApiModelProperty("故障状态")
    private String status;

    @ApiModelProperty("故障状态名称")
    private String statusName;

    /**线路编码*/
    @ApiModelProperty(value = "线路编码", required = true)
    private String lineCode;

    /**故障现象*/
    @ApiModelProperty(value = "故障现象分类编码",  required = true)
    private String faultPhenomenon;

    /**故障现象*/
    @ApiModelProperty(value = "故障现象分类",  required = true)
    private String faultPhenomenonName;
}
