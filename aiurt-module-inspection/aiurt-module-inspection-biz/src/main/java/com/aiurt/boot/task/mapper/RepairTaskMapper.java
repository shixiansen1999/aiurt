package com.aiurt.boot.task.mapper;

import cn.hutool.core.date.DateTime;
import com.aiurt.boot.index.dto.MapDTO;
import com.aiurt.boot.index.dto.RepairTaskNum;
import com.aiurt.boot.manager.dto.EquipmentDTO;
import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.manager.dto.SubsystemDTO;
import com.aiurt.boot.plan.dto.RepairPoolDetailsDTO;
import com.aiurt.boot.plan.dto.StationDTO;
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
    @DataPermission({
            @DataColumn(key = "deptName",value = "t2.org_code"),
            @DataColumn(key = "stationName",value = "t4.station_code"),
            @DataColumn(key = "majorName",value = "t6.major_code"),
            @DataColumn(key = "systemName",value = "t6.subsystem_code")
    })
    List<RepairTask> selectables(@Param("pageList") Page<RepairTask> pageList, @Param("condition") RepairTask condition);

    /**
     * 根据任务id查询任务，这里主要是选择数据后导出使用，因此加不加数据权限都可以
     * @param selections
     * @return
     */
    List<RepairTask> selectablesByIds(List<String> selections);

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
    List<RepairTaskDTO> selectTaskletForDevice(@Param("pageList") Page<RepairTaskDTO> pageList, @Param("condition") RepairTaskDTO condition,@Param("multipleDeviceTypes") String multipleDeviceTypes);


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
     * @param startDate
     * @param endDate
     * @return
     */
    @DataPermission({
            @DataColumn(key = "deptName",value = "t2.org_code"),
            @DataColumn(key = "stationName",value = "t3.station_code"),
            @DataColumn(key = "lineName",value = "t3.line_code"),
            @DataColumn(key = "majorName",value = "t4.major_code"),
            @DataColumn(key = "systemName",value = "t4.subsystem_code")
    })
    List<RepairTaskNum> selectRepairPoolList(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    /**
     * 获取检修情况列表。
     *
     * @param page 分页对象，用于指定查询的页码和每页记录数。
     * @param startDate 查询开始日期，根据此日期筛选符合条件的检修情任务。
     * @param stationCode 车站编码，用于筛选指定车站的检修情任务。
     * @param flag 车站编码，用于筛选指定车站的检修情任务。
     * @return 返回一个包含检修情任务详细信息的列表，每个检修情任务由一个 RepairPoolDetailsDTO 对象表示。
     */
    @DataPermission({
            @DataColumn(key = "deptName",value = "rtor.org_code"),
            @DataColumn(key = "stationName",value = "rtsr.station_code"),
            @DataColumn(key = "lineName",value = "rtsr.line_code"),
            @DataColumn(key = "majorName",value = "rtsrl.major_code"),
            @DataColumn(key = "systemName",value = "rtsrl.subsystem_code")
    })
    List<RepairPoolDetailsDTO> getMaintenanceSituation(@Param("page") Page<RepairPoolDetailsDTO> page, @Param("startDate") Date startDate, @Param("stationCode") String stationCode,@Param("flag") String flag);

    @DataPermission({
            @DataColumn(key = "deptName",value = "t2.org_code"),
            @DataColumn(key = "stationName",value = "t3.station_code"),
            @DataColumn(key = "lineName",value = "t3.line_code"),
            @DataColumn(key = "majorName",value = "t5.major_code"),
            @DataColumn(key = "systemName",value = "t5.subsystem_code")
    })
    List<RepairPoolDetailsDTO> selectRepairPoolList2(@Param("page") Page<RepairPoolDetailsDTO> page, @Param("startDate") Date startDate);

    /**
     * 根据code查询检修任务对应的组织机构编码
     *
     * @param planCodes
     * @return
     */
    List<MapDTO> selectOrgByCode(List<String> planCodes);

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
     * @param condition
     * @return
     */
    List<OverhaulStatisticsDTOS> readTeamList(@Param("condition") OverhaulStatisticsDTOS condition);

    /**
     * 查询班组所负责的检修的信息
     * @param condition
     * @return
     */
    List<OverhaulStatisticsDTOS> countTeamList(@Param("condition") OverhaulStatisticsDTOS condition);

    Long readTaskList(@Param("condition") OverhaulStatisticsDTOS condition);

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
     * 统计完成数量
     * @param condition
     * @return
     */
    Long countCompletedNumber(@Param("condition") OverhaulStatisticsDTO condition);

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
     * @param workLogOrgCategory 实施配置里面组织机构是班组的编码
     * @return
     */
    List<OverhaulStatisticsDTOS> getUserOrgCategory(@Param("id") String id, @Param("workLogOrgCategory") String workLogOrgCategory);

    /**
     * 查询检修站点
     * @param id
     * @return
     */
    List<String> getRepairTaskStation(@Param("id") String id);


    /**
     * 大屏统计
     * @return
     */
    List<SystemInformationDTO> getSystemInformation();

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
     * @param status
     * @return
     */
    List<String> getFaultCodeList(@Param("stationCode") List<String> stationCode,@Param("status") Long status);

    /**
     * 故障已完成数量
     * @param faultCode
     * @return
     */
    Long getFaultQuantity(@Param("faultCode") List<String> faultCode);
    /**
     * 检修池查找
     * @param startDate
     * @param endDate
     * @return
     */
    @DataPermission({
            @DataColumn(key = "deptName",value = "t2.org_code"),
            @DataColumn(key = "stationName",value = "t3.station_code"),
            @DataColumn(key = "lineName",value = "t3.line_code"),
            @DataColumn(key = "majorName",value = "t3.major_code"),
            @DataColumn(key = "systemName",value = "t3.subsystem_code")
    })
    List<RepairTaskNum> selectRepairPoolListSpecial(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
    /**
     * 查询管理负责人检修班组的信息
     * @param pageList
     * @param condition
     * @return
     */
    Page<OverhaulStatisticsDTOS> getAllTaskList(@Param("pageList") Page<OverhaulStatisticsDTOS> pageList, @Param("condition") OverhaulStatisticsDTOS condition);
    /**
     * 查询班组的人员所负责的检修的信息
     * @param condition
     * @return
     */
    List<OverhaulStatisticsDTO> countUserList(@Param("condition")OverhaulStatisticsDTOS condition);
}
