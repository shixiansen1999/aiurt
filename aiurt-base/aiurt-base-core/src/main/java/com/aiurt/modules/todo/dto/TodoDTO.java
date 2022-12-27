package com.aiurt.modules.todo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
    /**当前办理的用户账号(逗号隔开)*/
    @ApiModelProperty(value = "当前办理的用户账号(逗号隔开)")
    private String currentUserName;
    /**
     * CommonTodoStatus类有对应状态
     */
    /**任务类型（待办池类型：0：待办、1：已办、2：待阅、3：已阅）CommonTodoStatus*/
    @ApiModelProperty(value = "任务类型（待办池类型：0：待办、1：已办、2：待阅、3：已阅）")
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

}
