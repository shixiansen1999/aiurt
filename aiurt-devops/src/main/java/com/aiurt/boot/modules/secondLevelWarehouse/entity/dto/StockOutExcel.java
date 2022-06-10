package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author Administrator
 */
@Data
public class StockOutExcel {
    @Excel(name="序号",width = 15)
    private Integer serialNumber;

    @Excel(name="出库单号",width = 15)
    private String code;

    /**申领仓库 备件库*/
    @Excel(name = "出库仓库", width = 15)
    @ApiModelProperty(value = "出库仓库 二级库")
    private  String  outWarehouseName;


//    @Excel(name = "出库数量", width = 15)
//    @ApiModelProperty("出库总数量")
//    private Integer stockOutAllNum;
//
//    @ApiModelProperty("所属班组")
//    private String outOrganizationId;
//    @ApiModelProperty("所属部门")
//    @Excel(name = "所属部门", width = 15)
//    private String outOrganizationName;

    @Excel(name = "所属部门", width = 15)
    @ApiModelProperty("所属部门")
    private String department;

    @Excel(name = "出库数量", width = 10)
    @ApiModelProperty("出库数量")
    private Integer num;

    @Excel(name = "保管人", width = 15)
    @ApiModelProperty("保管人")
    private String operatorName;

    /**出库时间*/
    @ApiModelProperty(value = "出库时间")
    @Excel(name = "出库时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    private  java.util.Date  stockOutTime;

    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private  String  remarks;
}
