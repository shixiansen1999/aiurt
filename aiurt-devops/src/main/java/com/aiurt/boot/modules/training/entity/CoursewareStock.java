package com.aiurt.boot.modules.training.entity;

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
 * @Description: 课件库
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
@Data
@TableName("courseware_stock")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="courseware_stock对象", description="课件库")
public class CoursewareStock {

	/**主键id*/
	@TableId(type= IdType.AUTO)
    @ApiModelProperty(value = "主键id")
	private  Long  id;

	/**课件名称*/
	@Excel(name = "课件名称", width = 15)
    @ApiModelProperty(value = "课件名称")
	private  String  name;

	/**类型 1-pdf 2-音频 3-视频*/
	@Excel(name = "类型 1-pdf 2-音频 3-视频", width = 15)
    @ApiModelProperty(value = "类型 1-pdf 2-音频 3-视频")
	private  Integer  type;

	/**地址*/
	@Excel(name = "地址", width = 15)
    @ApiModelProperty(value = "地址")
	private  String  url;

	/**版本号*/
	@Excel(name = "版本号", width = 15)
    @ApiModelProperty(value = "版本号")
	private  String  version;

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
	private  Date  createTime;

	/**修改时间*/
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "修改时间")
	private  Date  updateTime;

	/**开始时间*/
	@Excel(name = "开始时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "开始时间")
	@TableField(exist = false)
	private  Date  startTime;

	/**结束时间*/
	@Excel(name = "结束时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "结束时间")
	@TableField(exist = false)
	private  Date  endTime;


    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String TYPE = "type";
    private static final String URL = "url";
    private static final String VERSION = "version";
    private static final String DEL_FLAG = "del_flag";
    private static final String CREATE_BY = "create_by";
    private static final String UPDATE_BY = "update_by";
    private static final String CREATE_TIME = "create_time";
    private static final String UPDATE_TIME = "update_time";


}
