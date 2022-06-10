package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;

/**
 * @Author WangHongTao
 * @Date 2021/11/18
 */
@Data
public class ReportRepairDTO {

    @ApiModelProperty(value = "主键id,自动递增")
    private Long id;

    @ApiModelProperty(value = "保管人")
    @NotBlank(message = "保管人不能为空")
    private  String  keepPerson;

    @ApiModelProperty(value = "报修/报废原因")
    @NotBlank(message = "报修/报废原因不能为空")
    private  String  scrapReason;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "送修时间")
    private  java.util.Date  repairTime;

    @ApiModelProperty(value = "送修部门")
    @NotBlank(message = "送修部门不能为空")
    private  String  scrapDepart;

}
