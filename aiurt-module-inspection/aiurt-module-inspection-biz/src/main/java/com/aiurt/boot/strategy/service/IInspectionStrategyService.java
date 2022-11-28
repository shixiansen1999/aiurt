package com.aiurt.boot.strategy.service;

import com.aiurt.boot.manager.dto.EquipmentOverhaulDTO;
import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.plan.dto.RepairDeviceDTO;
import com.aiurt.boot.strategy.dto.InspectionStrategyDTO;
import com.aiurt.boot.strategy.entity.InspectionStrategy;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @Description: inspection_strategy
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface IInspectionStrategyService extends IService<InspectionStrategy> {
    /**
     * 分页查询
     * @param page
     * @param inspectionStrategyDTO
     * @return
     */
    IPage<InspectionStrategyDTO> pageList(Page<InspectionStrategyDTO> page, InspectionStrategyDTO inspectionStrategyDTO);

    /**
     * 添加
     * @param inspectionStrategyDTO
     */
    void add(InspectionStrategyDTO inspectionStrategyDTO);

    /**
     * 编辑
     * @param inspectionStrategyDTO
     */
    void updateId(InspectionStrategyDTO inspectionStrategyDTO);

    /**
     * 删除关联表
     * @param id
     */
    void removeId(String id);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    InspectionStrategyDTO getId(String id);
    /**
     * 生成年检计划
     *
     * @param id
     * @return
     */
    Result addAnnualPlan(String id);
    /**
     * 重新生成年检计划
     *
     * @param id
     * @return
     */
    Result addAnnualNewPlan(String id);

    /**
     * 查询所选择的设备
     * @param code
     * @return
     */
    List<Device> viewDetails(String code);
    /**
     * 修改生效状态
     * @param id,status
     * @return
     */
    void modify(String id);

    /**
     * 根据检修策略code和检修标准id查询检修标准对应的设备
     * @param page
     * @param inspectionStrCode
     * @param inspectionStaCode
     * @return
     */
    IPage<RepairDeviceDTO> queryDeviceByCodeAndId(Page<RepairDeviceDTO> page, String inspectionStrCode, String inspectionStaCode);

    /**
     * 查询专业，专业子系统的信息
     *
     * @param id
     * @return
     */
    List<MajorDTO> selectMajorCodeList(String id);


    /**
     * 查询专业，专业子系统的信息
     *
     * @param strategyId
     * @param majorCode
     * @param subsystemCode
     * @return
     */
    EquipmentOverhaulDTO selectEquipmentOverhaulList(String strategyId, String majorCode, String subsystemCode);

    /**
     * 导出excel
     * @param request
     * @param inspectionStrategyDTO
     * @return
     */
    void exportXls(HttpServletRequest request, HttpServletResponse response,  InspectionStrategyDTO inspectionStrategyDTO);

    /**
     * 导入检修策略
     * @param request
     * @param response
     * @return
     */
    Result<?> importExcel(HttpServletRequest request, HttpServletResponse response);
}
