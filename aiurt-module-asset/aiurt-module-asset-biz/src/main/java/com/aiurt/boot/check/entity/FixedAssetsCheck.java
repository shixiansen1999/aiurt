package com.aiurt.boot.check.entity;

import com.aiurt.boot.asset.entity.FixedAssets;
import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: fixed_assets_check
 * @Author: aiurt
 * @Date:   2023-01-11
 * @Version: V1.0
 */
@Data
@TableName("fixed_assets_check")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fixed_assets_check对象", description="fixed_assets_check")
public class FixedAssetsCheck implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**盘点任务单号*/
	@Excel(name = "盘点任务单号", width = 15)
    @ApiModelProperty(value = "盘点任务单号")
    private java.lang.String inventoryList;
	/**适用组织机构编码*/
	@Excel(name = "适用组织机构编码", width = 15)
    @ApiModelProperty(value = "适用组织机构编码")
    private java.lang.String orgCode;
    /**适用组织机构编码*/
    @Excel(name = "适用组织机构名称", width = 15)
    @ApiModelProperty(value = "适用组织机构名称")
    @TableField(exist = false)
    private java.lang.String orgName;
    /**适用组织机构编码*/
    @Excel(name = "盘点状态", width = 15)
    @ApiModelProperty(value = "盘点状态")
    @Dict(dicCode = "fixed_assets_check_status")
    private java.lang.Integer status;
	/**资产分类编码*/
	@Excel(name = "资产分类编码", width = 15)
    @ApiModelProperty(value = "资产分类编码")
    private java.lang.String categoryCode;
    @Excel(name = "资产分类名称", width = 15)
    @ApiModelProperty(value = "资产分类名称")
    @TableField(exist = false)
    private java.lang.String categoryName;
	/**盘点数量*/
	@Excel(name = "盘点数量", width = 15)
    @ApiModelProperty(value = "盘点数量")
    private java.lang.Integer number;
	/**盘点人ID*/
	@Excel(name = "盘点人ID", width = 15)
    @ApiModelProperty(value = "盘点人ID")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    private java.lang.String checkId;
	/**审核人ID*/
	@Excel(name = "审核人ID", width = 15)
    @ApiModelProperty(value = "审核人ID")
    @Dict(dictTable = "sys_user", dicText = "realname", dicCode = "id")
    private java.lang.String auditId;
	/**盘点计划起始日期*/
	@Excel(name = "盘点计划起始日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "盘点计划起始日期")
    private java.util.Date planStartDate;
	/**盘点计划截止日期*/
	@Excel(name = "盘点计划截止日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "盘点计划截止日期")
    private java.util.Date planEndDate;
    @ApiModelProperty(value = "盘点计划日期范围")
    @TableField(exist = false)
    private String time;
	/**实际开始时间*/
	@Excel(name = "实际开始时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "实际开始时间")
    private java.util.Date actualStartTime;
	/**实际结束时间*/
	@Excel(name = "实际结束时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "实际结束时间")
    private java.util.Date actualEndTime;
	/**盘点类型(1年盘、2半年盘、3季盘)*/
	@Excel(name = "盘点类型(1年盘、2半年盘、3季盘)", width = 15)
    @ApiModelProperty(value = "盘点类型(1年盘、2半年盘、3季盘)")
    @Dict(dicCode = "check_type")
    private java.lang.Integer checkType;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
    @ApiModelProperty(value = "提交状态： 0未提交 1已提交")
    private java.lang.Integer isSubmit;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
    @TableField(exist = false)
    private List<FixedAssets> fixedAssetsList;
}
