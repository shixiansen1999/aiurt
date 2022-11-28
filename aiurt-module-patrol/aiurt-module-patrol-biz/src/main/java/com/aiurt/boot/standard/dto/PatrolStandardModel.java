package com.aiurt.boot.standard.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import com.aiurt.boot.standard.entity.PatrolStandardItems;
import com.aiurt.common.aspect.annotation.MajorFilterColumn;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/11/25
 * @desc
 */
@Data
public class PatrolStandardModel {
    /**巡检表名*/
    @Excel(name = "巡视标准表名称", width = 15,needMerge = true)
    @ApiModelProperty(value = "巡视标准表名称")
    private java.lang.String name;
    /**专业code*/
    @Excel(name = "适用专业", width = 15,needMerge = true)
    @ApiModelProperty(value = "专业code")
    @MajorFilterColumn
    private java.lang.String professionCode;
    /**与设备类型相关：0否 1 是*/
    @Excel(name = "是否与设备类型相关", width = 15,needMerge = true)
    @ApiModelProperty(value = "与设备类型相关：0否 1 是")
    @TableField(exist = false)
    private java.lang.String isDeviceType;
    /**指定具体设备：0否 1 是*/
    @ApiModelProperty(value = "指定具体设备：0否 1 是")
    private java.lang.Integer specifyDevice;
    @Excel(name = "生效状态", width = 15,needMerge = true)
    @ApiModelProperty(value = "生效状态：0停用 1启用")
    @TableField(exist = false)
    private java.lang.String statusName;
    /**设备类型code*/
    @Excel(name = "设备类型", width = 15,needMerge = true)
    @ApiModelProperty(value = "设备类型code")
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private java.lang.String deviceTypeName;
    /**错误原因*/
    @ApiModelProperty(value = "错误原因")
    @TableField(exist = false)
    private  String  standMistake;
    @ExcelCollection(name = "配置项")
    @ApiModelProperty(value = "配置项")
    @TableField(exist = false)
    private List<PatrolStandardItems> patrolStandardItemsList;
}
