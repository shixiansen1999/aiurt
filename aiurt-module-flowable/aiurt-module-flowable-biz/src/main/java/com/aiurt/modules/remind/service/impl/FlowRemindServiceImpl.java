package com.aiurt.modules.remind.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.pipeline.selector.LocalListBasedHandlerSelector;
import com.aiurt.modules.modeler.entity.ActCustomModelExt;
import com.aiurt.modules.modeler.service.IActCustomModelExtService;
import com.aiurt.modules.remind.cmd.CustomJobCmd;
import com.aiurt.modules.remind.context.FlowRemindContext;
import com.aiurt.modules.remind.handlers.BuildContextHandler;
import com.aiurt.modules.remind.handlers.RemindRecordUpdateHandler;
import com.aiurt.modules.remind.handlers.RemindRuleVerifyHandler;
import com.aiurt.modules.remind.handlers.RemindSendMessageHandler;
import com.aiurt.modules.remind.pipeline.RemindHandlerChainPipeline;
import com.aiurt.modules.remind.service.IFlowRemindService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.SecurityUtils;
import org.flowable.engine.*;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author fgw
 */
@Service
public class FlowRemindServiceImpl implements IFlowRemindService {

    @Autowired
    private RemindHandlerChainPipeline remindHandlerChainPipeline;
    @Autowired
    private ManagementService managementService;
    @Autowired
    private IActCustomModelExtService actCustomModelExtService;

    /**
     * 手工催办
     *
     * @param processInstanceId
     */
    @Override
    public void manualRemind(String processInstanceId) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> filterNames = new ArrayList<>();
        // 构造selector
        filterNames.add(BuildContextHandler.class.getSimpleName());
        filterNames.add(RemindRecordUpdateHandler.class.getSimpleName());
        filterNames.add(RemindRuleVerifyHandler.class.getSimpleName());
        filterNames.add(RemindSendMessageHandler.class.getSimpleName());
        LocalListBasedHandlerSelector filterSelector = new LocalListBasedHandlerSelector(filterNames);
        FlowRemindContext context = new FlowRemindContext(filterSelector);
        context.setProcessInstanceId(processInstanceId);
        context.setLoginName(loginUser.getUsername());
        context.setRealName(loginUser.getRealname());

        remindHandlerChainPipeline.getFilterChain().handle(context);
    }

    @Override
    public void timeoutRemind(TaskEntity taskEntity) {
        if (ObjectUtil.isEmpty(taskEntity)) {
            return;
        }
        String taskId = taskEntity.getId();
        String processInstanceId = taskEntity.getProcessInstanceId();
        String executionId = taskEntity.getExecutionId();
        String processDefinitionId = taskEntity.getProcessDefinitionId();
        //获取超时提醒配置
        ActCustomModelExt actCustomModelExt = actCustomModelExtService.getByProcessDefinitionId(processDefinitionId);
        if (Objects.nonNull(actCustomModelExt)  && Objects.nonNull(actCustomModelExt.getTimeoutRemindConfig()) && (Optional.ofNullable(actCustomModelExt.getIsTimeoutRemind()).orElse(0) == 1)){
            String timeoutRemindConfig = actCustomModelExt.getTimeoutRemindConfig().toString();
            // 解析 JSON 数据
            Date dueDate = null;
            ObjectMapper objectMapper = new ObjectMapper();
            if(StrUtil.isNotBlank(timeoutRemindConfig)){
                try {
                    JsonNode jsonNode = objectMapper.readTree(timeoutRemindConfig);
                    for (JsonNode node : jsonNode) {
                        String timeoutTimeType = node.get("timeoutTimeType") != null ? node.get("timeoutTimeType").asText() : "";
                        String timeoutTimeCount = node.get("timeoutTimeCount") != null ? node.get("timeoutTimeCount").asText() : "";
                        String remindRule = node.get("remindRule") != null ? node.get("remindRule").asText() : "";
                        String remindTimeType = node.get("remindTimeType") != null ? node.get("remindTimeType").asText() : "";
                        String remindTimeCount = node.get("remindTimeCount") != null ? node.get("remindTimeCount").asText() : "";

                        Date createTime = taskEntity.getCreateTime();
                        Calendar calendar = Calendar.getInstance();
                        //设置流程操作截止时间
                        switch (timeoutTimeType) {
                            case "day":
                                calendar.setTime(createTime);
                                calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(timeoutTimeCount));
                                break;
                            case "hour":
                                calendar.setTime(createTime);
                                calendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(timeoutTimeCount));
                                break;
                            case "min":
                                calendar.setTime(createTime);
                                calendar.add(Calendar.MINUTE, Integer.parseInt(timeoutTimeCount));
                                break;
                            default:
                                break;
                        }
                        //流程到达截止时间
                        Date endDateAfter = calendar.getTime();
                        //设置消息发送时间
                        switch (remindRule) {
                            case "1":
                                dueDate = endDateAfter;
                                break;
                            case "2":
                                calendar.setTime(endDateAfter);
                                calculateRemindDate(calendar, remindTimeType, -Integer.parseInt(remindTimeCount));
                                dueDate = calendar.getTime();
                                break;
                            case "3":
                                calendar.setTime(endDateAfter);
                                calculateRemindDate(calendar, remindTimeType, Integer.parseInt(remindTimeCount));
                                dueDate = calendar.getTime();
                                break;
                            default:
                                break;
                        }
                        //触发定时任务发送消息
                        managementService.executeCommand(new CustomJobCmd(processInstanceId, "超时提醒",taskId, executionId, dueDate));
                    }
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void calculateRemindDate(Calendar calendar, String remindTimeType, int remindTimeCount) {
        switch (remindTimeType) {
            case "day":
                calendar.add(Calendar.DAY_OF_YEAR, remindTimeCount);
                break;
            case "hour":
                calendar.add(Calendar.HOUR_OF_DAY, remindTimeCount);
                break;
            case "min":
                calendar.add(Calendar.MINUTE, remindTimeCount);
                break;
            default:
                break;
        }
    }

}
