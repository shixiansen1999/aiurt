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
    private String id;
    private String code;
    private String name;
    @ApiModelProperty(value = "一月")
    private String january ;
    @ApiModelProperty(value = "二月")
    private String february ;
    @ApiModelProperty(value = "三月")
    private String march ;
    @ApiModelProperty(value = "四月")
    private String april ;
    @ApiModelProperty(value = "五月")
    private String may ;
    @ApiModelProperty(value = "六月")
    private String june ;
    @ApiModelProperty(value = "七月")
    private String july ;
    @ApiModelProperty(value = "八月")
    private String august ;
    @ApiModelProperty(value = "九月")
    private String september ;
    @ApiModelProperty(value = "十月")
    private String october ;
    @ApiModelProperty(value = "十一月")
    private String november ;
    @ApiModelProperty(value = "十二月")
    private String december ;

    List<YearFaultDTO> yearFaultDtos;
}
