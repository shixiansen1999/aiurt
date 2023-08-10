package com.aiurt.modules.multideal.service;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

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
     *
     * @param nodeId
     * @param businessData
     * @param user
     * @return
     */
    List<String> getNextNodeUserList(String nodeId, JSONObject businessData, List<String> user);
}
