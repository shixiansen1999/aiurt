package com.aiurt.boot.report.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @projectName: aiurt-platform
 * @package: com.aiurt.boot.report.model.dto
 * @className: MonthDTO
 * @author: life-0
 * @date: 2022/9/22 9:27
 * @description: TODO
 * @version: 1.0
 */
@Data
public class MonthDTO {
    private String orgName;
    private String systemName;
    @ApiModelProperty(value = "一月")
    private Integer january;
    @ApiModelProperty(value = "二月")
    private Integer february;
    @ApiModelProperty(value = "三月")
    private Integer march;
    @ApiModelProperty(value = "四月")
    private Integer april;
    @ApiModelProperty(value = "五月")
    private Integer may;
    @ApiModelProperty(value = "六月")
    private Integer june;
    @ApiModelProperty(value = "七月")
    private Integer july;
    @ApiModelProperty(value = "八月")
    private Integer august;
    @ApiModelProperty(value = "九月")
    private Integer september;
    @ApiModelProperty(value = "十月")
    private Integer october;
    @ApiModelProperty(value = "十一月")
    private Integer november;
    @ApiModelProperty(value = "十二月")
    private Integer december;

}
