package com.aiurt.modules.message.dto;

import java.util.Date;

public interface IMessage {

    /**
     * 实例id
     * @return
     */
    String getProcessInstanceId();

    /**
     * 业务id
     * @return
     */
    String getBusKey();

    /**
     * 获取流程key
     * @return
     */
    String getProcessDefinitionKey();

    /**
     * 流程定义id
     * @return
     */
    String getProcessDefinitionName();

    /**
     * 发起用户
     * @return
     */
    String getStartUserId();

    /**
     * 流程开始时间
     * @return
     */
    Date getStartDate();
}
