package com.aiurt.boot.modules.secondLevelWarehouse.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

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


	@Excel(name = "物资图片", width = 15)
	@ApiModelProperty("图片路径")
	private String pictureUrl;

	/**所属部门*/
	@Excel(name = "所属部门", width = 15)
	@ApiModelProperty(value = "所属部门")
	private String department;

	/**所属系统*/
	@Excel(name = "所属系统", width = 15)
	@ApiModelProperty(value = "所属系统")
	@NotNull(message = "所属系统不能为空")
	private String systemCode;

	/**物资编号*/
	@Excel(name = "物资编号", width = 15)
    @ApiModelProperty(value = "物资编号")
	@NotNull(message = "物资编号不能为空")
	private String code;

	/**物资名称*/
	@Excel(name = "物资名称", width = 15)
    @ApiModelProperty(value = "物资名称")
	@NotNull(message = "物资名称不能为空")
	private String name;

	/**物资类型*/
	@Excel(name = "物资类型", width = 15)
    @ApiModelProperty(value = "物资类型（1：非生产类型 2：生产类型）")
	@NotNull(message = "物资类型不能为空")
	private Integer type;

	/**规格&型号*/
	@Excel(name = "规格&型号", width = 15)
    @ApiModelProperty(value = "规格&型号")
	private String specifications;

	/**大类编号*/
	@Excel(name = "大类编号", width = 15)
	@ApiModelProperty(value = "大类编号")
	@NotBlank(message = "物资大类不能为空")
	private String typeCode;

	/**小类编号*/
	@Excel(name = "小类编号", width = 15)
	@ApiModelProperty(value = "小类编号")
	@NotBlank(message = "物资小类不能为空")
	private String smallTypeCode;

	/**原产地*/
	@Excel(name = "原产地", width = 15)
    @ApiModelProperty(value = "原产地")
	private String countryOrigin;

	/**生产厂家*/
	@Excel(name = "生产厂家", width = 15)
    @ApiModelProperty(value = "生产厂家")
	private String manufacturer;

	/**品牌*/
	@Excel(name = "品牌", width = 15)
    @ApiModelProperty(value = "品牌")
	private String brand;

	/**单位*/
	@Excel(name = "单位", width = 15)
	@ApiModelProperty(value = "单位")
	@NotNull(message = "单位不能为空")
	private String unit;

	/**单价*/
	@Excel(name = "单价", width = 15)
	@ApiModelProperty(value = "单价")
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

	/**备注*/
	@ApiModelProperty(value = "备注")
	private String remark;

	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private java.util.Date createTime;

	/**修改时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private java.util.Date updateTime;

	@ApiModelProperty("物资类型名称")
	@TableField(exist = false)
	private String typeName;

	@ApiModelProperty("设备组件物资数量")
	@TableField(exist = false)
	private Integer amount;

    /**
     * 单价
     */
    @Excel(name = "总价", width = 15)
    @ApiModelProperty(value = "总价")
    private BigDecimal total;

	public static final String CODE = "code";

}
