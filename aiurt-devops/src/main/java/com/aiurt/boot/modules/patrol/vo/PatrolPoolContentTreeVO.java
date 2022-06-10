package com.aiurt.boot.modules.patrol.vo;

import com.swsc.copsms.modules.patrol.entity.PatrolPoolContent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @description: PatrolContentTreeVO
 * @author: Mr.zhao
 * @date: 2021/9/15 22:07
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "巡检项树形显示", description = "巡检项树形显示")
public class PatrolPoolContentTreeVO extends PatrolPoolContent implements Serializable {

	@ApiModelProperty(value = "报告id,每一项报告的id传这个")
	private Long reportId;


	@ApiModelProperty(value = "备注")
	private String note;

	@ApiModelProperty(value = "保存状态 0.保存 1.提交(不允许更改)")
	private Integer saveStatus;

	@ApiModelProperty(value = "保存状态 0.文字项 1.正常 2.异常(异常状态备注必有值)")
	private Integer reportStatus;

	@ApiModelProperty(value = "附件集合")
	private List<String > urlList;

	private List<PatrolPoolContentTreeVO> children;

}
