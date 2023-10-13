package com.aiurt.modules.material.entity;

import com.aiurt.common.system.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 领用物资明细
 * 
 * @author 华宜威
 * @date 2023-09-18 16:27:34
 */
@Data
@TableName("material_requisition_detail")
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="领用物资明细", description="领用物资明细")
public class MaterialRequisitionDetail extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**备件申领单表ID*/
    @ApiModelProperty(value = "备件申领单表ID")
    private String materialRequisitionId;
    /**物资id,关联material_base.id*/
    private String materialsId;
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
	/**删除状态(0.未删除 1.已删除)*/
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
    private Integer delFlag;
    /**可使用数量*/
    @ApiModelProperty(value = "可使用数量")
    private Integer availableNum;
    /**仓库编号*/
    @TableField(exist = false)
    @ApiModelProperty(value = "仓库编号")
    private String warehouseCode;
    /**出库记录id*/
    @TableField(exist = false)
    @ApiModelProperty(value = "出库记录id")
    private String orderId;
}
