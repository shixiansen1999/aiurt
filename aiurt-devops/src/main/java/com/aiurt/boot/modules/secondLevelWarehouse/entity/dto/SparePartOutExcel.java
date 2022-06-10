package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Author km
 * @Date 2021/9/23 15:09
 * @Version 1.0
 */
@Data
public class SparePartOutExcel {

    @Excel(name="序号",width = 15)
    @TableField(exist = false)
    private Integer serialNumber;

    /**所属系统*/
    @Excel(name = "所属系统", width = 15)
    @ApiModelProperty(value = "所属系统")
    private String system;

    /**物资编号*/
    @Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private  String  materialCode;

    /**备件名称*/
    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
    private  String  materialName;

    @ApiModelProperty(value = "备件类型")
    private  Integer  type;
    /**备件类型名称*/
    @Excel(name = "物资类型", width = 15)
    @ApiModelProperty(value = "物资类型")
    private  String  typeName;

    /**规格&型号*/
    @Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private String specifications;

    /**原产地*/
    @ApiModelProperty(value = "原产地")
    private String countryOrigin;
    /**生产商*/
    @Excel(name = "生产厂家", width = 15)
    @ApiModelProperty(value = "生产厂家")
    private String manufacturer;

    /**单位*/
    @Excel(name = "单位", width = 15)
    @ApiModelProperty(value = "单位")
    private String unit;

    /**存放位置*/
    @Excel(name = "存放位置", width = 15)
    @ApiModelProperty(value = "存放位置")
    private  String  orgId;

    /**单价*/
    @Excel(name = "单价", width = 15)
    @ApiModelProperty(value = "单价")
    private  Double  price;

    /**出库数量*/
    @Excel(name = "出库数量", width = 15)
    @ApiModelProperty(value = "出库数量")
    private  Integer  num;

    /**出库时间*/
    @Excel(name = "出库时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "出库时间")
    private  java.util.Date  outTime;


    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private  String remarks;


    /**品牌*/
    @ApiModelProperty(value = "品牌")
    private String brand;

}
