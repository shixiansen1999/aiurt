package com.aiurt.modules.personnelgroupstatistics.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.aiurt.boot.api.OverhaulApi;
import com.aiurt.boot.api.PatrolApi;
import com.aiurt.boot.dto.UserTeamParameter;
import com.aiurt.boot.dto.UserTeamPatrolDTO;
import com.aiurt.boot.index.dto.TeamWorkAreaDTO;
import com.aiurt.boot.task.dto.PersonnelTeamDTO;
import com.aiurt.modules.common.api.DailyFaultApi;
import com.aiurt.modules.fault.dto.FaultReportDTO;
import com.aiurt.modules.personnelgroupstatistics.dto.FaultRepairRecordDTO;
import com.aiurt.modules.personnelgroupstatistics.dto.TrainTaskDTO;
import com.aiurt.modules.personnelgroupstatistics.mapper.PersonnelGroupStatisticsMapper;
import com.aiurt.modules.personnelgroupstatistics.model.GroupModel;
import com.aiurt.modules.personnelgroupstatistics.model.PersonnelModel;
import com.aiurt.modules.personnelgroupstatistics.model.TeamPortraitModel;
import com.aiurt.modules.personnelgroupstatistics.model.TeamUserModel;
import com.aiurt.modules.personnelgroupstatistics.service.PersonnelGroupStatisticsService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
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
    public List<GroupModel> queryGroupPageList(List<String> departIds, String startTime, String endTime, Page<GroupModel> page) {
        //获取当前登录用户管辖的班组
        List<String> ids = getDepartIds(departIds);
        if (CollUtil.isNotEmpty(ids)) {
            List<GroupModel> personnelGroupModels = personnelGroupStatisticsMapper.queryGroupPageList(ids, page);
            //获取所有班组巡检参数数据

            //获取所有班组维修参数数据
            Map<String, FaultReportDTO> faultOrgReport = dailyFaultApi.getFaultOrgReport(departIds, startTime, endTime);
            ///获取所有班组检修参数数据
            Map<String, PersonnelTeamDTO> teamInformation = overhaulApi.teamInformation(DateUtil.parse(startTime, "yyyy-MM-dd HH:mm:ss"), DateUtil.parse(endTime, "yyyy-MM-dd HH:mm:ss"), departIds);

            for (GroupModel model : personnelGroupModels) {
                String teamId = model.getTeamId();
                //获取每一个班组巡检参数数据

                //获取每一个班组维修参数数据
                FaultReportDTO faultReportDTO = faultOrgReport.get(teamId);
                model.setFaultTotalTime(Convert.toStr(faultReportDTO.getFailureTime()));
                model.setAssortNum(Convert.toStr(faultReportDTO.getConstructorsNum()));
                model.setAssortTime(Convert.toStr(faultReportDTO.getConstructionHours()));
                //获取每一个班组检修参数数据
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
    public List<PersonnelModel> queryUserPageList(List<String> departIds, String startTime, String endTime, Page<PersonnelModel> page) {
        //获取当前登录用户管辖的班组
        List<String> ids = getDepartIds(departIds);
        if (CollUtil.isNotEmpty(ids)) {
            List<PersonnelModel> personnelModels = personnelGroupStatisticsMapper.queryUserPageList(ids, page);

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

            for (PersonnelModel model : personnelModels) {
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

            return personnelModels;
        }else {
            return CollUtil.newArrayList();
        }

    }

    @Override
    public TeamPortraitModel queryGroupById(String departId) {
        DateTime date = DateUtil.date();
        //获取上一个月第一天和最后一天时间
        date = DateUtil.offsetMonth(date, -1);
        DateTime start = DateUtil.beginOfMonth(date);
        DateTime end = DateUtil.endOfMonth(date);
        //获取一年前的开始时间
        DateTime lastYear = DateUtil.offsetMonth(start, -12);

        //班组信息
        TeamPortraitModel depart = personnelGroupStatisticsMapper.getDepart(departId);

        List<String> departIds = new ArrayList<>();
        departIds.add(departId);
        //获取当前班组巡检参数数据

        //获取当前班组维修参数数据
        Map<String, FaultReportDTO> faultOrgReport = dailyFaultApi.getFaultOrgReport(departIds, DateUtil.formatDateTime(lastYear), DateUtil.formatDateTime(end));
        ///获取当前班组检修参数数据
        Map<String, PersonnelTeamDTO> teamInformation = overhaulApi.teamInformation(lastYear, end,departIds);

        depart.setFaultTotalTime(Convert.toStr(faultOrgReport.get(departId).getFailureTime()));
        depart.setInspecitonTotalTime(Convert.toStr(teamInformation.get(departId).getOverhaulWorkingHours()));
        //depart.setPatrolTotalTime(Convert.toStr(userParameter.get(departId).getWorkHours()));
        //depart.setAverageMonthlyResidual(Convert.toStr(userParameter.get(departId).getAvgMissPatrolNumber()));
        depart.setAverageFaultTime(Convert.toStr(faultOrgReport.get(departId).getRepairTime()));

        //班组关联工区信息
        List<TeamPortraitModel> workArea = personnelGroupStatisticsMapper.getWorkArea(departId);
        List<String> position = workArea.stream().map(TeamPortraitModel::getPosition).collect(Collectors.toList());
        List<String> siteName = workArea.stream().map(TeamPortraitModel::getSiteName).collect(Collectors.toList());

        StringBuilder jurisdiction = new StringBuilder();
        for (TeamPortraitModel portraitDTO : workArea) {
            jurisdiction.append(portraitDTO.getSiteName()).append(":");
            //获取工区管辖范围
            List<TeamWorkAreaDTO> stationDetails = personnelGroupStatisticsMapper.getStationDetails(portraitDTO.getWorkAreaCode());
            if (CollUtil.isNotEmpty(stationDetails)) {
                List<String> line = stationDetails.stream().map(TeamWorkAreaDTO::getLineCode).collect(Collectors.toList());
                if (CollUtil.isNotEmpty(line)) {
                    for (String s : line) {
                        List<TeamWorkAreaDTO> collect = stationDetails.stream().filter(t -> t.getLineCode().equals(s)).collect(Collectors.toList());
                        jurisdiction.append(collect.get(0).getLineName())
                                .append(collect.get(0).getStationName())
                                .append(collect.get(collect.size() - 1).getStationName())
                                .append(collect.size()).append("站，");
                    }
                    if (jurisdiction.length() > 0) {
                        // 截取字符，去调最后一个，
                        jurisdiction.deleteCharAt(jurisdiction.length() - 1);
                    }
                }
            }
            jurisdiction.append("共").append(stationDetails.size()).append("站；");
        }
        if (jurisdiction.length() > 0) {
            // 截取字符,去掉最后一个；
            jurisdiction.deleteCharAt(jurisdiction.length() - 1);
        }
        depart.setPositionName(CollUtil.join(position, ","));
        depart.setSiteName(CollUtil.join(siteName, ","));
        depart.setJurisdiction(jurisdiction.toString());

        //获取班组维修响应时长
        List<LoginUser> users= iSysBaseAPI.getUserPersonnel(departId);
        List<String> list = users.stream().map(LoginUser::getId).collect(Collectors.toList());
        List<FaultRepairRecordDTO> repairDuration = personnelGroupStatisticsMapper.getRepairDuration(list, lastYear, end);
        long l = 0;
        for (FaultRepairRecordDTO repairRecordDTO : repairDuration) {
            // 响应时长： 接收到任务，开始维修时长
            Date receviceTime = repairRecordDTO.getReceviceTime();
            Date startTime = repairRecordDTO.getStartTime();
            Date time = repairRecordDTO.getEndTime();
            if (Objects.nonNull(startTime) && Objects.nonNull(receviceTime)) {
                long between = DateUtil.between(receviceTime, startTime, DateUnit.MINUTE);
                between = between == 0 ? 1 : between;
                l = l + between;
            }
            if (Objects.nonNull(startTime) && Objects.nonNull(time)) {
                long between = DateUtil.between(time, startTime, DateUnit.MINUTE);
                between = between == 0 ? 1 : between;
                l = l + between;
            }
        }
        BigDecimal bigDecimal = new BigDecimal(l);
        BigDecimal bigDecimal1 = new BigDecimal(12);
        String s = bigDecimal.divide(bigDecimal1, 0).toString();
        depart.setAverageTime(s);
        return depart;
    }

    @Override
    public TeamUserModel queryUserById(String userId) {
        DateTime date = DateUtil.date();
        //获取上一个月第一天和最后一天时间
        date = DateUtil.offsetMonth(date, -1);
        DateTime start = DateUtil.beginOfMonth(date);
        DateTime end = DateUtil.endOfMonth(date);
        //获取一年前的开始时间
        DateTime lastYear = DateUtil.offsetMonth(start, -12);

        //获取人员巡检参数数据
        UserTeamParameter userTeamParameter = new UserTeamParameter();
        userTeamParameter.setStartDate(DateUtil.formatDateTime(lastYear));
        userTeamParameter.setEndDate(DateUtil.formatDateTime(end));
        userTeamParameter.setUserId(userId);
        Map<String, UserTeamPatrolDTO> userParameter = patrolApi.getUserParameter(userTeamParameter);
        //获取人员维修参数数据
        Map<String, FaultReportDTO> faultUserReport = dailyFaultApi.getFaultUserReport(null, DateUtil.formatDateTime(lastYear), DateUtil.formatDateTime(end), userId);
        //获取人员检修参数数据
        Map<String, PersonnelTeamDTO> personnelInformation = overhaulApi.personnelInformation(lastYear, end, null, userId);

        TeamUserModel user = personnelGroupStatisticsMapper.getUser(userId);

        user.setFaultTotalTime(Convert.toStr(faultUserReport.get(userId).getFailureTime()));
        user.setInspecitonTotalTime(Convert.toStr(personnelInformation.get(userId).getOverhaulWorkingHours()));
        user.setPatrolTotalTime(Convert.toStr(userParameter.get(userId).getWorkHours()));
        user.setAverageMonthlyResidual(Convert.toStr(userParameter.get(userId).getAvgMissPatrolNumber()));
        user.setAverageFaultTime(Convert.toStr(faultUserReport.get(userId).getRepairTime()));

        List<String> userList = new ArrayList<>();
        userList.add(userId);
        //获取人员维修响应时长
        List<FaultRepairRecordDTO> repairDuration = personnelGroupStatisticsMapper.getRepairDuration(userList, lastYear, end);
        long l = 0;
        for (FaultRepairRecordDTO repairRecordDTO : repairDuration) {
            // 响应时长： 接收到任务，开始维修时长
            Date receviceTime = repairRecordDTO.getReceviceTime();
            Date startTime = repairRecordDTO.getStartTime();
            Date time = repairRecordDTO.getEndTime();
            if (Objects.nonNull(startTime) && Objects.nonNull(receviceTime)) {
                long between = DateUtil.between(receviceTime, startTime, DateUnit.MINUTE);
                between = between == 0 ? 1 : between;
                l = l + between;
            }
            if (Objects.nonNull(startTime) && Objects.nonNull(time)) {
                long between = DateUtil.between(time, startTime, DateUnit.MINUTE);
                between = between == 0 ? 1 : between;
                l = l + between;
            }
        }
        BigDecimal bigDecimal = new BigDecimal(l);
        BigDecimal bigDecimal1 = new BigDecimal(12);
        String s = bigDecimal.divide(bigDecimal1, 0).toString();
        user.setAverageTime(s);
        return user;
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
