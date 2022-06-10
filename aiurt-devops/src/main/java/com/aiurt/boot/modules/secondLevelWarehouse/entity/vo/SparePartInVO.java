package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import com.swsc.copsms.modules.secondLevelWarehouse.entity.SparePartInOrder;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Author km
 * @Date 2021/9/22 19:24
 * @Version 1.0
 */
@Data
public class SparePartInVO extends SparePartInOrder {
    @ApiModelProperty("备件名称")
    private String materialName;
    @ApiModelProperty("物资类型")
    private Integer type;
    @ApiModelProperty("规格")
    private String specifications;
    @ApiModelProperty(value = "原产地")
    private String countryOrigin;
    @ApiModelProperty(value = "生产商")
    private String manufacturer;
    @ApiModelProperty(value = "品牌")
    private String brand;
    @ApiModelProperty(value = "存放仓库名称")
    private String warehouseName;
    @ApiModelProperty("保管人")
    private String keeperName;


}
