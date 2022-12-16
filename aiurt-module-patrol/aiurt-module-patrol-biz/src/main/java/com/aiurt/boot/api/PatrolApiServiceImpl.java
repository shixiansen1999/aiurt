package com.aiurt.boot.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.dto.UserTeamParameter;
import com.aiurt.boot.dto.UserTeamPatrolDTO;
import com.aiurt.boot.screen.constant.ScreenConstant;
import com.aiurt.boot.screen.model.ScreenDurationTask;
import com.aiurt.boot.screen.service.PatrolScreenService;
import com.aiurt.boot.screen.utils.ScreenDateUtil;
import com.aiurt.boot.standard.mapper.PatrolStandardMapper;
import com.aiurt.boot.task.entity.PatrolAccompany;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cgkj0
 */
@Service
public class PatrolApiServiceImpl implements PatrolApi {
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private PatrolTaskMapper patrolTaskMapper;
    @Autowired
    private PatrolTaskDeviceMapper patrolTaskDeviceMapper;
    @Autowired
    private PatrolStandardMapper patrolStandardMapper;
    @Autowired
    private PatrolTaskStandardMapper taskStandardMapper;
    @Autowired
    private PatrolAccompanyMapper patrolAccompanyMapper;
    @Autowired
    private PatrolTaskUserMapper patrolTaskUserMapper;
    @Autowired
    private PatrolScreenService screenService;
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    /**
     * 首页-统计日程的巡视完成数
     *
     * @param year
     * @param month
     * @return
     */
    @Override
    public Map<String, Integer> getPatrolFinishNumber(int year, int month) {
        Map<String, Integer> map = new HashMap<>(16);
        Calendar instance = Calendar.getInstance();
        instance.set(year, month - 1, 1);
        // 所在月的第一天
        Date firstDay = DateUtil.parse(DateUtil.format(instance.getTime(), "yyyy-MM-dd 00:00:00"));
        instance.set(Calendar.DAY_OF_MONTH, instance.getActualMaximum(Calendar.DAY_OF_MONTH));
        // 所在月的最后一天
        Date lastDay = DateUtil.parse(DateUtil.format(instance.getTime(), "yyyy-MM-dd 23:59:59"));
        QueryWrapper<PatrolTask> taskWrapper = new QueryWrapper<>();
        taskWrapper.lambda().eq(PatrolTask::getDelFlag, 0)
                .eq(PatrolTask::getStatus, PatrolConstant.TASK_COMPLETE)
                .between(PatrolTask::getPatrolDate, firstDay, lastDay);
        List<PatrolTask> taskList = patrolTaskMapper.selectList(taskWrapper);

        instance.set(year, month - 1, 1);
        while (instance.get(Calendar.MONTH) == month - 1) {
            String date = DateUtil.format(instance.getTime(), "yyyy/MM/dd");
            int count = (int) taskList.stream().filter(l -> date.equals(DateUtil.format(l.getPatrolDate(), "yyyy/MM/dd"))).count();
            map.put(date, count);
            instance.add(Calendar.DATE, 1);
        }
        return map;
    }

    @Override
    public  HashMap<String, String> getUserTask(DateTime startTime, DateTime endTime) {
        HashMap<String, String> map = new HashMap<>();
        //获取当前用户在时间范围内的的巡检任务信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<PatrolTask> taskList = patrolTaskUserMapper.getUserTask(sysUser.getOrgCode());
        if (CollUtil.isNotEmpty(taskList)) {
            List<PatrolTaskDevice> taskDeviceList = new ArrayList<>();
            for (PatrolTask task : taskList) {
                //获取当前用户的任务中，已提交的所有的工单
                List<PatrolTaskDevice> devices = patrolTaskDeviceMapper.getTodaySubmit(startTime,endTime, task.getId(), null);
                if (ObjectUtil.isNotEmpty(devices)) {
                    taskDeviceList.addAll(devices);
                }
            }
            //获取当前部门人员作为同行人参与的单号
            List<LoginUser> sysUsers = iSysBaseAPI.getUserPersonnel(sysUser.getOrgId());
            List<String> userIds = Optional.ofNullable(sysUsers).orElse(Collections.emptyList()).stream().map(LoginUser::getId).collect(Collectors.toList());
            List<PatrolAccompany> accompanyList = patrolAccompanyMapper.selectList(new LambdaQueryWrapper<PatrolAccompany>().in(PatrolAccompany::getUserId, userIds).select(PatrolAccompany::getTaskDeviceCode));
            //获取当前部门人员的单号，已提交
            List<String> taskDeviceCodes = Optional.ofNullable(accompanyList).orElse(Collections.emptyList()).stream().map(PatrolAccompany::getTaskDeviceCode).collect(Collectors.toList());
            for (String accompanyCode : taskDeviceCodes) {
                List<PatrolTaskDevice> devices = patrolTaskDeviceMapper.getTodaySubmit(startTime,endTime, null, accompanyCode);
                if (ObjectUtil.isNotEmpty(devices)) {
                    taskDeviceList.addAll(devices);
                }
            }

            StringBuilder content = new StringBuilder();
            StringBuilder code = new StringBuilder();
            //获取这个任务下的工单所对应的站点
            if (CollUtil.isNotEmpty(taskDeviceList)) {
                for (PatrolTaskDevice patrolTaskDevice : taskDeviceList) {
                    String lineName = iSysBaseAPI.getPosition(patrolTaskDevice.getLineCode());
                    String positionName = iSysBaseAPI.getPosition(patrolTaskDevice.getPositionCode());
                    LoginUser userById = iSysBaseAPI.getUserById(patrolTaskDevice.getUserId());
                    content.append(lineName).append("通信专业车站各系统专用设备").append("-").append(positionName).append(" ").append(" 巡视人:").append(userById.getRealname()).append("。").append('\n');
                    code.append(patrolTaskDevice.getTaskCode()).append(",");
                }
            }

            if (content.length() > 1) {
                // 截取字符
                content = content.deleteCharAt(content.length() - 1);
                map.put("content", content.toString());
            }
            if (code.length() > 1) {
                // 截取字符
                code = content.deleteCharAt(code.length() - 1);
                map.put("code", code.toString());
            }
        }
        return map;
    }

    @Override
    public Map<String, BigDecimal> getPatrolUserHours(int type, String teamId) {
        Map<String, BigDecimal> userDurationMap = new HashMap<>(16);
        // 班组的人员
        List<LoginUser> userList = sysBaseApi.getUserPersonnel(teamId);
        String dateTime = ScreenDateUtil.getDateTime(type);
        Date startTime = DateUtil.parse(dateTime.split(ScreenConstant.TIME_SEPARATOR)[0]);
        Date endTime = DateUtil.parse(dateTime.split(ScreenConstant.TIME_SEPARATOR)[1]);

        // 获取巡视人员在指定时间范围内的任务时长(单位秒)
        List<ScreenDurationTask> list = patrolTaskUserMapper.getScreenUserDuration(startTime, endTime);
        // 获取同行人在指定时间范围内的任务时长(单位秒)
        List<ScreenDurationTask> accompanyList = patrolTaskUserMapper.getScreentAccompanyDuration(startTime, endTime);
        Map<String, Long> durationMap = list.stream().collect(Collectors.toMap(k -> k.getUserId(),
                v -> ObjectUtil.isEmpty(v.getDuration()) ? 0L : v.getDuration(), (a, b) -> a));
        Map<String, Long> accompanyMap = accompanyList.stream().collect(Collectors.toMap(k -> k.getUserId(),
                v -> ObjectUtil.isEmpty(v.getDuration()) ? 0L : v.getDuration(), (a, b) -> a));
        userList.stream().forEach(l -> {
            String userId = l.getId();
            Long timeOne = durationMap.get(userId);
            Long timeTwo = accompanyMap.get(userId);
            if (ObjectUtil.isEmpty(timeOne)) {
                timeOne = 0L;
            }
            if (ObjectUtil.isEmpty(timeTwo)) {
                timeTwo = 0L;
            }
            // 展示需要以小时数展示，并保留两位小数
            double time = 1.0 * (timeOne + timeTwo) / 3600;
            BigDecimal decimal = new BigDecimal(time).setScale(2, BigDecimal.ROUND_HALF_UP);
            userDurationMap.put(userId, decimal);
        });
        return userDurationMap;
    }

    @Override
    public BigDecimal getPatrolHours(int type, String teamId) {
        // 班组的人员
        List<LoginUser> userList = sysBaseApi.getUserPersonnel(teamId);
        if (CollUtil.isNotEmpty(userList)) {
            String dateTime = ScreenDateUtil.getDateTime(type);
            Date startTime = DateUtil.parse(dateTime.split(ScreenConstant.TIME_SEPARATOR)[0]);
            Date endTime = DateUtil.parse(dateTime.split(ScreenConstant.TIME_SEPARATOR)[1]);

            // 获取巡视人员在指定时间范围内的每一个任务的时长(单位秒)
            List<ScreenDurationTask> screenDuration = patrolTaskUserMapper.getScreenDuration(startTime, endTime, userList);
            // 获取同行人在指定时间范围内的每一个任务的任务时长(单位秒)
            List<ScreenDurationTask> screentPeerDuration = patrolTaskUserMapper.getScreentPeerDuration(startTime, endTime, userList);

            List<String> collect = screenDuration.stream().map(ScreenDurationTask::getTaskId).collect(Collectors.toList());
            //若同行人和指派人同属一个班组，则该班组只取一次工时，不能累加
            List<ScreenDurationTask> dtos = screentPeerDuration.stream().filter(t -> !collect.contains(t.getTaskId())).collect(Collectors.toList());
            dtos.addAll(screenDuration);
            BigDecimal sum = new BigDecimal("0.00");
            for (ScreenDurationTask dto : dtos) {
                sum = sum.add(new BigDecimal(dto.getDuration()));
            }
            //秒转时
            BigDecimal decimal = sum.divide(new BigDecimal("3600"), 1, BigDecimal.ROUND_HALF_UP);
            return decimal;
        }
        return new BigDecimal("0.00");
    }

    @Override
    public Map<String, UserTeamPatrolDTO> getUserParameter(UserTeamParameter userTeamParameter) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> userSysDepart = sysBaseApi.getUserSysDepart(user.getId());
        if (CollUtil.isNotEmpty(userTeamParameter.getOrgIdList())) {
            userSysDepart = userSysDepart.stream().filter(u -> userTeamParameter.getOrgIdList().contains(u.getId())).collect(Collectors.toList());
        }
        List<String> orgIds = userSysDepart.stream().map(SysDepartModel::getId).collect(Collectors.toList());
        //获取部门list下的人员
        List<LoginUser> useList = sysBaseApi.getUseList(orgIds);
        List<String> useIds = useList.stream().map(LoginUser::getId).collect(Collectors.toList());
        List<UserTeamPatrolDTO> userBaseList = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(userTeamParameter.getUserId())) {
            useIds = useIds.stream().filter(u -> u.equals(userTeamParameter.getUserId())).collect(Collectors.toList());
        }
        for (String useId : useIds) {
            UserTeamPatrolDTO zero = setZero(useId);
            userBaseList.add(zero);
        }
        //统计部门人员指派的计划数、实际完成数、巡检工时
        List<UserTeamPatrolDTO> userPlanTaskNumber = new ArrayList<>();
        //统计部门人员同行人的计划数、实际完成数、巡检工时
        List<UserTeamPatrolDTO> peoplePlanTaskNumber = new ArrayList<>();
        if (CollUtil.isNotEmpty(useIds)) {
            userPlanTaskNumber.addAll(patrolTaskUserMapper.getUserPlanNumber(useIds, userTeamParameter.getStartDate(), userTeamParameter.getEndDate()));
            peoplePlanTaskNumber.addAll(patrolTaskUserMapper.getPeoplePlanNumber(useIds, userTeamParameter.getStartDate(), userTeamParameter.getEndDate()));
        }
        //合并
        for (UserTeamPatrolDTO userPatrol : userPlanTaskNumber) {
            Integer planNumber = 0;
            Integer nowNumber = 0;
            double workHour = 0;
            BigDecimal workHours = null;
            if (CollUtil.isNotEmpty(peoplePlanTaskNumber)) {
                for (UserTeamPatrolDTO peoplePatrol : peoplePlanTaskNumber) {
                    if (userPatrol.getUserId().equals(peoplePatrol.getUserId())) {
                        planNumber = userPatrol.getPlanTaskNumber() + peoplePatrol.getPlanTaskNumber();
                        nowNumber = userPatrol.getActualFinishTaskNumber() + peoplePatrol.getActualFinishTaskNumber();
                        workHour = NumberUtil.add(userPatrol.getWorkHours(), peoplePatrol.getWorkHours()).doubleValue();
                        //计算工时
                        if (workHour != 0) {
                            workHours = new BigDecimal(workHour / 3600).setScale(2, BigDecimal.ROUND_HALF_UP);
                        }
                    }
                }
            } else {
                workHour = NumberUtil.add(userPatrol.getWorkHours(), 0).doubleValue();
                //计算工时
                if (workHour != 0) {
                    workHours = new BigDecimal(workHour / 3600).setScale(2, BigDecimal.ROUND_HALF_UP);
                }
            }
            if (planNumber != 0) {
                userPatrol.setPlanTaskNumber(planNumber);
            }
            if (nowNumber != 0) {
                userPatrol.setActualFinishTaskNumber(nowNumber);
            }
            if (ObjectUtil.isNotEmpty(workHours)) {
                userPatrol.setWorkHours(workHours);
            }
        }
        //额外人员
        List<UserTeamPatrolDTO> extraList = peoplePlanTaskNumber.stream().filter(p -> !userPlanTaskNumber.contains(p)).collect(Collectors.toList());
        userPlanTaskNumber.addAll(extraList);
        //计算计划完成率
        for (UserTeamPatrolDTO userPatrol : userPlanTaskNumber) {
            if (userPatrol.getPlanTaskNumber() == 0 || userPatrol.getActualFinishTaskNumber() == 0) {
                userPatrol.setPlanFinishRate(new BigDecimal(0));
            } else {
                BigDecimal b = new BigDecimal((1.0 * (userPatrol.getActualFinishTaskNumber()) / userPatrol.getPlanTaskNumber() * 100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                userPatrol.setPlanFinishRate(b);
            }
        }
        //统计部门人员的漏检数
        UserTeamParameter omitModel = new UserTeamParameter();
        BeanUtils.copyProperties(userTeamParameter, omitModel);
        String start = screenService.getOmitDateScope(DateUtil.parse(omitModel.getStartDate()));
        String end = screenService.getOmitDateScope(DateUtil.parse(omitModel.getEndDate()));
        omitModel.setStartDate(start.split("~")[0]);
        omitModel.setEndDate(end.split("~")[1]);
        // 统计部门人员的指派的漏检数
        List<UserTeamPatrolDTO> userOmitTaskNumber = new ArrayList<>();
        // 统计部门人员同行人的漏检数
        List<UserTeamPatrolDTO> peopleOmitTaskNumber = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(useIds)) {
            userOmitTaskNumber.addAll(patrolTaskUserMapper.getUserOmitNumber(useIds, omitModel.getStartDate(), omitModel.getEndDate()));
            peopleOmitTaskNumber.addAll(patrolTaskUserMapper.getPeopleOmitNumber(useIds, omitModel.getStartDate(), omitModel.getEndDate()));
        }
        for (UserTeamPatrolDTO userPatrol : userOmitTaskNumber) {
            Integer omitNumber = 0;
            for (UserTeamPatrolDTO peoplePatrol : peopleOmitTaskNumber) {
                if (userPatrol.getUserId().equals(peoplePatrol.getUserId())) {
                    omitNumber = userPatrol.getMissPatrolNumber() + peoplePatrol.getMissPatrolNumber();
                }
            }
            if (omitNumber != 0) {
                userPatrol.setMissPatrolNumber(omitNumber);
                BigDecimal avgMissNumber = NumberUtil.div(new BigDecimal(omitNumber), new BigDecimal(12)).setScale(2, BigDecimal.ROUND_HALF_UP);
                userPatrol.setAvgMissPatrolNumber(avgMissNumber);
            } else {
                userPatrol.setMissPatrolNumber(0);
                userPatrol.setAvgMissPatrolNumber(new BigDecimal(0));
            }
        }
        //额外人员漏检
        List<UserTeamPatrolDTO> extraOmitList = peopleOmitTaskNumber.stream().filter(p -> !userOmitTaskNumber.contains(p)).collect(Collectors.toList());
        userOmitTaskNumber.addAll(extraOmitList);
        for (UserTeamPatrolDTO patrolDTO : userBaseList) {
            for (UserTeamPatrolDTO dto : userPlanTaskNumber) {
                if (patrolDTO.getUserId().equals(dto.getUserId())) {
                    BeanUtils.copyProperties(dto, patrolDTO);
                }
            }
            for (UserTeamPatrolDTO omit : userOmitTaskNumber) {
                if (patrolDTO.getUserId().equals(omit.getUserId())) {
                    patrolDTO.setMissPatrolNumber(omit.getMissPatrolNumber());
                }
            }
        }
        Map<String, UserTeamPatrolDTO> groupBy = userBaseList.stream().collect(Collectors.toMap(UserTeamPatrolDTO::getUserId, v -> v, (a, b) -> a));
        return groupBy;
    }

    public UserTeamPatrolDTO setZero(String userId) {
        UserTeamPatrolDTO patrolDTO = new UserTeamPatrolDTO();
        patrolDTO.setUserId(userId);
        patrolDTO.setMissPatrolNumber(0);
        patrolDTO.setMissPatrolNumber(0);
        patrolDTO.setPlanFinishRate(new BigDecimal(0));
        patrolDTO.setWorkHours(new BigDecimal(0));
        patrolDTO.setActualFinishTaskNumber(0);
        patrolDTO.setPlanTaskNumber(0);
        patrolDTO.setAvgMissPatrolNumber(new BigDecimal(0));
        return patrolDTO;
    }

    public UserTeamPatrolDTO setTeamZero(String orgId) {
        UserTeamPatrolDTO patrolDTO = new UserTeamPatrolDTO();
        patrolDTO.setOrgId(orgId);
        patrolDTO.setMissPatrolNumber(0);
        patrolDTO.setMissPatrolNumber(0);
        patrolDTO.setPlanFinishRate(new BigDecimal(0));
        patrolDTO.setWorkHours(new BigDecimal(0));
        patrolDTO.setActualFinishTaskNumber(0);
        patrolDTO.setPlanTaskNumber(0);
        patrolDTO.setAvgMissPatrolNumber(new BigDecimal(0));
        return patrolDTO;
    }

    @Override
    public Map<String, UserTeamPatrolDTO> getUserTeamParameter(UserTeamParameter userTeamParameter) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysDepartModel> userSysDepart = sysBaseApi.getUserSysDepart(user.getId());
        //条件查询
        if (CollUtil.isNotEmpty(userTeamParameter.getOrgIdList())) {
            userSysDepart = userSysDepart.stream().filter(u -> userTeamParameter.getOrgIdList().contains(u.getId())).collect(Collectors.toList());
        }
        //点击班组，查询
        if (ObjectUtil.isNotEmpty(userTeamParameter.getOrgId())) {
            userSysDepart = userSysDepart.stream().filter(u -> userTeamParameter.getOrgId().contains(u.getId())).collect(Collectors.toList());
        }
        List<String> orgIds = userSysDepart.stream().map(SysDepartModel::getId).collect(Collectors.toList());
        List<UserTeamPatrolDTO> userBaseList = new ArrayList<>();
        for (String orgId : orgIds) {
            UserTeamPatrolDTO zero = setTeamZero(orgId);
            userBaseList.add(zero);
        }
        for (UserTeamPatrolDTO dto : userBaseList) {
            //获取部门下的人员
            List<LoginUser> useList = sysBaseApi.getUserPersonnel(dto.getOrgId());
            List<String> useIds = useList.stream().map(LoginUser::getId).collect(Collectors.toList());
            //计算计划巡检数的计划巡检数
            UserTeamPatrolDTO userPlanNumber = patrolTaskMapper.getUserPlanNumber(dto.getOrgId(), userTeamParameter.getStartDate(), userTeamParameter.getEndDate());
            //计算指派实际巡检数、同行人的实际巡检数
            List<UserTeamPatrolDTO> userNowNumber = new ArrayList<>();
            List<UserTeamPatrolDTO> peopleNowNumber = new ArrayList<>();
            if (CollUtil.isNotEmpty(useIds)) {
                userNowNumber.addAll(patrolTaskMapper.getUserNowNumber(useIds, userTeamParameter.getStartDate(), userTeamParameter.getEndDate()));
                peopleNowNumber.addAll(patrolTaskMapper.getPeopleNowNumber(useIds, userTeamParameter.getStartDate(), userTeamParameter.getEndDate()));
            }
            //过滤实际数不是同一任务的班组
            List<String> nowTaskIds = userNowNumber.stream().map(UserTeamPatrolDTO::getTaskId).collect(Collectors.toList());
            List<UserTeamPatrolDTO> notNowTasks = peopleNowNumber.stream().filter(u -> !nowTaskIds.contains(u.getTaskId())).collect(Collectors.toList());
            userNowNumber.addAll(notNowTasks);
            if (userPlanNumber.getPlanTaskNumber() != 0) {
                dto.setPlanTaskNumber(userPlanNumber.getPlanTaskNumber());
            }
            if (userNowNumber.size() != 0) {
                dto.setActualFinishTaskNumber(userNowNumber.size());
            }
            if (dto.getPlanTaskNumber() == 0 || dto.getActualFinishTaskNumber() == 0) {
                dto.setPlanFinishRate(new BigDecimal(0));
            }
            //计算计划完成率
            else {
                BigDecimal b = new BigDecimal((1.0 * (dto.getActualFinishTaskNumber()) / dto.getPlanTaskNumber() * 100)).setScale(2, BigDecimal.ROUND_HALF_UP);
                dto.setPlanFinishRate(b);
            }
            //计算工时
            if (ObjectUtil.isNotEmpty(dto.getWorkHours())) {
                List<UserTeamPatrolDTO> dtos = userNowNumber.stream().filter(e -> e.getWorkHours() != null).collect(Collectors.toList());
                BigDecimal planTotalWorkTime = dtos.stream().map(UserTeamPatrolDTO::getWorkHours).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal scale = NumberUtil.div(planTotalWorkTime, 3600).setScale(2, BigDecimal.ROUND_HALF_UP);
                dto.setWorkHours(scale);
            }
            //计算漏检数（先推算漏检日期）
            String start = screenService.getOmitDateScope(DateUtil.parse(userTeamParameter.getStartDate()));
            String end = screenService.getOmitDateScope(DateUtil.parse(userTeamParameter.getEndDate()));
            //计算指派的漏检数、同行人的漏检数
            List<UserTeamPatrolDTO> userOmitTasks = patrolTaskMapper.getUserOmitTasksNumber(useIds, start, end);
            List<UserTeamPatrolDTO> peopleOmitTasks = patrolTaskMapper.getPeopleOmitTasksNumber(useIds, start, end);
            //过滤漏检数不是同一任务的班组
            List<String> omitTaskIds = userOmitTasks.stream().map(UserTeamPatrolDTO::getTaskId).collect(Collectors.toList());
            List<UserTeamPatrolDTO> notOmitTaskIds = peopleOmitTasks.stream().filter(u -> !omitTaskIds.contains(u.getTaskId())).collect(Collectors.toList());
            userOmitTasks.addAll(notOmitTaskIds);
            if (userOmitTasks.size() != 0) {
                dto.setMissPatrolNumber(userOmitTasks.size());
                //计算平均每月漏检次数
                BigDecimal avgMissNumber = NumberUtil.div(new BigDecimal(userOmitTasks.size()), new BigDecimal(12)).setScale(2, BigDecimal.ROUND_HALF_UP);
                dto.setAvgMissPatrolNumber(avgMissNumber);
            }
        }
        Map<String, UserTeamPatrolDTO> groupBy = userBaseList.stream().collect(Collectors.toMap(UserTeamPatrolDTO::getOrgId, v -> v, (a, b) -> a));
        return groupBy;
    }
}
