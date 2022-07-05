package com.aiurt.modules.fault.mapper;

import java.util.List;

import com.aiurt.modules.basic.entity.CsWork;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: fault
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface FaultMapper extends BaseMapper<Fault> {

     /**
      * 根据编码查询故障工单
      * @param code
      * @return
      */
     Fault selectByCode(@Param("code") String code);

     /**
      * 根据专业编码查询作业类型
      * @param majorCode
      * @return
      */
     List<CsWork> queryCsWorkByMajorCode(@Param("majorCode") String majorCode);


     /**
      * 查询匹配的 故障解决方案
      * @param faultKnowledgeBase
      * @return
      */
     List<String> queryKnowledge(FaultKnowledgeBase faultKnowledgeBase);
}
