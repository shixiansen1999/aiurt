package com.aiurt.modules.modeler.dto;

import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 流程任务信息Vo对象。
 *
 * @author Jerry
 * @date 2021-06-06
 */
@ApiModel("流程任务信息Vo对象")
@Data
public class TaskInfoVo {

    /**
     * 流程节点任务类型。具体值可参考FlowTaskType常量值。
     */
    @ApiModelProperty(value = "流程节点任务类型")
    private Integer taskType;

    /**
     * 指定人。
     */
    @ApiModelProperty(value = "指定人")
    private String assignee;

    /**
     * 任务标识。
     */
    @ApiModelProperty(value = "任务标识")
    private String taskKey;

    /**
     * 是否分配给当前登录用户的标记。
     * 当该值为true时，登录用户启动流程时，就自动完成了第一个用户任务。
     */
    @ApiModelProperty(value = "是否分配给当前登录用户的标记")
    private Boolean assignedMe;

    /**
     * 动态表单Id。
     */
    @ApiModelProperty(value = "动态表单Id")
    private Long formId;

    @ApiModelProperty(value = "")
    private Integer type;

    /**
     * 静态表单路由。
     */
    @ApiModelProperty(value = "静态表单路由")
    private String url;


    @ApiModelProperty(value = "表单处理类名")
    private String className;

    /**
     * 候选组类型。
     */
    @ApiModelProperty(value = "候选组类型")
    private String groupType;

    /**
     * 只读标记。
     */
    @ApiModelProperty(value = "只读标记")
    private Boolean readOnly;

    /**
     * 前端所需的操作列表。
     */
    @ApiModelProperty(value = "前端所需的操作列表")
    List<JSONObject> operationList;

    /**
     * 任务节点的自定义变量列表。
     */
    @ApiModelProperty(value = "任务节点的自定义变量列表")
    List<JSONObject> variableList;
}
