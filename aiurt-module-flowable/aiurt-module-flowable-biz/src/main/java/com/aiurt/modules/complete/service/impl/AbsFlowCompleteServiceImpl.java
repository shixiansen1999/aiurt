package com.aiurt.modules.complete.service.impl;

import com.aiurt.modules.complete.dto.CompleteTaskContext;
import com.aiurt.modules.complete.dto.FlowCompleteReqDTO;
import com.aiurt.modules.complete.service.IFlowCompleteWithContextService;

/**
 * @author fgw
 */
public abstract class AbsFlowCompleteServiceImpl implements IFlowCompleteWithContextService {

    /**
     * 流程提交
     *
     * @param flowCompleteReqDTO
     */
    @Override
    public void complete(FlowCompleteReqDTO flowCompleteReqDTO) {
        CompleteTaskContext completeTaskContext = new CompleteTaskContext();
        complete(flowCompleteReqDTO, completeTaskContext);
    }


    /**
     * 流程提交
     *
     * @param flowCompleteReqDTO
     * @param completeTaskContext
     */
    @Override
    public void complete(FlowCompleteReqDTO flowCompleteReqDTO, CompleteTaskContext completeTaskContext) {
        completeTaskContext.setFlowCompleteReqDTO(flowCompleteReqDTO);
        preDeal(completeTaskContext);
        buildTaskContext(completeTaskContext);
        dealSignTask(completeTaskContext);
        dealNextNodeBeforeComplete(completeTaskContext);
        dealComplete(completeTaskContext);
        afterDeal(completeTaskContext);
    }


    /**
     * 始前处理
     *
     * @Description: preDeal
     * @param taskContext
     * @author fgw
     */
    public void preDeal(CompleteTaskContext taskContext) {

    }

    /**
     * 构建上下文环境。获取当前任务、节点、流程等信息。
     *
     * @Description: buildTaskContext
     * @param taskContext
     * @author fgw
     */
    public void buildTaskContext(CompleteTaskContext taskContext) {

    }

    /**
     * 处理会签任务
     *
     * @Description: dealSignTask
     * @param taskContext
     * @author fgw
     */
    public void dealSignTask(CompleteTaskContext taskContext) {

    }

    /**
     * 在任务执行完前处理下一个节点。设置下一个节点参数。
     *
     * @Description: dealNextNodeBeforeComplete
     * @param taskContext
     * @author fgw
     */
    public void dealNextNodeBeforeComplete(CompleteTaskContext taskContext) {

    }

    /**
     * 执行complete操作
     *
     * @Description: dealComplete
     * @param taskContext
     * @author fgw
     */
    public void dealComplete(CompleteTaskContext taskContext) {

    }

    /**
     * 完成后处理事件
     *
     * @Description: afterDeal
     * @param taskContext
     * @author fgw
     */
    public void afterDeal(CompleteTaskContext taskContext) {

    }
}
