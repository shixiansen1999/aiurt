package com.aiurt.boot.common.result;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author WangHongTao
 * @Date 2021/11/18
 *
 * 备件报损查询结果
 */
@Data
public class ScrapReportResult {

    @ApiModelProperty(value = "主键id,自动递增")
    private Long id;

    @ApiModelProperty(value = "申请人")
    private  String  createBy;

    @ApiModelProperty(value = "物资编号")
    private  String  materialCode;

    @ApiModelProperty(value = "物资名称")
    private  String  materialName;

    @ApiModelProperty(value = "线路")
    private  String  lineName;

    @ApiModelProperty(value = "报废/报修数量")
    private  Integer  num;

    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    @ApiModelProperty(value = "规格")
    private  String  specifications;

    @ApiModelProperty("物资类型")
    private Integer materialType;

    @ApiModelProperty("物资类型名称")
    private String materialTypeDesc;

    @ApiModelProperty(value = "所在班组")
    private  String  orgId;

}
