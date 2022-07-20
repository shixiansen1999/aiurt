package com.aiurt.modules.sparepart.entity.dto;

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
    private Integer serialNumber;

    @Excel(name="申领单号",width = 15)
    private String code;

    /**申领仓库 备件库*/
    @Excel(name = "申领仓库", width = 15)
    @ApiModelProperty(value = "申领仓库 备件库")
    private  String  warehouseName;

    @ApiModelProperty("备件类型")
    private Integer materialType;

    @Excel(name = "备件类型", width = 15)
    @ApiModelProperty("备件类型名称")
    private String materialTypeName;

    @Excel(name = "申领数量", width = 15)
    @ApiModelProperty("申领总数量")
    private Integer applyAllNum;


    @ApiModelProperty("所属班组")
    @Excel(name = "所属班组", width = 15)
    private String warehouseDepartment;

    @Excel(name = "保管人", width = 15)
    @ApiModelProperty("保管人")
    private String keeperName;

    /**申领时间*/
    @ApiModelProperty(value = "申领时间")
    private  java.util.Date  applyTime;

    @Excel(name = "申领时间", width = 15)
    private  String  applyTimeString;

    @ApiModelProperty(value = "状态")
    private  Integer  status;

    /**状态名称*/
    @Excel(name = "状态名称", width = 15)
    @ApiModelProperty(value = "状态名称")
    private  String  statusName;

    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private  String  remarks;
}
