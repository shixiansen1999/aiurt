package com.aiurt.modules.personnelgroupstatistics.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import com.aiurt.boot.api.OverhaulApi;
import com.aiurt.boot.api.PatrolApi;
import com.aiurt.boot.dto.UserTeamParameter;
import com.aiurt.boot.dto.UserTeamPatrolDTO;
import com.aiurt.boot.task.dto.PersonnelTeamDTO;
import com.aiurt.modules.common.api.DailyFaultApi;
import com.aiurt.modules.fault.dto.FaultReportDTO;
import com.aiurt.modules.personnelgroupstatistics.mapper.PersonnelGroupStatisticsMapper;
import com.aiurt.modules.personnelgroupstatistics.model.PersonnelGroupModel;
import com.aiurt.modules.personnelgroupstatistics.model.TeamPortraitModel;
import com.aiurt.modules.personnelgroupstatistics.model.TeamUserModel;
import com.aiurt.modules.personnelgroupstatistics.model.TrainTaskDTO;
import com.aiurt.modules.personnelgroupstatistics.service.PersonnelGroupStatisticsService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lkj
 */
@Service
public class PersonnelGroupStatisticsServiceImpl implements PersonnelGroupStatisticsService {

    @Autowired
    private PersonnelGroupStatisticsMapper personnelGroupStatisticsMapper;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Autowired
    private PatrolApi patrolApi;
    @Autowired
    private DailyFaultApi dailyFaultApi;
    @Autowired
    private OverhaulApi overhaulApi;

    @Override
    public List<PersonnelGroupModel> queryGroupPageList(List<String> departIds, String startTime, String endTime, Page<PersonnelGroupModel> page) {
        //获取当前登录用户管辖的班组
        List<String> ids = getDepartIds(departIds);
        if (CollUtil.isNotEmpty(ids)) {
            List<PersonnelGroupModel> personnelGroupModels = personnelGroupStatisticsMapper.queryGroupPageList(ids, page);
            //获取所有班组巡检参数数据

            //获取所有班组维修参数数据
            Map<String, FaultReportDTO> faultOrgReport = dailyFaultApi.getFaultOrgReport(departIds, startTime, endTime);
            ///获取所有班组检修参数数据
            Map<String, PersonnelTeamDTO> teamInformation = overhaulApi.teamInformation(DateUtil.parse(startTime, "yyyy-MM-dd HH:mm:ss"), DateUtil.parse(endTime, "yyyy-MM-dd HH:mm:ss"), departIds);

            for (PersonnelGroupModel model : personnelGroupModels) {
                String teamId = model.getTeamId();
                //获取每一个人员维修参数数据
                FaultReportDTO faultReportDTO = faultOrgReport.get(teamId);
                model.setFaultTotalTime(Convert.toStr(faultReportDTO.getFailureTime()));
                model.setAssortNum(Convert.toStr(faultReportDTO.getConstructorsNum()));
                model.setAssortTime(Convert.toStr(faultReportDTO.getConstructionHours()));
                //获取每一个人员检修参数数据
                PersonnelTeamDTO personnelTeamDTO = teamInformation.get(teamId);
                model.setInspecitonTotalTime(Convert.toStr(personnelTeamDTO.getOverhaulWorkingHours()));
                model.setInspecitonScheduledTasks(Convert.toStr(personnelTeamDTO.getPlanTaskNumber()));
                model.setInspecitonCompletedTasks(Convert.toStr(personnelTeamDTO.getCompleteTaskNumber()));
                model.setInspecitonPlanCompletion(Convert.toStr(personnelTeamDTO.getPlanCompletionRate()+"%"));
                model.setInspecitonMissingChecks("-");

                //培训完成次数
                Integer integer = personnelGroupStatisticsMapper.groupTrainFinishedNum(model.getTeamId(), startTime, endTime);
                model.setTrainFinish(Convert.toStr(integer));
            }

            return personnelGroupModels;
        }else {
            return CollUtil.newArrayList();
        }
    }

    @Override
    public List<PersonnelGroupModel> queryUserPageList(List<String> departIds, String startTime, String endTime, Page<PersonnelGroupModel> page) {
        //获取当前登录用户管辖的班组
        List<String> ids = getDepartIds(departIds);
        if (CollUtil.isNotEmpty(ids)) {
            List<PersonnelGroupModel> personnelGroupModels = personnelGroupStatisticsMapper.queryUserPageList(ids, page);

            //获取所有人员巡检参数数据
            UserTeamParameter userTeamParameter = new UserTeamParameter();
            userTeamParameter.setStartDate(startTime);
            userTeamParameter.setEndDate(endTime);
            userTeamParameter.setOrgIdList(ids);
            Map<String, UserTeamPatrolDTO> userParameter = patrolApi.getUserParameter(userTeamParameter);
            //获取所有人员维修参数数据
            Map<String, FaultReportDTO> faultUserReport = dailyFaultApi.getFaultUserReport(departIds, startTime, endTime, null);
            //获取所有人员检修参数数据
            Map<String, PersonnelTeamDTO> personnelInformation = overhaulApi.personnelInformation(DateUtil.parse(startTime, "yyyy-MM-dd HH:mm:ss"), DateUtil.parse(endTime, "yyyy-MM-dd HH:mm:ss"), departIds, null);

            for (PersonnelGroupModel model : personnelGroupModels) {
                String userId = model.getUserId();
                //获取每一个人员巡检参数数据
                UserTeamPatrolDTO userTeamPatrolDTO = userParameter.get(userId);
                model.setPatrolTotalTime(Convert.toStr(userTeamPatrolDTO.getWorkHours()));
                model.setPatrolScheduledTasks(Convert.toStr(userTeamPatrolDTO.getPlanTaskNumber()));
                model.setPatrolCompletedTasks(Convert.toStr(userTeamPatrolDTO.getActualFinishTaskNumber()));
                model.setPatrolPlanCompletion(Convert.toStr(userTeamPatrolDTO.getPlanFinishRate())+"%");
                model.setPatrolMissingChecks(Convert.toStr(userTeamPatrolDTO.getMissPatrolNumber()));
                //获取每一个人员维修参数数据
                FaultReportDTO faultReportDTO = faultUserReport.get(userId);
                model.setFaultTotalTime(Convert.toStr(faultReportDTO.getFailureTime()));
                model.setAssortNum(Convert.toStr(faultReportDTO.getConstructorsNum()));
                model.setAssortTime(Convert.toStr(faultReportDTO.getConstructionHours()));
                //获取每一个人员检修参数数据
                PersonnelTeamDTO personnelTeamDTO = personnelInformation.get(userId);
                model.setInspecitonTotalTime(Convert.toStr(personnelTeamDTO.getOverhaulWorkingHours()));
                model.setInspecitonScheduledTasks(Convert.toStr(personnelTeamDTO.getPlanTaskNumber()));
                model.setInspecitonCompletedTasks(Convert.toStr(personnelTeamDTO.getCompleteTaskNumber()));
                model.setInspecitonPlanCompletion(Convert.toStr(personnelTeamDTO.getPlanCompletionRate()+"%"));
                model.setInspecitonMissingChecks("-");

                //培训完成次数
                List<TrainTaskDTO> trainTaskDTOS = personnelGroupStatisticsMapper.userTrainFinishedNum(model.getUserId(), startTime, endTime);
                int size = trainTaskDTOS.size();
                for (TrainTaskDTO trainTaskDTO : trainTaskDTOS) {
                    String examStatus = trainTaskDTO.getExamStatus();
                    //当有考试的时候需要判断用户是否完成考试，如果没有完成考试则不算完成培训任务
                    if (examStatus.equals("1")) {
                        Integer exam = personnelGroupStatisticsMapper.isExam(trainTaskDTO.getTaskId(), model.getUserId());
                        if (exam == 0) {
                            size = size - 1;
                        }
                    }
                }
                model.setTrainFinish(Convert.toStr(size));
            }

            return personnelGroupModels;
        }else {
            return CollUtil.newArrayList();
        }

    }

    @Override
    public TeamPortraitModel queryGroupById(String departId) {

        return null;
    }

    @Override
    public TeamUserModel queryUserById(String userId) {

        return null;
    }

    private List<String> getDepartIds(List<String> departIds) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> userSysDepart = iSysBaseAPI.getUserSysDepart(sysUser.getId());
        List<String> ids = new ArrayList<>();
        if (CollUtil.isNotEmpty(departIds)) {
            ids.addAll(departIds);
        }else {
            ids.addAll(userSysDepart.stream().map(SysDepartModel::getId).collect(Collectors.toList()) );
        }
        return ids;
    }
}
