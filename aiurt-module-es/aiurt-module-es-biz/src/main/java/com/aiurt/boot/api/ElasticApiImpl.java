package com.aiurt.boot.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.core.common.model.HighLight;
import com.aiurt.boot.core.common.model.Sort;
import com.aiurt.boot.core.service.ElasticService;
import com.aiurt.boot.core.utils.ElasticTools;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseReqDTO;
import com.aiurt.modules.knowledge.dto.KnowledgeBaseResDTO;
import com.aiurt.modules.knowledge.entity.KnowledgeBase;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
            List<HighlightBuilder.Field> fields = highlightBuilder.fields();
            log.info("{}", fields);
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
            BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            // 多字段匹配
            if (ObjectUtil.isNotEmpty(keyword)) {
                String[] fieldNames = {"faultPhenomenon", "majorName", "systemName", "materialName", "deviceTypeName"};
                boolQueryBuilder.must(QueryBuilders.multiMatchQuery(keyword, fieldNames));
            }
            // 专业编号
            Optional.ofNullable(majorCode).ifPresent(major -> boolQueryBuilder.must(QueryBuilders.termQuery("majorCode", major)));
            // 子系统编号
            Optional.ofNullable(systemCode).ifPresent(system -> boolQueryBuilder.must(QueryBuilders.termQuery("systemCode", system)));
            // 设备类型编号
            Optional.ofNullable(deviceTypeCode).ifPresent(deviceType -> boolQueryBuilder.must(QueryBuilders.termQuery("deviceTypeCode", deviceType)));
            // 组件编号
            Optional.ofNullable(materialCode).ifPresent(material -> boolQueryBuilder.must(QueryBuilders.termQuery("materialCode", material)));
//            // 备件
//            if (ObjectUtil.isNotEmpty(materialCode)) {
//                boolQueryBuilder.must(QueryBuilders.nestedQuery(
//                        "reasonSolutions",
//                        QueryBuilders.nestedQuery("spareParts", QueryBuilders.termQuery("materialCode", materialCode), ScoreMode.Total),
//                        ScoreMode.Total
//                ));
//            }
            queryBuilder = boolQueryBuilder;
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
                knowledgeBaseRes.add(knowledgeBase);
            }
        }
        page.setCurrent(pageList.getCurrent());
        page.setSize(pageList.getSize());
        page.setTotal(pageList.getTotal());
        page.setRecords(knowledgeBaseRes);
        return page;
    }
}
