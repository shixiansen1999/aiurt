package com.aiurt.modules.sparepart.entity.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author km
 * @Date 2021/9/15 15:04
 * @Version 1.0
 */
@Data
public class EnumTypeVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 类型编号
     */
    private Integer code;
    /**
     * 类型描述
     */
    private String name;
}
