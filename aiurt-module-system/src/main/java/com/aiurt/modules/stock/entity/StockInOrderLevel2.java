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

import java.util.List;

/**
 * @Description: 二级库入库单信息表
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("stock_in_order_level2")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="二级库入库单信息表", description="二级库入库单信息表")
public class StockInOrderLevel2 extends DictEntity {

	/**主键id*/
	@TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
	@JsonSerialize(using = ToStringSerializer.class)
	private  String  id;

	/**入库单号*/
	@Excel(name = "入库单号")
	@ApiModelProperty(value = "入库单号")
	private  String  orderCode;

	/**机构编码*/
	@ApiModelProperty(value = "机构编码")
	@Excel(name="机构编码",width=15)
	@DeptFilterColumn
	private String orgCode;

	/**年份*/
	@Excel(name = "仓库编号")
	@ApiModelProperty(value = "仓库编号")
	@Dict(dictTable ="stock_level2_info",dicText = "warehouse_name",dicCode = "warehouse_code")
	private  String  warehouseCode;
	@Excel(name = "仓库名称")
	@ApiModelProperty(value = "仓库名称")
	@TableField(exist = false)
	private  String  warehouseName;

	/**入库时间 CURRENT_TIMESTAMP*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm")
	@ApiModelProperty(value = "入库时间")
	private  java.util.Date  entryTime;

	/**入库时间开始 CURRENT_TIMESTAMP*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "入库时间开始")
	@TableField(exist = false)
	private  java.util.Date  entryTimeBegin;

	/**入库时间结束 CURRENT_TIMESTAMP*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "入库时间结束")
	@TableField(exist = false)
	private  java.util.Date  entryTimeEnd;

	/**入库操作用户ID*/
	@Excel(name = "入库操作用户ID")
	@ApiModelProperty(value = "入库操作用户ID")
	@Dict(dictTable ="sys_user",dicText = "realname",dicCode = "id")
	private  String  userId;

	/**入库单状态：1待提交、2待确认、3已确认*/
	@Excel(name = "入库单状态")
	@ApiModelProperty(value = "入库单状态：1待提交、2待确认、3已确认")
	@Dict(dicCode = "stock_in_order_level2_status")
	private  String  status;

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

	@ApiModelProperty(value = "提报物资列表")
	@TableField(exist = false)
	private List<StockIncomingMaterials> stockIncomingMaterialsList;

	/**锚点*/
	@TableField(exist = false)
	private  Integer  anchor;

	/**登录用户管理部门权限集合*/
	@TableField(exist = false)
	private List<String> userOrgCodes;
}
