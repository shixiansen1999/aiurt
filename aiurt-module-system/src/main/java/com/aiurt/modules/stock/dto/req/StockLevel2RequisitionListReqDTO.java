package com.aiurt.modules.stock.dto.req;

import com.aiurt.modules.base.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 二级库申领分页列表查询的请求DTO
 *
 * @author 华宜威
 * @date 2023-09-21 14:50:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StockLevel2RequisitionListReqDTO extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**申领单号*/
    @ApiModelProperty(value = "申领单号")
    private java.lang.String code;

    /**组织机构编号*/
    @ApiModelProperty(value = "组织机构编号")
    private java.lang.String sysOrgCode;

    /**申领状态：1待提交、2待确认、3已确认、4审核中、5已通过、6已驳回、7已完成*/
    @ApiModelProperty(value = "申领状态：1待提交、2待确认、3已确认、4审核中、5已通过、6已驳回、7已完成")
    private java.lang.Integer status;

    /**申领单类型（1维修领用，3三级库领用，2二级库领用）*/
    @ApiModelProperty(value = "申领单类型（1维修领用，3三级库领用，2二级库领用）")
    private java.lang.Integer materialRequisitionType;

    /**搜索开始时间->领用时间大于等于的时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "搜索开始时间->领用时间大于等于的时间")
    private Date searchBeginTime;

    /**搜索结束时间->领用时间小于等于的时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "搜索结束时间->领用时间小于等于的时间")
    private Date searchEndTime;

}
