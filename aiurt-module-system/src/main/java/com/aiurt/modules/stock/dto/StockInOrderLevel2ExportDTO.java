package com.aiurt.modules.stock.dto;


import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;


/**
 * @author zwl
 * @version 1.0
 * @date 2022/1/17
 * @desc
 */
@Data
public class StockInOrderLevel2ExportDTO implements Serializable {

    /**主键id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "主键id")
    private String id;

    /**入库单号*/
    @ApiModelProperty(value = "入库单号")
    private  String  orderCode;

    /**仓库编码*/
    @ApiModelProperty(value = "仓库编号")
    @Dict(dictTable ="stock_level2_info",dicText = "warehouse_name",dicCode = "warehouse_code")
    private  String  warehouseCode;
    /**入库仓库*/
    @TableField(exist = false)
    @ApiModelProperty(value = "入库仓库")
    @Excel(name = "入库仓库", width = 15,needMerge = true)
    private String warehouseName;


    /**入库人*/
    @TableField(exist = false)
    @ApiModelProperty(value = "入库人")
    @Excel(name = "入库人", width = 15,needMerge = true)
    private String realName;

    /**入库操作用户ID*/
    @ApiModelProperty(value = "入库操作用户ID")
    @Dict(dictTable ="sys_user",dicText = "realname",dicCode = "id")
    private  String  userId;

    /**入库时间 CURRENT_TIMESTAMP*/
    @Excel(name = "入库时间", width = 15,needMerge = true)
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "入库时间")
    private  String  entryTime;

    /**入库单状态：1待提交、2待确认、3已确认*/
    @Excel(name = "入库单状态",width = 15,needMerge = true)
    @ApiModelProperty(value = "入库单状态：1待提交、2待确认、3已确认")
    @Dict(dicCode = "stock_in_order_level2_status")
    private  String  status;

    /**入库人工号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "入库人工号")
    private String workNo;

    /**备注*/
    @TableField(exist = false)
    @ApiModelProperty(value = "备注")
    @Excel(name = "备注", width = 15,needMerge = true)
    private String note;


    /**物资信息*/
    @TableField(exist = false)
    @ApiModelProperty(value = "物资信息")
    @ExcelCollection(name = "物资信息")
    private List<StockIncomingMaterialsExportDTO> stockIncomingMaterialsDTOList;

}
