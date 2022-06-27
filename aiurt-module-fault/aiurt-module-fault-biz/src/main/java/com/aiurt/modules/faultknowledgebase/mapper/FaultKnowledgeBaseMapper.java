package com.aiurt.modules.faultknowledgebase.mapper;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 故障知识库
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
public interface FaultKnowledgeBaseMapper extends BaseMapper<FaultKnowledgeBase> {
    /**
     * 分页查询故障知识库
     * @param page
     * @param condition
     * @return List<FaultAnalysisReport>
     * */
    List<FaultKnowledgeBase> readAll(@Param("page")Page<FaultKnowledgeBase> page, @Param("condition")FaultKnowledgeBase condition);

    /**
     * 分页查询故障知识库
     * @param id
     * @return List<FaultAnalysisReport>
     * */
    FaultKnowledgeBase readOne(@Param("id")String id);
}
