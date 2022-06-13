package com.aiurt.boot.modules.fault.mapper;

import com.aiurt.common.result.FaultKnowledgeBaseResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.aiurt.boot.modules.fault.entity.FaultKnowledgeBase;
import com.aiurt.boot.modules.fault.param.FaultKnowledgeBaseParam;
import org.apache.ibatis.annotations.Param;
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
     * @param param
     * @return
     */
    IPage<FaultKnowledgeBaseResult> queryFaultKnowledgeBase(IPage<FaultKnowledgeBaseResult> page,
                                                            @Param("param") FaultKnowledgeBaseParam param);

    /**
     * 根据id查询关联故障
     * @param id
     * @return
     */
    String selectCodeById(Long id);

    /**
     * 更改关联故障
     * @param id
     * @param faultCodes
     */
    void updateAssociateFault(Integer id, String faultCodes);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    FaultKnowledgeBaseResult selectByKnowledgeId(Long id);

    /**
     * 修改查看次数
     * @param num
     * @param id
     * @return
     */
    void updateScanNum(Integer num ,Long id);

}
