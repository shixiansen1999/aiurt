package com.aiurt.boot.modules.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.fault.entity.KnowledgeBaseEnclosure;

import java.util.List;

/**
 * @Description: 故障知识库附件表
 * @Author: swsc
 * @Date:   2021-09-17
 * @Version: V1.0
 */
public interface KnowledgeBaseEnclosureMapper extends BaseMapper<KnowledgeBaseEnclosure> {

    /**
     * 根据id查询
     * @param id
     * @return
     */
    List<String> selectByKnowledgeId(Long id);

    /**
     * 删除附件
     * @param id
     */
    void deleteByName(Long id);

}
