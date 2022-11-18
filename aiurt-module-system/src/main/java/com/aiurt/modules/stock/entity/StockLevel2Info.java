package com.aiurt.modules.stock.entity;

import com.aiurt.common.aspect.annotation.DeptFilterColumn;
import com.aiurt.common.aspect.annotation.Dict;
import com.aiurt.common.system.base.annotation.ExcelExtend;
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

/**
 * @Description: 二级库
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Data
@TableName("stock_level2_info")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="二级库", description="二级库")
public class StockLevel2Info extends DictEntity {

	/**主键id*/
	@TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
	@JsonSerialize(using = ToStringSerializer.class)
	private  String  id;

	/**仓库编号*/
	@Excel(name = "二级库编号",width = 15)
	@ExcelExtend(isRequired = true,remark = "必填字段，且不能重复，支持数字，英文字母，符号等")
	@ApiModelProperty(value = "仓库编号")
	private  String  warehouseCode;

	/**仓库名称*/
	@Excel(name = "二级库名称",width = 15)
	@ExcelExtend(isRequired = true,remark = "必填字段，不能重复")
	@ApiModelProperty(value = "仓库名称")
	private  String  warehouseName;


	/**所属部门*/
	@ApiModelProperty(value = "所属部门")
	@DeptFilterColumn
	private String orgCode;
	/**组织机构id*/
	@Excel(name = "组织机构",width = 15,dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
	@ExcelExtend(isRequired = true,remark = "必填字段，且与系统下拉项保持一致")
	@ApiModelProperty(value = "组织机构id")
	@Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
	private  String  organizationId;
	/**组织机构id手动翻译*/
	@ApiModelProperty(value = "组织机构id手动翻译")
	@TableField(exist = false)
	private  String  organizationIdName;

	/**二级库状态：0停用 1启用*/
    @ApiModelProperty(value = "二级库状态：0停用 1启用")
	@Dict(dicCode = "stock_level2_info_status")
	private  Integer  status;

	/**备注*/
	@Excel(name = "备注",width = 15)
	@ExcelExtend(remark = "选填字段")
	@ApiModelProperty(value = "备注")
	private  String  remark;

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

	/**入库时间开始*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "入库时间开始")
	@TableField(exist = false)
	private  java.util.Date  startTime;

	/**入库时间结束*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "入库时间结束")
	@TableField(exist = false)
	private  java.util.Date  endTime;
}
