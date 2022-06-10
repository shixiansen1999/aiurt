package com.aiurt.boot.modules.patrol.vo.statistics;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Mr.zhao
 * @date 2022/1/18 15:54
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SimpStringSqlVO implements Serializable {
	private static final long serialVersionUID = 1L;

	private String key;

	private Integer name;

	private Integer count;

}
