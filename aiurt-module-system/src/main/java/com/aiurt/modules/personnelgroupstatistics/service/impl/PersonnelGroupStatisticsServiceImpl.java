package com.aiurt.modules.personnelgroupstatistics.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
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
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
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
    public Page<GroupModel> queryGroupPageList(List<String> departIds, String startTime, String endTime, Page<GroupModel> page) {
        //获取当前登录用户管辖的班组
        List<String> ids = getDepartIds(departIds);
        if (CollUtil.isNotEmpty(ids)) {
            List<GroupModel> personnelGroupModels = personnelGroupStatisticsMapper.queryGroupPageList(ids, page);
            //获取所有班组巡检参数数据
            UserTeamParameter userTeamParameter = new UserTeamParameter();
            userTeamParameter.setStartDate(startTime);
            userTeamParameter.setEndDate(endTime);
            userTeamParameter.setOrgIdList(ids);
            Map<String, UserTeamPatrolDTO> teamParameter = patrolApi.getUserTeamParameter(userTeamParameter);

            //获取所有班组维修参数数据
            Map<String, FaultReportDTO> faultOrgReport = dailyFaultApi.getFaultOrgReport(departIds, startTime, endTime);
            ///获取所有班组检修参数数据
            Map<String, PersonnelTeamDTO> teamInformation = overhaulApi.teamInformation(DateUtil.parse(startTime, "yyyy-MM-dd"), DateUtil.parse(endTime, "yyyy-MM-dd"), departIds);

            for (GroupModel model : personnelGroupModels) {
                String teamId = model.getTeamId();
                //获取每一个班组巡检参数数据
                UserTeamPatrolDTO userTeamPatrolDTO = teamParameter.get(teamId);
                if (ObjectUtil.isNotEmpty(userTeamPatrolDTO)) {
                    model.setPatrolTotalTime(Convert.toStr(userTeamPatrolDTO.getWorkHours()));
                    model.setPatrolScheduledTasks(Convert.toStr(userTeamPatrolDTO.getPlanTaskNumber()));
                    model.setPatrolCompletedTasks(Convert.toStr(userTeamPatrolDTO.getActualFinishTaskNumber()));
                    model.setPatrolPlanCompletion(Convert.toStr(userTeamPatrolDTO.getPlanFinishRate())+"%");
                    model.setPatrolMissingChecks(Convert.toStr(userTeamPatrolDTO.getMissPatrolNumber()));
                }else {
                    model.setPatrolTotalTime("0");
                    model.setPatrolScheduledTasks("0");
                    model.setPatrolCompletedTasks("0");
                    model.setPatrolPlanCompletion("0%");
                    model.setPatrolMissingChecks("0");
                }

                //获取每一个班组维修参数数据
                FaultReportDTO faultReportDTO = faultOrgReport.get(teamId);
                if (ObjectUtil.isNotEmpty(faultReportDTO)) {
                    model.setFaultTotalTime(Convert.toStr(faultReportDTO.getFailureTime()));
                    model.setAssortNum(Convert.toStr(faultReportDTO.getConstructorsNum()));
                    model.setAssortTime(Convert.toStr(faultReportDTO.getConstructionHours()));
                }else {
                    model.setFaultTotalTime("0");
                    model.setAssortNum("0");
                    model.setAssortTime("0");
                }

                //获取每一个班组检修参数数据
                PersonnelTeamDTO personnelTeamDTO = teamInformation.get(teamId);
                if (ObjectUtil.isNotEmpty(personnelTeamDTO)) {
                    model.setInspecitonTotalTime(Convert.toStr(personnelTeamDTO.getOverhaulWorkingHours()));
                    model.setInspecitonScheduledTasks(Convert.toStr(personnelTeamDTO.getPlanTaskNumber()));
                    model.setInspecitonCompletedTasks(Convert.toStr(personnelTeamDTO.getCompleteTaskNumber()));
                    model.setInspecitonPlanCompletion(Convert.toStr(personnelTeamDTO.getPlanCompletionRate())+"%");
                    model.setInspecitonMissingChecks("-");
                }else {
                    model.setInspecitonTotalTime("0");
                    model.setInspecitonScheduledTasks("0");
                    model.setInspecitonCompletedTasks("0");
                    model.setInspecitonPlanCompletion("0%");
                    model.setInspecitonMissingChecks("-");
                }

                //培训完成次数
                Integer integer = personnelGroupStatisticsMapper.groupTrainFinishedNum(model.getTeamId(), startTime, endTime);
                model.setTrainFinish(Convert.toStr(integer));

                model.setEmergencyResponseNum("-");
                model.setEmergencyHandlingHours("-");
            }

            return page.setRecords(personnelGroupModels);
        }else {
            return page.setRecords(CollUtil.newArrayList());
        }
    }

    @Override
    public Page<PersonnelModel> queryUserPageList(List<String> departIds, String startTime, String endTime, Page<PersonnelModel> page) {
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
            Map<String, PersonnelTeamDTO> personnelInformation = overhaulApi.personnelInformation(DateUtil.parse(startTime, "yyyy-MM-dd"), DateUtil.parse(endTime, "yyyy-MM-dd"), departIds, null);

            for (PersonnelModel model : personnelModels) {
                String userId = model.getUserId();
                //获取每一个人员巡检参数数据
                UserTeamPatrolDTO userTeamPatrolDTO = userParameter.get(userId);
                if (ObjectUtil.isNotEmpty(userTeamPatrolDTO)) {
                    model.setPatrolTotalTime(Convert.toStr(userTeamPatrolDTO.getWorkHours()));
                    model.setPatrolScheduledTasks(Convert.toStr(userTeamPatrolDTO.getPlanTaskNumber()));
                    model.setPatrolCompletedTasks(Convert.toStr(userTeamPatrolDTO.getActualFinishTaskNumber()));
                    model.setPatrolPlanCompletion(Convert.toStr(userTeamPatrolDTO.getPlanFinishRate())+"%");
                    model.setPatrolMissingChecks(Convert.toStr(userTeamPatrolDTO.getMissPatrolNumber()));
                } else {
                    model.setPatrolTotalTime("0");
                    model.setPatrolScheduledTasks("0");
                    model.setPatrolCompletedTasks("0");
                    model.setPatrolPlanCompletion("0%");
                    model.setPatrolMissingChecks("0");
                }
                //获取每一个人员维修参数数据
                FaultReportDTO faultReportDTO = faultUserReport.get(userId);
                if (ObjectUtil.isNotEmpty(faultReportDTO)) {
                    model.setFaultTotalTime(Convert.toStr(faultReportDTO.getFailureTime()));
                    model.setAssortNum(Convert.toStr(faultReportDTO.getConstructorsNum()));
                    model.setAssortTime(Convert.toStr(faultReportDTO.getConstructionHours()));
                } else {
                    model.setFaultTotalTime("0");
                    model.setAssortNum("0");
                    model.setAssortTime("0");
                }

                //获取每一个人员检修参数数据
                PersonnelTeamDTO personnelTeamDTO = personnelInformation.get(userId);
                if (ObjectUtil.isNotEmpty(personnelTeamDTO)) {
                    model.setInspecitonTotalTime(Convert.toStr(personnelTeamDTO.getOverhaulWorkingHours()));
                    model.setInspecitonScheduledTasks(Convert.toStr(personnelTeamDTO.getPlanTaskNumber()));
                    model.setInspecitonCompletedTasks(Convert.toStr(personnelTeamDTO.getCompleteTaskNumber()));
                    model.setInspecitonPlanCompletion(Convert.toStr(personnelTeamDTO.getPlanCompletionRate())+"%");
                    model.setInspecitonMissingChecks("-");
                } else {
                    model.setInspecitonTotalTime("0");
                    model.setInspecitonScheduledTasks("0");
                    model.setInspecitonCompletedTasks("0");
                    model.setInspecitonPlanCompletion("0%");
                    model.setInspecitonMissingChecks("-");
                }
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

                model.setEmergencyResponseNum("-");
                model.setEmergencyHandlingHours("-");
            }

            return page.setRecords(personnelModels);
        }else {
            return page.setRecords(CollUtil.newArrayList());
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
        DateTime lastYear = DateUtil.offsetMonth(start, -11);

        //班组信息
        TeamPortraitModel depart = personnelGroupStatisticsMapper.getDepart(departId);

        List<String> departIds = new ArrayList<>();
        departIds.add(departId);
        //获取当前班组巡检参数数据
        UserTeamParameter userTeamParameter = new UserTeamParameter();
        userTeamParameter.setStartDate(DateUtil.formatDateTime(lastYear));
        userTeamParameter.setEndDate(DateUtil.formatDateTime(end));
        userTeamParameter.setOrgId(departId);
        Map<String, UserTeamPatrolDTO> teamParameter = patrolApi.getUserTeamParameter(userTeamParameter);
        //获取当前班组维修参数数据
        Map<String, FaultReportDTO> faultOrgReport = dailyFaultApi.getFaultOrgReport(departIds, DateUtil.formatDateTime(lastYear), DateUtil.formatDateTime(end));
        ///获取当前班组检修参数数据
        String startDate = DateUtil.formatDate(lastYear);
        String endDate = DateUtil.formatDate(end);
        Map<String, PersonnelTeamDTO> teamInformation = overhaulApi.teamInformation(DateUtil.parse(startDate), DateUtil.parse(endDate),departIds);

        if (ObjectUtil.isNotEmpty(faultOrgReport.get(departId))) {
            depart.setFaultTotalTime(Convert.toStr(faultOrgReport.get(departId).getFailureTime()));
            depart.setAverageFaultTime(Convert.toStr(faultOrgReport.get(departId).getRepairTime()));
        }else {
            depart.setFaultTotalTime("0");
            depart.setAverageFaultTime("0");
        }

        if (ObjectUtil.isNotEmpty(teamInformation.get(departId))) {
            depart.setInspecitonTotalTime(Convert.toStr(teamInformation.get(departId).getOverhaulWorkingHours()));
        }else {
            depart.setInspecitonTotalTime("0");
        }

        if (ObjectUtil.isNotEmpty(teamParameter.get(departId))) {
            depart.setPatrolTotalTime(Convert.toStr(teamParameter.get(departId).getWorkHours()));
            depart.setAverageMonthlyResidual(Convert.toStr(teamParameter.get(departId).getAvgMissPatrolNumber()));
        }else {
            depart.setPatrolTotalTime("0");
            depart.setAverageMonthlyResidual("0");
        }

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
        if (CollUtil.isNotEmpty(repairDuration)) {
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
            BigDecimal bigDecimal1 = new BigDecimal(repairDuration.size());
            String s = bigDecimal.divide(bigDecimal1, 0).toString();
            depart.setAverageTime(s);
        } else {
            depart.setAverageTime("0");
        }

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
        DateTime lastYear = DateUtil.offsetMonth(start, -11);

        //获取人员巡检参数数据
        UserTeamParameter userTeamParameter = new UserTeamParameter();
        userTeamParameter.setStartDate(DateUtil.formatDateTime(lastYear));
        userTeamParameter.setEndDate(DateUtil.formatDateTime(end));
        userTeamParameter.setUserId(userId);
        Map<String, UserTeamPatrolDTO> userParameter = patrolApi.getUserParameter(userTeamParameter);
        //获取人员维修参数数据
        Map<String, FaultReportDTO> faultUserReport = dailyFaultApi.getFaultUserReport(null, DateUtil.formatDateTime(lastYear), DateUtil.formatDateTime(end), userId);
        //获取人员检修参数数据
        String startDate = DateUtil.formatDate(lastYear);
        String endDate = DateUtil.formatDate(end);
        Map<String, PersonnelTeamDTO> personnelInformation = overhaulApi.personnelInformation(DateUtil.parse(startDate), DateUtil.parse(endDate), null, userId);

        TeamUserModel user = personnelGroupStatisticsMapper.getUser(userId);

        if (ObjectUtil.isNotEmpty(faultUserReport.get(userId))) {
            user.setFaultTotalTime(Convert.toStr(faultUserReport.get(userId).getFailureTime()));
            user.setAverageFaultTime(Convert.toStr(faultUserReport.get(userId).getRepairTime()));
        }else {
            user.setFaultTotalTime("0");
            user.setAverageFaultTime("0");
        }

        if (ObjectUtil.isNotEmpty(personnelInformation.get(userId))) {
            user.setInspecitonTotalTime(Convert.toStr(personnelInformation.get(userId).getOverhaulWorkingHours()));
        }else {
            user.setInspecitonTotalTime("0");
        }

        if (ObjectUtil.isNotEmpty(userParameter.get(userId))) {
            user.setPatrolTotalTime(Convert.toStr(userParameter.get(userId).getWorkHours()));
            user.setAverageMonthlyResidual(Convert.toStr(userParameter.get(userId).getAvgMissPatrolNumber()));
        }else {
            user.setPatrolTotalTime("0");
            user.setAverageMonthlyResidual("0");
        }
        List<String> userList = new ArrayList<>();
        userList.add(userId);
        //获取人员维修响应时长
        List<FaultRepairRecordDTO> repairDuration = personnelGroupStatisticsMapper.getRepairDuration(userList, lastYear, end);
        if (CollUtil.isNotEmpty(repairDuration)) {
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
            BigDecimal bigDecimal1 = new BigDecimal(repairDuration.size());
            String s = bigDecimal.divide(bigDecimal1, 0).toString();
            user.setAverageTime(s);
        }else {
            user.setAverageTime("0");
        }

        return user;
    }

    @Override
    public ModelAndView reportGroupExport(HttpServletRequest request, String startTime, String endTime,String exportField) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        Page<GroupModel> page = new Page<>(1, 9999);
        Page<GroupModel> groupModelPage = this.queryGroupPageList(null, startTime, endTime, page);
        List<GroupModel> records = groupModelPage.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            //导出文件名称
            mv.addObject(NormalExcelConstants.FILE_NAME, "班组统计报表");
            //excel注解对象Class
            mv.addObject(NormalExcelConstants.CLASS, GroupModel.class);
            //自定义导出字段
            mv.addObject(NormalExcelConstants.EXPORT_FIELDS,exportField);
            //自定义表格参数
            mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("统计分析-班组统计报表", "班组统计报表"));
            //导出数据列表
            mv.addObject(NormalExcelConstants.DATA_LIST, records);
        }
        return mv;
    }

    @Override
    public ModelAndView reportUserExport(HttpServletRequest request, String startTime, String endTime,String exportField) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        Page<PersonnelModel> page = new Page<>(1, 9999);
        Page<PersonnelModel> personnelModelPage = this.queryUserPageList(null, startTime, endTime, page);
        List<PersonnelModel> records = personnelModelPage.getRecords();
        if (CollectionUtil.isNotEmpty(records)) {
            //导出文件名称
            mv.addObject(NormalExcelConstants.FILE_NAME, "人员统计报表");
            //excel注解对象Class
            mv.addObject(NormalExcelConstants.CLASS, PersonnelModel.class);
            //自定义导出字段
            mv.addObject(NormalExcelConstants.EXPORT_FIELDS,exportField);
            //自定义表格参数
            mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("统计分析-人员统计报表", "人员统计报表"));
            //导出数据列表
            mv.addObject(NormalExcelConstants.DATA_LIST, records);
        }
        return mv;
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
