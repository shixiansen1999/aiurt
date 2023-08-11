package com.aiurt.modules.common.constant;

/**
 * @author fgw
 */
public interface FlowVariableConstant {

    /**
     * 变量类型 0：状态
     */
    Integer VARIABLE_TYPE_0 = 0;

    /**
     * 变量类型 1：变量
     */
    Integer VARIABLE_TYPE_1 = 1;


    /**
     * 已完成的实例数量
     */
    String NUMBER_OF_COMPLETED_INSTANCES = "nrOfCompletedInstances";


    /**
     * 获取当前实例的索引（从0开始）
     */
    String LOOP_COUNTER = "loopCounter";

    /**
     * 多实例
     */
    String ASSIGNEE_LIST = "assigneeList_userTask_";

}
