package com.aiurt.modules.subsystem.dto;

import io.swagger.annotations.ApiModelProperty;
import liquibase.pro.packaged.S;
import lombok.Data;

import java.util.List;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.modules.subsystem.dto
 * @className: YearFaultDTO
 * @author: life-0
 * @date: 2022/10/18 14:20
 * @description: TODO
 * @version: 1.0
 */
@Data
public class YearFaultDTO {
    private String code;
    private String name;
    @ApiModelProperty(value = "一月")
    private String january = "0";
    @ApiModelProperty(value = "二月")
    private String february = "0";
    @ApiModelProperty(value = "三月")
    private String march = "0";
    @ApiModelProperty(value = "四月")
    private String april = "0";
    @ApiModelProperty(value = "五月")
    private String may = "0";
    @ApiModelProperty(value = "六月")
    private String june = "0";
    @ApiModelProperty(value = "七月")
    private String july = "0";
    @ApiModelProperty(value = "八月")
    private String august = "0";
    @ApiModelProperty(value = "九月")
    private String september = "0";
    @ApiModelProperty(value = "十月")
    private String october = "0";
    @ApiModelProperty(value = "十一月")
    private String november = "0";
    @ApiModelProperty(value = "十二月")
    private String december = "0";

    List<YearFaultDTO> yearFaultDTOS;
}
