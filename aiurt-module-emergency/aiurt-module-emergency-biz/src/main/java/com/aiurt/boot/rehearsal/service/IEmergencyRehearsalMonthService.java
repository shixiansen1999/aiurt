package com.aiurt.boot.rehearsal.service;

import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalMonth;
import com.aiurt.boot.rehearsal.vo.EmergencyRehearsalMonthVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: emergency_rehearsal_month
 * @Author: aiurt
 * @Date: 2022-11-29
 * @Version: V1.0
 */
public interface IEmergencyRehearsalMonthService extends IService<EmergencyRehearsalMonth> {

    String addMonthPlan(EmergencyRehearsalMonth emergencyRehearsalMonth);

    /**
     * 生成月计划编码
     *
     * @return
     */
    String getMonthCode();

    /**
     * 应急月演练计划-分页列表查询
     *
     * @param page
     * @param emergencyRehearsalMonth
     * @return
     */
    IPage<EmergencyRehearsalMonthVO> queryPageList(Page<EmergencyRehearsalMonthVO> page, EmergencyRehearsalMonth emergencyRehearsalMonth);

    /**
     * 应急月演练计划-通过id删除
     *
     * @param id
     */
    void delete(String id);
}
