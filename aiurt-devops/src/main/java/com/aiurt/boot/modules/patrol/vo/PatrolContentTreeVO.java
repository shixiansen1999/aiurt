package com.aiurt.boot.modules.patrol.vo;

import com.swsc.copsms.modules.patrol.entity.PatrolContent;
import io.swagger.annotations.ApiModel;
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
public class PatrolContentTreeVO extends PatrolContent implements Serializable {

	private List<PatrolContentTreeVO> children;

}
