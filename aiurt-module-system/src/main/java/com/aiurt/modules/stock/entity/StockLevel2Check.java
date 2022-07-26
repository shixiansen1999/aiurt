package com.aiurt.modules.stock.entity;

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

import java.util.List;

/**
 * @Description: 二级库盘点任务表
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("stock_level2_check")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="二级库盘点任务表", description="二级库盘点任务表")
public class StockLevel2Check extends DictEntity {

	/**主键id*/
	@TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
	@JsonSerialize(using = ToStringSerializer.class)
	private  String  id;

	/**盘点任务单号*/
	@Excel(name = "盘点任务单号")
	@ApiModelProperty(value = "盘点任务单号")
	private  String  stockCheckCode;

	/**年份*/
	@Excel(name = "仓库编号")
	@ApiModelProperty(value = "仓库编号")
	@Dict(dictTable ="stock_level2_info",dicText = "warehouse_name",dicCode = "warehouse_code")
	private  String  warehouseCode;
	@Excel(name = "仓库名称")
	@ApiModelProperty(value = "仓库名称")
	@TableField(exist = false)
	private  String  warehouseName;


	/**计划开始时间开始 CURRENT_TIMESTAMP*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "计划开始时间开始")
	@TableField(exist = false)
	private  java.util.Date  planStartTimeStart;


	/**计划开始时间结束 CURRENT_TIMESTAMP*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "计划开始时间结束")
	@TableField(exist = false)
	private  java.util.Date  planStartTimeEnd;

	/**计划开始时间 CURRENT_TIMESTAMP*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	@ApiModelProperty(value = "计划开始时间")
	private  java.util.Date  planStartTime;

	/**计划结束时间 CURRENT_TIMESTAMP*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	@ApiModelProperty(value = "计划结束时间")
	private  java.util.Date  planEndTime;

	/**盘点开始时间 CURRENT_TIMESTAMP*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	@ApiModelProperty(value = "盘点开始时间")
	private  java.util.Date  checkStartTime;

	/**盘点结束时间 CURRENT_TIMESTAMP*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	@ApiModelProperty(value = "盘点结束时间")
	private  java.util.Date  checkEndTime;

	/**盘点数量*/
	@Excel(name = "盘点数量")
	@ApiModelProperty(value = "盘点数量")
	private  Integer  checkNum;

	/**盘点人id*/
	@Excel(name = "盘点人id")
	@ApiModelProperty(value = "盘点人id")
	@Dict(dictTable ="sys_user",dicText = "realname",dicCode = "id")
	private  String  checkerId;

	/**备注*/
	@Excel(name = "备注")
	@ApiModelProperty(value = "备注")
	private  String  note;

	/**盘点任务状态：1待下发，2待确认，3待执行，4执行中，5已完成*/
	@Excel(name = "盘点任务状态")
	@ApiModelProperty(value = "盘点任务状态：1待下发，2待确认，3待执行，4执行中，5已完成")
	@Dict(dicCode = "stock_level2_check_status")
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
	private List<StockLevel2CheckDetail> stockLevel2CheckDetailList;
}
