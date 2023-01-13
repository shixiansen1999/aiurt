package com.aiurt.boot.rehearsal.mapper;

import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalMonthDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import com.aiurt.boot.rehearsal.vo.EmergencyRehearsalMonthVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: emergency_rehearsal_month
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
public interface EmergencyRehearsalMonthMapper extends BaseMapper<EmergencyRehearsalMonth> {
    /**
     * 应急月演练计划-分页列表查询
     *
     * @param page
     * @param emergencyRehearsalMonthDTO
     * @return
     */
    IPage<EmergencyRehearsalMonthVO> queryPageList(@Param("page") Page<EmergencyRehearsalMonthVO> page, @Param("condition") EmergencyRehearsalMonthDTO emergencyRehearsalMonthDTO);
}
