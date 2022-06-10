package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import com.sun.istack.NotNull;
import com.swsc.copsms.modules.secondLevelWarehouse.entity.StockInOrderLevel2Detail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Author km
 * @Date 2021/9/16 17:55
 * @Version 1.0
 */
@Data
public class StockInDetailVO extends StockInOrderLevel2Detail {

    @ApiModelProperty(value = "物资名称")
    private String materialName;

    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
    private Integer type;

    @ApiModelProperty(value = "类型名称")
    private String typeName;

    @ApiModelProperty(value = "规格&型号")
    private String specifications;

    @ApiModelProperty(value = "原产地")
    private String countryOrigin;

    @ApiModelProperty(value = "生产商")
    private String manufacturer;

    @ApiModelProperty(value = "品牌")
    private String brand;

    @ApiModelProperty(value = "单位")
    private String unit;


}
