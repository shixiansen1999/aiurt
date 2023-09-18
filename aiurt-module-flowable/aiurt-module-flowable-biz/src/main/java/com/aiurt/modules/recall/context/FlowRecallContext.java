package com.aiurt.modules.recall.context;

import com.aiurt.modules.common.pipeline.context.AbstractFlowContext;
import com.aiurt.modules.common.pipeline.selector.HandlerSelector;
import com.aiurt.modules.modeler.entity.ActCustomModelExt;
import com.aiurt.modules.remind.entity.ActCustomRemindRecord;
import lombok.Getter;
import lombok.Setter;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.task.api.Task;

import java.util.List;
import java.util.Objects;

/**
 * @author fgw
 */
@Setter
@Getter
public class FlowRecallContext extends AbstractFlowContext {
    /**
     * 当前用户账号
     */
    private String loginName;

    /**
     * 流程创建的用户名
     */
    private String realName;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 流程实例
     */
    private HistoricProcessInstance processInstance;

    /**
     * 流程撤回原因
     */
    private String recallReason;

    /**
     * 代办的用户任务
     */
    private List<Task> taskList;

    /**
     * 撤回人员
     */
    private List<String> userNameList;

    /**
     * 是否继续执行下一任务链
     */
    private Boolean continueChain;

    /**
     * 流程扩展属性
     */
    private ActCustomModelExt actCustomModelExt;


    public FlowRecallContext(HandlerSelector selector) {
        super(selector);
    }


    /**
     * 是否继续执行下一个链
     *
     * @return
     */
    @Override
    public boolean continueChain() {

        if (Objects.nonNull(this.continueChain)) {
            return this.continueChain;
        }
        return true;
    }
}
