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
            if (log.isDebugEnabled()) {
                log.debug("审批去重，该流程不开始审批去重");
            }
            context.setContinueChain(false);
            return;
        }


        String duplicateRule = actCustomModelExt.getDedulicateRule();

        if (StrUtil.isBlank(duplicateRule)) {
            if (log.isDebugEnabled()) {
                log.debug("审批去重，该流程没有配置去重规则");
            }
            context.setContinueChain(false);
            return;
        }

        if (log.isDebugEnabled()) {
            log.debug("审批去重，该流程去重规则：{}", duplicateRule);
        }
        Task task = context.getTask();
        List<HistoricTaskInstance> historicTaskInstanceList = context.getHistoricTaskInstanceList();


        Map<String, List<HistoricTaskInstance>> nodeIdMap = historicTaskInstanceList.stream()
                .filter(historicTaskInstance -> !StrUtil.equalsIgnoreCase(historicTaskInstance.getId(), task.getId()))
                .collect(Collectors.groupingBy(HistoricTaskInstance::getTaskDefinitionKey));

        ProcessInstance processInstance = context.getProcessInstance();
        String definitionId = processInstance.getProcessDefinitionId();

        // 获取连续节点前节点的数据
        List<FlowElement> preFlowElementList = flowElementUtil.getPreFlowElement(definitionId, task.getTaskDefinitionKey());
        List<String> list = StrUtil.split(duplicateRule, ',');
        // 1, 间断重复， 2：连续
        AtomicBoolean flag = new AtomicBoolean(false);
        list.stream().forEach(rule->{
            switch (rule) {
                case  "2" :
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
                    Set<String> nodeIdSet = preFlowElementList.stream().map(FlowElement::getId).collect(Collectors.toSet());
                    Map<String, List<HistoricTaskInstance>> duplicateUserNameMap = historicTaskInstanceList.stream()
                            .filter(historicTaskInstance -> (!StrUtil.equalsIgnoreCase(historicTaskInstance.getId(), task.getId()))
                                    && (!nodeIdSet.contains(historicTaskInstance.getTaskDefinitionKey())))
                            .collect(Collectors.groupingBy(HistoricTaskInstance::getAssignee));
                    List<HistoricTaskInstance> historicTaskInstances = duplicateUserNameMap.get(task.getAssignee());
                    if (CollUtil.isNotEmpty(historicTaskInstances)) {
                        flag.set(true);
                    }
                    break;
            }
        });
        if (log.isDebugEnabled()) {
            log.debug("审批去重，任务id：{}，规则校验结果：{}", task.getId(), flag.get());
        }
        context.setContinueChain(flag.get());
    }
}
