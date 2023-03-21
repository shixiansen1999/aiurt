package com.aiurt.boot.task.mapper;

import cn.hutool.core.date.DateTime;
import com.aiurt.boot.manager.dto.EquipmentDTO;
import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.manager.dto.SubsystemDTO;
import com.aiurt.boot.plan.dto.RepairPoolDetailsDTO;
import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.plan.entity.RepairPoolOrgRel;
import com.aiurt.boot.plan.entity.RepairPoolStationRel;
import com.aiurt.boot.task.dto.*;
import com.aiurt.boot.task.entity.*;
import com.aiurt.common.aspect.annotation.DataColumn;
import com.aiurt.common.aspect.annotation.DataPermission;
import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @Description: repair_task
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@EnableDataPerm
public interface RepairTaskMapper extends BaseMapper<RepairTask> {

    /**
     * 检修任务列表查询
     *
     * @param pageList
     * @param condition
     * @return
     */
    List<RepairTask> selectables(@Param("pageList") Page<RepairTask> pageList, @Param("condition") RepairTask condition);


    /**
     * 检修任务清单查询
     *
     * @param pageList
     * @param condition
     * @return
     */
    List<RepairTaskDTO> selectTasklet(@Param("pageList") Page<RepairTaskDTO> pageList, @Param("condition") RepairTaskDTO condition);

    List<RepairTaskDTO> selectTask(@Param("taskId") String taskId);

    /**
     * 站点下拉查询
     * @param taskId
     * @return
     */
    List<RepairTaskStationDTO> repairTaskStationList(@Param("taskId") String taskId);

    /**
     * 维修单下拉查询
     * @param taskId
     * @param stationCode
     * @return
     */
    List<RepairTaskDTO> selectTaskList(@Param("taskId") String taskId,@Param("stationCode") String stationCode);

    /**
     * 维修单下拉查询有设备
     * @param taskId
     * @return
     */
    List<RepairTaskDTO> selectDeviceTaskList(@Param("taskId") String taskId);

    /**
     * 检修任务详情
     * @param taskId
     * @param stationCode
     * @param deviceId
     * @return
     */
    CheckListDTO selectRepairTaskInfo(@Param("taskId") String taskId,@Param("stationCode") String stationCode,@Param("deviceId") String deviceId);

    /**
     * 设备台账-检修履历
     *
     * @param pageList
     * @param condition
     * @return
     */
    List<RepairTaskDTO> selectTaskletForDevice(@Param("pageList") Page<RepairTaskDTO> pageList, @Param("condition") RepairTaskDTO condition);


    /**
     * 查询站点信息
     *
     * @param planCode
     * @return
     */
    List<StationDTO> selectStationList(String planCode);

    /**
     * 查询设备信息
     *
     * @param code
     * @return
     */
    List<StationDTO> selectStationLists(String code);

    /**
     * 查询编码信息
     *
     * @param id
     * @param majorCode
     * @param subsystemCode
     * @return
     */
    List<RepairTaskDTO> selectCodeList(@Param("id") String id, @Param("majorCode") String majorCode, @Param("subsystemCode") String subsystemCode);

    /**
     * 翻译专业信息
     *
     * @param codeList
     * @return
     */
    List<MajorDTO> translateMajor(List<String> codeList);

    /**
     * 翻译子系统信息
     *
     * @param majorCode
     * @param systemCode
     * @return
     */
    List<SubsystemDTO> translateSubsystem(@Param("majorCode") String majorCode, @Param("systemCode") String systemCode);


    /**
     * 根据设备类型编码集合查询设备类型信息
     *
     * @param codeList 设备类型编码
     * @return
     */
    List<EquipmentDTO> queryNameByCode(List<String> codeList);

    /**
     * 查询检修单信息
     *
     * @param id
     * @return
     */
    CheckListDTO selectCheckList(String id);

    /**
     * 查询任务结果
     *
     * @param id
     * @param status
     * @return
     */
    List<RepairTaskResult> selectSingle(@Param("id") String id, @Param("status") Integer status);


    /**
     * 查询附件信息
     *
     * @param resultId
     * @return
     */
    List<RepairTaskEnclosure> selectEnclosure(String resultId);

    /**
     * 检修池查找
     * @param page
     * @param startDate
     * @param stationCode
     * @param taskCode
     * @return
     */
    List<RepairPoolDetailsDTO> selectRepairPoolList(@Param("page") Page<RepairPoolDetailsDTO> page, @Param("startDate") Date startDate, @Param("stationCode") String stationCode, @Param("taskCode") Set<String> taskCode,@Param("taskId") Set<String> taskId);

    /**
     * 根据code查询检修任务对应的组织机构编码
     *
     * @param planCode
     * @return
     */
    List<String> selectOrgByCode(String planCode);

    /**
     * 按天查询检修任务完成数
     *
     * @param dateTime
     * @return
     */
    List<RepairPoolDetailsDTO> inspectionNumByDay(@Param("dateTime") DateTime dateTime, @Param("repairTaskOrgRels") List<RepairTaskOrgRel> repairTaskOrgRels, @Param("repairTaskStationRels")List<RepairTaskStationRel> repairTaskStationRels, @Param("poolCodeList")List<RepairTaskStandardRel> poolCodeList);

    /**
     * 根据检修任务单号查询异常项目
     * @param code
     * @return
     */
    Integer getTaskExceptionItem(String code);

    /**
     * 根据检修任务单号的检修时间，不用审核和验收取提交时间，审核不验收取审核时间，审核且验收取验收时间
     * @param code
     * @return
     */
    List<Date> getTaskInspectionTime(String code);


    /**
     * 查询管理负责人检修班组的信息
     * @param pageList
     * @param condition
     * @return
     */
    List<OverhaulStatisticsDTOS> readTeamList(@Param("pageList") Page<OverhaulStatisticsDTOS> pageList,@Param("condition") OverhaulStatisticsDTOS condition);

    /**
     * 查询管理负责人检修班组的信息
     * @param condition
     * @return
     */
    List<OverhaulStatisticsDTOS> readTeamLists(@Param("condition") OverhaulStatisticsDTOS condition);

    /**
     * 查询班组下检修人员
     * @param condition
     * @return
     */
    List<OverhaulStatisticsDTO> readNameList(@Param("condition") OverhaulStatisticsDTOS condition);

    /**
     * 查询班组下检修人员
     * @param condition
     * @return
     */
    List<OverhaulStatisticsDTO> readNameLists(@Param("condition") OverhaulStatisticsDTO condition);

    /**
     * 获取状态
     * @param id
     * @return
     */
    List<Integer> getStatus(String id);

    /**
     * 班组编码
     * @param id
     * @return
     */
    String getOrgCode(String id);

    /**
     * 获取名字
     * @param id
     * @return
     */
    String getRealName(String id);

    /**
     * 查询班组下所有人员
     * @param condition
     * @return
     */
    List<OverhaulStatisticsDTO> realNameList(@Param("condition") OverhaulStatisticsDTOS condition);

    /**
     * 获取班组
     * @param id
     * @return
     */
    List<OverhaulStatisticsDTOS> selectDepart(@Param("id") String id);

    /**
     * 获取用户班组
     * @param id
     * @return
     */
    List<OverhaulStatisticsDTOS> getUserOrgCategory(@Param("id") String id);

    /**
     * 查询检修站点
     * @param id
     * @return
     */
    List<String> getRepairTaskStation(@Param("id") String id);


    /**
     * 分页查询
     * @param pageList
     * @return
     */
    List<SystemInformationDTO> getSystemInformation(Page<SystemInformationDTO> pageList);

    /**
     * 统计数量(检修)
     * @param stationCode
     * @param status
     * @return
     */
    Long getMaintenanceQuantity(@Param("stationCode") List<String> stationCode,@Param("status") Long status);

    /**
     * 统计数量(巡检)
     * @param stationCode
     * @param status
     * @return
     */
    Long getInspection(@Param("stationCode") List<String> stationCode,@Param("status") Long status);

    /**
     * 根据站点Code查询故障信息
     * @param stationCode
     * @return
     */
    List<String> getFaultCodeList(@Param("stationCode") List<String> stationCode);

    /**
     * 故障已完成数量
     * @param faultCode
     * @return
     */
    Long getFaultQuantity(@Param("faultCode") List<String> faultCode);

}
