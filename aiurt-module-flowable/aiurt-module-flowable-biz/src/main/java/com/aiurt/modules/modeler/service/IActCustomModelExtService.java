package com.aiurt.modules.modeler.service;

import com.aiurt.modules.modeler.entity.ActCustomModelExt;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 流程属性扩展表
 * @Author: aiurt
 * @Date:   2023-09-12
 * @Version: V1.0
 */
public interface IActCustomModelExtService extends IService<ActCustomModelExt> {

    /**
     * 根据流程定义信息查询流属性
     * @param processDefinitionId
     * @return
     */
    ActCustomModelExt getByProcessDefinitionId(String processDefinitionId);
}
