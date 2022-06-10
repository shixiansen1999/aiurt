package com.aiurt.boot.modules.training.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Description: 培训计划
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Data
@TableName("training_plan")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="training_plan对象", description="培训计划")
public class TrainingPlan {

	/**主键id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id")
	private  Long  id;

	/**计划名称*/
	@Excel(name = "计划名称", width = 15)
    @ApiModelProperty(value = "计划名称")
	private  String  name;

	/**主讲人*/
	@Excel(name = "主讲人", width = 15)
    @ApiModelProperty(value = "主讲人")
	private  String  presenter;

	/**培训方式 数据字典配置*/
	@Excel(name = "培训方式 数据字典配置", width = 15)
    @ApiModelProperty(value = "培训方式 数据字典配置")
	private  Integer  trainingMethods;

	/**培训类型 数据字典配置*/
	@Excel(name = "培训类型 数据字典配置", width = 15)
    @ApiModelProperty(value = "培训类型 数据字典配置")
	private  Integer  trainingType;

	/**培训地点*/
	@Excel(name = "培训地点", width = 15)
    @ApiModelProperty(value = "培训地点")
	private  String  address;

	/**开始日期*/
	@Excel(name = "开始日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "开始日期")
	private  java.util.Date  startDate;

	/**结束日期*/
	@Excel(name = "结束日期", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "结束日期")
	private  java.util.Date  endDate;

	/**课时（分钟）*/
	@Excel(name = "课时（分钟）", width = 15)
    @ApiModelProperty(value = "课时（分钟）")
	private  Integer  classHour;

	/**课件id集合*/
	@Excel(name = "课件id集合", width = 15)
    @ApiModelProperty(value = "课件id集合")
	private  String  coursewares;

	/**说明*/
	@Excel(name = "说明", width = 15)
    @ApiModelProperty(value = "说明")
	private  String  remarks;

	/**删除状态 0-未删除 1-已删除*/
	@Excel(name = "删除状态 0-未删除 1-已删除", width = 15)
    @ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
	private  Integer  delFlag;

	/**创建人*/
	@Excel(name = "创建人", width = 15)
    @ApiModelProperty(value = "创建人")
	private  String  createBy;

	/**修改人*/
	@Excel(name = "修改人", width = 15)
    @ApiModelProperty(value = "修改人")
	private  String  updateBy;

	/**创建时间*/
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建时间")
	private  java.util.Date  createTime;

	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private  java.util.Date  updateTime;

	/**培训对象*/
	@Excel(name = "培训对象", width = 15)
	@ApiModelProperty(value = "培训对象")
	@TableField(exist = false)
	private ArrayList<String> planObj;

	/**二维码*/
	@Excel(name = "二维码", width = 15)
	@ApiModelProperty(value = "二维码")
	@TableField(exist = false)
	private  java.util.Date  QRCode;

	/**开始时间*/
	@Excel(name = "开始时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "开始时间")
	@TableField(exist = false)
	private  java.util.Date  startTime;

	/**结束时间*/
	@Excel(name = "结束时间", width = 15, format = "yyyy-MM-dd")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern="yyyy-MM-dd")
	@ApiModelProperty(value = "结束时间")
	@TableField(exist = false)
	private  java.util.Date  endTime;


    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PRESENTER = "presenter";
    public static final String TRAINING_METHODS = "training_methods";
    public static final String TRAINING_TYPE = "training_type";
    public static final String ADDRESS = "address";
    public static final String START_DATE = "start_date";
    public static final String END_DATE = "end_date";
    public static final String CLASS_HOUR = "class_hour";
    public static final String COURSEWARES = "coursewares";
    public static final String REMARKS = "remarks";
    public static final String DEL_FLAG = "del_flag";
    public static final String CREATE_BY = "create_by";
    public static final String UPDATE_BY = "update_by";
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATE_TIME = "update_time";


}
