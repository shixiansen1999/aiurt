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
     * 名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 条件
     */
    private String condition;

    /**
     * 值
     */
    private String value;

    /**
     * 关系
     */
    private String relation;

    /**
     * 数字类型
     */
    private String type;
}
