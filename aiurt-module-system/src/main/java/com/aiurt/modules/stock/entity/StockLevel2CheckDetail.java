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
 * @Description: 二级库盘点任务单详情
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("stock_level2_check_detail")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="二级库盘点任务单详情", description="二级库盘点任务单详情")
public class StockLevel2CheckDetail extends DictEntity {

	/**主键id*/
	@TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
	@JsonSerialize(using = ToStringSerializer.class)
	private  String  id;

	/**盘点任务单号*/
	@Excel(name = "盘点任务单号")
	@ApiModelProperty(value = "盘点任务单号")
	private  String  stockCheckCode;

	/**仓库编号*/
	@Excel(name = "仓库编号")
	@ApiModelProperty(value = "仓库编号")
	@Dict(dictTable ="stock_level2_info",dicText = "warehouse_name",dicCode = "warehouse_code")
	private  String  warehouseCode;
	@Excel(name = "仓库名称")
	@ApiModelProperty(value = "仓库名称")
	@TableField(exist = false)
	private  String  warehouseName;

	/**物资编号*/
	@Excel(name = "物资编号")
	@ApiModelProperty(value = "物资编号")
	private  String  materialCode;

	/**实盘数量*/
	@Excel(name = "实盘数量")
	@ApiModelProperty(value = "实盘数量")
	private  Integer  actualNum;

	/**盘盈数量*/
	@Excel(name = "盘盈数量")
	@ApiModelProperty(value = "盘盈数量")
	private  Integer  profitNum;

	/**盘亏数量*/
	@Excel(name = "盘亏数量")
	@ApiModelProperty(value = "盘亏数量")
	private  Integer  lossNum;

	/**账面价值*/
	@Excel(name = "账面价值")
	@ApiModelProperty(value = "账面价值")
	private  String  bookValue;

	/**账面数量*/
	@Excel(name = "账面数量")
	@ApiModelProperty(value = "账面数量")
	private  Integer  bookNumber;

	/**备注*/
	@Excel(name = "备注")
	@ApiModelProperty(value = "备注")
	private  String  note;

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

	/**物资名称*/
	@Excel(name = "物资名称")
	@ApiModelProperty(value = "物资名称")
	@TableField(exist = false)
	private  String  materialName;

	/**物资类型*/
	@Excel(name = "物资类型")
	@ApiModelProperty(value = "物资类型")
	@TableField(exist = false)
	@Dict(dicCode = "material_type")
	private  String  type;

	/**单位*/
	@Excel(name = "单位")
	@ApiModelProperty(value = "单位")
	@TableField(exist = false)
	@Dict(dicCode = "materian_unit")
	private  String  unit;

}
