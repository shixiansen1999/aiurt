package com.aiurt.modules.subsystem.service;

import com.aiurt.modules.subsystem.dto.SubsystemFaultDTO;
import com.aiurt.modules.subsystem.dto.YearFaultDTO;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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
     * @param time
     * @param subsystemCode
     * @param deviceTypeCode
     * @return
     */
    Page<SubsystemFaultDTO> getSubsystemFailureReport(Page<SubsystemFaultDTO> page, String time, SubsystemFaultDTO subsystemCode, List<String> deviceTypeCode);

    /**
     * 统计报表-子系统分析-年次数数据
     * @return
     */
    List<YearFaultDTO> yearFault();

    /**
     * 下拉框
     * @param subsystemCode
     * @return
     */
    List<SubsystemFaultDTO> deviceTypeCodeByNameDTO(String subsystemCode);

    /**
     * 统计报表-子系统分析-年分钟
     * @return
     */
    List<YearFaultDTO> yearMinuteFault();

    /**
     * 导出
     * @param request
     * @param subsystemCode
     * @param deviceTypeCode
     * @param time
     * @param exportField
     * @return
     */
    ModelAndView reportSystemExport(HttpServletRequest request, SubsystemFaultDTO subsystemCode, List<String> deviceTypeCode, String time, String exportField);
}
