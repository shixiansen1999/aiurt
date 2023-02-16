package com.aiurt.boot.service;

import com.aiurt.modules.search.entity.FileAnalysisData;
import com.aiurt.modules.search.dto.DocumentManageRequestDTO;
import com.aiurt.modules.search.dto.SearchRequestDTO;
import com.aiurt.modules.search.dto.SearchResponseDTO;
import com.aiurt.modules.search.dto.TermResponseDTO;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2023/2/159:11
 */
public interface ISearchService {

    /**
     * 分页查询故障知识库
     * @param searchRequest
     * @return
     */
    IPage<SearchResponseDTO> faultKnowledgeList(SearchRequestDTO searchRequest);

    /**
     * 故障知识库词语补全提示
     * @param searchKey
     * @return
     */
    List<TermResponseDTO> suggest(String searchKey);

    /**
     * 规程规范与知识库词语补全提示
     * @param searchKey
     * @return
     */
    List<TermResponseDTO> documentManageSuggest(String searchKey);

    /**
     * 分页查询规程规范与知识库
     * @param documentManageRequest
     * @return
     */
    IPage<FileAnalysisData> documentManageList(DocumentManageRequestDTO documentManageRequest);

}
