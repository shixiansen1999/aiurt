package com.aiurt.modules.sysFile.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: TypeNameVO
 * @author: Mr.zhao
 * @date: 2021/11/23 16:13
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class TypeNameVO implements Serializable {
	private String name;

}
