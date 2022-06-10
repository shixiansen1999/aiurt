package com.aiurt.boot.modules.fault.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import javax.validation.constraints.NotNull;

@Data
public class SparePartDTO {

    /**设备id*/
    @Excel(name = "设备id", width = 15)
    @ApiModelProperty(value = "设备id")
    private Long deviceId;

    /**原备件编号*/
    @Excel(name = "原备件编号", width = 15)
    @ApiModelProperty(value = "原备件编号")
    private String oldSparePartCode;

    /**原备件数量*/
    @Excel(name = "原备件数量", width = 15)
    @ApiModelProperty(value = "原备件数量")
    @NotNull(message = "备件更换数量不能为空")
    private Integer oldSparePartNum;

    /**原备件仓库*/
    @Excel(name = "原备件仓库", width = 15)
    @ApiModelProperty(value = "原备件仓库")
    private String oldSparePartDepot;

    /**新备件编号*/
    @Excel(name = "新备件编号", width = 15)
    @ApiModelProperty(value = "新备件编号")
    private String newSparePartCode;

    /**新备件数量*/
    @Excel(name = "新备件数量", width = 15)
    @ApiModelProperty(value = "新备件数量")
    private Integer newSparePartNum;

    /**新备件仓库*/
    @Excel(name = "新备件仓库", width = 15)
    @ApiModelProperty(value = "新备件仓库")
    private String newSparePartDepot;
}
