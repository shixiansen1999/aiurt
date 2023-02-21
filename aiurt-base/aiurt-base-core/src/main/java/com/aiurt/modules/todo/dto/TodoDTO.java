package com.aiurt.modules.todo.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.util.Map;

/**
 * @Description 待办任务dto
 * @Author MrWei
 * @Date 2022/12/21 11:06
 **/
@Data
public class TodoDTO {
    /**步骤（任务）名称*/
    @ApiModelProperty(value = "步骤（任务）名称")
    private String taskName;
    /**业务id*/
    @ApiModelProperty(value = "业务id")
    private String businessKey;
    /**业务类型*/
    @ApiModelProperty(value = "业务类型")
    private String businessType;
    /**当前办理的用户账号(逗号隔开)*/
    @ApiModelProperty(value = "当前办理的用户账号(逗号隔开)")
    private String currentUserName;
    @Excel(name = "任务类型（fault故障，bpmn流程，inspection检修，patrol：巡视）", width = 15)
    @ApiModelProperty(value = "任务类型（fault故障，bpmn流程，inspection检修，patrol：巡视）")
    private String taskType;
    /**
     * CommonTodoStatus类有对应状态
     */
    /**任务状态（待办池状态：0：待办、1：已办、2：待阅、3：已阅）CommonTodoStatus*/
    @ApiModelProperty(value = "任务状态（待办池状态：0：待办、1：已办、2：待阅、3：已阅）")
    private String todoType;
    /**名称*/
    @ApiModelProperty(value = "名称")
    private String processDefinitionName;
    /**跳转的url*/
    @ApiModelProperty(value = "跳转的url")
    private String url;
    /**app跳转的url*/
    @ApiModelProperty(value = "app跳转的url")
    private String appUrl;
    /**是否为定时任务标识*/
    private Boolean timedTask;
    /**摘要*/
    @ApiModelProperty("摘要")
    private java.lang.String msgAbstract;
    /**发布内容*/
    @ApiModelProperty("发布内容")
    private String publishingContent;
    /**标题*/
    @ApiModelProperty("标题")
    private String title;
    /**模板消息对应的模板编码*/
    private String templateCode;
    /**是否发送Markdown格式的消息*/
    @TableField(exist = false)
    private Boolean markdown;
    /**解析模板内容 对应的数据*/
    @TableField(exist = false)
    private Map<String, Object> data;
    public TodoDTO() {
    }

    public TodoDTO(String taskName, String businessKey, String businessType, String currentUserName, String taskType, String todoType, String processDefinitionName, String url, String appUrl) {
        this.taskName = taskName;
        this.businessKey = businessKey;
        this.businessType = businessType;
        this.currentUserName = currentUserName;
        this.taskType = taskType;
        this.todoType = todoType;
        this.processDefinitionName = processDefinitionName;
        this.url = url;
        this.appUrl = appUrl;
    }
}
