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
 * @Description: 消息是否已读
 * @Author: swsc
 * @Date: 2021-10-29
 * @Version: V1.0
 */
@Data
@TableName("t_message_read")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "message_read对象", description = "消息是否已读")
public class MessageRead {

	/**
	 * id
	 */
	@TableId(type = IdType.AUTO)
	@ApiModelProperty(value = "id")
	public Long id;

	/**
	 * 消息id
	 */
	@Excel(name = "消息id", width = 15)
	@ApiModelProperty(value = "消息id")
	public Long messageId;

	/**
	 * 用户id
	 */
	@Excel(name = "用户id", width = 15)
	@ApiModelProperty(value = "用户id")
	public String staffId;

	/**
	 * 用户名称
	 */
	@Excel(name = "用户名称", width = 15)
	@ApiModelProperty(value = "用户id")
	public String staffName;

	/**
	 * 是否已读：0未读 1已读
	 */
	@Excel(name = "是否已读：0未读 1已读", width = 15)
	@ApiModelProperty(value = "是否已读：0未读 1已读")
	public Integer readFlag;

	/**
	 * 删除状态 0-未删除 1-已删除
	 */
	@ApiModelProperty(value = "删除状态 0-未删除 1-已删除")
	public Integer delFlag;

	/**
	 * 创建人
	 */
	@Excel(name = "创建人", width = 15)
	@ApiModelProperty(value = "创建人")
	public String createBy;

	/**
	 * 修改人
	 */
	@Excel(name = "修改人", width = 15)
	@ApiModelProperty(value = "修改人")
	public String updateBy;

	/**
	 * 创建时间 CURRENT_TIMESTAMP
	 */
	@Excel(name = "创建时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "创建时间 CURRENT_TIMESTAMP")
	public Date createTime;

	/**
	 * 修改时间 根据当前时间戳更新
	 */
	@Excel(name = "修改时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@ApiModelProperty(value = "修改时间 根据当前时间戳更新")
	public Date updateTime;

}
