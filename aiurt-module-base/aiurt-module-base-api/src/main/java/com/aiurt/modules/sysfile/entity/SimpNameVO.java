package com.aiurt.modules.sysfile.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @description: SimpNameVO
 * @author: Mr.zhao
 * @date: 2021/11/25 23:25
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class SimpNameVO implements Serializable {
 private static final long serialVersionUID = 1L;

 private String name;

 private Integer num;
}
