package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Author km
 * @Date 2021/9/18 14:43
 * @Version 1.0
 */
@Data
public class StockInOrderLevel2Excel {

    @Excel(name="序号",width = 15)
    @TableField(exist = false)
    private Integer serialNumber;

    /**入库单号*/
    @Excel(name = "入库单号", width = 15)
    @ApiModelProperty(value = "入库单号")
    private  String  orderCode;

    @ApiModelProperty("入库仓库名称")
    @Excel(name = "入库仓库", width = 15)
    private String warehouseName;

    @ApiModelProperty("操作人名称")
    @Excel(name = "操作人", width = 15)
    private String operatorName;

    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "入库时间")
    @Excel(name = "入库时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    private  java.util.Date  stockInTime;

    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private  String  note;

}
