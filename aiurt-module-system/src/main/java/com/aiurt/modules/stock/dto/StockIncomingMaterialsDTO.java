package com.aiurt.modules.stock.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zwl
 * @version 1.0
 * @date 2022/1/17
 * @desc
 */
@Data
public class StockIncomingMaterialsDTO implements Serializable {

    /**主键id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;

    /**物资编码*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资编码")
    @Excel(name = "物资编码", width = 15)
    private java.lang.String materialCode;


    /**物资名称*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资名称")
    @Excel(name = "物资名称", width = 15)
    private java.lang.String materialName;

    /**入库数量*/
    @TableField(exist = false)
    @ApiModelProperty(value = "入库数量")
    @Excel(name = "入库数量", width = 15)
    private java.lang.Integer num;


    /**错误原因*/
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private  String  materialMistake;
}
