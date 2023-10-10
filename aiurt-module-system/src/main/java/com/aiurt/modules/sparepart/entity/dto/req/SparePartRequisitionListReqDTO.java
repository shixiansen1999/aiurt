package com.aiurt.modules.sparepart.entity.dto.req;

import com.aiurt.modules.base.BaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 三级库申领分页列表查询的请求DTO
 *
 * @author 华宜威
 * @date 2023-09-21 14:50:46
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SparePartRequisitionListReqDTO extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**申领单号*/
    @ApiModelProperty(value = "申领单号")
    private String code;

    /**组织机构编号*/
    @ApiModelProperty(value = "组织机构编号")
    private String sysOrgCode;

    /**申领仓库编号*/
    @ApiModelProperty(value = "申领仓库编号")
    private String applyWarehouseCode;

    /**申领状态：1待提交、2待确认、3已确认、4审核中、5已通过、6已驳回、7已完成*/
    @ApiModelProperty(value = "申领状态：1待提交、2待确认、3已确认、4审核中、5已通过、6已驳回、7已完成")
    private String status;

    /**申领单类型（1维修领用，3三级库领用，2二级库领用）*/
    @ApiModelProperty(value = "申领单类型（1维修领用，3三级库领用，2二级库领用）")
    private String materialRequisitionType;

    /**申领单类型（1维修领用，3三级库领用，2二级库领用）*/
    @ApiModelProperty(value = "多申领单类型查询,逗号隔开")
    @TableField(exist = false)
    private String materialRequisitionTypes;

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

    /**app搜索框*/
    @ApiModelProperty(value = "app搜索框")
    private java.lang.String search;
}
