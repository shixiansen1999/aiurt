package com.aiurt.boot.modules.patrol.vo.statistics;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: TaskErrorVO
 * @author: Mr.zhao
 * @date: 2021/11/19 18:22
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class TaskErrorVO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long poolId;

	private String errorFlag;



}
