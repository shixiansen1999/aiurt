package com.aiurt.modules.deduplicate.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.deduplicate.context.FlowDeduplicateContext;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.modeler.entity.ActCustomModelExt;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Slf4j
@Component
public class DuplicateRuleVerifyHandler<T extends FlowDeduplicateContext> extends AbstractFlowHandler<T> {

    @Autowired
    private FlowElementUtil flowElementUtil;

    /**
     * 执行任务
     *
     * @param context
     */
    @Override
    public void handle(T context) {

        ActCustomModelExt actCustomModelExt = context.getActCustomModelExt();
        int isDeduplicate = Optional.ofNullable(actCustomModelExt.getIsDedulicate()).orElse(0);

        if (isDeduplicate == 0 ) {
            log.info("审批人去重， 不开启去重");
            context.setContinueChain(false);
            return;
        }


        String duplicateRule = actCustomModelExt.getDedulicateRule();

        if (StrUtil.isBlank(duplicateRule)) {
            log.info("审批人去重， 没选择去重规则");
            context.setContinueChain(false);
            return;
        }
        Task task = context.getTask();
        List<HistoricTaskInstance> historicTaskInstanceList = context.getHistoricTaskInstanceList();
        Map<String, List<HistoricTaskInstance>> duplicateUserNameMap = historicTaskInstanceList.stream()
                .filter(historicTaskInstance -> !StrUtil.equalsIgnoreCase(historicTaskInstance.getId(), task.getId())).collect(Collectors.groupingBy(HistoricTaskInstance::getAssignee));
        Map<String, List<HistoricTaskInstance>> nodeIdMap = historicTaskInstanceList.stream().filter(historicTaskInstance -> !StrUtil.equalsIgnoreCase(historicTaskInstance.getId(), task.getId()))
                .collect(Collectors.groupingBy(HistoricTaskInstance::getTaskDefinitionKey));

        ProcessInstance processInstance = context.getProcessInstance();
        String definitionId = processInstance.getProcessDefinitionId();
        List<String> list = StrUtil.split(duplicateRule, ',');
        // 1, 连续， 2：重复
        AtomicBoolean flag = new AtomicBoolean(false);
        list.stream().forEach(rule->{
            switch (rule) {
                case  "1" :
                    // 获取连续节点
                    List<FlowElement> preFlowElementList = flowElementUtil.getPreFlowElement(definitionId, task.getTaskDefinitionKey());
                    preFlowElementList.forEach(flowElement->{
                        List<HistoricTaskInstance> historicTaskInstances = nodeIdMap.get(flowElement.getId());
                        if (CollUtil.isNotEmpty(historicTaskInstances)) {
                            Set<String> set = historicTaskInstances.stream().map(HistoricTaskInstance::getAssignee).collect(Collectors.toSet());
                            if (set.contains(task.getAssignee())) {
                                flag.set(true);
                            }
                        }
                    });
                    break;
                default:
                    List<HistoricTaskInstance> historicTaskInstances = duplicateUserNameMap.get(task.getAssignee());
                    if (CollUtil.isNotEmpty(historicTaskInstances)) {
                        flag.set(true);
                    }
                    break;
            }
        });

        context.setContinueChain(flag.get());
    }
}
