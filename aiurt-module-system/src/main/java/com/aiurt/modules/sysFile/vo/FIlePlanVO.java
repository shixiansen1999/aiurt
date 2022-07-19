package com.aiurt.modules.sysFile.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @description: FIlePlanVO
 * @author: Mr.zhao
 * @date: 2021/11/29 14:18
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class FIlePlanVO implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 主键id
	 */
	@ApiModelProperty(value = "主键id")
	private Long id;
	/**
	 * 类型id
	 */
	@ApiModelProperty(value = "类型id")
	private Long typeId;

	/**
	 * 文件类型
	 */
	@ApiModelProperty(value = "文件类型")
	private String type;

	/**
	 * 分类名称
	 */
	@ApiModelProperty(value = "分类名称")
	private String typeName;
	/**
	 * 文件名称
	 */
	@ApiModelProperty(value = "文件名称")
	private String fileName;
	/**
	 * 文件url
	 */
	@ApiModelProperty(value = "文件url")
	private String url;
	/**
	 * 是否为文件 0.否 1.是
	 */
	@ApiModelProperty("是否为文件 0.否 1.是")
	private Integer status;


	@ApiModelProperty("上一级id")
	private Long parentId;

	private List<FIlePlanVO> children;


	@ApiModelProperty("计数id,无用字段")
	private Long tempId;
}
