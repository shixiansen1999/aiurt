package com.aiurt.modules.flow.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.flowable.task.api.TaskInfo;

import java.util.Date;

/**
 * @author wgp
 * @Title:
 * @Description: 审批数据
 * @date 2022/7/2512:07
 */
@Data
@NoArgsConstructor
@TableName(value = "act_custom_task_comment")
public class CustomTaskComment {

    @ApiModelProperty("主键Id")
    private Long id;

    @ApiModelProperty(value = "流程实例Id")
    private String processInstanceId;

    @ApiModelProperty(value = "任务Id")
    private String taskId;

    @ApiModelProperty(value = "任务标识")
    private String taskKey;

    @ApiModelProperty(value = "任务名称")
    private String taskName;

    @ApiModelProperty(value = "审批类型")
    private String approvalType;

    @ApiModelProperty(value = "批注内容")
    private String comment;

    @ApiModelProperty(value = "委托指定人，比如加签、转办等。")
    private String delegateAssginee;

    @ApiModelProperty(value = "自定义数据。开发者可自行扩展，推荐使用JSON格式数据。")
    private String customBusinessData;

    @ApiModelProperty(value = "创建者Id")
    private Long createUserId;

    @ApiModelProperty(value = "创建者登录名")
    private String createLoginName;

    @ApiModelProperty(value = "创建者显示名")
    private String createUsername;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    public CustomTaskComment(TaskInfo task) {
        this.fillWith(task);
    }

    public void fillWith(TaskInfo task) {
        this.taskId = task.getId();
        this.taskKey = task.getTaskDefinitionKey();
        this.taskName = task.getName();
        this.processInstanceId = task.getProcessInstanceId();
    }

}
