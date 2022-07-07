package com.aiurt.boot.task.service;

import com.aiurt.boot.task.dto.*;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.param.PatrolTaskParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: patrol_task
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface IPatrolTaskService extends IService<PatrolTask> {

    /**
     * app-巡检任务池
     *
     * @param pageList
     * @param patrolTaskDTO
     * @return
     */
    Page<PatrolTaskDTO> getPatrolTaskPoolList(Page<PatrolTaskDTO> pageList, PatrolTaskDTO patrolTaskDTO);

    /**
     * app-巡检任务列表
     *
     * @param pageList
     * @param patrolTaskDTO
     * @return author hlq
     */
    Page<PatrolTaskDTO> getPatrolTaskList(Page<PatrolTaskDTO> pageList, PatrolTaskDTO patrolTaskDTO);

    /**
     * app巡检任务领取、确认、执行
     *
     * @param patrolTaskDTO
     */
    void getPatrolTaskReceive(PatrolTaskDTO patrolTaskDTO);

    /**
     * app-app巡检任务-退回
     *
     * @param patrolTaskDTO
     */
    void getPatrolTaskReturn(PatrolTaskDTO patrolTaskDTO);

    /**
     * app巡检任务-指派人员查询
     *
     * @param orgCoed
     */
    List<PatrolTaskUserDTO> getPatrolTaskAppointSelect(PatrolOrgDTO orgCoed);

    /**
     * PC巡检任务池列表
     *
     * @param page
     * @return
     */
    IPage<PatrolTaskParam> getTaskList(Page<PatrolTaskParam> page, PatrolTaskParam patrolTaskParam);

    /**
     * app巡检任务-指派人员
     *
     * @param patrolTaskUserDTO
     * @return
     */
    void getPatrolTaskAppoint(List<PatrolTaskUserDTO> patrolTaskUserDTO);

    /**
     * PC巡检任务池详情-基本信息
     *
     * @return
     */
    PatrolTaskParam selectBasicInfo(PatrolTaskParam patrolTaskParam);

    /**
     * PC巡检任务池-任务指派
     *
     * @param ppatrolAppointInfoDTO
     * @return
     */
    int taskAppoint(PatrolAppointInfoDTO ppatrolAppointInfoDTO);

    /**
     * PC巡检任务池-任务作废
     *
     * @param list
     * @return
     */
    int taskDiscard(List<PatrolTask> list);

    /**
     * app巡检任务提交-统计工单数量
     *
     * @param patrolTaskSubmitDTO
     * @return
     */
    PatrolTaskSubmitDTO getSubmitTaskCount(PatrolTaskSubmitDTO patrolTaskSubmitDTO);

    /**
     * pc手工下放任务列表
     *
     * @param pageList
     * @param patrolTaskDTO
     * @return
     */
    Page<PatrolTaskDTO> getPatrolTaskManualList(Page<PatrolTaskDTO> pageList, PatrolTaskDTO patrolTaskDTO);

    /**
     * 根据任务记录ID获取专业子系统的联动信息
     *
     * @param id
     */
    List<MajorDTO> getMajorSubsystemGanged(String id);

    /**
     * PC巡检任务列表-任务审核
     *
     * @param code
     */
    int taskAudit(String code, Integer auditStatus, String auditReason, String remark);

    /**
     * app-提交任务
     *
     * @param patrolTaskDTO
     */
    void getPatrolTaskSubmit(PatrolTaskDTO patrolTaskDTO);

    /**
     * pc手工下放任务-新增
     *
     * @param patrolTaskManualDTO
     */
    void getPatrolTaskManualListAdd(PatrolTaskManualDTO patrolTaskManualDTO);

    /**
     * 漏检任务处置
     *
     * @param task
     * @param omitExplain
     * @return
     */
    int taskDispose(PatrolTask task, String omitExplain);

    /**
     * pc手工下放任务-编辑-详情
     *
     * @param pageList
     * @param id
     * @return
     */
    Page<PatrolTaskStandardDTO> getPatrolTaskManualDetail(Page<PatrolTaskStandardDTO> pageList, String id);

    /**
     * 根据任务编号列表获取组织机构下的用户信息
     *
     * @param list
     * @return
     */
    List<PatrolUserInfoDTO> getAssignee(List<String> list);

    /**
     * 巡检漏检任务处理-重新生成任务
     *
     * @param patrolRebuildDTO
     */
    String rebuildTask(PatrolRebuildDTO patrolRebuildDTO);

    /**
     * pc手工下放任务-编辑
     *
     * @param patrolTaskManualDTO
     */
    void getPatrolTaskManualEdit(PatrolTaskManualDTO patrolTaskManualDTO);
}
