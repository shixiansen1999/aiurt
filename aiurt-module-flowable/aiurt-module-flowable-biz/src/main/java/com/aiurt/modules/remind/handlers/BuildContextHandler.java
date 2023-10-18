package com.aiurt.modules.remind.handlers;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtErrorEnum;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.modeler.entity.ActCustomModelExt;
import com.aiurt.modules.modeler.service.IActCustomModelExtService;
import com.aiurt.modules.remind.context.FlowRemindContext;
import com.aiurt.modules.remind.entity.ActCustomRemindRecord;
import com.aiurt.modules.remind.service.IActCustomRemindRecordService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * <P>催办上下文构建处理器</P>
 * @author fgw
 */
@Slf4j
@Component
public class BuildContextHandler extends AbstractFlowHandler<FlowRemindContext> {

    @Resource
    private HistoryService historyService;

    @Resource
    private IActCustomModelExtService modelExtService;

    @Resource
    private TaskService taskService;

    @Resource
    private IActCustomRemindRecordService remindRecordService;



    /**
     * 具体执行
     *
     * @param context
     */
    @Override
    public void handle(FlowRemindContext context) {

        // 具体逻辑实现
        String processInstanceId = context.getProcessInstanceId();
        String loginName = context.getLoginName();
        if (log.isDebugEnabled()) {
            log.debug("手工催办，开始构建催办上下文, 流程实例id：{}，催办人：{}", processInstanceId, loginName);
        }
        // 流程实例
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        if (Objects.isNull(historicProcessInstance)) {
            throw new AiurtBootException(AiurtErrorEnum.PROCESS_INSTANCE_NOT_FOUND.getCode(), AiurtErrorEnum.FLOW_DEFINITION_NOT_FOUND.getMessage());
        }

        // 上次提交记录
        ActCustomRemindRecord lastRemindRecord = remindRecordService.getByProcessInstanceId(processInstanceId, loginName);

        ActCustomModelExt modelExt = modelExtService.getByProcessDefinitionId(historicProcessInstance.getProcessDefinitionId());

        List<Task> taskList = taskService.createTaskQuery().active().processInstanceId(processInstanceId).list();
        // 过滤不需要消息提醒的用户
        taskList = taskList.stream().filter(task -> !StrUtil.equalsIgnoreCase(task.getAssignee(), loginName))
                .collect(Collectors.toList());

        context.setActCustomModelExt(modelExt);
        context.setLastRemindRecord(lastRemindRecord);
        context.setProcessInstance(historicProcessInstance);
        context.setTaskList(taskList);

        if (Objects.isNull(modelExt) || CollUtil.isEmpty(taskList)) {
            if (log.isDebugEnabled()) {
                log.debug("手工催办，不执行下一执行器，原因流程配置信息不存在或者没有需要提醒的用户任务, 流程实例id：{}，催办人：{}",
                        processInstanceId, loginName);
            }
            // 流程属性没有不执行之后的处理器
            context.setContinueChain(false);
        }

        if (log.isDebugEnabled()) {
            log.debug("手工催办，结束构建催办上下文, 流程实例id：{}，催办人：{}", processInstanceId, loginName);
        }
    }
}
