package com.aiurt.modules.material.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author : sbx
 * @description : 班组库领用物资明细信息
 * @date : 2023/9/21 23:52
 */
@Data
public class MaterialRequisitionDetailInfoDTO extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**主键*/
    @ApiModelProperty(value = "主键")
    private String id;
    /**备件申领单表ID*/
    @ApiModelProperty(value = "备件申领单表ID")
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
    /**单位翻译*/
    @ApiModelProperty(value = "单位翻译")
    private String unitName;
    /**参考单价*/
    @ApiModelProperty(value = "参考单价")
    private BigDecimal price;
    /**参考总价*/
    @ApiModelProperty(value = "参考总价")
    private BigDecimal totalPrices;
    /**备注*/
    @ApiModelProperty(value = "备注")
    private String remarks;
    /**删除状态(0.未删除 1.已删除)*/
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
    private Integer delFlag;
    /**规格&型号*/
    @ApiModelProperty(value = "规格&型号")
    private String specifications;
}
