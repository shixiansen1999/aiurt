package com.aiurt.modules.complete.service.impl;

import com.aiurt.modules.complete.dto.CompleteTaskContext;

/**
 * @author fgw
 */
public class CommonFlowTaskCompleteServiceImpl extends AbsFlowCompleteServiceImpl{

    /**
     * 始前处理
     *
     * @param taskContext
     * @Description: preDeal
     * @author fgw
     */
    @Override
    public void preDeal(CompleteTaskContext taskContext) {
        super.preDeal(taskContext);
    }

    /**
     * 构建上下文环境。获取当前任务、节点、流程等信息。
     *
     * @param taskContext
     * @Description: buildTaskContext
     * @author fgw
     */
    @Override
    public void buildTaskContext(CompleteTaskContext taskContext) {

    }

    /**
     * 处理会签任务
     *
     * @param taskContext
     * @Description: dealSignTask
     * @author fgw
     */
    @Override
    public void dealSignTask(CompleteTaskContext taskContext) {
        super.dealSignTask(taskContext);
    }

    /**
     * 在任务执行完前处理下一个节点。设置下一个节点参数。
     *
     * @param taskContext
     * @Description: dealNextNodeBeforeComplete
     * @author fgw
     */
    @Override
    public void dealNextNodeBeforeComplete(CompleteTaskContext taskContext) {
        // 判断是否是自动提交的，如果不是自动提交则需要获取下一个节点的人员信息

    }

    /**
     * 执行complete操作
     *
     * @param taskContext
     * @Description: dealComplete
     * @author fgw
     */
    @Override
    public void dealComplete(CompleteTaskContext taskContext) {
        super.dealComplete(taskContext);
    }

    /**
     * 完成后处理事件
     *
     * @param taskContext
     * @Description: afterDeal
     * @author fgw
     */
    @Override
    public void afterDeal(CompleteTaskContext taskContext) {
        super.afterDeal(taskContext);
    }
}
