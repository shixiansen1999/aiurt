package com.aiurt.boot.check.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import com.aiurt.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: fixed_assets_check_detail
 * @Author: aiurt
 * @Date:   2023-01-17
 * @Version: V1.0
 */
@Data
@TableName("fixed_assets_check_detail")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="fixed_assets_check_detail对象", description="fixed_assets_check_detail")
public class FixedAssetsCheckDetail implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**盘点任务表ID*/
	@Excel(name = "盘点任务表ID", width = 15)
    @ApiModelProperty(value = "盘点任务表ID")
    private String checkId;
	/**资产编号*/
	@Excel(name = "资产编号", width = 15)
    @ApiModelProperty(value = "资产编号")
    private String assetCode;
	/**资产名称*/
	@Excel(name = "资产名称", width = 15)
    @ApiModelProperty(value = "资产名称")
    private String assetName;
	/**存放地点编码*/
	@Excel(name = "存放地点编码", width = 15)
    @ApiModelProperty(value = "存放地点编码")
    private String location;
	/**资产分类编码*/
	@Excel(name = "资产分类编码", width = 15)
    @ApiModelProperty(value = "资产分类编码")
    @Dict(dictTable = "fixed_assets_category", dicCode = "category_code", dicText = "category_name")
    private String categoryCode;
	/**盘点前账面数量*/
	@Excel(name = "盘点前账面数量", width = 15)
    @ApiModelProperty(value = "盘点前账面数量")
    private Integer beforeNumber;
	/**变更后账面数量*/
	@Excel(name = "变更后账面数量", width = 15)
    @ApiModelProperty(value = "变更后账面数量")
    private Integer afterNumber;
	/**删除状态： 0未删除 1已删除*/
	@Excel(name = "删除状态： 0未删除 1已删除", width = 15)
    @ApiModelProperty(value = "删除状态： 0未删除 1已删除")
    private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
