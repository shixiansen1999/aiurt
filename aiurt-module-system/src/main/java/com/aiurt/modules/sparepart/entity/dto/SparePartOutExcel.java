package com.aiurt.modules.sparepart.entity.dto;

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

    /**物资编号*/
    @Excel(name = "备件编号", width = 15)
    @ApiModelProperty(value = "备件编号")
    private  String  materialCode;

    /**备件名称*/
    @Excel(name = "备件名称", width = 15)
    @ApiModelProperty(value = "备件名称")
    private  String  materialName;

    /**所在仓库*/
    @Excel(name = "所在仓库", width = 15)
    @ApiModelProperty(value = "所在仓库")
    private  String  warehouseName;

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
    @Excel(name = "出库数量", width = 15)
    @ApiModelProperty(value = "出库数量")
    private  Integer  num;


    /**出库时间*/
    @Excel(name = "出库时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "出库时间")
    private  java.util.Date  createTime;

}
