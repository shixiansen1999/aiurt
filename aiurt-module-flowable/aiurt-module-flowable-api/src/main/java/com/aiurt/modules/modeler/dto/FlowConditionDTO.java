package com.aiurt.modules.modeler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author fgw
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlowConditionDTO implements Serializable {

    /**
     * 编号
     */
    private String number;

    /**
     * 字段名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 条件规则
     */
    private String condition;

    /**
     * 条件值
     */
    private String value;
    /**
     * 中文条件值
     */
    private String chineseValue;

    /**
     * 关系
     */
    private String relation;

    /**
     * 数字类型
     */
    private String type;
}
