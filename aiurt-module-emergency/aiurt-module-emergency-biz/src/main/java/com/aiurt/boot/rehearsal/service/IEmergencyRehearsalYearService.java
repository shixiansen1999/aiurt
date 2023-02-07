package com.aiurt.boot.rehearsal.service;

import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearAddDTO;
import com.aiurt.boot.rehearsal.dto.EmergencyRehearsalYearDTO;
import com.aiurt.boot.rehearsal.entity.EmergencyRehearsalYear;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    /**
     * 应急演练管理-年演练计划添加
     *
     * @param emergencyRehearsalYearAddDTO
     */
    String add(EmergencyRehearsalYearAddDTO emergencyRehearsalYearAddDTO);

    /**
     * 应急演练管理-年演练计划通过id删除
     *
     * @param id
     */
    void delete(String id);

    /**
     * 应急演练管理-年演练计划编辑
     *
     * @param emergencyRehearsalYearAddDTO
     */
    String edit(EmergencyRehearsalYearAddDTO emergencyRehearsalYearAddDTO);

    /**
     * 应急演练管理-导出年演练计划excel
     *
     * @param request
     * @param ids
     * @return
     */
    void exportXls(HttpServletRequest request, HttpServletResponse response, String ids,String orgCode);
}
