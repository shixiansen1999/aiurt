package com.aiurt.modules.multideal.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

/**
 * @author fgw
 * 多实例用户接口
 */
public interface IMultiInstanceUserService {

    /**
     * 获取当前活动的用户列表
     * @param taskId
     * @return
     */
    List<String> getCurrentUserList(String taskId);

    /**
     * 查询下一步用户信息
     * @param nodeId
     * @param businessData
     * @param user
     * @return
     */
    List<String> getNextNodeUserList(String nodeId, Map<String, Object> businessData, List<String> user);
}
