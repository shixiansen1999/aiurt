package com.aiurt.modules.flow.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.flowable.task.api.TaskInfo;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: act_custom_task_comment
 * @Author: aiurt
 * @Date:   2022-07-26
 * @Version: V1.0
 */
@Data
@TableName("act_custom_task_comment")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="act_custom_task_comment对象", description="act_custom_task_comment")
@NoArgsConstructor
public class ActCustomTaskComment implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键Id*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键Id")
    private String id;
	/**流程实例Id*/
    @ApiModelProperty(value = "流程实例Id")
    private String processInstanceId;
	/**任务Id*/
    @ApiModelProperty(value = "任务Id")
    private String taskId;
	/**任务标识*/
    @ApiModelProperty(value = "任务标识")
    private String taskKey;
	/**任务名称*/
    @ApiModelProperty(value = "任务名称")
    private String taskName;
	/**审批类型*/
    @ApiModelProperty(value = "审批类型")
    private String approvalType;
	/**批注内容*/
    @ApiModelProperty(value = "批注内容")
    private String comment;
	/**委托指定人，比如加签、转办等*/
    @ApiModelProperty(value = "委托指定人，比如加签、转办等")
    private String delegateAssignee;
	/**自定义数据。开发者可自行扩展，推荐使用JSON格式数据*/
    @ApiModelProperty(value = "自定义数据。开发者可自行扩展，推荐使用JSON格式数据")
    private String customBusinessData;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private String createBy;
	/**创建时间*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern="yyyy-MM-dd")
    @ApiModelProperty(value = "创建时间")
    private Date createTime;
	/**创建人名称*/
    @ApiModelProperty(value = "创建人名称")
    private String createRealname;

    public ActCustomTaskComment(TaskInfo task) {
        this.fillWith(task);
    }

    public void fillWith(TaskInfo task) {
        this.taskId = task.getId();
        this.taskKey = task.getTaskDefinitionKey();
        this.taskName = task.getName();
        this.processInstanceId = task.getProcessInstanceId();
    }
}
