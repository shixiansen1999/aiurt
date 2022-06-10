package com.aiurt.boot.common.result;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Author WangHongTao
 * @Date 2021/11/18
 *
 * 送修详情
 */
@Data
public class ReportRepairResult {

    @ApiModelProperty(value = "主键id,自动递增")
    private Long id;

    @ApiModelProperty(value = "物资名称")
    private  String  materialName;

    @ApiModelProperty(value = "报废/报修数量")
    private  Integer  num;

    @ApiModelProperty(value = "送修班组")
    private  String  scrapDepart;

    @ApiModelProperty(value = "保管人")
    private  String  keepPerson;

    @ApiModelProperty(value = "送修原因")
    private  String  scrapReason;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "送修时间")
    private  java.util.Date  repairTime;

    @ApiModelProperty(value = "操作人")
    private  String  createBy;

}
