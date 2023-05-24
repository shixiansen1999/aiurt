package com.aiurt.modules.faultcausesolution.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;

/**
 * 故障备件DTO对象
 */
@Data
@AllArgsConstructor
@ApiModel(value = "故障备件DTO对象", description = "故障备件DTO对象")
public class FaultSparePartDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 备件编码
     */
    @Excel(name = "备件编码", width = 15)
    @ApiModelProperty(value = "备件编码")
    private String sparePartCode;
    /**
     * 备件名称
     */
    @Excel(name = "备件名称", width = 15)
    @ApiModelProperty(value = "备件名称")
    private String sparePartName;
    /**
     * 规格型号
     */
    @Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private String specification;
    /**
     * 数量
     */
    @Excel(name = "数量", width = 15)
    @ApiModelProperty(value = "数量")
    private Integer number;
}
