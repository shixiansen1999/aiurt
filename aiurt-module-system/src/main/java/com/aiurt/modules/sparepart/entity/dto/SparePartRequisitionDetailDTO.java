package com.aiurt.modules.sparepart.entity.dto;

import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 三级库申领领用物资明细的DTO
 * 
 * @author 华宜威
 * @date 2023-09-18 16:27:34
 */
@Data
public class SparePartRequisitionDetailDTO implements Serializable {
    private static final long serialVersionUID = 1L;

	/**申领单表ID*/
    @ApiModelProperty(value = "申领单表ID")
    private String materialRequisitionId;
	/**物资编号*/
    @ApiModelProperty(value = "物资编号")
    private String materialsCode;
	/**物资名称*/
    @ApiModelProperty(value = "物资名称")
    private String materialsName;
	/**申请出库数量*/
    @ApiModelProperty(value = "申请出库数量")
    private Integer applyNum;
	/**实际出库数量*/
    @ApiModelProperty(value = "实际出库数量")
    private Integer actualNum;
	/**单位*/
    @ApiModelProperty(value = "单位")
    @Dict(dicCode = "materian_unit")
    private String unit;
	/**参考单价*/
    @ApiModelProperty(value = "参考单价")
    private BigDecimal price;
	/**参考总价*/
    @ApiModelProperty(value = "参考总价")
    private BigDecimal totalPrices;
	/**备注*/
    @ApiModelProperty(value = "备注")
    private String remarks;

}
