package com.aiurt.modules.sysfile.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: FileAppParam
 * @author: Mr.zhao
 * @date: 2021/11/10 9:20
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class FileAppParam implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty("页码")
	private Integer pageNo = 1;

	@ApiModelProperty("每页条数")
	private Integer pageSize = 10;

	@ApiModelProperty("文件夹id")
	private Long typeId;

}
