package com.aiurt.boot.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.EsConstant;
import com.aiurt.boot.service.ISearchService;
import com.aiurt.boot.utils.ElasticsearchClientUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.search.dto.DocumentManageRequestDTO;
import com.aiurt.modules.search.dto.SearchRequestDTO;
import com.aiurt.modules.search.dto.SearchResponseDTO;
import com.aiurt.modules.search.dto.TermResponseDTO;
import com.aiurt.modules.search.entity.FileAnalysisData;
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

import java.lang.reflect.Field;
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

    /**
     * 分页查询故障知识库
     *
     * @param searchRequest
     * @return
     */
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

    /**
     * 故障知识库词语补全提示
     *
     * @param searchKey
     * @return
     */
    @Override
    public List<TermResponseDTO> suggest(String searchKey) {
        return doSuggest(searchKey, EsConstant.FAULT_KNOWLEDGE_INDEX, EsConstant.FAULT_PHENOMENON, EsConstant.FAULT_KNOWLEDGE_SUGGEST);
    }

    /**
     * 规程规范与知识库词语补全提示
     *
     * @param searchKey
     * @return
     */
    @Override
    public List<TermResponseDTO> documentManageSuggest(String searchKey) {
        return doSuggest(searchKey, EsConstant.FILE_DATA_INDEX, EsConstant.ATTACHMENT_NAME, EsConstant.DOCUMENT_MANAGE_SUGGEST);
    }

    /**
     * 分页查询规程规范与知识库
     *
     * @param documentManageRequest
     * @return
     */
    @Override
    public IPage<FileAnalysisData> documentManageList(DocumentManageRequestDTO documentManageRequest) {
        // 构建DSL语句
        SearchSourceBuilder builder = buildDocumentManageSourceBuilder(documentManageRequest);
        // 查询es
        SearchResponse searchResponse = esUtil.queryDocument(EsConstant.FILE_DATA_INDEX, builder);
        // 构建返回结果
        IPage<FileAnalysisData> result = buildDocumentManageResponse(documentManageRequest, searchResponse);
        return result;
    }

    private IPage<FileAnalysisData> buildDocumentManageResponse(DocumentManageRequestDTO documentManageRequest, SearchResponse searchResponse) {
        Page<FileAnalysisData> result = new Page<>();
        // 解析结果,处理高亮字段替换
        SearchHits hits = searchResponse.getHits();
        List<FileAnalysisData> searchResponsList = CollUtil.newArrayList();
        if (ObjectUtil.isNotEmpty(hits.getHits())) {
            for (SearchHit hit : hits.getHits()) {
                // 处理高亮字段
                buildHighlight(hit, FileAnalysisData.class);
            }
        }

        // 结果记录
        result.setRecords(searchResponsList);
        // 分页信息 - 当前页
        result.setCurrent(documentManageRequest.getPageNo());
        // 分页信息 - 每页显示条数
        result.setSize(documentManageRequest.getPageSize());
        // 分页信息 - 总记录数
        result.setTotal(hits.getTotalHits().value);
        return result;
    }

    /**
     * 高亮字段替换内容
     *
     * @param hit
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> T buildHighlight(SearchHit hit, Class<T> clazz) {
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        T value = JSON.parseObject(hit.getSourceAsString(), clazz);
        Field[] fields = clazz.getDeclaredFields();

        // 遍历所有字段属性
        for (Field field : fields) {
            field.setAccessible(true);

            // 驼峰转换
            String name = StrUtil.toUnderlineCase(field.getName());

            if (highlightFields.containsKey(name)) {
                HighlightField highlightField = highlightFields.get(name);
                String replaceValue = highlightField.fragments()[0].toString();
                try {
                    field.set(value, replaceValue);
                } catch (IllegalAccessException e) {
                    log.error("buildHighlight IllegalAccessException：{}", e);
                }
            }
        }
        return (T) value;
    }

    /**
     * 构建规范知识库查询器
     *
     * @param documentManageRequest
     * @return
     */
    private SearchSourceBuilder buildDocumentManageSourceBuilder(DocumentManageRequestDTO documentManageRequest) {
        SearchSourceBuilder builder = new SearchSourceBuilder();

        /**
         * 查询：
         * 模糊匹配[故障现象，故障原因，解决方案，方法]
         * 过滤[故障现象分类，设备分类，设备组件]
         * 排序[创建时间]
         */
        // 构建bool - query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (StrUtil.isNotEmpty(documentManageRequest.getKeyword())) {
            boolQuery.must(QueryBuilders.multiMatchQuery(documentManageRequest.getKeyword(), EsConstant.NAME, EsConstant.CONTENT));
        }
        // filter - 按照文档格式进行查询
        if (StrUtil.isNotEmpty(documentManageRequest.getFormat())) {
            boolQuery.filter(QueryBuilders.termQuery(EsConstant.FORMAT, documentManageRequest.getFormat()));
        }

        // filter - 按照文档类型进行查询
        if (StrUtil.isNotEmpty(documentManageRequest.getTypeId())) {
            boolQuery.filter(QueryBuilders.termQuery(EsConstant.TYPE_ID, documentManageRequest.getTypeId()));
        }

        // 封装查询条件
        builder.query(boolQuery);

        // 排序
        setSort(documentManageRequest.getSort(), builder);

        // 分页
        setPage(documentManageRequest.getPageNo(), documentManageRequest.getPageSize(), builder);

        // 高亮
        setHighLight(documentManageRequest.getKeyword(), builder, EsConstant.NAME, EsConstant.CONTENT);
        return builder;
    }

    /**
     * 词语补全建议
     *
     * @param searchKey
     * @param esIndex      es索引
     * @param suggestField 建议字段
     * @param suggestIden  es存储标识
     * @return
     */
    private List<TermResponseDTO> doSuggest(String searchKey, String esIndex, String suggestField, String suggestIden) {
        // 构建DSL语句
        SearchSourceBuilder builder = buildSuggestSearchSourceBuilder(searchKey, suggestField, suggestIden);
        // 查询es
        SearchResponse searchResponse = esUtil.queryDocument(esIndex, builder);
        // 构建返回结果
        List<TermResponseDTO> result = buildSuggestResponse(searchResponse, suggestIden);
        return result;
    }


    /**
     * 解析词语建议结果
     *
     * @param searchResponse
     * @return
     */
    private List<TermResponseDTO> buildSuggestResponse(SearchResponse searchResponse, String suggestIden) {
        List<TermResponseDTO> result = CollUtil.newArrayList();

        Suggest suggest = searchResponse.getSuggest();
        List<? extends Suggest.Suggestion.Entry<? extends Suggest.Suggestion.Entry.Option>> entries =
                suggest.getSuggestion(suggestIden).getEntries();

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
     * 构建词语建议查询器
     *
     * @param searchKey
     * @return
     */
    private SearchSourceBuilder buildSuggestSearchSourceBuilder(String searchKey, String suggestField, String suggestIden) {
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // suggestField为指定在哪个字段搜索，searchKey为输入内容，TEN为10，代表输出显示最大条数
        CompletionSuggestionBuilder suggestionBuilderDistrict = SuggestBuilders
                .completionSuggestion(suggestField + EsConstant.SUGGEST_SUFFIX)
                .size(EsConstant.TEN)
                .skipDuplicates(true);

        SuggestBuilder suggestBuilder = new SuggestBuilder();
        // suggestIden是一个存储标识，下文调用的时候保持一致即可
        suggestBuilder.addSuggestion(suggestIden, suggestionBuilderDistrict).setGlobalText(searchKey);
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

        // 解析结果,处理高亮字段替换
        SearchHits hits = searchResponse.getHits();
        List<SearchResponseDTO> searchResponsList = CollUtil.newArrayList();
        if (null != hits.getHits() && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                searchResponsList.add(buildHighlight(hit, SearchResponseDTO.class));
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
        setSort(searchRequestDto.getSort(), builder);

        // 分页
        setPage(searchRequestDto.getPageNo(), searchRequestDto.getPageSize(), builder);

        // 高亮 - 字段：method,faultPhenomenon，faultReason，solution
        setHighLight(searchRequestDto.getKeyword(), builder, EsConstant.METHOD, EsConstant.FAULT_PHENOMENON, EsConstant.FAULT_REASON, EsConstant.SOLUTION);
        return builder;
    }

    /**
     * 关键词高亮显示
     *
     * @param keyword    关键词
     * @param builder    查询构造器
     * @param fieldNames 高亮字段
     */
    private void setHighLight(String keyword, SearchSourceBuilder builder, String... fieldNames) {
        if (StrUtil.isNotEmpty(keyword) && null != fieldNames) {
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags(EsConstant.HIGH_LIGHT_PRE_TAGS);
            highlightBuilder.postTags(EsConstant.HIGH_LIGHT_POST_TAGS);
            for (String field : fieldNames) {
                highlightBuilder.field(new HighlightBuilder.Field(field));
                builder.highlighter(highlightBuilder);
            }
        }
    }


    /**
     * 设置分页
     *
     * @param pageNo
     * @param pageSize
     * @param builder
     */
    private void setPage(Integer pageNo, Integer pageSize, SearchSourceBuilder builder) {
        /**
         * 分页：es索引从0开始
         * pageNo:1 from:0 pageSize:10 [0,1,2,3,4,5,6,7,8,9]
         * pageNo:2 from:1 pageSize:10
         * from = (pageNum-1) * size
         */
        Integer pageNoTemp = ObjectUtil.isEmpty(pageNo) || pageNo < 1 ? EsConstant.FAULT_KNOWLEDGE_PAGE_NO_1 : pageNo;
        Integer pageSizeTemp = ObjectUtil.isEmpty(pageSize) ? EsConstant.FAULT_KNOWLEDGE_PAGE_SIZE_10 : pageSize;
        builder.from((pageNoTemp - 1) * pageSizeTemp);
        builder.size(pageSizeTemp);
    }

    /**
     * 条件排序
     *
     * @param sort
     * @param builder
     */
    private void setSort(String sort, SearchSourceBuilder builder) {
        if (StrUtil.isNotEmpty(sort)) {
            // sort=createTime_asc,updateTime_desc
            List<String> sortList = StrUtil.split(sort, CommonConstant.COMMA_SEPARATOR);
            for (String sortStr : sortList) {
                String[] s = sortStr.split(CommonConstant.UNDER_LINE_SEPARATOR);
                SortOrder order = s[1].equalsIgnoreCase(EsConstant.SORT_ORDER_ASC) ? SortOrder.ASC : SortOrder.DESC;
                builder.sort(s[0], order);
            }
        }
    }
}
