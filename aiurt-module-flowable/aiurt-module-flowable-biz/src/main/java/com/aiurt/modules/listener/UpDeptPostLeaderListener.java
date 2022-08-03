package com.aiurt.modules.listener;

import com.aiurt.modules.constants.FlowConstant;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.task.service.delegate.DelegateTask;

import java.util.Map;

/**
 * 当用户任务的候选组为上级部门领导岗位时，该监听器会在任务创建时，获取当前流程实例发起人的部门领导。
 * 并将其指派为当前任务的候选组。
 *
 * @author fgw
 * @date 2021-08-03
 */
@Slf4j
public class UpDeptPostLeaderListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        Map<String, Object> variables = delegateTask.getVariables();
        if (variables.get(FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER_VAR) == null) {
            delegateTask.setAssignee(variables.get(FlowConstant.PROC_INSTANCE_START_USER_NAME_VAR).toString());
        }
    }
}
