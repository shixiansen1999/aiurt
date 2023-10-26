package com.aiurt.modules.message.dto;

import lombok.Data;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.util.HashMap;
import java.util.List;

@Data
public class MessageContext {

    private List<Task> taskList;

    private HashMap<String, Object> map;

    private ProcessInstance processInstance;

    private HistoricProcessInstance historicProcessInstance;




}
