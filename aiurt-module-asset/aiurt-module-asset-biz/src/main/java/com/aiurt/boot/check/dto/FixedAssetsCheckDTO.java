package com.aiurt.boot.check.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * 盘点任务分页列表的DTO对象
 */
@Data
@ApiModel(value = "盘点任务分页列表的DTO对象", description = "盘点任务分页列表的DTO对象")
public class FixedAssetsCheckDTO {
    /**
     * 盘点任务单号
     */
    @ApiModelProperty(value = "盘点任务单号")
    private java.lang.String inventoryList;
    /**
     * 适用组织机构编码
     */
    @ApiModelProperty(value = "适用组织机构编码")
    private java.lang.String orgCode;
    /**
     * 盘点状态
     */
    @ApiModelProperty(value = "盘点状态")
    private java.lang.Integer status;
    /**
     * 资产分类编码
     */
    @ApiModelProperty(value = "资产分类编码")
    private java.lang.String categoryCode;
    /**
     * 资产分类编码
     */
    @ApiModelProperty(value = "资产分类编码所有子级")
    private List<String> categoryCodes;
    /**
     * 盘点人ID
     */
    @ApiModelProperty(value = "盘点人ID")
    private java.lang.String checkId;
    /**
     * 盘点计划起始日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "盘点计划起始日期")
    private java.util.Date planStartDate;
    /**
     * 盘点计划截止日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @ApiModelProperty(value = "盘点计划截止日期")
    private java.util.Date planEndDate;
    /**
     * 审核分页查询接口标志，审核分页查询传true
     */
    @ApiModelProperty(value = "审核分页查询接口标志，审核分页查询传true")
    private boolean flag;
    /**
     * 待审核状态
     */
    private Integer auditStatus;
    /**
     * 用户账号
     */
    private String userName;
}
