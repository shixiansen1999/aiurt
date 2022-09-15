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
public class FaultLargeInfoDTO extends DictEntity {
    private static final long serialVersionUID = 1L;

    /**专业子系统编码*/
    @ApiModelProperty(value = "专业子系统编码")
    @Dict(dictTable = "cs_subsystem", dicText = "system_name", dicCode = "system_code")
    @SystemFilterColumn
    private String subSystemCode;


    /**站点*/
    @ApiModelProperty(value = "站点",  required = true)
    @Dict(dictTable = "cs_station", dicText = "station_name", dicCode = "station_code")
    private String stationCode;

    /**故障发生时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "dd日 HH:mm")
    @DateTimeFormat(pattern="dd日 HH:mm" )
    @ApiModelProperty(value = "故障发生时间dd HH:mm",  required = true)
    private Date happenTime;

    @ApiModelProperty("维修负责人")
    private String appointUserName;

    @ApiModelProperty("故障状态")
    @Dict(dicCode ="fault_status")
    private String status;

    /**线路编码*/
    @ApiModelProperty(value = "线路编码", required = true)
    @Dict(dictTable = "cs_line", dicText = "line_name", dicCode = "line_code")
    private String lineCode;


}
