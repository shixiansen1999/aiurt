package com.aiurt.modules.base;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel(description = "实体对象基础类")
@Data
public class BaseEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 排序字段，多个排序字段使用逗号隔开
	 */
	@ApiModelProperty(value = "排序字段")
	@TableField(exist = false)
	protected String column;
	/**
	 * 排序类型,多个排序字段使用逗号隔开
	 * order取值：asc(升序),desc(降序)
	 */
	@ApiModelProperty(value = "排序类型")
	@TableField(exist = false)
	protected String order;
}
