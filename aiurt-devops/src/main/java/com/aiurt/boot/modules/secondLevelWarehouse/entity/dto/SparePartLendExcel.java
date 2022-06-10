package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Author km
 * @Date 2021/9/22 17:56
 * @Version 1.0
 */
@Data
public class SparePartLendExcel {
    @Excel(name="序号",width = 15)
    @TableField(exist = false)
    private Integer serialNumber;

    /**物资编号*/
    @Excel(name = "备件编号", width = 15)
    @ApiModelProperty(value = "备件编号")
    private  String  materialCode;

    /**备件名称*/
    @Excel(name = "备件名称", width = 15)
    @ApiModelProperty(value = "备件名称")
    private  String  materialName;


    @ApiModelProperty(value = "备件类型")
    private  Integer  type;
    /**备件类型名称*/
    @Excel(name = "备件类型", width = 15)
    @ApiModelProperty(value = "备件类型")
    private  String  typeName;

    /**规格&型号*/
    @Excel(name = "规格&型号", width = 15)
    @ApiModelProperty(value = "规格&型号")
    private String specifications;

    /**原产地*/
    @Excel(name = "原产地", width = 15)
    @ApiModelProperty(value = "原产地")
    private String countryOrigin;
    /**生产商*/
    @Excel(name = "生产商", width = 15)
    @ApiModelProperty(value = "生产商")
    private String manufacturer;
    /**品牌*/
    @Excel(name = "品牌", width = 15)
    @ApiModelProperty(value = "品牌")
    private String brand;

    /**借出数量*/
    @Excel(name = "借出数量", width = 15)
    @ApiModelProperty(value = "借出数量")
    private  Integer  lendNum;

    /**借出时间*/
    @Excel(name = "借出时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "借出时间")
    private  java.util.Date  lendTime;

    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private  String  remarks;

    /**还回时间*/
    @Excel(name = "还回时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "还回时间")
    private  java.util.Date  backTime;

    @ApiModelProperty(value = "状态")
    private  Integer  status;
    /**状态（0-未还 1-已还）*/
    @Excel(name = "状态（0-未还 1-已还）", width = 15)
    @ApiModelProperty(value = "状态（0-未还 1-已还）")
    private  String  statusString;

    /**借出仓库*/
    @Excel(name = "借出仓库", width = 15)
    @ApiModelProperty(value = "借出仓库")
    private  String  warehouseName;
}
