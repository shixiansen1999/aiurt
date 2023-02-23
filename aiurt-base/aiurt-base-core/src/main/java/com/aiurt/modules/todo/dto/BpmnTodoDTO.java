package com.aiurt.modules.todo.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

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
    /**
     * 消息类型：org.jeecg.common.constant.enums.MessageTypeEnum
     *  XT("system",  "系统消息")
     *  YJ("email",  "邮件消息")
     *  DD("dingtalk", "钉钉消息")
     *  QYWX("wechat_enterprise", "企业微信")
     *  WX("wechat","微信")
     *  DX("short_message","短信")
     */
    protected String type;
    /**解析模板内容 对应的数据*/
    @TableField(exist = false)
    private Map<String, Object> data;

    private String processName;

    private String processCode;
}
