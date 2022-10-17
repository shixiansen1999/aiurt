package com.aiurt.modules.subsystem.service;

import com.aiurt.modules.subsystem.dto.SubsystemFaultDTO;
import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

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
    IPage<SubsystemFaultDTO> getSubsystemFailureReport(Page<?> page, String time, String subsystemCode, List<String> deviceTypeCode);
}
