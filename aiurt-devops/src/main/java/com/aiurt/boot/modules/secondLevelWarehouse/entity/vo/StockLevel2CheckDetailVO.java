package com.aiurt.boot.modules.secondLevelWarehouse.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.aiurt.boot.modules.secondLevelWarehouse.entity.StockLevel2CheckDetail;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author km
 * @Date 2021/9/18 16:25
 * @Version 1.0
 */
@Data
public class StockLevel2CheckDetailVO extends StockLevel2CheckDetail {
    @ApiModelProperty("所在仓库")
    private String warehouseName;
    @ApiModelProperty("物资名称")
    private String materialName;
    @ApiModelProperty("物资类型")
    private Integer type;
    @ApiModelProperty("规格")
    private String specifications;
    @ApiModelProperty("单位")
    private String unit;
    @ApiModelProperty("单价")
    private BigDecimal price;
    @ApiModelProperty("账面数量")
    private Integer bookNum;
    @ApiModelProperty("账面价值")
    private BigDecimal bookPrice;
    @ApiModelProperty("存放地")
    private String storagePlace;
    /**使用时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "使用时间")
    private Date useTime;
}
