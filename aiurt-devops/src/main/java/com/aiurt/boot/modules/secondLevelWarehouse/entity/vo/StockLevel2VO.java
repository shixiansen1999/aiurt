package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * Created with IntelliJ IDEA.
 * Description:
 *
 * @Author: km
 * DateTime: 2021/9/29 10:19
 */
@Data
public class StockLevel2VO {
    @ApiModelProperty(value = "主键自增id")
    private Long id;

    /**线路*/
    @Excel(name = "线路", width = 15)
    @ApiModelProperty(value = "线路")
    private  String  lineName;

    /**所属系统*/
    @Excel(name = "所属系统", width = 15)
    @ApiModelProperty(value = "所属系统")
    private String systemCode;

    /**物资编号*/
    @Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private String materialCode;

    /**物资名称*/
    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
    private String materialName;

    /**物资类型*/
    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
    private Integer type;

    /**物资类型*/
    @Excel(name = "物资类型", width = 15)
    @ApiModelProperty("物资类型名称")
    private String typeName;

    /**规格型号*/
    @Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private String specifications;

    /**生产厂家*/
    @Excel(name = "生产厂家", width = 15)
    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    /**单位*/
    @Excel(name = "单位", width = 10)
    @ApiModelProperty("单位")
    private String unit;

    /**入库时间*/
    @Excel(name = "入库时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "入库时间")
    private  java.util.Date  stockInTime;

    /**数量*/
    @Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private Integer num;

    /**存放位置*/
    @ApiModelProperty(value = "存放位置")
    private  String  location;

    @Excel(name = "存放位置", width = 15)
    @ApiModelProperty(value = "存放位置")
    private String warehouseName;

    /**保管人*/
    @Excel(name = "保管人", width = 15)
    @ApiModelProperty(value = "保管人")
    private  String  keeperName;

    /**单价*/
    @Excel(name = "单价", width = 15)
    @ApiModelProperty(value = "单价")
    private Integer price;

    /**总价*/
    @Excel(name = "总价", width = 15)
    @ApiModelProperty(value = "总价")
    private Integer totalPrice;

    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "原产地")
    private String countryOrigin;

    @ApiModelProperty("品牌")
    private String brand;

    @ApiModelProperty(value = "仓库编号")
    private String warehouseCode;

    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
    @TableLogic
    private Integer delFlag;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "修改人")
    private String updateBy;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private java.util.Date updateTime;

}
