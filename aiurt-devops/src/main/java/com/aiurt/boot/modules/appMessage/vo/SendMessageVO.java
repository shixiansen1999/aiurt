package com.aiurt.boot.modules.appMessage.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @description: SendMessageVO
 * @author: Mr.zhao
 * @date: 2021/11/17 9:39
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SendMessageVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 标题
	 */
	@ApiModelProperty(value = "标题")
	private String title;

	/**
	 * 消息内容
	 */
	@ApiModelProperty(value = "消息内容")
	private String content;

	/**
	 * 创建人
	 */
	@ApiModelProperty(value = "创建人")
	private String createBy;

	/**
	 * 接收消息的用户id集合
	 */
	@ApiModelProperty(value = "接收消息的用户id集合")
	private List<String> userIds;
}
