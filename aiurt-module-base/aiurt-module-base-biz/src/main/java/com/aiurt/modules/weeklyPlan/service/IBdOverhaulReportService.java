package com.aiurt.modules.weeklyplan.service;

import com.aiurt.modules.weeklyplan.entity.BdOverhaulReport;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.util.List;

/**
 * @Description: bd_overhaul_report
 * @Author: jeecg-boot
 * @Date:   2021-05-17
 * @Version: V1.0
 */
public interface IBdOverhaulReportService extends IService<BdOverhaulReport> {
    /*
    // 添加检修任务指派时选择的设备信息
    List<BdDeviceArchives> getDeviceArchives(String stationId,String typeId,String code,String name);
    // 根据id获取站点指派记录信息
    ListOverhaulReportDTO getAssignStationOverhaulReportById(String taskId);
    // 获取站点指派记录信息
    Page<ListOverhaulReportDTO> getAssignStationOverhaulReport(Page<ListOverhaulReportDTO> pageList, ListOverhaulReportParamDTO listOverhaulReportParamDTO);
    // 根据设备id和计划id查询计划检修单
    OverhaulDeviceReportDTO getOverhaulDeviceReportByTaskIdAndequipmentId(String taskId, String equipmentId);
    // 获得设备类型树
    List<TreeNode>  getEquipmentTypeTree(String stationId);
    // 获得设备下拉列表（检修单）
    List<GetEquipmentDropDownDTO> getEquipmentDropDown(String taskId);
    // 驻班工程师签字
    Result<?> signatureOfResidentEngineer(SignatureParamDTO signatureParamDTO);
    // 根据设备id查询所需要的检修项目
    List<OverhaulEquipmentDTO> getOverhaulContentByequipmentId(String equipmentId,String equipmentCode,String taskId);
    // 完成检修项目
    Result<?> carryOutOverhaulContent(List<BdOverhaulDeviceReport> bdOverhaulDeviceReport);
    //安技报表 - 常规检修
    List<OverhaulReportDTO> getConventionalOverhaulReport(Integer year, Integer month, String stationIds, Integer teamId);
    // 任务跳转查询检修单
    OverhaulDeviceReportDTO getOverhaulDeviceReportByTaskId(String taskId);
    // 安卓app结束检修任务
    Result<?> endOverhaulTask(String taskId);
    // 查看检修任务的检修资料
    BdOverhaulData getOverhaulDataBytaskIdAndEquipmentId(String taskId, String equipmentId);
    // 获取每天工区的检修任务数量
    List<ModuleStatisticsExport> getSiteOverhaulNum();
    // 添加检修任务
    Result<?> addOverhaul(BdOverhaulReport bdOverhaulReport);
     */

    /**
     * 根据计划令id撤销检修任务
     * @param id 计划令id
     * @return
     */
    Result<?> cancelTask(String id);
}
