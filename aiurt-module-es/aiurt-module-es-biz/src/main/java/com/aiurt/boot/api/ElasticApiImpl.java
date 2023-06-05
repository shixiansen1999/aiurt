package com.aiurt.boot.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.core.common.model.HighLight;
import com.aiurt.boot.core.common.model.Sort;
import com.aiurt.boot.core.service.ElasticService;
import com.aiurt.boot.core.utils.ElasticTools;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseMatchDTO;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseReqDTO;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseResDTO;
import com.aiurt.modules.knowledge.entity.KnowledgeBase;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ElasticApiImpl implements ElasticAPI {

    @Autowired
    private ElasticService elasticService;

    @Override
    public BulkResponse[] saveBatch(List<KnowledgeBase> list) throws Exception {
        return elasticService.saveBatch(list);
    }

    @Override
    public IPage<KnowledgeBaseResDTO> search(Page<KnowledgeBaseResDTO> page, KnowledgeBaseReqDTO knowledgeBaseReqDTO) throws IOException {
        String[] indexs = {"knowledge_base"};
        // 条件查询
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        // 高亮设置
        HighLight highLight = null;
        List<String> highlightField = ElasticTools.getHighlightField(KnowledgeBase.class);
        if (CollUtil.isNotEmpty(highlightField)) {
            highLight = new HighLight();
//            highLight.setPreTag("<span style='color:red'>");
//            highLight.setPostTag("</span>");
//            highLight.setHighLightList(highlightField);
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.preTags("<span style='color:red'>");
            highlightBuilder.postTags("</span>");
            for (String field : highlightField) {
                highlightBuilder.field(field);
            }
//            List<HighlightBuilder.Field> fields = highlightBuilder.fields();
//            log.info("{}", fields);
            highLight.setHighlightBuilder(highlightBuilder);
        }
        // 排序
        Sort sort = null;
        if (ObjectUtil.isNotEmpty(knowledgeBaseReqDTO)) {
            String keyword = knowledgeBaseReqDTO.getKeyword();
            String majorCode = knowledgeBaseReqDTO.getMajorCode();
            String systemCode = knowledgeBaseReqDTO.getSystemCode();
            String deviceTypeCode = knowledgeBaseReqDTO.getDeviceTypeCode();
            String materialCode = knowledgeBaseReqDTO.getMaterialCode();
            Integer sortFlag = knowledgeBaseReqDTO.getSort();
            AtomicReference<BoolQueryBuilder> boolQueryBuilder = new AtomicReference<>();
//            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            // 多字段匹配
            if (ObjectUtil.isNotEmpty(keyword)) {
                if (ObjectUtil.isEmpty(boolQueryBuilder.get())) {
                    boolQueryBuilder.set(QueryBuilders.boolQuery());
                }
                String[] fieldNames = {
                        "faultPhenomenon", "knowledgeBaseTypeName", "majorName",
                        "systemName", "materialName", "deviceTypeName", "faultLevelName"
                };
                boolQueryBuilder.get().should(QueryBuilders.multiMatchQuery(keyword, fieldNames));
                boolQueryBuilder.get().should(QueryBuilders.nestedQuery(
                        "reasonSolutions",
                        QueryBuilders.matchQuery("reasonSolutions.faultCause", keyword),
                        ScoreMode.Total)
                );
            }
            // 专业编号
            Optional.ofNullable(majorCode).ifPresent(major -> {
                if (ObjectUtil.isEmpty(boolQueryBuilder.get())) {
                    boolQueryBuilder.set(QueryBuilders.boolQuery());
                }
                boolQueryBuilder.get().must(QueryBuilders.termQuery("majorCode", major));
            });
            // 子系统编号
            Optional.ofNullable(systemCode).ifPresent(system -> {
                if (ObjectUtil.isEmpty(boolQueryBuilder.get())) {
                    boolQueryBuilder.set(QueryBuilders.boolQuery());
                }
                boolQueryBuilder.get().must(QueryBuilders.termQuery("systemCode", system));
            });
            // 设备类型编号
            Optional.ofNullable(deviceTypeCode).ifPresent(deviceType -> {
                if (ObjectUtil.isEmpty(boolQueryBuilder.get())) {
                    boolQueryBuilder.set(QueryBuilders.boolQuery());
                }
                boolQueryBuilder.get().must(QueryBuilders.termQuery("deviceTypeCode", deviceType));
            });
            // 组件编号
            Optional.ofNullable(materialCode).ifPresent(material -> {
                if (ObjectUtil.isEmpty(boolQueryBuilder.get())) {
                    boolQueryBuilder.set(QueryBuilders.boolQuery());
                }
                boolQueryBuilder.get().must(QueryBuilders.termQuery("materialCode", material));
            });
//            // 备件
//            if (ObjectUtil.isNotEmpty(materialCode)) {
//                boolQueryBuilder.must(QueryBuilders.nestedQuery(
//                        "reasonSolutions",
//                        QueryBuilders.nestedQuery("spareParts", QueryBuilders.termQuery("materialCode", materialCode), ScoreMode.Total),
//                        ScoreMode.Total
//                ));
//            }
            if (ObjectUtil.isNotEmpty(boolQueryBuilder.get())) {
                queryBuilder = boolQueryBuilder.get();
            }
            // 根据浏览次数降序排序
            if (ObjectUtil.isNotEmpty(sortFlag)) {
                Sort.Order sortOrder = new Sort.Order(SortOrder.DESC, "scanNum");
                sort = new Sort(sortOrder);
            }
        }
        IPage<KnowledgeBase> pageList = elasticService.search(new Page(page.getCurrent(), page.getSize()), queryBuilder, indexs, KnowledgeBase.class, highLight, sort);
        List<KnowledgeBase> records = pageList.getRecords();
        List<KnowledgeBaseResDTO> knowledgeBaseRes = new ArrayList<>();
        KnowledgeBaseResDTO knowledgeBase = null;
        if (CollUtil.isNotEmpty(records)) {
            for (KnowledgeBase record : records) {
                knowledgeBase = new KnowledgeBaseResDTO();
                BeanUtils.copyProperties(record, knowledgeBase);
                knowledgeBase.setTitle(record.getFaultPhenomenon());
                // 组件/部位字段拼接专业等信息
                String joinComponent = Arrays.asList(record.getMajorName(), record.getDeviceTypeName(), record.getMaterialName())
                        .stream()
                        .filter(l -> ObjectUtil.isNotEmpty(l))
                        .collect(Collectors.joining("/"));
                knowledgeBase.setJoinComponent(joinComponent);
                knowledgeBaseRes.add(knowledgeBase);
            }
        }
        page.setCurrent(pageList.getCurrent());
        page.setSize(pageList.getSize());
        page.setTotal(pageList.getTotal());
        page.setRecords(knowledgeBaseRes);
        return page;
    }

    @Override
    public IPage<KnowledgeBaseResDTO> knowledgeBaseMatching(Page<KnowledgeBaseResDTO> page, KnowledgeBaseMatchDTO knowledgeBaseMatchDTO) throws IOException {
        String[] indexs = {"knowledge_base"};
        // 条件查询
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        if (ObjectUtil.isNotEmpty(knowledgeBaseMatchDTO)) {
            List<String> devices = knowledgeBaseMatchDTO.getDevices();
            List<String> phenomenons = knowledgeBaseMatchDTO.getPhenomenons();
            AtomicReference<BoolQueryBuilder> boolQueryBuilder = new AtomicReference<>();
            Optional.ofNullable(knowledgeBaseMatchDTO.getMajor())
                    .ifPresent(major -> {
                        if (ObjectUtil.isEmpty(boolQueryBuilder.get())) {
                            boolQueryBuilder.set(QueryBuilders.boolQuery());
                        }
                        boolQueryBuilder.get().must(QueryBuilders.matchQuery("majorName", major));
                    });
            Optional.ofNullable(knowledgeBaseMatchDTO.getSubsystem())
                    .ifPresent(subsystem -> {
                        if (ObjectUtil.isEmpty(boolQueryBuilder.get())) {
                            boolQueryBuilder.set(QueryBuilders.boolQuery());
                        }
                        boolQueryBuilder.get().must(QueryBuilders.matchQuery("systemName", subsystem));
                    });
            if (CollUtil.isNotEmpty(devices)) {
                if (ObjectUtil.isEmpty(boolQueryBuilder.get())) {
                    boolQueryBuilder.set(QueryBuilders.boolQuery());
                }
                boolQueryBuilder.get().must(QueryBuilders.matchQuery("deviceTypeName", devices.stream().collect(Collectors.joining(" "))));
            }
            if (CollUtil.isNotEmpty(phenomenons)) {
                if (ObjectUtil.isEmpty(boolQueryBuilder.get())) {
                    boolQueryBuilder.set(QueryBuilders.boolQuery());
                }
                boolQueryBuilder.get().must(QueryBuilders.matchQuery("faultPhenomenon", phenomenons.stream().collect(Collectors.joining(" "))));
            }
            if (ObjectUtil.isNotEmpty(boolQueryBuilder.get())) {
                queryBuilder = boolQueryBuilder.get();
            }
        }
        IPage<KnowledgeBase> pageList = elasticService.search(new Page(page.getCurrent(), page.getSize()), queryBuilder, indexs, KnowledgeBase.class, null, null);
        List<KnowledgeBase> records = pageList.getRecords();
        List<KnowledgeBaseResDTO> knowledgeBaseRes = new ArrayList<>();
        KnowledgeBaseResDTO knowledgeBase = null;
        if (CollUtil.isNotEmpty(records)) {
            for (KnowledgeBase record : records) {
                knowledgeBase = new KnowledgeBaseResDTO();
                BeanUtils.copyProperties(record, knowledgeBase);
                knowledgeBase.setTitle(record.getFaultPhenomenon());
                knowledgeBaseRes.add(knowledgeBase);
            }
        }
        page.setCurrent(pageList.getCurrent());
        page.setSize(pageList.getSize());
        page.setTotal(pageList.getTotal());
        page.setRecords(knowledgeBaseRes);
        return page;
    }

    @Override
    public List<String> phenomenonMatching(KnowledgeBaseMatchDTO knowledgeBaseMatchDTO) throws Exception {
        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
        final String fieldName = "faultPhenomenon";
        String index = "knowledge_base";
        if (ObjectUtil.isNotEmpty(knowledgeBaseMatchDTO)) {
            List<String> devices = knowledgeBaseMatchDTO.getDevices();
            List<String> phenomenons = knowledgeBaseMatchDTO.getPhenomenons();
            AtomicReference<BoolQueryBuilder> boolQueryBuilder = new AtomicReference<>();
            Optional.ofNullable(knowledgeBaseMatchDTO.getMajor())
                    .ifPresent(major -> {
                        if (ObjectUtil.isEmpty(boolQueryBuilder.get())) {
                            boolQueryBuilder.set(QueryBuilders.boolQuery());
                        }
                        boolQueryBuilder.get().must(QueryBuilders.matchQuery("majorName", major));
                    });
            Optional.ofNullable(knowledgeBaseMatchDTO.getSubsystem())
                    .ifPresent(subsystem -> {
                        if (ObjectUtil.isEmpty(boolQueryBuilder.get())) {
                            boolQueryBuilder.set(QueryBuilders.boolQuery());
                        }
                        boolQueryBuilder.get().must(QueryBuilders.matchQuery("systemName", subsystem));
                    });
            if (CollUtil.isNotEmpty(devices)) {
                if (ObjectUtil.isEmpty(boolQueryBuilder.get())) {
                    boolQueryBuilder.set(QueryBuilders.boolQuery());
                }
                String device = devices.stream().collect(Collectors.joining(" "));
                boolQueryBuilder.get().must(QueryBuilders.matchQuery("deviceTypeName", device));
            }
            if (CollUtil.isNotEmpty(phenomenons)) {
                if (ObjectUtil.isEmpty(boolQueryBuilder.get())) {
                    boolQueryBuilder.set(QueryBuilders.boolQuery());
                }
                String phenomenon = phenomenons.stream().collect(Collectors.joining(" "));
                boolQueryBuilder.get().must(QueryBuilders.matchQuery("faultPhenomenon", phenomenon));
            }
            if (ObjectUtil.isNotEmpty(boolQueryBuilder.get())) {
                queryBuilder = boolQueryBuilder.get();
            }
        }
        SearchSourceBuilder builder = new SearchSourceBuilder();
        CollapseBuilder collapseBuilder = new CollapseBuilder(fieldName + ".keyword");
        builder.query(queryBuilder);
        builder.size(10);
        builder.collapse(collapseBuilder);

        SearchRequest searchRequest = new SearchRequest(index);
        searchRequest.source(builder);

        List<String> phenomenons = new ArrayList<>();
        SearchResponse searchResponse = elasticService.search(searchRequest);
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit hit : searchHits) {
            KnowledgeBase knowledgeBase = JSON.parseObject(hit.getSourceAsString(), KnowledgeBase.class);
            if (ObjectUtil.isNotEmpty(knowledgeBase) && ObjectUtil.isNotEmpty(knowledgeBase.getFaultPhenomenon())) {
                phenomenons.add(knowledgeBase.getFaultPhenomenon());
            }
        }
        return phenomenons;
    }

    @Override
    public void removeKnowledgeBase(String id) {
        try {
            elasticService.deleteById(id, KnowledgeBase.class);
        } catch (Exception e) {
            log.error("删除ES故障知识库记录失败：", e.getMessage());
        }
    }

    @Override
    public void removeBatchKnowledgeBase(List<String> ids) {
        try {
            BoolQueryBuilder builder = QueryBuilders.boolQuery().must(QueryBuilders.termsQuery("id", ids));
            elasticService.deleteByCondition(builder, KnowledgeBase.class);
        } catch (Exception e) {
            log.error("批量删除ES故障知识库记录失败：", e.getMessage());
        }
    }
}
