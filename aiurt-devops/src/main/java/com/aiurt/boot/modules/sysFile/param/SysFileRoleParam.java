package com.aiurt.boot.modules.sysFile.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: SysFileRoleParam
 * @author: Mr.zhao
 * @date: 2021/10/26 15:49
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SysFileRoleParam implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("类型id")
	private Long typeId;

	@ApiModelProperty("用户id")
	private String userId;

	@ApiModelProperty("查看状态 0:不允许 1:允许")
	private Integer lookStatus;

	@ApiModelProperty("编辑/上传状态 0:不允许 1:允许")
	private Integer editStatus;
}

