package com.aiurt.boot.asset.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @author admin
 */
@Data
public class FixedAssetsModel {
    /**资产名称*/
    @Excel(name = "资产名称", width = 15)
    @ApiModelProperty(value = "资产名称")
    private java.lang.String assetName;
    /**存放地点*/
    @Excel(name = "存放地点", width = 15)
    @ApiModelProperty(value = "存放地点")
    private java.lang.String locationName;
    /**资产分类*/
    @Excel(name = "资产分类", width = 15)
    @ApiModelProperty(value = "资产分类")
    private java.lang.String categoryName;
    /**资产名称*/
    @Excel(name = "资产编号", width = 15)
    @ApiModelProperty(value = "资产编号")
    private java.lang.String assetCode;
    /**使用组织机构*/
    @Excel(name = "使用组织机构", width = 15)
    @ApiModelProperty(value = "使用组织机构")
    private java.lang.String orgName;
    /**规格型号*/
    @Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private java.lang.String specification;
    /**账面数量*/
    @Excel(name = "账面数量", width = 15,type = 4)
    @ApiModelProperty(value = "账面数量")
    private java.lang.Integer number;
    /**房产证号*/
    @Excel(name = "房产证号", width = 15)
    @ApiModelProperty(value = "房产证号")
    private java.lang.String houseNumber;
    /**建成/购置时间*/
    @Excel(name = "建成/购置时间", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "建成/购置时间")
    private java.util.Date buildBuyDate;
    /**建筑面积*/
    @Excel(name = "建筑面积", width = 15,type = 4)
    @ApiModelProperty(value = "建筑面积")
    private java.math.BigDecimal coveredArea;
    /**计量单位(1个、2栋、3台)*/
    @Excel(name = "计量单位", width = 15)
    @ApiModelProperty(value = "计量单位(1个、2栋、3台)")
    private java.lang.String unitsName;
    /**累计折旧*/
    @Excel(name = "累计折旧", width = 15,type = 4)
    @ApiModelProperty(value = "累计折旧")
    private java.math.BigDecimal accumulatedDepreciation;
    /**账面原值*/
    @Excel(name = "账面原值", width = 15,type = 4)
    @ApiModelProperty(value = "账面原值")
    private java.math.BigDecimal assetOriginal;
    /**责任人*/
    @Excel(name = "责任人", width = 15)
    @ApiModelProperty(value = "责任人")
    private java.lang.String responsibilityName;
    /**启用状态(0停用、1启用)*/
    @Excel(name = "启用状态", width = 15)
    @ApiModelProperty(value = "启用状态(0停用、1启用)")
    private String statusName;
    /**折旧年限*/
    @Excel(name = "折旧年限", width = 15)
    @ApiModelProperty(value = "折旧年限")
    private java.lang.String depreciableLife;
    /**使用年限*/
    @Excel(name = "使用年限", width = 15)
    @ApiModelProperty(value = "使用年限")
    private java.lang.String durableYears;
    /**开始使用日期*/
    @Excel(name = "开始使用日期", width = 15, format = "yyyy-MM-dd")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始使用日期")
    private java.util.Date startDate;

    /**错误原因*/
    @ApiModelProperty(value = "错误原因")
    private String mistake;

    @ApiModelProperty(value = "使用组织机构")
    private String orgCode;
    @ApiModelProperty(value = "存放地点")
    private String location;
    @ApiModelProperty(value = "启用状态(0停用、1启用)")
    private Integer status;
    @ApiModelProperty(value = "计量单位(1个、2栋、3台)")
    private Integer units;
    @ApiModelProperty(value = "资产分类")
    private String categoryCode;
}
