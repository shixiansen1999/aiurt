package com.aiurt.modules.stock.dto;


import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


/**
 * @author zwl
 * @version 1.0
 * @date 2022/1/17
 * @desc
 */
@Data
public class StockInOrderLevel2DTO implements Serializable {

    /**主键id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "主键id")
    private java.lang.String id;


    /**入库仓库*/
    @TableField(exist = false)
    @ApiModelProperty(value = "入库仓库")
    @Excel(name = "入库仓库", width = 15)
    private java.lang.String warehouseName;


    /**入库人*/
    @TableField(exist = false)
    @ApiModelProperty(value = "入库人")
    @Excel(name = "入库人", width = 15)
    private java.lang.String realName;

    /**入库人工号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "入库人工号")
    @Excel(name = "入库人工号", width = 15)
    private java.lang.String workNo;

    /**备注*/
    @TableField(exist = false)
    @ApiModelProperty(value = "备注")
    @Excel(name = "备注", width = 15)
    private java.lang.String note;


    /**物资信息*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资信息")
    @ExcelCollection(name = "物资信息")
    private List<StockIncomingMaterialsDTO> stockIncomingMaterialsDTOList;


    /**错误原因*/
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private  String stockInOrderLevelMistake;
}
