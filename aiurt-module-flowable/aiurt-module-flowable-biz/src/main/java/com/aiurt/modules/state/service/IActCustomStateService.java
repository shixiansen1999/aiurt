package com.aiurt.modules.state.service;

import com.aiurt.modules.state.entity.ActCustomState;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: act_custom_state
 * @Author: wgp
 * @Date: 2023-08-15
 * @Version: V1.0
 */
public interface IActCustomStateService extends IService<ActCustomState> {
    /**
     * 查询指定的流程任务的流程状态。
     *
     * @param processInstanceId 流程实例Id。
     * @return 查询结果。
     */
    ActCustomState getCustomStateByProcessInstanceId(String processInstanceId);

}
