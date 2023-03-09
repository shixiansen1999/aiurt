package com.aiurt.boot.mapper;


import com.aiurt.modules.search.dto.FaultKnowledgeBaseDTO;

import java.util.List;

/**
 * @Description: 故障操作日志
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
public interface EsMapper{
    /**
     * 根据编码查询故障工单
     * @param
     * @return
     */
    List<FaultKnowledgeBaseDTO> selectList();

}
