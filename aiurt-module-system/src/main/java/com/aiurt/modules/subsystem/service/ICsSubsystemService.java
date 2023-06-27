package com.aiurt.modules.subsystem.service;

import com.aiurt.modules.subsystem.dto.SubsystemFaultDTO;
import com.aiurt.modules.subsystem.dto.SystemByCodeDTO;
import com.aiurt.modules.subsystem.dto.YearFaultDTO;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description: cs_subsystem
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface ICsSubsystemService extends IService<CsSubsystem> {
    /**
     * 添加
     *
     * @param csSubsystem
     * @return
     */
    Result<?> add(CsSubsystem csSubsystem);
    /**
     * 编辑
     *
     * @param csSubsystem
     * @return
     */
    Result<?> update(CsSubsystem csSubsystem);

    /**
     * 统计报表-子系统分析
     * @param page
     * @param startTime
     * @param endTime
     * @param subsystemCode
     * @param deviceTypeCode
     * @return
     */
    Page<SubsystemFaultDTO> getSubsystemFailureReport(Page<SubsystemFaultDTO> page, String startTime, String endTime, SubsystemFaultDTO subsystemCode, List<String> deviceTypeCode);

    /**
     * 统计报表-子系统分析-年次数数据
     * @return
     */
    List<YearFaultDTO> yearFault(String name);

    /**
     * 下拉框
     * @param subsystemCode
     * @return
     */
    List<SubsystemFaultDTO> deviceTypeCodeByNameDTO(List<String> subsystemCode);

    /**
     * 统计报表-子系统分析-年分钟
     * @return
     */
    List<YearFaultDTO> yearMinuteFault(String name);

    /**
     * 导出
     * @param request
     * @param subsystemCode
     * @param deviceTypeCode
     * @param startTime
     * @param endTime
     * @param exportField
     * @return
     */
    ModelAndView reportSystemExport(HttpServletRequest request, SubsystemFaultDTO subsystemCode, List<String> deviceTypeCode, String startTime, String endTime, String exportField);

    /**
     * 根据code查询
     * @param subsystemCode
     * @return
     */
    SystemByCodeDTO csSubsystemByCode(String subsystemCode);

    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException;

    List<YearFaultDTO> yearTrendChartFault(String startTime, String endTime, List<String> systemCodes);
}
