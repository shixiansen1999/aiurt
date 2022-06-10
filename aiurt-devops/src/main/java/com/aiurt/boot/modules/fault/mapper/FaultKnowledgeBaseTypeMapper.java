package com.aiurt.boot.modules.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.swsc.copsms.modules.fault.entity.FaultKnowledgeBaseType;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 故障知识库类型
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface FaultKnowledgeBaseTypeMapper extends BaseMapper<FaultKnowledgeBaseType> {


    /**
     * 删除故障知识库类型
     * @param id
     * @return
     */
    int deleteOne(@Param("id") Integer id);

}
