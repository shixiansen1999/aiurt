package com.aiurt.boot.modules.patrol.vo.statistics;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Mr.zhao
 * @date 2022/1/17 16:04
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SimpIntegerSqlVO implements Serializable {
	private static final long serialVersionUID = 1L;


	private Integer key;

	private Integer name;

	private Integer count;

}
