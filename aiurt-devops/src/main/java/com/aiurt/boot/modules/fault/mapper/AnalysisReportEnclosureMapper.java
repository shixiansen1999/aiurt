package com.aiurt.boot.modules.fault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.fault.entity.AnalysisReportEnclosure;

import java.util.List;

/**
 * @Description: 分析报告-附件表
 * @Author: swsc
 * @Date:   2021-09-14
 * @Version: V1.0
 */
public interface AnalysisReportEnclosureMapper extends BaseMapper<AnalysisReportEnclosure> {

    /**
     * 根据id查询附件列表
     * @param analysisReportId
     * @return
     */
    List<String> query(Long analysisReportId);

}
