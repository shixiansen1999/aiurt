package com.aiurt.boot.knowledge.service;

import com.aiurt.modules.knowledge.dto.KnowledgeBaseReqDTO;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseResDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author
 * @description
 */
public interface KnowledgeBaseSearchService {
    /**
     * 分页列表查询
     *
     * @param page
     * @param knowledgeBaseReqDTO
     * @return
     */
    IPage<KnowledgeBaseResDTO> search(Page<KnowledgeBaseResDTO> page, KnowledgeBaseReqDTO knowledgeBaseReqDTO);
}
