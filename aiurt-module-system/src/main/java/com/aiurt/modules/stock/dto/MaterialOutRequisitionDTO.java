package com.aiurt.modules.stock.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.aiurt.modules.stock.entity.StockOutboundMaterials;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author : sbx
 * @description :
 * @date : 2023/9/27 11:43
 */
@Data
public class MaterialOutRequisitionDTO extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**申领单id*/
    @ApiModelProperty(value = "申领单id")
    private String id;
    /**申领单号*/
    @ApiModelProperty(value = "申领单号")
    private String code;
    /**领用单名称*/
    @ApiModelProperty(value = "领用单名称")
    private String name;
    /**申领人ID*/
    @ApiModelProperty(value = "申领人ID")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    private String applyUserId;
    /**组织机构编号*/
    @ApiModelProperty(value = "组织机构编号")
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    private String sysOrgCode;
    /**领用时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "领用时间")
    private Date applyTime;
    /**计划领用时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "计划领用时间")
    private Date planApplyTime;
    /**领用线路编码*/
    @ApiModelProperty(value = "领用线路编码")
    @Dict(dictTable = "cs_line", dicText = "line_name", dicCode = "line_code")
    private String applyLineCode;
    /**申领仓库编号*/
    @ApiModelProperty(value = "申领仓库编号")
    private String applyWarehouseCode;
    /**保管仓库编号*/
    @ApiModelProperty(value = "保管仓库编号")
    private String custodialWarehouseCode;
    /**申领仓库名称*/
    @ApiModelProperty(value = "申领仓库名称")
    private String applyWarehouseName;
    /**保管仓库名称*/
    @ApiModelProperty(value = "保管仓库名称")
    private String custodialWarehouseName;
    /**申领状态：1待提交、2待确认、3已确认、4审核中、5已通过、6已驳回、7已完成*/
    @ApiModelProperty(value = "申领状态：1待提交、2待确认、3已确认、4审核中、5已通过、6已驳回、7已完成")
    @Dict(dicCode = "material_requisition_status")
    private Integer status;
    /**提交状态（0-未提交 1-已提交）*/
    @ApiModelProperty(value = "提交状态（0-未提交 1-已提交）")
    @Dict(dicCode = "material_requisition_commit_status")
    private Integer commitStatus;
    /**申领单类型（1维修领用，3三级库领用，2二级库领用）*/
    @ApiModelProperty(value = "申领单类型（1维修领用，3三级库领用，2二级库领用）")
    private Integer materialRequisitionType;
    /**领用类型（1特殊领用，2普通领用）*/
    @ApiModelProperty(value = "领用类型（1特殊领用，2普通领用）")
    @Dict(dicCode = "material_requisition_apply_type")
    private Integer applyType;
    /**关联维修单*/
    @ApiModelProperty(value = "关联维修单")
    private String faultRepairRecordId;
    /**用途*/
    @ApiModelProperty(value = "用途")
    private String useTo;
    /**备注*/
    @ApiModelProperty(value = "备注")
    private String remarks;
    /**出库操作用户ID*/
    @ApiModelProperty(value = "出库操作用户ID")
    @Dict(dictTable ="sys_user",dicText = "realname",dicCode = "id")
    private String userId;
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
    @ApiModelProperty(value = "出库时间")
    private Date outTime;
    @ApiModelProperty(value = "出库单备注")
    private  String  outOrderRemark;
    /**物资申领数量合计*/
    @ApiModelProperty(value = "物资出库合计")
    private Integer applyTotalCount;
    /**出库合计*/
    @ApiModelProperty(value = "物资出库合计")
    private Integer totalCount;
    /**出库单号*/
    @ApiModelProperty(value = "出库单号")
    private  String  orderCode;
    /**物资(二级库出库）*/
    @ApiModelProperty(value = "物资(二级库出库）")
    private List<StockOutboundMaterials> stockOutboundMaterialsList;
}
