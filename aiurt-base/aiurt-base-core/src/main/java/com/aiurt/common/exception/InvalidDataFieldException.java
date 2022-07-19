package com.aiurt.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 无效的实体对象字段的自定义异常。
 *
 * @author wgp
 * @date 2022-07-16
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InvalidDataFieldException extends RuntimeException {

    private final String modelName;
    private final String fieldName;

    /**
     * 构造函数。
     *
     * @param modelName 实体对象名。
     * @param fieldName 字段名。
     */
    public InvalidDataFieldException(String modelName, String fieldName) {
        super("Invalid FieldName [" + fieldName + "] in Model Class [" + modelName + "].");
        this.modelName = modelName;
        this.fieldName = fieldName;
    }
}
