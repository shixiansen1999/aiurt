package com.aiurt.modules.message.dto;

import lombok.Data;
import org.flowable.engine.runtime.ProcessInstance;

import java.util.Date;


/**
 * @author gaowei
 */
@Data
public class ProcessInstanceMessage extends AbstractMessage{

    private ProcessInstance processInstance;

    /**
     * 实例id
     *
     * @return
     */
    @Override
    public String getProcessInstanceId() {
        return processInstance.getProcessInstanceId();
    }

    /**
     * 业务id
     *
     * @return
     */
    @Override
    public String getBusKey() {
        return processInstance.getBusinessKey();
    }

    /**
     * 获取流程key
     *
     * @return
     */
    @Override
    public String getProcessDefinitionKey() {
        return processInstance.getProcessDefinitionKey();
    }

    /**
     * 流程定义id
     *
     * @return
     */
    @Override
    public String getProcessDefinitionName() {
        return processInstance.getProcessDefinitionName();
    }

    /**
     * 发起用户
     *
     * @return
     */
    @Override
    public String getStartUserId() {
        return processInstance.getStartUserId();
    }

    /**
     * 流程开始时间
     *
     * @return
     */
    @Override
    public Date getStartDate() {
        return processInstance.getStartTime();
    }
}
