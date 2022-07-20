package com.aiurt.modules.sysFile.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Objects;

/**
 * @description: SimpUserVO
 * @author: Mr.zhao
 * @date: 2021/10/29 10:41
 */
@Data
@Accessors(chain = true)
public class SimpUserVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("用户id")
	private String userId;

	@ApiModelProperty("用户名称")
	private String userName;

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SimpUserVO that = (SimpUserVO) o;
		return Objects.equals(userId, that.userId) && Objects.equals(userName, that.userName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(userId, userName);
	}
}
