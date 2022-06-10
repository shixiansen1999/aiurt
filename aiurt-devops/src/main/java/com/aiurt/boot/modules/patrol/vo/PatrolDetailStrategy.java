package com.aiurt.boot.modules.patrol.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @description: PatrolDetailStrategy
 * @author: Mr.zhao
 * @date: 2021/9/24 23:01
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PatrolDetailStrategy implements Serializable {

	private static final long serialVersionUID = 1L;


	/**主键id		自动递增*/
	@ApiModelProperty(value = "主键id")
	private Long id;

	/**标题 巡检表名称*/
	@ApiModelProperty(value = "标题")
	private String title;


	/**适用组织id集合		英文逗号分割*/
	@ApiModelProperty(value = "适用组织id集合		英文逗号分割")
	private List<String> organizationIds;


	/**巡检频率		1.一天1次 2.一周2次 3.一周1次*/
	@ApiModelProperty(value = "巡检频率		1.一天1次 2.一周2次 3.一周1次")
	private Integer tactics;


	/**适用组织id集合		英文逗号分割*/
	@ApiModelProperty(value = "适用周集合		英文逗号分割")
	private List<String> dayOfWeek;
}
