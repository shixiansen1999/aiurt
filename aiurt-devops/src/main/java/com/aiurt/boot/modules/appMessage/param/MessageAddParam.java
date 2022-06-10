package com.aiurt.boot.modules.appMessage.param;

import com.aiurt.boot.modules.appMessage.entity.Message;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @description: MessageAddVO
 * @author: Mr.zhao
 * @date: 2021/11/15 9:56
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Builder
public class MessageAddParam extends Message implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "消息对象")
	private Message message;

	@ApiModelProperty(value = "用户id集合")
	private List<String> userIds;

	@ApiModelProperty(value = "用户名称集合,后端处理")
	private List<String> userNames;


}
