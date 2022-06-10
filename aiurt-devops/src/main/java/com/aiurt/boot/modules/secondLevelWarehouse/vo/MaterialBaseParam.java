package com.aiurt.boot.modules.secondLevelWarehouse.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Author WangHongTao
 * @Date 2021/12/29
 */
@Data
public class MaterialBaseParam {

    /**物资编号*/
    @Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
    private String code;

    /**物资名称*/
    @Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
    private String name;

    /**物资类型*/
    @Excel(name = "物资类型", width = 15)
    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
    private Integer type;

    /**所属系统*/
    @Excel(name = "所属系统", width = 15)
    @ApiModelProperty(value = "所属系统")
    private String systemCode;
}
