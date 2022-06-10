package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;

/**
 * @Author km
 * @Date 2021/9/18 20:04
 * @Version 1.0
 */
@Data
public class StockLevel2CheckDetailExcel {
    @Excel(name="序号",width = 15)
    @TableField(exist = false)
    private Integer serialNumber;
    @Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private  String  materialCode;
    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty("物资名称")
    private String materialName;
    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty("物资类型名称")
    private String typeName;
    @Excel(name = "规格", width = 15)
    @ApiModelProperty("规格")
    private String specifications;
    @Excel(name = "账面价值", width = 15)
    @ApiModelProperty("账面价值")
    private BigDecimal bookPrice;
    @Excel(name = "物资单价", width = 15)
    @ApiModelProperty("物资单价")
    private BigDecimal price;
    @Excel(name = "账面数量", width = 15)
    @ApiModelProperty("账面数量")
    private Integer bookNum;
    @Excel(name = "实盘数量", width = 15)
    @ApiModelProperty(value = "实盘数量")
    private  Integer  actualNum;
    @Excel(name = "使用时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
    private  java.util.Date  updateTime;
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private  String  note;
}
