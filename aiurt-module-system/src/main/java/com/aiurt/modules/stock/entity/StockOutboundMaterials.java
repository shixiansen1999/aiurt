package com.aiurt.modules.stock.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.modules.basic.entity.DictEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
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

/**
 * @Description: 出库物资表
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("stock_outbound_materials")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="出库物资表", description="出库物资表")
public class StockOutboundMaterials extends DictEntity {

	/**主键id*/
	@TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
	@JsonSerialize(using = ToStringSerializer.class)
	private  String  id;

	/**出库单号*/
	@Excel(name = "出库单号")
	@ApiModelProperty(value = "出库单号")
	private  String  outOrderCode;

	/**物资编号*/
	@Excel(name = "物资编号")
	@ApiModelProperty(value = "物资编号")
	private  String  materialCode;

	/**出库仓库编号*/
	@Excel(name = "出库仓库编号")
	@ApiModelProperty(value = "出库仓库编号")
	@Dict(dictTable ="stock_level2_info",dicText = "warehouse_name",dicCode = "warehouse_code")
	private  String  warehouseCode;

	/**仓库现有库存*/
	@Excel(name = "仓库现有库存")
	@ApiModelProperty(value = "仓库现有库存")
	private  Integer  inventory;

	/**申请出库数量*/
	@Excel(name = "申请出库数量")
	@ApiModelProperty(value = "申请出库数量")
	private  Integer  applyOutput;

	/**出库数量*/
	@Excel(name = "实际出库数量")
	@ApiModelProperty(value = "实际出库数量")
	private  Integer  actualOutput;

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
}
