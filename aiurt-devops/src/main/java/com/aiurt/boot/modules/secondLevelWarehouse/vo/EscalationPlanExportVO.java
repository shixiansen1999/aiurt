package com.aiurt.boot.modules.secondLevelWarehouse.vo;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @description: EscalationPlanExportVO
 * @author: Mr.zhao
 * @date: 2021/12/6 15:10
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class EscalationPlanExportVO implements Serializable {

	private static final long serialVersionUID = 1L;


	/**
	 * 年份
	 */
	@Excel(name = "年份", width = 15)
	@ApiModelProperty(value = "年份")
	private String reportYear;

	/**
	 * 提报类型
	 */
	@Dict(dicCode = "escalation_type")
	@Excel(name = "提报类型", width = 15)
	@ApiModelProperty(value = "提报类型")
	private String reportType;

	/**
	 * 专业类型
	 */
	@Excel(name = "专业类型", width = 15)
	@ApiModelProperty(value = "专业类型")
	private String specialtyType;

	/**
	 * 系统类型名称
	 */
	@Excel(name = "系统名称", width = 15)
	@ApiModelProperty(value = "系统名称")
	private String systemName;

	/**
	 * 物资名称
	 */
	@Excel(name = "物资名称", width = 15)
	@ApiModelProperty(value = "物资名称")
	private String name;

	/**
	 * 单位
	 */
	@Excel(name = "单位", width = 15)
	@ApiModelProperty(value = "单位")
	private String unit;

	/**
	 * 品牌
	 */
	@Excel(name = "品牌", width = 15)
	@ApiModelProperty(value = "品牌")
	private String brand;

	/**
	 * 规格&型号
	 */
	@Excel(name = "规格&型号", width = 15)
	@ApiModelProperty(value = "规格&型号")
	private String specifications;

	/**
	 * 技术参数
	 */
	@Excel(name = "技术参数", width = 15)
	@ApiModelProperty(value = "技术参数")
	private String parameter;

	/**
	 * 计划采购数量
	 */
	@Excel(name = "计划采购数量", width = 15)
	@ApiModelProperty(value = "计划采购数量")
	private Integer nums;

	/**
	 * 到货数量
	 */
	@Excel(name = "到货数量", width = 15)
	@ApiModelProperty(value = "到货数量")
	private Integer arrivalNum;

	/**
	 * 参考单价
	 */
	@Excel(name = "参考单价", width = 15)
	@ApiModelProperty(value = "参考单价")
	private BigDecimal unitPrice;

	/**
	 * 参考总价
	 */
	@Excel(name = "参考总价", width = 15)
	@ApiModelProperty(value = "参考总价")
	private BigDecimal totalPrice;

	/**
	 * 资金出处
	 */
	@Dict(dicCode = "source_funds")
	@Excel(name = "资金出处", width = 15)
	@ApiModelProperty(value = "资金出处")
	@TableField(value = "source_funds")
	private String sourceFunds;

	/**
	 * 进货时间
	 */
	@Excel(name = "进货时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
	@DateTimeFormat(pattern = "yyyy-MM-dd",iso = DateTimeFormat.ISO.DATE)
	@ApiModelProperty(value = "进货时间")
	private Date purchaseTime;

	/**
	 * 提报部门
	 */
	@Excel(name = "提报部门", width = 15)
	private String orgId;

	/**
	 * 图片url
	 */
	@Excel(name = "图片地址", width = 25)
	@ApiModelProperty(value = "图片url")
	private String imgUrl;


}
