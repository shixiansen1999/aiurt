package com.aiurt.boot.rehearsal.service;

import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: emergency_rehearsal_year
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyRehearsalYearService extends IService<EmergencyRehearsalYear> {
    /**
     * 应急演练管理-年演练计划分页列表查询
     *
     * @param page
     * @param emergencyRehearsalYearDTO
     * @return
     */
    IPage<EmergencyRehearsalYear> queryPageList(Page<EmergencyRehearsalYear> page, EmergencyRehearsalYearDTO emergencyRehearsalYearDTO);
}
