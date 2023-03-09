package com.aiurt.modules.todo.dto;

import io.swagger.annotations.ApiOperation;
import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
public class SysTodoCountDTO implements Serializable {


    private Integer  count;

    private Integer undoCount;

    private String taskType;

    private String processCode;

    private String processName;

    private String businessType;
}
