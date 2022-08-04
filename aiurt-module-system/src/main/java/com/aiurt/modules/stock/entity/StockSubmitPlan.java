package com.aiurt.modules.stock.entity;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

/**
 * @Description: 物资提报计划表
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("stock_submit_plan")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="物资提报计划表", description="物资提报计划表")
public class StockSubmitPlan extends DictEntity {

	/**主键id*/
	@TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
	@JsonSerialize(using = ToStringSerializer.class)
	private  String  id;

	/**提报计划编号*/
	@Excel(name = "提报计划编号")
	@ApiModelProperty(value = "提报计划编号")
	private  String  code;

	/**年份*/
	@Excel(name = "年份")
	@ApiModelProperty(value = "年份")
	private Integer  year;

	/**提报类型*/
	@Excel(name = "提报类型")
	@ApiModelProperty(value = "提报类型")
	@Dict(dicCode = "stock_submit_plan_submit_type")
	private  String  submitType;

	/**提报部门ID*/
	@Excel(name = "提报部门ID")
    @ApiModelProperty(value = "提报部门ID")
	@Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
	private  String  orgId;
	/**机构编码*/
	@ApiModelProperty(value = "机构编码")
	@Excel(name="机构编码",width=15)
	@DeptFilterColumn
	private String orgCode;
	/**机构名称*/
	@ApiModelProperty(value = "机构名称")
	@TableField(exist = false)
	private String departName;

	/**提报用户ID*/
	@Excel(name = "提报用户ID")
	@ApiModelProperty(value = "提报用户ID")
	@Dict(dictTable ="sys_user",dicText = "realname",dicCode = "id")
	private  String  userId;

	/**提报时间 CURRENT_TIMESTAMP*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	@ApiModelProperty(value = "提报时间 CURRENT_TIMESTAMP")
	private  java.util.Date  submitTime;

	/**提报计划状态：1待提交、2已提交*/
	@Excel(name = "提报计划状态")
	@ApiModelProperty(value = "提报计划状态：1待提交、2已提交")
	@Dict(dicCode = "stock_submit_plan_status")
	private  String  status;

	/**创建人*/
    @ApiModelProperty(value = "创建人")
	@Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
	private  String  createBy;

	/**修改人*/
    @ApiModelProperty(value = "修改人")
	private  String  updateBy;

	/**创建时间 CURRENT_TIMESTAMP*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间 CURRENT_TIMESTAMP")
	private  java.util.Date  createTime;

	/**修改时间 根据当前时间戳更新*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间 根据当前时间戳更新")
	private  java.util.Date  updateTime;

	@ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
	@TableLogic
	private  Integer  delFlag;

	@ApiModelProperty(value = "提报物资列表")
	@TableField(exist = false)
	private List<StockSubmitMaterials> stockSubmitMaterialsList;
}
