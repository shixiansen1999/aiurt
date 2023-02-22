package com.aiurt.modules.modeler.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description: 流程定义节点属性
 * @Author: aiurt
 * @Date:   2022-08-02
 * @Version: V1.0
 */
@Data
@TableName("act_custom_task_ext")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="act_custom_task_ext对象", description="流程定义节点属性")
public class ActCustomTaskExt implements Serializable {
    private static final long serialVersionUID = 1L;

	/**流程引擎的定义Id*/
    @ApiModelProperty(value = "流程引擎的定义Id")
    private String processDefinitionId;

	/**流程引擎任务Id*/
    @ApiModelProperty(value = "流程引擎任务Id")
    private String taskId;

	/**操作列表JSON*/
    @ApiModelProperty(value = "操作列表JSON")
    private String operationListJson;

	/**变量列表JSON*/
    @ApiModelProperty(value = "变量列表JSON")
    private String variableListJson;

	/**存储多实例的assigneeList的JSON*/
    @ApiModelProperty(value = "存储多实例的assigneeList的JSON")
    private String assigneeListJson;

	/**分组类型*/
    @ApiModelProperty(value = "分组类型")
    private String groupType;

	/**保存岗位相关的数据*/
    @ApiModelProperty(value = "保存岗位相关的数据")
    private String deptPostListJson;

	/**保存角色Id数据*/
    @ApiModelProperty(value = "保存角色Id数据")
    private String roleIds;

	/**保存部门Id数据*/
    @ApiModelProperty(value = "保存部门Id数据")
    private String deptIds;

	/**保存候选组用户名数据*/
    @ApiModelProperty(value = "保存候选组用户名数据")
    private String candidateUsernames;

	/**抄送相关的数据*/
    @ApiModelProperty(value = "抄送相关的数据")
    private String copyListJson;

    /**表单数据*/
    @ApiModelProperty(value = "表单数据")
    private String formJson;

    @ApiModelProperty(value = "动态选人(流程变量)")
    private String dynamicVariable;

    @ApiModelProperty(value = "条件表单式")
    private String conditionExpression;
}
