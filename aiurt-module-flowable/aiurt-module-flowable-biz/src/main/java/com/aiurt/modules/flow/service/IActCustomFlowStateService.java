package com.aiurt.modules.flow.service;

import com.aiurt.modules.flow.entity.ActCustomFlowState;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;
import java.util.Set;

/**
 * @Description: 流程系统状态表
 * @Author: fugaowei
 * @Date:   2023-10-25
 * @Version: V1.0
 */
public interface IActCustomFlowStateService extends IService<ActCustomFlowState> {

    /**
     * 更新流程状态
     * @param processInstanceId
     * @param state
     */
    void updateFlowState(String processInstanceId, Integer state);

    /**
     * 获取流程在状态
     * @param processInstanceIdSet
     * @return
     */
    Map<String, String> flowStateMap(Set<String> processInstanceIdSet);
}
