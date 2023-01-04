package com.aiurt.modules.sparepart.entity.dto;
/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023/1/4
 * @time: 12:25
 */


import cn.afterturn.easypoi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.modules.sparepart.entity.SparePartInOrder;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023-01-04 12:25
 */
@Data
public class SparePartInOrderImportExcelDTO implements Serializable {

    /**所属专业*/
    @Excel(name = "专业", width = 15)
    @ApiModelProperty(value = "专业名称")
    private  String  majorName;

    /**子系统名称*/
    @Excel(name = "子系统", width = 15)
    @ApiModelProperty(value = "子系统名称")
    private  String  systemName;

    /**仓库编码*/
    @ApiModelProperty(value = "仓库编码")
    private String warehouseCode;

    /**仓库名称*/
    @Excel(name = "保管仓库", width = 15)
    @ApiModelProperty(value = "仓库名称")
    @TableField(exist = false)
    private String warehouseName;

    /**物资编号*/
    @Excel(name = "物资编码", width = 15)
    @ApiModelProperty(value = "物资编号")
    private String materialCode;

    /**名称*/
    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
    @TableField(exist = false)
    private  String  name;


    /**入库数量*/
    @Excel(name = "入库数量", width = 15)
    @ApiModelProperty(value = "入库数量")
    private String num;

    /**
     * 备件入库错误原因
     */
    @ApiModelProperty(value = "备件入库错误原因")
    @TableField(exist = false)
    private String errorReason;


}
