package com.aiurt.modules.modeler.service;

import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: flowable流程模板定义信息
 * @Author: aiurt
 * @Date:   2022-07-08
 * @Version: V1.0
 */
public interface IActCustomModelInfoService extends IService<ActCustomModelInfo> {

    /**
     * 添加流程模板
     * @param actCustomModelInfo
     * @return
     */
    ActCustomModelInfo add(ActCustomModelInfo actCustomModelInfo);

    /**
     * 删除模型
     * @param idList
     */
    void deleteById(List<String> idList);

    /**
     * 根据modelid查询模板信息
     * @param modelId
     * @return
     */
    ActCustomModelInfo queryByModelId(String modelId);
}
