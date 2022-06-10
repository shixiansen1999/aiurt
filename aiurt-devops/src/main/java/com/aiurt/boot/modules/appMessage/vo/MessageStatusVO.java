package com.aiurt.boot.modules.appMessage.vo;

import com.aiurt.boot.modules.appMessage.entity.Message;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: MessageStatusVO
 * @author: Mr.zhao
 * @date: 2021/11/17 11:04
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class MessageStatusVO extends Message implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty("读取状态,0:已读 1.未读")
	private Integer readFlag;

}
