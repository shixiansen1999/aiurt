package com.aiurt.boot.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.EsConstant;
import com.aiurt.boot.service.ISearchService;
import com.aiurt.boot.utils.ElasticsearchClientUtil;
import com.aiurt.modules.search.dto.SearchRequestDTO;
import com.aiurt.modules.search.dto.SearchResponseDTO;
import com.aiurt.modules.search.dto.TermResponseDTO;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2023/2/159:12
 */
@Service
@Slf4j
public class SearchServiceImpl implements ISearchService {
    @Autowired
    private ElasticsearchClientUtil esUtil;

    @Override
    public IPage<SearchResponseDTO> faultKnowledgeList(SearchRequestDTO searchRequest) {
        // 构建DSL语句
        SearchSourceBuilder builder = buildSearchSourceBuilder(searchRequest);
        // 查询es
        SearchResponse searchResponse = esUtil.queryDocument(EsConstant.FAULT_KNOWLEDGE_INDEX, builder);
        // 构建返回结果
        IPage<SearchResponseDTO> result = buildSearchResponse(searchRequest, searchResponse);
        return result;
    }

    @Override
    public List<TermResponseDTO> suggest(String searchKey) {
        // 构建DSL语句
        SearchSourceBuilder builder = buildSuggestSearchSourceBuilder(searchKey);
        // 查询es
        SearchResponse searchResponse = esUtil.queryDocument(EsConstant.FAULT_KNOWLEDGE_INDEX, builder);
        // 构建返回结果
        List<TermResponseDTO> result = buildSuggestResponse(searchResponse);
        return result;
    }


    /**
     * 解析词语建议结果
     *
     * @param searchResponse
     * @return
     */
    private List<TermResponseDTO> buildSuggestResponse(SearchResponse searchResponse) {
        List<TermResponseDTO> result = CollUtil.newArrayList();

        Suggest suggest = searchResponse.getSuggest();
        List<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> entries =
                suggest.getSuggestion(EsConstant.FAULT_KNOWLEDGE_SUGGEST).getEntries();

        for (Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option> entry : entries) {
            for (Suggest.Suggestion.Entry.Option option : entry.getOptions()) {
                String keyword = option.getText().toString();
                if (StrUtil.isNotEmpty(keyword)) {
                    if (result.contains(keyword)) {
                        continue;
                    }
                    result.add(new TermResponseDTO(keyword));
                }
            }
        }

        return result;
    }

    /**
     * 构建
     *
     * @param searchKey
     * @return
     */
    private SearchSourceBuilder buildSuggestSearchSourceBuilder(String searchKey) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // suggestField为指定在哪个字段搜索，searchKey为输入内容，TEN为10，代表输出显示最大条数
        CompletionSuggestionBuilder suggestionBuilderDistrict = SuggestBuilders
                .completionSuggestion(EsConstant.FAULT_PHENOMENON)
                .size(EsConstant.TEN)
                .skipDuplicates(true);

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        // fault_knowledge_suggest是一个存储标识，下文调用的时候保持一致即可
        suggestBuilder.addSuggestion(EsConstant.FAULT_KNOWLEDGE_SUGGEST, suggestionBuilderDistrict).setGlobalText(searchKey);
        searchSourceBuilder.suggest(suggestBuilder);

        return searchSourceBuilder;
    }

    /**
     * 构建返回结果
     *
     * @param searchRequest
     * @param searchResponse
     * @return
     */
    private IPage<SearchResponseDTO> buildSearchResponse(SearchRequestDTO searchRequest, SearchResponse searchResponse) {
        Page<SearchResponseDTO> searchResponseDTOPage = new Page<>();

        // 解析结果
        SearchHits hits = searchResponse.getHits();
        List<SearchResponseDTO> searchResponsList = CollUtil.newArrayList();
        if (null != hits.getHits() && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SearchResponseDTO searchResponseDto = JSON.parseObject(sourceAsString, SearchResponseDTO.class);

                // 处理高亮字段
                Map<String, HighlightField> highlightFields = hit.getHighlightFields();
                HighlightField highlightFaultPhenomenon = highlightFields.get(EsConstant.FAULT_PHENOMENON);
                if (ObjectUtil.isNotEmpty(highlightFaultPhenomenon)) {
                    String replaceFaultPhenomenon = highlightFaultPhenomenon.getFragments()[0].string();
                    searchResponseDto.setFaultPhenomenon(replaceFaultPhenomenon);
                }

                HighlightField highlightFaultReason = highlightFields.get(EsConstant.FAULT_REASON);
                if (ObjectUtil.isNotEmpty(highlightFaultReason)) {
                    String replaceFaultReason = highlightFaultReason.getFragments()[0].string();
                    ;
                    searchResponseDto.setFaultReason(replaceFaultReason);
                }

                HighlightField highlightSolution = highlightFields.get(EsConstant.SOLUTION);
                if (ObjectUtil.isNotEmpty(highlightSolution)) {
                    String replaceSolution = highlightSolution.getFragments()[0].string();
                    ;
                    searchResponseDto.setSolution(replaceSolution);

                }

                HighlightField highlightMethod = highlightFields.get(EsConstant.METHOD);
                if (ObjectUtil.isNotEmpty(highlightMethod)) {
                    String replaceMethod = highlightMethod.getFragments()[0].string();
                    ;
                    searchResponseDto.setMethod(replaceMethod);

                }
                searchResponsList.add(searchResponseDto);
            }
        }

        // 结果记录
        searchResponseDTOPage.setRecords(searchResponsList);
        // 分页信息 - 当前页
        searchResponseDTOPage.setCurrent(searchRequest.getPageNo());
        // 分页信息 - 每页显示条数
        searchResponseDTOPage.setSize(searchRequest.getPageSize());
        // 分页信息 - 总记录数
        searchResponseDTOPage.setTotal(hits.getTotalHits().value);
        return searchResponseDTOPage;
    }

    /**
     * 构建DSL语句
     *
     * @param searchRequestDto
     * @return
     */
    private SearchSourceBuilder buildSearchSourceBuilder(SearchRequestDTO searchRequestDto) {
        SearchSourceBuilder builder = new SearchSourceBuilder();

        /**
         * 查询：
         * 模糊匹配[故障现象，故障原因，解决方案，方法]
         * 过滤[故障现象分类，设备分类，设备组件]
         * 排序[创建时间]
         */
        // 构建bool - query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StrUtil.isNotEmpty(searchRequestDto.getKeyword())) {
            boolQuery.must(QueryBuilders.multiMatchQuery(searchRequestDto.getKeyword(), EsConstant.METHOD, EsConstant.FAULT_PHENOMENON, EsConstant.FAULT_REASON, EsConstant.SOLUTION));
        }
        // filter - 按照故障现象分类进行查询
        if (StrUtil.isNotEmpty(searchRequestDto.getKnowledgeBaseTypeCode())) {
            boolQuery.filter(QueryBuilders.termQuery(EsConstant.KNOWLEDGE_BASE_TYPE_CODE, searchRequestDto.getKnowledgeBaseTypeCode()));
        }
        // filter - 按照设备分类进行查询
        if (StrUtil.isNotEmpty(searchRequestDto.getDeviceTypeCode())) {
            boolQuery.filter(QueryBuilders.termQuery(EsConstant.DEVICE_TYPE_CODE, searchRequestDto.getDeviceTypeCode()));
        }
        // filter - 按照设备组件进行查询
        if (StrUtil.isNotEmpty(searchRequestDto.getMaterialCode())) {
            boolQuery.filter(QueryBuilders.termQuery(EsConstant.MATERIAL_CODE, searchRequestDto.getMaterialCode()));
        }

        // 封装查询条件
        builder.query(boolQuery);

        // 排序
        if (StrUtil.isNotEmpty(searchRequestDto.getSort())) {
            // sort=createTime_asc,updateTime_desc
            String sort = searchRequestDto.getSort();
            List<String> sortList = StrUtil.split(sort, ',');
            for (String sortStr : sortList) {
                String[] s = sortStr.split("_");
                SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
                builder.sort(s[0], order);
            }
        }
        /**
         * 分页：es索引从0开始
         * pageNo:1 from:0 pageSize:10 [0,1,2,3,4,5,6,7,8,9]
         * pageNo:2 from:1 pageSize:10
         * from = (pageNum-1) * size
         */
        Integer pageNo = ObjectUtil.isEmpty(searchRequestDto.getPageNo()) || searchRequestDto.getPageNo() < 1 ? EsConstant.FAULT_KNOWLEDGE_PAGE_NO_1 : searchRequestDto.getPageNo();
        Integer pageSize = ObjectUtil.isEmpty(searchRequestDto.getPageSize()) ? EsConstant.FAULT_KNOWLEDGE_PAGE_SIZE_10 : searchRequestDto.getPageSize();
        builder.from((pageNo - 1) * pageSize);
        builder.size(pageSize);

        // 高亮 - 字段：method,faultPhenomenon，faultReason，solution
        if (StrUtil.isNotEmpty(searchRequestDto.getKeyword())) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field(new HighlightBuilder.Field(EsConstant.METHOD));
            highlightBuilder.field(new HighlightBuilder.Field(EsConstant.FAULT_PHENOMENON));
            highlightBuilder.field(new HighlightBuilder.Field(EsConstant.FAULT_REASON));
            highlightBuilder.field(new HighlightBuilder.Field(EsConstant.SOLUTION));
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            builder.highlighter(highlightBuilder);
        }
        return builder;
    }
}
