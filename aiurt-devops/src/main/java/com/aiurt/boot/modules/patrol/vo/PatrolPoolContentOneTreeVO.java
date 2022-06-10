package com.aiurt.boot.modules.patrol.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @description: PatrolPoolContentOneTreeVO
 * @author: Mr.zhao
 * @date: 2021/11/10 18:18
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PatrolPoolContentOneTreeVO implements Serializable {
	private static final long serialVersionUID = 1L;

	@ApiModelProperty(value = "报告id,每一项报告的id传这个")
	private Long contentId;

	@ApiModelProperty(value = "报告id,每一项报告的id传这个")
	private Long reportId;

	@ApiModelProperty(value = "备注")
	private String note;

	@ApiModelProperty(value = "保存状态 0.保存 1.提交(不允许更改)")
	private Integer saveStatus;

	@ApiModelProperty(value = "文字状态 0.文字项 1.正常 2.异常(异常状态备注必有值)")
	private Integer reportStatus;

	@ApiModelProperty(value = "附件集合")
	private List<String> urlList;

	private PatrolPoolContentOneTreeVO children;

	@ApiModelProperty(value = "修改状态字段")
	private Integer flag;

	@ApiModelProperty(value = "名称")
	private String content;

	private Long poolId;

	private Long parentId;

	@ApiModelProperty(value = "计数")
	private Integer count;

	private Integer tempLen;
}
