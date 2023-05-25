package com.aiurt.boot.knowledge.service.impl;

import com.aiurt.boot.knowledge.service.KnowledgeBaseSearchService;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseReqDTO;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseResDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author
 * @description
 */
@Slf4j
@Service
public class KnowledgeBaseSearchServiceImpl implements KnowledgeBaseSearchService {
    @Override
    public IPage<KnowledgeBaseResDTO> search(Page<KnowledgeBaseResDTO> page, KnowledgeBaseReqDTO knowledgeBaseReqDTO) {
        page.setRecords(new ArrayList<>());
        return page;
    }
}
