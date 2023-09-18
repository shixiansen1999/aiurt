package com.aiurt.modules.stock.entity;

import com.aiurt.common.system.base.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * 出入库记录表实体类
 * 
 * @author 华宜威
 * @date 2023-09-18 16:43:37
 */
@Data
@TableName("material_stock_out_in_record")
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="出入库记录表", description="出入库记录表")
public class MaterialStockOutInRecord extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

	/**物资编号*/
    @ApiModelProperty(value = "物资编号")
    private java.lang.String materialCode;
	/**出库的仓库编码*/
    @ApiModelProperty(value = "出库的仓库编码")
    private java.lang.String warehouseCode;
	/**组织机构编号*/
    @ApiModelProperty(value = "组织机构编号")
    private java.lang.String sysOrgCode;
	/**出库数量*/
    @ApiModelProperty(value = "出库数量")
    private java.lang.Integer num;
	/**确认时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "确认时间")
    private java.util.Date confirmTime;
	/**确认人ID*/
    @ApiModelProperty(value = "确认人ID")
    private java.lang.String confirmUserId;
	/**出库单号*/
    @ApiModelProperty(value = "出库单号")
    private java.lang.String orderCode;
	/**备件出库单状态：1待确认、2已确认*/
    @ApiModelProperty(value = "备件出库单状态：1待确认、2已确认")
    private java.lang.Integer status;
	/**领料单表ID*/
    @ApiModelProperty(value = "领料单表ID")
    private java.lang.String materialRequisitionId;
	/**记录类型（3三级库出入库，2二级库出入库）*/
    @ApiModelProperty(value = "记录类型（3三级库出入库，2二级库出入库）")
    private java.lang.Integer materialRequisitionType;
	/**库存结余*/
    @ApiModelProperty(value = "库存结余")
    private java.lang.Integer balance;
	/**出入库类型（待定）*/
    @ApiModelProperty(value = "出入库类型（待定）")
    private java.lang.Integer outType;
	/**备注*/
    @ApiModelProperty(value = "备注")
    private java.lang.String remarks;
	/**删除状态(0.未删除 1.已删除)*/
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
    private java.lang.Integer delFlag;

}
