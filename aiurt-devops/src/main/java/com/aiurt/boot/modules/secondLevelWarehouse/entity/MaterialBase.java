package com.aiurt.boot.modules.secondLevelWarehouse.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.*;
import com.sun.istack.NotNull;
import com.swsc.copsms.common.enums.MaterialTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 物资基础信息
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
@Data
@TableName("material_base")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="material_base对象", description="物资基础信息")
public class MaterialBase {


	/**主键id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id")
	private Long id;
	@ApiModelProperty("图片路径")
	private String pictureUrl;
	/**物资编号*/
	@Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
	@NotNull
	private String code;
	/**物资名称*/
	@Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
	@NotNull
	private String name;
	/**仓库编号*/
	@Excel(name = "仓库编号", width = 15)
    @ApiModelProperty(value = "仓库编号")
	private String warehouseCode;
	/**仓库名称*/
	@Excel(name = "仓库名称", width = 15)
    @ApiModelProperty(value = "仓库名称")
	private String warehouseName;

	/**物资类型*/
	@Excel(name = "物资类型", width = 15)
    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
	@NotNull
	private Integer type;


	/**规格类型*/
	@Excel(name = "规格类型", width = 15)
    @ApiModelProperty(value = "规格&型号")
	private String specifications;
	/**原产地*/
	@Excel(name = "原产地", width = 15)
    @ApiModelProperty(value = "原产地")
	private String countryOrigin;
	/**生产商*/
	@Excel(name = "生产商", width = 15)
    @ApiModelProperty(value = "生产商")
	private String manufacturer;
	/**品牌*/
	@Excel(name = "品牌", width = 15)
    @ApiModelProperty(value = "品牌")
	private String brand;
	/**单位*/
	@Excel(name = "单位", width = 15)
	@ApiModelProperty(value = "单位")
	@NotNull
	private String unit;

	/**价格*/
	@Excel(name = "价格", width = 15)
	@ApiModelProperty(value = "价格")
	@NotNull
	private BigDecimal price;


	/**删除状态(0.未删除 1.已删除)*/
    @ApiModelProperty(value = "删除状态(0.未删除 1.已删除)")
	@TableLogic
	private Integer delFlag;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
	private String createBy;
	/**修改人*/
    @ApiModelProperty(value = "修改人")
	private String updateBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private Date createTime;
	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private Date updateTime;

	@ApiModelProperty("物资类型名称")
	@TableField(exist = false)
	private String typeName;



}
