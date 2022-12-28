package com.aiurt.boot.constant;

/**
 * 巡视待办消息的跳转url常量类
 */
public interface PatrolMessageUrlConstant {
    /**
     * 巡视任务确认后执行任务跳转的URL
     */
    String AFFIRM_URL = "@/views/pollingCheck/issueModal.vue";
    /**
     * 巡视任务确认后执行任务APP跳转的URL
     */
    String AFFIRM_APP_URL = "";
    /**
     * 提交后审核跳转的URL
     */
    String AUDIT_URL = "@/views/pollingCheck/PatrolTaskModal.vue";
    /**
     * 提交后审核APP跳转的URL
     */
    String AUDIT_APP_URL = "";
}
