package com.aiurt.modules.sparepart.entity.dto.resp;

import com.aiurt.common.aspect.annotation.Dict;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;

/**
 * 二级库申领分页列表查询的响应DTO
 *
 * @author 华宜威
 * @date 2023-09-21 14:57:49
 */
@Data
public class SparePartRequisitionListRespDTO implements Serializable {
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
    private java.util.Date applyTime;

    /**申领状态：1待提交、2待确认、3已确认、4审核中、5已通过、6已驳回、7已完成*/
    @ApiModelProperty(value = "申领状态：1待提交、2待确认、3已确认、4审核中、5已通过、6已驳回、7已完成")
    @Dict(dicCode = "material_requisition_status")
    private Integer status;

    /**领用类型（1特殊领用，2普通领用）*/
    @ApiModelProperty(value = "领用类型（1特殊领用，2普通领用）")
    @Dict(dicCode = "material_requisition_apply_type")
    private Integer applyType;

}
