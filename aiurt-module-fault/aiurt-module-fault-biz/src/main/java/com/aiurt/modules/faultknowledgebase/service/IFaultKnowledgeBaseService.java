package com.aiurt.modules.faultknowledgebase.service;

import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 故障知识库
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
public interface IFaultKnowledgeBaseService extends IService<FaultKnowledgeBase> {
    /**
     * 故障知识库查询
     * @param page
     * @param faultKnowledgeBase
     * @return IPage<faultKnowledgeBase>
     */
    IPage<FaultKnowledgeBase> readAll(Page<FaultKnowledgeBase> page, FaultKnowledgeBase faultKnowledgeBase);
}
