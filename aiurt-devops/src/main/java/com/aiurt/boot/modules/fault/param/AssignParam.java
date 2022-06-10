package com.aiurt.boot.modules.fault.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @description: AssignParam
 * @author: Mr.zhao
 * @date: 2021/10/1 18:19
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class AssignParam implements Serializable {

	@NotNull
	private  String code;

	@NotNull
	private  String appointUserId;

	@NotNull(message = "")
	private  String workType;

	private  String planOrderCode;

	private  String planOrderImg;

}
