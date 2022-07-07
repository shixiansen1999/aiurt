package com.aiurt.boot.task.mapper;

import com.aiurt.boot.task.dto.PatrolTaskDTO;
import com.aiurt.boot.task.dto.PatrolTaskUserContentDTO;
import com.aiurt.boot.task.dto.SubsystemDTO;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.param.PatrolTaskParam;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: patrol_task
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface PatrolTaskMapper extends BaseMapper<PatrolTask> {
    /**
     * app-巡检任务池列表
     *
     * @param pageList
     * @param patrolTaskDTO
     * @return
     */
    List<PatrolTaskDTO> getPatrolTaskPoolList(@Param("pageList") Page<PatrolTaskDTO> pageList, @Param("patrolTaskDTO") PatrolTaskDTO patrolTaskDTO);

    /**
     * app-巡检任务列表
     *
     * @param patrolTaskDTO
     * @return author hlq
     */
    List<PatrolTaskDTO> getPatrolTaskList(@Param("pageList") Page<PatrolTaskDTO> pageList, @Param("patrolTaskDTO") PatrolTaskDTO patrolTaskDTO);

    /**
     * app-获取组织机构名称
     *
     * @param taskCode
     * @return author hlq
     */
    List<String> getOrganizationName(@Param("taskCode") String taskCode);

    /**
     * app-获取站点名称
     *
     * @param code
     * @return author hlq
     */
    List<String> getStationName(String code);

    /**
     * app-获取巡检人名称
     *
     * @param code
     * @return author hlq
     */
    List<String> getPatrolUserName(String code);

    /**
     * 查询巡检任务列表
     *
     * @param page
     * @param patrolTaskParam
     * @return
     */
    IPage<PatrolTaskParam> getTaskList(Page<PatrolTaskParam> page, @Param("patrolTask") PatrolTaskParam patrolTaskParam);

    /**
     * app-获取退回人的名称
     *
     * @param patrolReturnUserId
     * @return
     */
    String getUserName(String patrolReturnUserId);

    /**
     * app-获取部门code
     *
     * @param taskCode
     * @return
     */
    List<String> getOrgCode(String taskCode);

    /**
     * app-获取指派人员信息
     *
     * @return
     */
    List<PatrolTaskUserContentDTO> getUser(@Param("code") String code);


    /**
     * app-获取组织机构名称
     *
     * @param code
     * @return
     */
    String getOrgName(String code);

    /**
     * PC巡检任务池详情-基本信息
     *
     * @param patrolTaskParam
     * @return
     */
    PatrolTaskParam selectBasicInfo(@Param("patrolTaskParam") PatrolTaskParam patrolTaskParam);

    /**
     * PC-手工下方列表
     *
     * @param pageList
     * @param patrolTaskDTO
     * @return
     */
    List<PatrolTaskDTO> getPatrolTaskManualList(@Param("pageList") Page<PatrolTaskDTO> pageList, @Param("patrolTaskDTO") PatrolTaskDTO patrolTaskDTO);

    /**
     * 根据专业编码获取专业名称
     *
     * @param code
     * @return
     */
    String getMajorNameByMajorCode(String code);

    /**
     * 根据子系统编码获取子系统名称
     *
     * @param code
     * @return
     */
    String getSubsystemNameBySystemCode(String code);

    /**
     * 根据用户ID获取用户名
     *
     * @param userId
     * @return
     */
    String getUsername(String userId);

    /**
     * 获取专业下的子系统信息
     *
     * @param majorCode
     * @param subsystem
     * @return
     */
    List<SubsystemDTO> getMajorSubsystemGanged(@Param("majorCode") String majorCode, @Param("subsystemList") List<SubsystemDTO> subsystem);
}
