package com.aiurt.modules.fault.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author zwl
 */
@Data
public class HitchDrillingDTO {


    @ApiModelProperty("线路名称")
    private String line;


    @ApiModelProperty("故障原因")
    private String gzyy;


    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm" )
    @ApiModelProperty(value = "故障发生时间yyyy-MM-dd HH:mm",  required = true)
    private Date gztime;


    @ApiModelProperty("状态")
    @Dict(dicCode = "fault_status")
    private String gzstate;



}
