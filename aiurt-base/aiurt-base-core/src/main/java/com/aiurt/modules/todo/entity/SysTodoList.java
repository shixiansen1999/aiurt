package com.aiurt.modules.todo.entity;

import com.aiurt.common.aspect.annotation.Dict;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 待办池列表
 * @Author: aiurt
 * @Date:   2022-12-21
 * @Version: V1.0
 */
@Data
@TableName("sys_todo_list")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="sys_todo_list对象", description="待办池列表")
public class SysTodoList implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    @Dict(dictTable ="sys_user",dicText = "realname",dicCode = "username")
    private String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    @Dict(dictTable ="sys_depart",dicText = "depart_name",dicCode = "org_code")
    private String sysOrgCode;
	/**步骤（任务）名称*/
	@Excel(name = "步骤（任务）名称", width = 15)
    @ApiModelProperty(value = "步骤（任务）名称")
    private String taskName;
	/**任务定义id*/
	@Excel(name = "任务定义id", width = 15)
    @ApiModelProperty(value = "任务定义id")
    private String taskKey;
	/**任务id*/
	@Excel(name = "任务id", width = 15)
    @ApiModelProperty(value = "任务id")
    private String taskId;
	/**业务id*/
	@Excel(name = "业务id", width = 15)
    @ApiModelProperty(value = "业务id")
    private String businessKey;
	/**当前办理的用户账号(逗号隔开)*/
	@Excel(name = "当前办理的用户账号", width = 15)
    @ApiModelProperty(value = "当前办理的用户账号")
    private String currentUserName;
	/**流程实例id*/
	@Excel(name = "流程实例id", width = 15)
    @ApiModelProperty(value = "流程实例id")
    private String processInstanceId;
	/**任务类型（fault故障，bpmn流程，inspection检修，patrol：巡视）*/
	@Excel(name = "任务类型（fault故障，bpmn流程，inspection检修，patrol：巡视）", width = 15)
    @ApiModelProperty(value = "任务类型（fault故障，bpmn流程，inspection检修，patrol：巡视）")
    private String taskType;
	/**任务类型（待办池类型：0：待办、1：已办、2：待阅、3：已阅）*/
	@Excel(name = "任务状态（待办池类型：0：待办、1：已办、2：待阅、3：已阅）", width = 15)
    @ApiModelProperty(value = "任务类型（待办池类型：0：待办、1：已办、2：待阅、3：已阅）")
    @Dict(dicCode = "todo_task_state")
    private String todoType;
	/**名称*/
	@Excel(name = "名称", width = 15)
    @ApiModelProperty(value = "名称")
    private String processDefinitionName;
	/**实际办理人*/
	@Excel(name = "实际办理人", width = 15)
    @ApiModelProperty(value = "实际办理人")
    private String actualUserName;
	/**流程定义key*/
	@Excel(name = "流程定义key", width = 15)
    @ApiModelProperty(value = "流程定义key")
    private String processDefinitionKey;
	/**跳转的url*/
	@Excel(name = "跳转的url", width = 15)
    @ApiModelProperty(value = "跳转的url")
    private String url;
    /**跳转的url针对app页面*/
    @Excel(name = "跳转的url", width = 15)
    @ApiModelProperty(value = "app跳转的url")
    private String appUrl;
	@ApiModelProperty(value = "url类型：0动态表单，1路由表单")
	private String urlType;
}
