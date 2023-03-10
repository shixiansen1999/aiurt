package com.aiurt.modules.faultproducereport.mapper;

import com.aiurt.modules.faultproducereport.dto.FaultProduceReportDTO;
import com.aiurt.modules.faultproducereport.entity.FaultProduceReport;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 生产日报
 * @Author: aiurt
 * @Date:   2023-02-23
 * @Version: V1.0
 */
public interface FaultProduceReportMapper extends BaseMapper<FaultProduceReport> {

    FaultProduceReport getDetail(@Param("id") String id);

    List<FaultProduceReportDTO> queryPageList(@Param("pageList") Page<FaultProduceReportDTO> pageList, @Param("majorCodeList") List<String> majorCodeList, @Param("beginDay") String beginDay, @Param("endDay") String endDay);

    /**
     * 生产日报审核分页列表查询
     * @param pageList
     * @param majorCodeList
     * @param beginDay
     * @param endDay
     * @return
     */
    List<FaultProduceReportDTO> queryPageAuditList(@Param("pageList") Page<FaultProduceReportDTO> pageList, @Param("userName") String userName, @Param("majorCodeList") List<String> majorCodeList, @Param("beginDay") String beginDay, @Param("endDay") String endDay);

    List<String> selectIdList(@Param("majorCodeList") List<String> majorCodeList, @Param("beginDay") String beginDay, @Param("endDay") String endDay);
}
