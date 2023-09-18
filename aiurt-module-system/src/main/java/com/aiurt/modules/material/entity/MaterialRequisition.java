package com.aiurt.modules.material.entity;

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
 * 领料单
 *
 * @author 华宜威
 * @date 2023-09-18 16:07:55
 */
@Data
@TableName("material_requisition")
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "领料单", description = "领料单")
public class MaterialRequisition extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**申领单号*/
    @ApiModelProperty(value = "申领单号")
    private java.lang.String code;
    /**领用单名称*/
    @ApiModelProperty(value = "领用单名称")
    private java.lang.String name;
    /**申领人ID*/
    @ApiModelProperty(value = "申领人ID")
    private java.lang.String applyUserId;
    /**组织机构编号*/
    @ApiModelProperty(value = "组织机构编号")
    private java.lang.String sysOrgCode;
    /**领用时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "领用时间")
    private java.util.Date applyTime;
    /**申领仓库编号*/
    @ApiModelProperty(value = "申领仓库编号")
    private java.lang.String applyWarehouseCode;
    /**申领数量*/
    @ApiModelProperty(value = "申领数量")
    private java.lang.Integer applyNumber;
    /**保管仓库编号*/
    @ApiModelProperty(value = "保管仓库编号")
    private java.lang.String custodialWarehouseCode;
    /**申领状态：1待提交、2待确认、3已确认*/
    @ApiModelProperty(value = "申领状态：1待提交、2待确认、3已确认")
    private java.lang.Integer status;
    /**提交状态（0-未提交 1-已提交）*/
    @ApiModelProperty(value = "提交状态（0-未提交 1-已提交）")
    private java.lang.Integer commitStatus;
    /**申领单类型（1维修领用，3三级库领用，2二级库领用）*/
    @ApiModelProperty(value = "申领单类型（1维修领用，3三级库领用，2二级库领用）")
    private java.lang.Integer materialRequisitionType;
    /**领用类型（1特殊领用，2普通领用）*/
    @ApiModelProperty(value = "领用类型（1特殊领用，2普通领用）")
    private java.lang.Integer applyType;
    /**关联维修单*/
    @ApiModelProperty(value = "关联维修单")
    private java.lang.String faultRepairRecordId;
    /**用途*/
    @ApiModelProperty(value = "用途")
    private java.lang.String useTo;
    /**备注*/
    @ApiModelProperty(value = "备注")
    private java.lang.String remarks;
    /**删除状态(0.未删除 1.已删除)*/
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
    private java.lang.Integer delFlag;


}
