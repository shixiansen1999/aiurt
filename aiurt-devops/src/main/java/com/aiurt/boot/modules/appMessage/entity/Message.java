package com.aiurt.boot.modules.appMessage.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Description: 消息
 * @Author: swsc
 * @Date: 2021-10-29
 * @Version: V1.0
 */
@Data
@TableName("t_message")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "message对象", description = "消息")
public class Message {

	/**
	 * id
	 */
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "id")
	private Long id;

	/**
	 * 标题
	 */
	@Excel(name = "标题", width = 15)
	@ApiModelProperty(value = "标题")
	private String title;

	/**
	 * 消息内容
	 */
	@Excel(name = "消息内容", width = 15)
	@ApiModelProperty(value = "消息内容")
	private String content;

	/**
	 * 消息内容
	 */
	@Excel(name = "消息类型", width = 15,replace = "普通消息_0,特情信息_1,工作日志_2")
	@ApiModelProperty(value = "消息类型")
	private Integer type;

	/**
	 * 保留字段,可存跳转参数
	 */
	@ApiModelProperty(value = "保留字段,可存跳转参数")
	private String code;

	/**
	 * 删除状态 0-未删除 1-已删除
	 */
	@Excel(name = "删除状态 0-未删除 1-已删除", width = 15)
	@ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
	private Integer delFlag;

	/**
	 * 创建人
	 */
	@Excel(name = "创建人", width = 15)
	@ApiModelProperty(value = "创建人")
	private String createBy;

	/**
	 * 修改人
	 */
	@Excel(name = "修改人", width = 15)
	@ApiModelProperty(value = "修改人")
	private String updateBy;

	/**
	 * 创建时间 CURRENT_TIMESTAMP
	 */
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "MM月dd日 HH:mm")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间")
	private Date createTime;

	/**
	 * 修改时间 根据当前时间戳更新
	 */
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "修改时间")
	private Date updateTime;
}
