package com.aiurt.boot.modules.manage.entity;

import java.io.Serializable;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 二级仓库基础表
 * @Author: swsc
 * @Date:   2021-09-22
 * @Version: V1.0
 */
@Data
@TableName("cs_stock_level_two")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="cs_stock_level_two对象", description="二级仓库基础表")
public class StockLevelTwo {

	/**id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "id")
	private  Integer  id;

	/**编号*/
	@Excel(name = "编号", width = 15)
    @ApiModelProperty(value = "编号")
	private  String  stockCode;

	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
	private  String  stockName;

	/**状态0启用1停用*/
	@Excel(name = "状态0启用1停用", width = 15)
    @ApiModelProperty(value = "状态0启用1停用")
	private  Integer  status;

	/**备注*/
	@Excel(name = "备注", width = 15)
    @ApiModelProperty(value = "备注")
	private  String  description;

	/**删除状态0未删除1已删除*/
	@Excel(name = "删除状态0未删除1已删除", width = 15)
    @ApiModelProperty(value = "删除状态0未删除1已删除")
	private  Integer  delFlag;

	/**createTime*/
	@Excel(name = "createTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "createTime")
	private  Date  createTime;

	/**updateTime*/
	@Excel(name = "updateTime", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "updateTime")
	private  Date  updateTime;


    private static final String ID = "id";
    private static final String STOCK_CODE = "stock_code";
    private static final String STOCK_NAME = "stock_name";
    private static final String STATUS = "status";
    private static final String DESCRIPTION = "description";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";


}
