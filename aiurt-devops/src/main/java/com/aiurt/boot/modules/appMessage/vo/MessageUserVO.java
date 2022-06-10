package com.aiurt.boot.modules.appMessage.vo;

import com.aiurt.boot.modules.appMessage.entity.Message;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: MessageUserVO
 * @author: Mr.zhao
 * @date: 2021/11/15 9:21
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class MessageUserVO extends Message implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "用户id集合,逗号(,)分割")
	private String staffIds;

	@ApiModelProperty(value = "用户名称集合,逗号(,)分割")
	private String staffNames;

	@ApiModelProperty("读取状态 0.未读 1.已读")
	private Integer readFlag;

}
