package com.aiurt.boot.task.mapper;

import cn.hutool.core.date.DateTime;
import com.aiurt.boot.manager.dto.EquipmentDTO;
import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.manager.dto.SubsystemDTO;
import com.aiurt.boot.plan.dto.RepairPoolDetailsDTO;
import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.task.dto.CheckListDTO;
import com.aiurt.boot.task.dto.RepairTaskDTO;
import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.boot.task.entity.RepairTaskEnclosure;
import com.aiurt.boot.task.entity.RepairTaskResult;
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
}
