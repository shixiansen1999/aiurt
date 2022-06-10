package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Author km
 * @Date 2021/9/17 17:57
 * @Version 1.0
 */
@Data
public class StockApplyExcel {

    @Excel(name="序号",width = 15)
    @TableField(exist = false)
    private Integer serialNumber;

    @Excel(name="申领单号",width = 15)
    @ApiModelProperty(value = "申领单号")
    private String code;

    @Excel(name = "申领仓库", width = 15)
    @ApiModelProperty(value = "申领仓库 备件库")
    private  String  warehouseName;

    @Excel(name = "申领数量", width = 15)
    @ApiModelProperty("申领数量")
    private Integer applyNum;

    @Excel(name = "所属班组", width = 15)
    @ApiModelProperty("申领班组")
    private String department;

    @Excel(name = "保管人", width = 15)
    @ApiModelProperty("保管人")
    private String operatorName;

    @Excel(name = "申领时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申领时间")
    private  java.util.Date  applyTime;

    @ApiModelProperty(value = "状态")
    private  Integer  status;

    @Excel(name = "状态名称", width = 15)
    @ApiModelProperty(value = "状态名称")
    private  String  statusName;

    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private  String  remarks;
}
