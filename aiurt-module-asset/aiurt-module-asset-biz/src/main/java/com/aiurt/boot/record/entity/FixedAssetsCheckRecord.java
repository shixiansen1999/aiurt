package com.aiurt.boot.record.entity;

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
 * @Description: fixed_assets_check_record
 * @Author: aiurt
 * @Date:   2023-01-11
 * @Version: V1.0
 */
@Data
@TableName("fixed_assets_check_record")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fixed_assets_check_record对象", description="fixed_assets_check_record")
public class FixedAssetsCheckRecord implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**盘点任务表主键*/
	@Excel(name = "盘点任务表主键", width = 15)
    @ApiModelProperty(value = "盘点任务表主键")
    private java.lang.String checkId;
	/**资产编号*/
	@Excel(name = "资产编号", width = 15)
    @ApiModelProperty(value = "资产编号")
    private java.lang.String assetCode;
	/**资产名称*/
	@Excel(name = "资产名称", width = 15)
    @ApiModelProperty(value = "资产名称")
    private java.lang.String assetName;
	/**存放地点编码*/
	@Excel(name = "存放地点编码", width = 15)
    @ApiModelProperty(value = "存放地点编码")
    private java.lang.String location;
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
    private java.lang.Integer actualNumber;
    /**盘盈/盘亏数量*/
    @Excel(name = "盘盈/盘亏数量", width = 15)
    @ApiModelProperty(value = "盘盈/盘亏数量")
    private java.lang.Integer profitLoss;
	/**自用资产数量*/
	@Excel(name = "自用资产数量", width = 15)
    @ApiModelProperty(value = "自用资产数量")
    private java.lang.Integer oneselfAssetNumber;
	/**他用资产数量*/
	@Excel(name = "他用资产数量", width = 15)
    @ApiModelProperty(value = "他用资产数量")
    private java.lang.Integer othersAssetNumber;
	/**累计折旧*/
	@Excel(name = "累计折旧", width = 15)
    @ApiModelProperty(value = "累计折旧")
    private java.math.BigDecimal accumulatedDepreciation;
	/**闲置资产数量*/
	@Excel(name = "闲置资产数量", width = 15)
    @ApiModelProperty(value = "闲置资产数量")
    private java.lang.Integer leisureAssetNumber;
	/**闲置资产数量面积*/
	@Excel(name = "闲置资产数量面积", width = 15)
    @ApiModelProperty(value = "闲置资产数量面积")
    private java.math.BigDecimal leisureArea;
	/**资产抵押、质押及担保情况*/
	@Excel(name = "资产抵押、质押及担保情况", width = 15)
    @ApiModelProperty(value = "资产抵押、质押及担保情况")
    private java.lang.String hypothecate;
	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
    private java.lang.String remark;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private java.lang.Integer delFlag;
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
}
