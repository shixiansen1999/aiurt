package com.aiurt.modules.flow.constants;

import java.util.HashMap;
import java.util.Map;

/**
 * 工作流任务触发BUTTON。
 *
 * @author Jerry
 * @date 2021-06-06
 */
public final class FlowApprovalType {

    /**
     * 保存。(填写完表单信息后，保存数据并发起流程)
     */
    public static final String SAVE = "save";

    /**
     * 保存发起流程
     */
    public static final String ONLY_SAVE = "only_save";

    /**
     * 同意。提交（提交给下一节点处理）
     */
    public static final String AGREE = "agree";

    /**
     * 拒绝。 也是
     * 与AGREE 的功能是一致的，直接提交到下一步
     */
    @Deprecated
    public static final String REFUSE = "refuse";

    /**
     * 拒绝
     *
     * 与AGREE 的功能是一致的，直接提交到下一步
     */
    @Deprecated
    public static final String REJECT_TO_STAR = "rejectToStart";

    /**
     * 驳回。
     * 与AGREE 的功能是一致的，直接提交到下一步
     */
    @Deprecated
    public static final String REJECT = "reject";

    /**
     * 退回到指定的节点
     */
    private static final String REJECT_WANT_TASK = "reject_want_task";

    /**
     *  驳回到第一个用户任务,退回流程第一个节点
     */
    public static final String REJECT_FIRST_USER_TASK = "reject_first_user_task";


    /**
     * 转办
     */
    public static final String TRANSFER = "transfer";


    /**
     * 作废, 撤销，直接到结束节点
     */
    public static final String CANCEL = "cancel";

    /**
     * 删除流程
     */
    public static final String DELETE = "delete";


    /**
     * 终止流程
     */
    public static final String STOP = "stop";

    /**
     * 加签
     */
    public static final String ADD_MULTI = "addMulti";

    /**
     *
     */
    public static final String REDUCE_MULTI = "reduceMulti";

    public static final String RECALL = "recall";

    public static final String AUTO_COMPLETE = "AUTO_COMPLETE";



    public static final Map<Object, String> DICT_MAP = new HashMap<>(16);
    static {
        DICT_MAP.put(SAVE, "保存");
        DICT_MAP.put(AGREE, "提交");
        DICT_MAP.put(REJECT_TO_STAR, "拒绝");
        DICT_MAP.put(REFUSE, "拒绝");
        DICT_MAP.put(REJECT_TO_STAR, "拒绝");
        DICT_MAP.put(REJECT, "拒绝");
        DICT_MAP.put(REJECT_WANT_TASK, "退回到指定的节点");
        DICT_MAP.put(REJECT_FIRST_USER_TASK, "退回申请人");
        DICT_MAP.put(TRANSFER, "转办");
        DICT_MAP.put(CANCEL, "撤销");
        DICT_MAP.put(DELETE, "删除流程");
        DICT_MAP.put(STOP, "终止流程");
        DICT_MAP.put(ADD_MULTI, "加签");
        DICT_MAP.put(REDUCE_MULTI, "减签");
        DICT_MAP.put(RECALL, "撤回");
        DICT_MAP.put(AUTO_COMPLETE, "自动通过");
    }

    /**
     * 判断参数是否为当前常量字典的合法值。
     *
     * @param value 待验证的参数值。
     * @return 合法返回true，否则false。
     */
    public static boolean isValid(Integer value) {
        return value != null && DICT_MAP.containsKey(value);
    }

    /**
     * 私有构造函数，明确标识该常量类的作用。
     */
    private FlowApprovalType() {
    }
}
