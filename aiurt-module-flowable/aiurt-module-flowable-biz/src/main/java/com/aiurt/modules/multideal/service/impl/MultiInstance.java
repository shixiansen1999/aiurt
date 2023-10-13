package com.aiurt.modules.multideal.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.aiurt.modules.flow.utils.FlowElementUtil;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.multideal.entity.ActCustomMultiRecord;
import com.aiurt.modules.multideal.service.IActCustomMultiRecordService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.ehcache.shadow.org.terracotta.statistics.TableSkeleton;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.util.ExecutionGraphUtil;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author fgw
 */
@Service
public class MultiInstance {

    protected static final String NUMBER_OF_INSTANCES = "nrOfInstances";
    protected static final String NUMBER_OF_ACTIVE_INSTANCES = "nrOfActiveInstances";
    protected static final String NUMBER_OF_COMPLETED_INSTANCES = "nrOfCompletedInstances";

    @Autowired
    private TaskService taskService;

    @Autowired
    private IActCustomMultiRecordService multiRecordService;

    @Autowired
    private IActCustomTaskExtService taskExtService;



    /**
     * 评估结果判定条件
     * @param execution 分配执行实例
     */
    public boolean accessCondition(DelegateExecution execution){


        // 顶级
        List<Task> taskList = taskService.createTaskQuery().processInstanceId(execution.getProcessInstanceId()).list();
        if (CollUtil.isEmpty(taskList)) {
            return true;
        }
        Task task = taskList.get(0);
        ActCustomTaskExt actCustomTaskExt = taskExtService.getByProcessDefinitionIdAndTaskId(task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        if (Objects.isNull(actCustomTaskExt)) {
            return true;
        }



        // 判断是否加签
        int addMulti = Optional.ofNullable(actCustomTaskExt.getIsAddMulti()).orElse(0);
        if (addMulti == 0) {
            return true;
        }

        // 获取
        LambdaQueryWrapper<ActCustomMultiRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ActCustomMultiRecord::getParentExecutionId, execution.getId())
                .eq(ActCustomMultiRecord::getProcessInstanceId, execution.getProcessInstanceId());
        Long num = multiRecordService.getBaseMapper().selectCount(queryWrapper);
        if (Objects.nonNull(num) && num>0) {
            // 获取实例总数
            int nrOfInstances = execution.getVariable(NUMBER_OF_INSTANCES, Integer.class);
            // 已完成实例总数
            int nrOfCompletedInstances = execution.getVariable(NUMBER_OF_COMPLETED_INSTANCES, Integer.class);
            return nrOfCompletedInstances >= nrOfInstances;
        }

        return true;
    }


    /**
     * 获取根目录
     * @param execution
     * @return
     */
    protected DelegateExecution getMultiInstanceRootExecution(DelegateExecution execution) {
        return ExecutionGraphUtil.getMultiInstanceRootExecution((ExecutionEntity) execution);
    }
}
