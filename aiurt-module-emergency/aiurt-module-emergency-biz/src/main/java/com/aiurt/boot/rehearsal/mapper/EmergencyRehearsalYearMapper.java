package com.aiurt.boot.rehearsal.mapper;

import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: emergency_rehearsal_year
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
public interface EmergencyRehearsalYearMapper extends BaseMapper<EmergencyRehearsalYear> {
    /**
     * 应急演练管理-年演练计划分页列表查询
     *
     * @param page
     * @param emergencyRehearsalYearDTO
     * @param orgCodes
     * @return
     */
    Page<EmergencyRehearsalYear> queryPageList(@Param("page") Page<EmergencyRehearsalYear> page,
                                               @Param("condition") EmergencyRehearsalYearDTO emergencyRehearsalYearDTO,
                                               @Param("orgCodes") List<String> orgCodes);
}
