package com.aiurt.modules.constants;

/**
 * @author fgw
 */
public class FlowConstant {

    /**
     * 标识流程实例启动用户的变量名。
     */
    public final static String START_USER_NAME_VAR = "${startUserName}";

    /**
     * 流程实例发起人变量名。
     */
    public final static String PROC_INSTANCE_INITIATOR_VAR = "initiator";

    /**
     * 流程实例中发起人用户的变量名。
     */
    public final static String PROC_INSTANCE_START_USER_NAME_VAR = "startUserName";

    /**
     * 流程任务的指定人变量。
     */
    public final static String TASK_APPOINTED_ASSIGNEE_VAR = "appointedAssignee";

    /**
     * 操作类型变量。
     */
    public final static String OPERATION_TYPE_VAR = "operationType";

    /**
     * 多任务拒绝数量变量。
     */
    public final static String MULTI_REFUSE_COUNT_VAR = "multiRefuseCount";

    /**
     * 多任务同意数量变量。
     */
    public final static String MULTI_AGREE_COUNT_VAR = "multiAgreeCount";

    /**
     * 多任务弃权数量变量。
     */
    public final static String MULTI_ABSTAIN_COUNT_VAR = "multiAbstainCount";

    /**
     * 会签发起任务。
     */
    public final static String MULTI_SIGN_START_TASK_VAR = "multiSignStartTask";

    /**
     * 会签任务总数量。
     */
    public final static String MULTI_SIGN_NUM_OF_INSTANCES_VAR = "multiNumOfInstances";

    /**
     * 多实例实例数量变量。
     */
    public final static String NUMBER_OF_INSTANCES_VAR = "nrOfInstances";

    /**
     * 多任务指派人列表变量。
     */
    public final static String MULTI_ASSIGNEE_LIST_VAR = "assigneeList";

    /**
     * 上级部门领导审批变量。
     */
    public final static String GROUP_TYPE_UP_DEPT_POST_LEADER_VAR = "upDeptPostLeader";

    /**
     * 本部门领导审批变量。
     */
    public final static String GROUP_TYPE_DEPT_POST_LEADER_VAR = "deptPostLeader";

    /**
     * 所有部门岗位审批变量。
     */
    public final static String GROUP_TYPE_ALL_DEPT_POST_VAR = "allDeptPost";

    /**
     * 本部门岗位审批变量。
     */
    public final static String GROUP_TYPE_SELF_DEPT_POST_VAR = "selfDeptPost";

    /**
     * 上级部门岗位审批变量。
     */
    public final static String GROUP_TYPE_UP_DEPT_POST_VAR = "upDeptPost";

    /**
     * 任意部门关联的岗位审批变量。
     */
    public final static String GROUP_TYPE_DEPT_POST_VAR = "deptPost";

    /**
     * 指定角色分组变量。
     */
    public final static String GROUP_TYPE_ROLE_VAR = "role";

    /**
     * 指定部门分组变量。
     */
    public final static String GROUP_TYPE_DEPT_VAR = "dept";

    /**
     * 指定用户分组变量。
     */
    public final static String GROUP_TYPE_USER_VAR = "user";

    /**
     * 岗位。
     */
    public final static String GROUP_TYPE_POST = "POST";

    /**
     * 上级部门领导审批。
     */
    public final static String GROUP_TYPE_UP_DEPT_POST_LEADER = "UP_DEPT_POST_LEADER";

    /**
     * 本部门岗位领导审批。
     */
    public final static String GROUP_TYPE_DEPT_POST_LEADER = "DEPT_POST_LEADER";

    /**
     * 本部门岗位前缀。
     */
    public final static String SELF_DEPT_POST_PREFIX = "SELF_DEPT_";

    /**
     * 上级部门岗位前缀。
     */
    public final static String UP_DEPT_POST_PREFIX = "UP_DEPT_";

    /**
     * 当前流程实例所有任务的抄送数据前缀。
     */
    public final static String COPY_DATA_MAP_PREFIX = "copyDataMap_";

    /**
     * 作为临时变量存入任务变量JSONObject对象时的key。
     */
    public static final String COPY_DATA_KEY = "copyDataKey";

    /**
     * 是否为主版本(1是)
     */
    public static final String MAIN_VERSION_1 = "1";
}
