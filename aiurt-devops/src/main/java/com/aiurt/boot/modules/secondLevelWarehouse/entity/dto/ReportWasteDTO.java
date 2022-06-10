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
public class ReportWasteDTO {

    @ApiModelProperty(value = "主键id,自动递增")
    private Long id;

    @ApiModelProperty(value = "报修/报废原因")
    @NotBlank(message = "报修/报废原因不能为空")
    private  String  scrapReason;

    @ApiModelProperty(value = "规定年限")
    private  Integer  serviceLife;

    @ApiModelProperty(value = "使用年限")
    private  Integer  useLife;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "购置日期")
    //@NotNull(message = "购置日期不能为空")
    private  java.util.Date  buyTime;
}
