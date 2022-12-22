package com.aiurt.common.constant;

/**
 * 	待办任务- 任务状态
 * @Author wgp
 * 任务状态（待办池类型：0：待办、1：已办、2：待阅、3：已阅）
 */
public interface CommonTodoStatus {

    /**
     * 待办
     */
    public static final String TODO_STATUS_0 = "0";

    /**
     * 已办
     */
	public static final String DONE_STATUS_1 = "1";

    /**
     * 待阅
     */
	public static final String TODO_READ_STATUS_2 = "2";

    /**
     * 已阅
     */
	public static final String  DONE_READ_STATUS_3 = "3";

}
