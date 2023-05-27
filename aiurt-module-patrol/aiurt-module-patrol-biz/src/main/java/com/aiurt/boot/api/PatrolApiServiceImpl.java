package com.aiurt.boot.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.dto.UserTeamParameter;
import com.aiurt.boot.dto.UserTeamPatrolDTO;
import com.aiurt.boot.screen.constant.ScreenConstant;
import com.aiurt.boot.screen.model.ScreenDurationTask;
import com.aiurt.boot.screen.model.ScreenModule;
import com.aiurt.boot.screen.service.PatrolScreenService;
import com.aiurt.boot.screen.utils.ScreenDateUtil;
import com.aiurt.boot.standard.mapper.PatrolStandardMapper;
import com.aiurt.boot.statistics.dto.IndexCountDTO;
import com.aiurt.boot.statistics.model.PatrolSituation;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.config.datafilter.constant.DataPermRuleType;
import com.aiurt.config.datafilter.utils.ContextUtil;
import com.aiurt.config.datafilter.utils.SqlBuilderUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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
    private PatrolTaskOrganizationMapper patrolTaskOrganizationMapper;
    @Autowired
    private PatrolStandardMapper patrolStandardMapper;
    @Autowired
    private PatrolTaskStationMapper patrolTaskStationMapper;
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
    @Autowired
    private ISysParamAPI sysParamApi;
    /**
     * 首页-统计日程的巡视完成数
     *
     * @param year
     * @param month
     * @return
     */
    @Override
    public Map<String, Integer> getPatrolFinishNumber(int year, int month,HttpServletRequest request) {
        Map<String, Integer> map = new HashMap<>(16);
        Calendar instance = Calendar.getInstance();
        instance.set(year, month - 1, 1);
        // 所在月的第一天
        Date firstDay = DateUtil.parse(DateUtil.format(instance.getTime(), "yyyy-MM-dd 00:00:00"));
        instance.set(Calendar.DAY_OF_MONTH, instance.getActualMaximum(Calendar.DAY_OF_MONTH));
        // 所在月的最后一天
        Date lastDay = DateUtil.parse(DateUtil.format(instance.getTime(), "yyyy-MM-dd 23:59:59"));

        // 获取权限数据
        String filterConditions = this.getPermissionSQL(request);
        IndexCountDTO indexCountDTO = new IndexCountDTO(firstDay, lastDay, filterConditions);
        List<PatrolSituation> overviewInfoCount = new ArrayList<>();
        //根据配置决定是否需要把工单数量作为任务数量
        SysParamModel paramModel = sysParamApi.selectByCode(SysParamCodeConstant.PATROL_TASK_DEVICE_NUM);
        boolean value = "1".equals(paramModel.getValue());
        if (value) {
            overviewInfoCount = patrolTaskMapper.getTaskDeviceCountONMonth(indexCountDTO);
        } else {
            overviewInfoCount = patrolTaskMapper.getCountONMonth(indexCountDTO);
        }

        instance.set(year, month - 1, 1);
        while (instance.get(Calendar.MONTH) == month - 1) {
            String date = DateUtil.format(instance.getTime(), "yyyy/MM/dd");
            if (CollUtil.isNotEmpty(overviewInfoCount)) {
                PatrolSituation patrolSituation = overviewInfoCount.stream().filter(l -> date.equals(DateUtil.format(l.getPatrolDate(), "yyyy/MM/dd"))).findFirst().orElse(new PatrolSituation());
                map.put(date, ObjectUtil.isNotEmpty(patrolSituation) && patrolSituation.getFinish() != null ? patrolSituation.getFinish().intValue() : 0);
            } else {
                map.put(date, 0);
            }
            instance.add(Calendar.DATE, 1);
        }
        return map;
    }

    /**
     * 获取数据权限的SQL片段
     */
    public String getPermissionSQL(HttpServletRequest request) {
        Map<String, String> map = (Map<String, String>) request.getAttribute(ContextUtil.FILTER_DATA_AUTHOR_RULES);
        Map<String, String> mapping = this.getColumnMapping();
        String filterConditions = SqlBuilderUtil.buildSql(map, mapping);
        return filterConditions;
    }

    /**
     * 初始化并返回一个预定义的列映射。
     *
     * @return 返回一个包含预定义列映射的 Map 对象
     */
    public Map<String, String> getColumnMapping() {
        Map<String, String> columnMapping = new HashMap<>(8);
        // 当前的部门
        columnMapping.put(DataPermRuleType.TYPE_DEPT_ONLY, "pto.org_code");
        // 管理的部门
        columnMapping.put(DataPermRuleType.TYPE_MANAGE_DEPT, "pto.org_code");
        // 管理的线路
        columnMapping.put(DataPermRuleType.TYPE_MANAGE_LINE_ONLY, "pts.station_code");
        // 管理的站点
        columnMapping.put(DataPermRuleType.TYPE_MANAGE_STATION_ONLY, "pts.line_code");
        // 管理的专业
        columnMapping.put(DataPermRuleType.TYPE_MANAGE_MAJOR_ONLY, "ptsd.profession_code");
        // 管理的子系统
        columnMapping.put(DataPermRuleType.TYPE_MANAGE_SYSTEM_ONLY, "ptsd.subsystem_code");
        return columnMapping;
    }

    @Override
    public  HashMap<String, String> getUserTask(DateTime startTime, DateTime endTime) {
        HashMap<String, String> map = new HashMap<>();
        //获取当前用户在时间范围内的的巡检任务信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<PatrolTaskDevice> taskDeviceList = new ArrayList<>();
        //2023-03-27 需求确认，工作日志只看本班组下的相应时间的任务，不看工单，因此在sql合并
        //获取当前用户的任务中，已提交的所有的工单
        List<PatrolTaskDevice> devices = patrolTaskDeviceMapper.getTodaySubmit(startTime,endTime,sysUser.getOrgCode());
        if (ObjectUtil.isNotEmpty(devices)) {
            taskDeviceList.addAll(devices);
        }
        //2023-3-27 需求确认，同行人也是本班组的人，去掉同行人，防止重复
//            //获取当前部门人员作为同行人参与的单号
//            List<LoginUser> sysUsers = iSysBaseAPI.getUserPersonnel(sysUser.getOrgId());
//            List<String> userIds = Optional.ofNullable(sysUsers).orElse(Collections.emptyList()).stream().map(LoginUser::getId).collect(Collectors.toList());
//            List<PatrolAccompany> accompanyList = patrolAccompanyMapper.selectList(new LambdaQueryWrapper<PatrolAccompany>().in(PatrolAccompany::getUserId, userIds).select(PatrolAccompany::getTaskDeviceCode));
//            //获取当前部门人员的单号，已提交
//            List<String> taskDeviceCodes = Optional.ofNullable(accompanyList).orElse(Collections.emptyList()).stream().map(PatrolAccompany::getTaskDeviceCode).distinct().collect(Collectors.toList());
//            for (String accompanyCode : taskDeviceCodes) {
//                List<PatrolTaskDevice> devices = patrolTaskDeviceMapper.getTodaySubmit(startTime,endTime, null, accompanyCode);
//                if (ObjectUtil.isNotEmpty(devices)) {
//                    taskDeviceList.addAll(devices);
//                }
//            }

        StringBuilder content = new StringBuilder();
        StringBuilder code = new StringBuilder();
        String string = null;
        //获取这个任务下的工单所对应的站点
        if (CollUtil.isNotEmpty(taskDeviceList)) {
            List<PatrolTaskDevice> collect = taskDeviceList.stream().distinct().collect(Collectors.toList());
            for (PatrolTaskDevice patrolTaskDevice : collect) {
                String lineName = iSysBaseAPI.getPosition(patrolTaskDevice.getLineCode());
                String stationName = iSysBaseAPI.getPosition(patrolTaskDevice.getStationCode());
                LoginUser userById = iSysBaseAPI.getUserById(patrolTaskDevice.getUserId());
                if (StrUtil.isNotBlank(patrolTaskDevice.getTaskId())){
                    PatrolTask patrolTask = patrolTaskMapper.selectOne(new LambdaQueryWrapper<PatrolTask>().eq(PatrolTask::getDelFlag,CommonConstant.DEL_FLAG_0).eq(PatrolTask::getId, patrolTaskDevice.getTaskId()));
                    if (ObjectUtil.isNotNull(patrolTask)){
                        string = patrolTask.getName();
                    }
                }
                content.append(lineName).append(string).append("-").append(stationName).append(" ").append(" 巡视人:").append(userById.getRealname()).append("。").append('\n');
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
            code = code.deleteCharAt(code.length() - 1);
            map.put("code", code.toString());
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
            // 通信5期，班组的巡视工时改为同行人累加，需要把所有的人都加起来
            // List<String> collect = screenDuration.stream().map(ScreenDurationTask::getTaskId).collect(Collectors.toList());
            // //若同行人和指派人同属一个班组，则该班组只取一次工时，不能累加
            // List<ScreenDurationTask> dtos = screentPeerDuration.stream().filter(t -> !collect.contains(t.getTaskId())).collect(Collectors.toList());
            // dtos.addAll(screenDuration);
            List<ScreenDurationTask> dtos = new ArrayList<>();
            dtos.addAll(screentPeerDuration);
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

        //获取部门list下的人员
        List<LoginUser> useList = sysBaseApi.getUseList(userTeamParameter.getOrgIdList());
        if (CollUtil.isEmpty(useList)) {
            return new HashMap<String, UserTeamPatrolDTO>();
        }
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
        if (ObjectUtil.isNotEmpty(useIds)) {
            userOmitTaskNumber.addAll(patrolTaskUserMapper.getUserOmitNumber(useIds, omitModel.getStartDate(), omitModel.getEndDate()));
        }
        for (UserTeamPatrolDTO userPatrol : userOmitTaskNumber) {
            Integer omitNumber = userPatrol.getMissPatrolNumber();
            if (omitNumber != 0) {
                userPatrol.setMissPatrolNumber(omitNumber);
                BigDecimal avgMissNumber = NumberUtil.div(new BigDecimal(omitNumber), new BigDecimal(12)).setScale(2, BigDecimal.ROUND_HALF_UP);
                userPatrol.setAvgMissPatrolNumber(avgMissNumber);
            } else {
                userPatrol.setMissPatrolNumber(0);
                userPatrol.setAvgMissPatrolNumber(new BigDecimal(0));
            }
        }

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

        List<String> orgIds = new ArrayList<>();
        if (StrUtil.isNotEmpty(userTeamParameter.getOrgId())) {
            orgIds.add(userTeamParameter.getOrgId());
        } else {
            orgIds.addAll(userTeamParameter.getOrgIdList());
        }

        if (CollUtil.isEmpty(orgIds)) {
            return new HashMap<>();
        }

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
            // 通信5期，班组的巡视工时改为同行人累加，需要把所有的人都加起来
            List<UserTeamPatrolDTO> allNowNumber = new ArrayList<>();
            if (CollUtil.isNotEmpty(useIds)) {
                userNowNumber.addAll(patrolTaskMapper.getUserNowNumber(useIds, userTeamParameter.getStartDate(), userTeamParameter.getEndDate()));
                peopleNowNumber.addAll(patrolTaskMapper.getPeopleNowNumber(useIds, userTeamParameter.getStartDate(), userTeamParameter.getEndDate()));
                // 通信5期，班组的巡视工时改为同行人累加，需要把所有的人都加起来
                allNowNumber.addAll(userNowNumber);
                allNowNumber.addAll(peopleNowNumber);
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
                // 通信5期，班组的巡视工时改为同行人累加
                List<UserTeamPatrolDTO> dtos = allNowNumber.stream().filter(e -> e.getWorkHours() != null).collect(Collectors.toList());
                BigDecimal planTotalWorkTime = dtos.stream().map(UserTeamPatrolDTO::getWorkHours).reduce(BigDecimal.ZERO, BigDecimal::add);
                BigDecimal scale = NumberUtil.div(planTotalWorkTime, 3600).setScale(2, BigDecimal.ROUND_HALF_UP);
                dto.setWorkHours(scale);
            }
            //计算漏检数（先推算漏检日期）
            String start = screenService.getOmitDateScope(DateUtil.parse(userTeamParameter.getStartDate())).split(ScreenConstant.TIME_SEPARATOR)[0];
            String end = screenService.getOmitDateScope(DateUtil.parse(userTeamParameter.getEndDate())).split(ScreenConstant.TIME_SEPARATOR)[1];
            ScreenModule module = new ScreenModule();
            module.setDiscardStatus(PatrolConstant.TASK_UNDISCARD);
            SysDepartModel sysDepartModel = sysBaseApi.selectAllById(dto.getOrgId());
            module.setOrgCodes(StrUtil.splitTrim(sysDepartModel.getOrgCode(), ","));
            module.setStartTime(DateUtil.parse(start));
            module.setEndTime(DateUtil.parse(end));
            module.setOmit(PatrolConstant.OMIT_STATUS);
            long omitNum = patrolTaskMapper.getScreenDataCount(module).stream().count();

            dto.setMissPatrolNumber((int) omitNum);
            //计算平均每月漏检次数
            BigDecimal avgMissNumber = NumberUtil.div(new BigDecimal((int) omitNum), new BigDecimal(12)).setScale(2, BigDecimal.ROUND_HALF_UP);
            dto.setAvgMissPatrolNumber(avgMissNumber);

        }
        Map<String, UserTeamPatrolDTO> groupBy = userBaseList.stream().collect(Collectors.toMap(UserTeamPatrolDTO::getOrgId, v -> v, (a, b) -> a));
        return groupBy;
    }
}
