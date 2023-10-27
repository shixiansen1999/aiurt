package com.aiurt.modules.remind.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.message.dto.HistoricProcessInstanceMessage;
import com.aiurt.modules.message.service.ISysFlowMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.HistoryService;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngines;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.job.service.JobHandler;
import org.flowable.job.service.impl.persistence.entity.JobEntity;
import org.flowable.task.api.Task;
import org.flowable.variable.api.delegate.VariableScope;
import org.jeecg.common.util.SpringContextUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author fgw 超时提醒处理器
 */
@Slf4j
public class TimeOutRemindJobHandler  implements JobHandler {

    public static final String TYPE = "timeout-remind-handler";


    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void execute(JobEntity jobEntity, String s, VariableScope variableScope, CommandContext commandContext) {
        // 执行id
        String processInstanceId = jobEntity.getProcessInstanceId();

        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
        String jobHandlerConfiguration = jobEntity.getJobHandlerConfiguration();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Task> taskList =new ArrayList<>();
        if(StrUtil.isNotBlank(jobHandlerConfiguration)){
            try {
                JsonNode jsonNode = objectMapper.readTree(jobHandlerConfiguration);
                String taskId = jsonNode.get("task_id").asText();
                Task task = taskService.createTaskQuery().active().taskId(taskId).singleResult();
                if (ObjectUtil.isNotEmpty(task)) {
                    taskList.add(task);
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        // 初始化流程引擎
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        // 获取历史服务
        HistoryService historyService = processEngine.getHistoryService();
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        Date startTime = historicProcessInstance.getStartTime();
        String createTime = DateUtil.format(startTime, "yyyy-MM-dd HH:mm");

        // 发送消息
        HashMap<String, Object> map = new HashMap<>(16);
        map.put("creatBy", "管理员");
        map.put("creatTime", createTime);
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, historicProcessInstance.getBusinessKey());
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.BPM.getType());

        HistoricProcessInstanceMessage message = new HistoricProcessInstanceMessage();
        message.setHistoricProcessInstance(historicProcessInstance);
        message.setTaskList(taskList);
        message.setTemplateCode("bpm_service_recall_process");
        message.setMsgAbstract("有流程【超时】提醒");
        message.setPublishingContent("您有一条新的流程超时提醒，请尽快处理！");
        message.setType("XT");
        message.setMap(map);
        message.setUserName("admin");
        ISysFlowMessageService sysMessageService = SpringContextUtils.getBean(ISysFlowMessageService.class);
        sysMessageService.sendMessage(message);
    }
}
