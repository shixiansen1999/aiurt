package com.aiurt.boot.modules.fault.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.swsc.copsms.common.result.FaultCodesResult;
import com.swsc.copsms.common.result.FaultKnowledgeBaseResult;
import com.swsc.copsms.common.result.FaultResult;
import com.swsc.copsms.modules.fault.entity.FaultKnowledgeBase;
import com.swsc.copsms.modules.fault.param.FaultKnowledgeBaseParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 故障知识库
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface FaultKnowledgeBaseMapper extends BaseMapper<FaultKnowledgeBase> {

    /**
     * 查询故障知识库
     * @param page
     * @param queryWrapper
     * @param param
     * @return
     */
    IPage<FaultKnowledgeBaseResult> queryFaultKnowledgeBase(IPage<FaultKnowledgeBaseResult> page, Wrapper<FaultKnowledgeBaseResult> queryWrapper,
                                                            @Param("param") FaultKnowledgeBaseParam param);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    String selectCodeById(Integer id);

    /**
     * 更改关联故障
     * @param id
     * @param faultCodes
     */
    void updateAssociateFault(Integer id, String faultCodes);

    /**
     * 删除故障知识库
     * @param id
     * @return
     */
    int deleteOne(@Param("id") Integer id);

}
