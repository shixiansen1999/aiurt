package com.aiurt.modules.sparepart.entity.dto.req;

import com.aiurt.modules.sparepart.entity.dto.SparePartRequisitionDetailDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

/**
 * 三级库申领的添加、编辑等请求DTO
 *
 * @author 华宜威
 * @date 2023-09-21 10:22:29
 */
@Data
public class SparePartRequisitionAddReqDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**申领单id*/
    @ApiModelProperty(value = "申领单id")
    private String id;

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
    private String applyLineCode;

    /**申领仓库编号*/
    @ApiModelProperty(value = "申领仓库编号")
    private java.lang.String applyWarehouseCode;

    /**申领二级库仓库编号*/
    @ApiModelProperty(value = "申领二级库仓库编号")
    private java.lang.String leve2WarehouseCode;

    /**保管仓库编号*/
    @ApiModelProperty(value = "保管仓库编号")
    private java.lang.String custodialWarehouseCode;

    /**申领单类型（1维修领用，3三级库领用，2二级库领用）*/
    @ApiModelProperty(value = "申领单类型（1维修领用，3三级库领用，2二级库领用）")
    private java.lang.Integer materialRequisitionType;

    /**领用类型（1特殊领用，2普通领用）*/
    @ApiModelProperty(value = "领用类型（1特殊领用，2普通领用）")
    private java.lang.Integer applyType;

    /**申领状态：1待提交、2待确认、3已确认、4审核中、5已通过、6已驳回、7已完成*/
    @ApiModelProperty(value = "申领状态：1待提交、2待确认、3已确认、4审核中、5已通过、6已驳回、7已完成")
    private java.lang.Integer status;

    /**提交状态（0-未提交 1-已提交）*/
    @ApiModelProperty(value = "提交状态（0-未提交 1-已提交）")

    private java.lang.Integer commitStatus;
    /**是否已被使用(0未使用，1已使用)*/
    @ApiModelProperty(value = "是否已被使用(0未使用，1已使用)")
    private java.lang.Integer isUsed;

    /**用途*/
    @ApiModelProperty(value = "用途")
    private java.lang.String useTo;

    /**关联维修单*/
    @ApiModelProperty(value = "关联维修单")
    private java.lang.String faultRepairRecordId;

    /**物资清单*/
    @ApiModelProperty(value = "物资清单")
    List<SparePartRequisitionDetailDTO> sparePartRequisitionDetailDTOS;

}
