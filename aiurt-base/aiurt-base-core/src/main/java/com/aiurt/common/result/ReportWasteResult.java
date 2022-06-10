package com.aiurt.common.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Author WangHongTao
 * @Date 2021/11/18
 *
 * 报废详情
 */
@Data
public class ReportWasteResult {

    @ApiModelProperty(value = "主键id,自动递增")
    private Long id;

    @ApiModelProperty(value = "物资名称")
    private  String  materialName;

    @ApiModelProperty(value = "报废/报修数量")
    private  Integer  num;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "购置日期")
    private  java.util.Date  buyTime;

    @ApiModelProperty(value = "规定年限")
    private  Integer  serviceLife;

    @ApiModelProperty(value = "使用年限")
    private  Integer  useLife;

    @ApiModelProperty(value = "报修/报废原因")
    private  String  scrapReason;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "送修/报废时间")
    private  java.util.Date  repairTime;

    @ApiModelProperty(value = "操作人")
    private  String  createBy;
}
