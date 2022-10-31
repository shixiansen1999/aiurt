package com.aiurt.modules.online.businessdata.service;

import com.aiurt.modules.online.businessdata.entity.ActCustomBusinessData;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 流程中间业务数据
 * @Author: aiurt
 * @Date:   2022-10-27
 * @Version: V1.0
 */
public interface IActCustomBusinessDataService extends IService<ActCustomBusinessData> {

    /**
     *
     * @param processInstanceId
     * @return
     */
    ActCustomBusinessData queryByProcessInstanceId(String processInstanceId, String taskId);



}
