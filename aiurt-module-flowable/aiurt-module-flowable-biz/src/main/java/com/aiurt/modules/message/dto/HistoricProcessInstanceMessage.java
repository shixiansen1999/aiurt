package com.aiurt.modules.message.dto;

import lombok.Data;
import org.flowable.engine.history.HistoricProcessInstance;

import java.util.Date;

/**
 * @author gaowei
 */
@Data
public class HistoricProcessInstanceMessage extends AbstractMessage{

    private HistoricProcessInstance historicProcessInstance;

    /**
     * 实例id
     *
     * @return
     */
    @Override
    public String getProcessInstanceId() {
        return historicProcessInstance.getId();
    }

    /**
     * 业务id
     *
     * @return
     */
    @Override
    public String getBusKey() {
        return historicProcessInstance.getBusinessKey();
    }

    /**
     * 获取流程key
     *
     * @return
     */
    @Override
    public String getProcessDefinitionKey() {
        return historicProcessInstance.getProcessDefinitionKey();
    }

    /**
     * 流程定义id
     *
     * @return
     */
    @Override
    public String getProcessDefinitionName() {
        return historicProcessInstance.getProcessDefinitionName();
    }

    /**
     * 发起用户
     *
     * @return
     */
    @Override
    public String getStartUserId() {
        return historicProcessInstance.getStartUserId();
    }

    /**
     * 流程开始时间
     *
     * @return
     */
    @Override
    public Date getStartDate() {
        return historicProcessInstance.getStartTime();

    }
}
