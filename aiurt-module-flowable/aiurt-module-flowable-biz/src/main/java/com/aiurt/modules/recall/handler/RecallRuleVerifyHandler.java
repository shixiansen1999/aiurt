package com.aiurt.modules.recall.handler;/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023/9/14
 * @time: 15:46
 */

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtErrorEnum;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.modeler.entity.ActCustomModelExt;
import com.aiurt.modules.recall.context.FlowRecallContext;
import com.aiurt.modules.remind.context.FlowRemindContext;
import com.aiurt.modules.remind.entity.ActCustomRemindRecord;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.HistoricTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author: qkx
 * @date: 2023-09-14 15:46
 */
@Component
public class RecallRuleVerifyHandler extends AbstractFlowHandler<FlowRecallContext> {
    @Autowired
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    @Resource
    private RuntimeService runtimeService;
    @Override
    public void handle(FlowRecallContext context) {
        String processInstanceId = context.getProcessInstanceId();
        // 流程未结束
        HistoricProcessInstance processInstance = context.getProcessInstance();
        Date endTime = processInstance.getEndTime();
        //流程已结束，不允许撤回
        if (Objects.nonNull(endTime)) {
            context.setContinueChain(false);
            throw new AiurtBootException(AiurtErrorEnum.NEXT_NODE_IS_END.getCode(), AiurtErrorEnum.NEXT_NODE_IS_END.getMessage());
        }
        //撤回按钮配置未开启不能撤回
        ActCustomModelExt actCustomModelExt = context.getActCustomModelExt();
        Integer isRecall = Optional.of(actCustomModelExt.getIsRecall()).orElse(0);
        if (isRecall == 0) {
            context.setContinueChain(false);
        }
        //判断流程当前达到节点是否在流程配置节点集合中
        //获取流程配置中的节点集合
        String recallNodeId = actCustomModelExt.getRecallNodeId();
        String[] split = recallNodeId.split(",");
        //获取流程当前节点
        List<Task> taskList = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .list();

// 获取所有流程定义key
        List<String> keyList = taskList.stream()
                .map(Task::getTaskDefinitionKey)
                .filter(s -> Arrays.asList(split).contains(s))
                .distinct()
                .collect(Collectors.toList());

        if (CollUtil.isNotEmpty(keyList)) {
            List<ExecutionEntityImpl> executions = keyList.stream()
                    .flatMap(key -> runtimeService.createExecutionQuery()
                            .processInstanceId(processInstanceId)
                            .activityId(key)
                            .list().stream()
                    )
                    .map(execution -> (ExecutionEntityImpl) execution)
                    .collect(Collectors.toList());

            if (executions.stream().anyMatch(instance -> instance.getIsActive() && instance.getTaskCount() != 0)) {
                context.setContinueChain(false);
                throw new AiurtBootException("已有审批人处理，无法撤回");
            }
        } else {
            context.setContinueChain(false);
            throw new AiurtBootException("当前流程不在可撤回范围内，无法撤回");
        }
    }
}
