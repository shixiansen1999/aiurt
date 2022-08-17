package com.aiurt.modules.sparepart.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 更换记录
 * @author fgw
 */
@Data
public class SparePartReplaceDTO implements Serializable {

    private static final long serialVersionUID = -748463721663411450L;


    /**维修记录单号*/
    @ApiModelProperty(value = "维修记录单号")
    private String maintenanceRecord;

    /**出库记录表ID*/
    @ApiModelProperty(value = "出库记录表ID")
    private String outOrderId;

    /**物资编码*/
    @ApiModelProperty(value = "物资编码")
    private String materialsCode;

    /**被替换的组件编码*/
    @ApiModelProperty(value = "被替换的组件编码")
    private String replaceSubassemblyCode;

    /**替换的组件编码*/
    @ApiModelProperty(value = "替换的组件编码")
    private String subassemblyCode;
}
