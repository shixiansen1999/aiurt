package com.aiurt.modules.material.dto;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

/**
 * 领料单详情的响应DTO
 * @author : sbx
 * @date : 2023/9/21 23:03
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MaterialRequisitionInfoDTO extends DictEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**申领单id*/
    @ApiModelProperty(value = "申领单id")
    private java.lang.String id;
    /**申领单号*/
    @ApiModelProperty(value = "申领单号")
    private java.lang.String code;
    /**领用单名称*/
    @ApiModelProperty(value = "领用单名称")
    private java.lang.String name;
    /**申领人ID*/
    @ApiModelProperty(value = "申领人ID")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    private java.lang.String applyUserId;
    /**组织机构编号*/
    @ApiModelProperty(value = "组织机构编号")
    @Dict(dictTable = "sys_depart", dicText = "depart_name", dicCode = "org_code")
    private java.lang.String sysOrgCode;
    /**领用时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "领用时间")
    private java.util.Date applyTime;
    /**计划领用时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "计划领用时间")
    private java.util.Date planApplyTime;
    /**领用线路编码*/
    @ApiModelProperty(value = "领用线路编码")
    @Dict(dictTable = "cs_line", dicText = "line_name", dicCode = "line_code")
    private String applyLineCode;
    /**申领仓库编号*/
    @ApiModelProperty(value = "申领仓库编号")
    private java.lang.String applyWarehouseCode;
    /**保管仓库编号*/
    @ApiModelProperty(value = "保管仓库编号")
    private java.lang.String custodialWarehouseCode;
    /**申领仓库名称*/
    @ApiModelProperty(value = "申领仓库名称")
    private String applyWarehouseName;
    @ApiModelProperty(value = "申领二级库仓库编号")
    private java.lang.String leve2WarehouseCode;
    /**申领二级库仓库名称*/
    @ApiModelProperty(value = "申领二级库仓库名称")
    private java.lang.String leve2WarehouseName;
    /**保管仓库名称*/
    @ApiModelProperty(value = "保管仓库名称")
    private String custodialWarehouseName;
    /**申领状态：1待提交、2待确认、3已确认、4审核中、5已通过、6已驳回、7已完成*/
    @ApiModelProperty(value = "申领状态：1待提交、2待确认、3已确认、4审核中、5已通过、6已驳回、7已完成")
    @Dict(dicCode = "material_requisition_status")
    private java.lang.Integer status;
    /**提交状态（0-未提交 1-已提交）*/
    @ApiModelProperty(value = "提交状态（0-未提交 1-已提交）")
    @Dict(dicCode = "material_requisition_commit_status")
    private java.lang.Integer commitStatus;
    /**申领单类型（1维修领用，3三级库领用，2二级库领用）*/
    @ApiModelProperty(value = "申领单类型（1维修领用，3三级库领用，2二级库领用）")
    private java.lang.Integer materialRequisitionType;
    /**领用类型（1特殊领用，2普通领用）*/
    @ApiModelProperty(value = "领用类型（1特殊领用，2普通领用）")
    @Dict(dicCode = "material_requisition_apply_type")
    private java.lang.Integer applyType;
    /**关联维修单*/
    @ApiModelProperty(value = "关联维修单")
    @Dict(dictTable = "fault_repair_record", dicCode = "id", dicText = "fault_code")
    private java.lang.String faultRepairRecordId;
    /**用途*/
    @ApiModelProperty(value = "用途")
    private java.lang.String useTo;
    /**备注*/
    @ApiModelProperty(value = "备注")
    private java.lang.String remarks;

    /**申领的物资清单*/
    @ApiModelProperty(value = "申领的物资清单")
    List<MaterialRequisitionDetailInfoDTO> materialRequisitionDetailInfoDTOList;
}
