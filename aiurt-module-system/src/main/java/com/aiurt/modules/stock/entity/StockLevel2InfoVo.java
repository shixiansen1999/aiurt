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
public class StockLevel2InfoVo extends DictEntity {

	/**主键id*/
	@TableId(type= IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
	@JsonSerialize(using = ToStringSerializer.class)
	private  String  id;

	/**仓库名称*/
	@Excel(name = "仓库名称",width = 15)
	@ExcelExtend(isRequired = true)
	@ApiModelProperty(value = "仓库名称")
	private  String  warehouseName;

	/**仓库编号*/
	@Excel(name = "仓库编号",width = 15)
	@ExcelExtend(isRequired = true)
	@ApiModelProperty(value = "仓库编号")
	private  String  warehouseCode;

	/**备注*/
	@Excel(name = "备注")
	@ApiModelProperty(value = "备注")
	private  String  remark;

	/**所属部门*/
	@ApiModelProperty(value = "所属部门")
	@DeptFilterColumn
	private String orgCode;
	/**组织机构id*/
	@Excel(name = "组织机构id",width = 15,dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
	@ExcelExtend(isRequired = true)
	@ApiModelProperty(value = "组织机构id")
	@Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "id")
	private  String  organizationId;
	/**组织机构id手动翻译*/
	@ApiModelProperty(value = "组织机构id手动翻译")
	@TableField(exist = false)
	private  String  organizationIdName;

	/**二级库状态：0停用 1启用*/
	@Excel(name = "二级库状态",width = 15,dicCode = "stock_level2_info_status")
	@ExcelExtend(isRequired = true)
    @ApiModelProperty(value = "二级库状态：0停用 1启用")
	@Dict(dicCode = "stock_level2_info_status")
	private  Integer  status;

	/**错误原因*/
	@Excel(name = "错误原因", width = 15)
	@ApiModelProperty(value = "错误原因")
	private String errorCause;

}
