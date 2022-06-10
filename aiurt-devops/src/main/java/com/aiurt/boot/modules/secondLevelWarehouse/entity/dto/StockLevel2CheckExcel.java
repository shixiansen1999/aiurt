package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Author km
 * @Date 2021/9/18 19:15
 * @Version 1.0
 */
@Data
public class StockLevel2CheckExcel {
    @Excel(name="序号",width = 15)
    @TableField(exist = false)
    private Integer serialNumber;

    /**盘点任务单号*/
    @Excel(name = "盘点任务单号", width = 15)
    @ApiModelProperty(value = "盘点任务单号")
    private  String  stockCheckCode;

    /**盘点仓库编号*/
    @Excel(name = "盘点仓库名称", width = 15)
    @ApiModelProperty(value = "盘点仓库名称")
    private  String  warehouseName;

    /**盘点数量*/
    @Excel(name = "盘点数量", width = 15)
    @ApiModelProperty(value = "盘点数量")
    private  Integer  checkNum;

    @Excel(name = "仓库所属部门", width = 15)
    @ApiModelProperty("仓库所属部门")
    private String warehouseDepartment;

    @Excel(name = "盘点人名称", width = 15)
    @ApiModelProperty("盘点人名称")
    private String checkerName;

    /**盘点开始时间*/
    @Excel(name = "盘点时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "盘点开始时间")
    private  java.util.Date  checkStartTime;

    /**盘点结束时间*/
    @Excel(name = "盘点结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "盘点结束时间")
    private  java.util.Date  checkEndTime;

    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private  String  note;
}
