package com.aiurt.boot.task.mapper;

import cn.hutool.core.date.DateTime;
import com.aiurt.boot.manager.dto.EquipmentDTO;
import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.manager.dto.SubsystemDTO;
import com.aiurt.boot.plan.dto.RepairPoolDetailsDTO;
import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.task.dto.*;
import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.boot.task.entity.RepairTaskEnclosure;
import com.aiurt.boot.task.entity.RepairTaskResult;
import com.aiurt.modules.fault.dto.FaultFrequencyDTO;
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

    /**
     * 站点下拉查询
     * @param taskId
     * @return
     */
    List<RepairTaskStationDTO> repairTaskStationList(@Param("taskId") String taskId);

    /**
     * 站点下拉查询
     * @param taskId
     * @return
     */
    List<RepairTaskDTO> selectTaskList(@Param("taskId") String taskId);

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
     * @param page
     * @param startDate
     * @param stationCode
     * @return
     */
    List<RepairPoolDetailsDTO> selectRepairPoolList(@Param("page") Page<RepairPoolDetailsDTO> page, @Param("startDate") Date startDate, @Param("stationCode") String stationCode, @Param("taskCode") Set<String> taskCode);

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
    List<RepairPoolDetailsDTO> inspectionNumByDay(@Param("dateTime") DateTime dateTime);

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


    List<OverhaulStatisticsDTOS> readTeamList(@Param("pageList") Page<OverhaulStatisticsDTOS> pageList,@Param("condition") OverhaulStatisticsDTOS condition);

    List<OverhaulStatisticsDTOS> readTeamLists(@Param("condition") OverhaulStatisticsDTOS condition);

    List<OverhaulStatisticsDTO> readNameList(@Param("condition") OverhaulStatisticsDTOS condition);

    List<OverhaulStatisticsDTO> readNameLists(@Param("condition") OverhaulStatisticsDTO condition);

    List<Integer> getStatus(String id);

    String getOrgCode(String id);

    String getRealName(String id);

    List<OverhaulStatisticsDTO> realNameList(@Param("condition") OverhaulStatisticsDTOS condition);

    List<OverhaulStatisticsDTOS> selectDepart(@Param("id") String id);

    List<OverhaulStatisticsDTOS> getUserOrgCategory(@Param("id") String id);
}
