package org.jeecg.common.system.api;

import java.util.List;

/**
 * 系统用户使用数量
 */
public interface ISysUserUsageApi {

    /**
     * 更新用户使用数
     * @param userId
     * @param userNameList
     */
    public void updateSysUserUsage(String userId, List<String> userNameList);
}
