package com.aiurt.modules.faultknowledgebasetype.service;

import com.aiurt.modules.faultknowledgebasetype.dto.MajorDTO;
import com.aiurt.modules.faultknowledgebasetype.entity.FaultKnowledgeBaseType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 故障知识分类
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
public interface IFaultKnowledgeBaseTypeService extends IService<FaultKnowledgeBaseType> {
    /**
     * 知识库类别树
     * @return
     */
    List<MajorDTO> faultKnowledgeBaseTypeTreeList();
    /**
     *   添加
     * @param faultKnowledgeBaseType
     * @return
     */
    void add(FaultKnowledgeBaseType faultKnowledgeBaseType);

}
