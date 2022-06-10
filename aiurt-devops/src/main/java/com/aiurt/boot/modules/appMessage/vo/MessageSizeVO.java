package com.aiurt.boot.modules.appMessage.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: MessageSizeVO
 * @author: Mr.zhao
 * @date: 2021/11/12 17:21
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class MessageSizeVO implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty("列表内容")
	private IPage<MessageStatusVO> page;
	@ApiModelProperty(value = "未读消息条数")
	private Integer unReadSize;
	@ApiModelProperty(value = "已读消息条数")
	private Integer readSize;

}
