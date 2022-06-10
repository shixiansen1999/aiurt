package com.aiurt.boot.modules.secondLevelWarehouse.entity.dto;

import com.sun.istack.NotNull;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartApplyMaterial;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Author km
 * @Date 2021/9/17 19:14
 * @Version 1.0
 */
@Data
public class SpareApplyMaterialDTO extends SparePartApplyMaterial {
    /**物资名称*/
    @ApiModelProperty(value = "物资名称")
    private String name;
    /**物资类型*/
    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
    private Integer type;
    /**规格类型*/
    @ApiModelProperty(value = "规格&型号")
    private String specifications;
    /**原产地*/
    @ApiModelProperty(value = "原产地")
    private String countryOrigin;
    /**生产商*/
    @ApiModelProperty(value = "生产商")
    private String manufacturer;
    /**品牌*/
    @ApiModelProperty(value = "品牌")
    private String brand;
}
