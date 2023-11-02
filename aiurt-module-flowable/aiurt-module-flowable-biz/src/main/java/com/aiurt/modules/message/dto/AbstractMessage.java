package com.aiurt.modules.message.dto;

import lombok.Data;
import org.flowable.task.api.Task;

import java.util.HashMap;
import java.util.List;

/**
 * @author gaowei
 */
@Data
public abstract class AbstractMessage implements IMessage{

    /**
     * 任务列表
     */
    private List<Task> taskList;

    /**
     * 模板内的数据
     */
    private HashMap<String, Object> map;

    /**
     * 消息目标那编码
     */
    private String templateCode;

    /**
     * 消息title
     */
    private String msgAbstract;

    /**
     * 副标题
     */
    private String publishingContent;

    /**
     * 发送的渠道，默认我iXT ，多个逗号隔开
     */
    private String type;

    /**
     * 发起人
     */
    private String userName;

}
