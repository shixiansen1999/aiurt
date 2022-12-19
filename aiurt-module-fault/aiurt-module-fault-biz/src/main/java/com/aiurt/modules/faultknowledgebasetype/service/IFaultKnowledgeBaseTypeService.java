package com.aiurt.modules.faultknowledgebasetype.service;

import com.aiurt.modules.faultknowledgebasetype.dto.MajorDTO;
import com.aiurt.modules.faultknowledgebasetype.dto.SelectTableDTO;
import com.aiurt.modules.faultknowledgebasetype.entity.FaultKnowledgeBaseType;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

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
     * @param majorCode
     * @param systemCode
     * @return
     */
    List<MajorDTO> faultKnowledgeBaseTypeTreeList(String majorCode,String systemCode);
    /**
     *   添加
     * @param faultKnowledgeBaseType
     * @return
     */
    Result<String> add(FaultKnowledgeBaseType faultKnowledgeBaseType);

    List<SelectTableDTO> knowledgeBaseTypeTreeList(String systemCode);
}
