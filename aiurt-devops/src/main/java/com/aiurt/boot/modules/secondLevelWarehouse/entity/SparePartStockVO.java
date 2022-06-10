package com.aiurt.boot.modules.secondLevelWarehouse.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 备件仓库信息
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Data
public class SparePartStockVO {


	@ApiModelProperty(value = "id")
	private  Integer  id;

	@ApiModelProperty(value = "编号")
	private  String  warehouseCode;

	@ApiModelProperty(value = "名称")
	private  String  warehouseName;

	@ApiModelProperty(value = "状态0启用1停用")
	private  Integer  status;

	@ApiModelProperty(value = "备注")
	private  String  description;

	@ApiModelProperty(value = "删除状态0未删除1已删除")
	private  Integer  delFlag;

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "createTime")
	private  Date  createTime;

	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "updateTime")
	private  Date  updateTime;



}
