package com.aiurt.modules.sparepart.entity.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * @Author km
 * @Date 2021/9/17 17:57
 * @Version 1.0
 */
@Data
public class StockApplyExcel {
    @Excel(name="序号",width = 15)
    private Integer serialNumber;

    @Excel(name="申领状态",width = 15)
    private String status;

    @Excel(name="申领单号",width = 15)
    private String code;

    /**申领仓库*/
    @Excel(name = "申领仓库", width = 15)
    @ApiModelProperty(value = "申领仓库")
    private  String  applyWarehouse;

    /**保管仓库*/
    @Excel(name = "保管仓库", width = 15)
    @ApiModelProperty(value = "保管仓库")
    private  String  custodialWarehouse;

    @Excel(name = "申领数量", width = 15)
    @ApiModelProperty("申领数量")
    private Integer applyNumber;

    @Excel(name = "申领人", width = 15)
    @ApiModelProperty("申领人")
    private String applyUser;

    @Excel(name = "申领时间", width = 15, format = "yyyy-MM-dd HH:mm")
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "申领时间")
    private  String  applyTime;

    /**备注*/
    @Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private  String  remarks;
}
