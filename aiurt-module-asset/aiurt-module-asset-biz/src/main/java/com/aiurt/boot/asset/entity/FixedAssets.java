package com.aiurt.boot.asset.entity;

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

/**
 * @Description: fixed_assets
 * @Author: aiurt
 * @Date:   2023-01-11
 * @Version: V1.0
 */
@Data
@TableName("fixed_assets")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fixed_assets对象", description="fixed_assets")
public class FixedAssets implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**资产名称*/
	@Excel(name = "资产名称", width = 15)
    @ApiModelProperty(value = "资产名称")
    private java.lang.String assetName;
    /**资产名称*/
    @Excel(name = "资产编号", width = 15)
    @ApiModelProperty(value = "资产编号")
    private java.lang.String assetCode;
	/**资产分类编码*/
	@Excel(name = "资产分类编码", width = 15)
    @ApiModelProperty(value = "资产分类编码")
    private java.lang.String categoryCode;
	/**使用组织机构编码*/
	@Excel(name = "使用组织机构编码", width = 15)
    @ApiModelProperty(value = "使用组织机构编码")
    private java.lang.String orgCode;
	/**账面数量*/
	@Excel(name = "账面数量", width = 15)
    @ApiModelProperty(value = "账面数量")
    private java.lang.Integer number;
    /**实盘数量*/
    @Excel(name = "实盘数量", width = 15)
    @ApiModelProperty(value = "实盘数量")
    @TableField(exist = false)
    private java.lang.Integer actualNumber;
    /**实盘数量*/
    @Excel(name = "盘盈盘亏", width = 15)
    @ApiModelProperty(value = "盘盈盘亏")
    @TableField(exist = false)
    private java.lang.Integer num;
	/**存放地点编码*/
	@Excel(name = "存放地点编码", width = 15)
    @ApiModelProperty(value = "存放地点编码")
    private java.lang.String location;
	/**责任人ID*/
	@Excel(name = "责任人ID", width = 15)
    @ApiModelProperty(value = "责任人ID")
    private java.lang.String responsibilityId;
	/**规格型号*/
	@Excel(name = "规格型号", width = 15)
    @ApiModelProperty(value = "规格型号")
    private java.lang.String specification;
	/**计量单位(1个、2栋、3台)*/
	@Excel(name = "计量单位(1个、2栋、3台)", width = 15)
    @ApiModelProperty(value = "计量单位(1个、2栋、3台)")
    private java.lang.Integer units;
	/**房产证号*/
	@Excel(name = "房产证号", width = 15)
    @ApiModelProperty(value = "房产证号")
    private java.lang.String houseNumber;
	/**建成/购置时间*/
	@Excel(name = "建成/购置时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "建成/购置时间")
    private java.util.Date buildBuyDate;
	/**建筑面积*/
	@Excel(name = "建筑面积", width = 15)
    @ApiModelProperty(value = "建筑面积")
    private java.math.BigDecimal coveredArea;
	/**启用状态(0停用、1启用)*/
	@Excel(name = "启用状态(0停用、1启用)", width = 15)
    @ApiModelProperty(value = "启用状态(0停用、1启用)")
    private java.lang.Integer status;
	/**折旧年限*/
	@Excel(name = "折旧年限", width = 15)
    @ApiModelProperty(value = "折旧年限")
    private java.lang.String depreciableLife;
	/**使用年限*/
	@Excel(name = "使用年限", width = 15)
    @ApiModelProperty(value = "使用年限")
    private java.lang.String durableYears;
	/**开始使用日期*/
	@Excel(name = "开始使用日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始使用日期")
    private java.util.Date startDate;
	/**账面原值*/
	@Excel(name = "账面原值", width = 15)
    @ApiModelProperty(value = "账面原值")
    private java.math.BigDecimal assetOriginal;
	/**累计折旧*/
	@Excel(name = "累计折旧", width = 15)
    @ApiModelProperty(value = "累计折旧")
    private java.math.BigDecimal accumulatedDepreciation;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date updateTime;
}
