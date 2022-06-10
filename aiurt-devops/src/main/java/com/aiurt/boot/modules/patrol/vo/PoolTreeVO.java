package com.aiurt.boot.modules.patrol.vo;

import com.aiurt.boot.modules.patrol.entity.PatrolPoolContent;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @description: PoolTreeVO
 * @author: Mr.zhao
 * @date: 2021/9/24 18:18
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class PoolTreeVO extends PatrolPoolContent implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<PoolTreeVO> children;

}
