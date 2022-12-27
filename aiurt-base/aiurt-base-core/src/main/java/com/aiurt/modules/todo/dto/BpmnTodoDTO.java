package com.aiurt.modules.todo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description 待办任务dto
 * @Author MrWei
 * @Date 2022/12/21 11:06
 **/
@Data
public class BpmnTodoDTO extends TodoDTO {
    /**任务定义id*/
    @ApiModelProperty(value = "任务定义id")
    private String taskKey;

    /**任务id*/
    @ApiModelProperty(value = "任务id")
    private String taskId;

    /**流程实例id*/
    @ApiModelProperty(value = "流程实例id")
    private String processInstanceId;

    /**流程定义key*/
    @ApiModelProperty(value = "流程定义key")
    private String processDefinitionKey;

    @ApiModelProperty(value = "url类型：0动态表单，1路由表单")
    private String urlType;
}
