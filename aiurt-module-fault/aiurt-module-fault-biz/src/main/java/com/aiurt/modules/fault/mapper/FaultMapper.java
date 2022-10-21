package com.aiurt.modules.fault.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.basic.entity.CsWork;
import com.aiurt.modules.fault.dto.FaultFrequencyDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultRepairRecord;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * @Description: fault
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@EnableDataPerm
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

     /**
      * 分页查询
      * @param page
      * @param knowledgeBase
      * @return
      */
     List<FaultKnowledgeBase> pageList(Page<FaultKnowledgeBase> page, @Param("condition") FaultKnowledgeBase knowledgeBase);


     /**
      *
      * @param startDate
      * @param endDate
      * @return
      */
     List<FaultFrequencyDTO> selectBySubSystemCode(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

     /**
      * 翻译专业信息
      * @param codeList
      * @return
      */
     List<String> translateMajors(List<String> codeList);


     /**
      * 翻译专业子系统信息
      * @param codeList
      * @return
      */
     List<String> translateSubsystems(List<String> codeList);

     /**
      * 获取线路名
      * @param stationCode
      * @return
      */
    String getStationName(String stationCode);

     /**
      * 获取维修状态名
      * @param status
      * @return
      */
     String getStatusName(Integer status);

    /**
     * 获取维修单是否是当天
     * @param id
     * @param date
     * @return
     */
    FaultRepairRecord getUserToday(@Param("id") String id, @Param("date")Date date);
}
