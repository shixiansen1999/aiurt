package com.aiurt.boot.bigscreen.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.api.PatrolApi;
import com.aiurt.boot.bigscreen.mapper.BigScreenPlanMapper;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.index.dto.*;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.plan.dto.CodeManageDTO;
import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.plan.mapper.RepairPoolMapper;
import com.aiurt.boot.task.entity.RepairTaskUser;
import com.aiurt.boot.task.mapper.RepairTaskMapper;
import com.aiurt.boot.task.mapper.RepairTaskUserMapper;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.fault.dto.RepairRecordDetailDTO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2022/9/1316:58
 */
@Service
public class BigscreenPlanService {

    @Resource
    private ISysBaseAPI sysBaseAPI;
    @Resource
    private RepairPoolMapper repairPoolMapper;
    @Resource
    private InspectionManager manager;
    @Resource
    private RepairTaskMapper repairTaskMapper;
    @Resource
    private RepairTaskUserMapper repairTaskUserMapper;
    @Resource
    private BigScreenPlanMapper bigScreenPlanMapper;

    @Resource
    private PatrolApi patrolApi;

    /**
     * 获取大屏的检修概况数量
     *
     * @param lineCode 线路code
     * @param type     类型:1：本周，2：上周，3：本月， 4：上月
     * @return
     */
    public PlanIndexDTO getOverviewInfo(String lineCode, String type) {
        PlanIndexDTO result = new PlanIndexDTO();

        // 根据类型获取开始时间和结束时间
        Date[] time = getTimeByType(type);

        if (time.length > 0) {
            // 根据自身管理专业和传入线路过滤出班组
            List<String> orgCodes = sysBaseAPI.getTeamBylineAndMajor(lineCode);

            // 筛选出来的班组为空，则直接返回
            if (CollUtil.isEmpty(orgCodes)) {
                result.setSum(0L);
                result.setFinish(0L);
                result.setOmit(0L);
                result.setTodayFinish(0L);
                return result;
            }

            // 根据传入的进行时间过滤
            List<InspectionDTO> inspectionDataNoPage = repairPoolMapper.getInspectionDataNoPage(orgCodes, null, time[0], time[1]);

            // 填充计划检修数
            result.setSum(CollUtil.isNotEmpty(inspectionDataNoPage) ? inspectionDataNoPage.size() : 0L);

            // 填充检修完成数
            result.setFinish(CollUtil.isNotEmpty(inspectionDataNoPage) ? inspectionDataNoPage.stream().filter(re -> InspectionConstant.COMPLETED.equals(re.getStatus())).count() : 0L);

            // 填充漏检数
            result.setOmit(0L);

            // 填充今日检修数
            List<InspectionDTO> todayInspectionNum = repairPoolMapper.getInspectionTodayDataNoPage(new Date(), orgCodes);
            result.setTodayFinish(CollUtil.isNotEmpty(todayInspectionNum) ? todayInspectionNum.size() : 0L);
        }
        return result;
    }


    /**
     * 功能：巡检修数据分析->检修数据统计(带分页)
     *
     * @param lineCode 线路code
     * @param type     类型:1：本周，2：上周，3：本月， 4：上月
     * @param item     1计划数，2完成数，3漏检数，4今日检修数
     * @param page     分页参数
     * @return
     */
    public IPage<InspectionDTO> getInspectionDataPage(String lineCode, String type, Integer item, Page<InspectionDTO> page) {
        List<InspectionDTO> result = new ArrayList<>();

        // 校验,必填字段为空则直接返回
        if (StrUtil.isEmpty(type)) {
            return page;
        }

        // 默认查询的是计划总数
        if (ObjectUtil.isEmpty(item)) {
            item = InspectionConstant.PLAN_TOTAL_1;
        }

        // 根据类型获取开始时间和结束时间
        Date[] time = getTimeByType(type);
        if (time.length > 0) {
            List<String> orgCodes = sysBaseAPI.getTeamBylineAndMajor(lineCode);
            // 通过传入的线路和自身管理的专业没有查询到班组，则直接返回
            if (CollUtil.isEmpty(orgCodes)) {
                return page;
            }

            // 查询计划数、完成数
            if (InspectionConstant.PLAN_TOTAL_1.equals(item) || InspectionConstant.PLAN_FINISH_2.equals(item)) {
                result = repairPoolMapper.getInspectionData(page, orgCodes, item, time[0], time[1]);
            }

            // TODO 漏检
            // 查询今日检修
            if (InspectionConstant.PLAN_TODAY_4.equals(item)) {
                result = repairPoolMapper.getInspectionTodayData(page, new Date(), orgCodes);
            }

            // 统一处理结果
            if (CollUtil.isNotEmpty(result)) {
                handleResult(result);
            }

        }
        return page.setRecords(result);
    }

    /**
     * 功能：巡检修数据分析->检修数据统计（不带分页）
     *
     * @param lineCode 线路code
     * @param type     类型:1：本周，2：上周，3：本月， 4：上月
     * @param item     1计划数，2完成数，3漏检数，4今日检修数
     * @return
     */
    public List<InspectionDTO> getInspectionDataNoPage(String lineCode, String type, Integer item) {
        List<InspectionDTO> result = new ArrayList<>();

        // 校验,必填字段为空则直接返回
        if (StrUtil.isEmpty(type)) {
            return result;
        }

        // 默认查询的是计划总数
        if (ObjectUtil.isEmpty(item)) {
            item = InspectionConstant.PLAN_TOTAL_1;
        }

        // 根据类型获取开始时间和结束时间
        Date[] time = getTimeByType(type);

        if (time.length > 0) {
            List<String> orgCodes = sysBaseAPI.getTeamBylineAndMajor(lineCode);
            if (CollUtil.isEmpty(orgCodes)) {
                return result;
            }

            // 填充计划数、完成数
            if (InspectionConstant.PLAN_TOTAL_1.equals(item) || InspectionConstant.PLAN_FINISH_2.equals(item)) {
                result = repairPoolMapper.getInspectionDataNoPage(orgCodes, item, time[0], time[1]);
            }

            // TODO 漏检
            // 填充今日检修
            if (InspectionConstant.PLAN_TODAY_4.equals(item)) {
                result = repairPoolMapper.getInspectionTodayDataNoPage(new Date(), orgCodes);
            }

            // 统一处理结果
            if (CollUtil.isNotEmpty(result)) {
                long start = System.currentTimeMillis();

                handleResult(result);
                long end = System.currentTimeMillis();
                System.out.println(end - start);
            }
        }
        return result;
    }

    /**
     * 处理检修数据统计结果
     *
     * @param result
     */
    public void handleResult(List<InspectionDTO> result) {
        if (CollUtil.isNotEmpty(result)) {
            // 任务状态字典
            Map<String, String> taskStateMap = Optional.ofNullable(sysBaseAPI.getDictItems(DictConstant.INSPECTION_TASK_STATE)).orElse(CollUtil.newArrayList()).stream().collect(Collectors.toMap(DictModel::getValue, DictModel::getText));

            // 检修状态
            List<String> taskCodes = result.stream().map(InspectionDTO::getCode).collect(Collectors.toList());

            // 组织机构
            Map<String, List<String>> orgMap = new HashMap<>(64);
            List<CodeManageDTO> orgList = repairPoolMapper.selectOrgByCodes(taskCodes);
            if (CollUtil.isNotEmpty(orgList)) {
                orgMap = orgList.stream().collect(Collectors.toMap(CodeManageDTO::getCode, CodeManageDTO::getList));
            }
            Map<String, List<String>> finalOrgMap = orgMap;

            // 站点
            Map<String, List<StationDTO>> staMap = new HashMap<>(64);
            List<CodeManageDTO> staList = repairPoolMapper.selectStationList(taskCodes);
            if (CollUtil.isNotEmpty(orgList)) {
                staMap = staList.stream().collect(Collectors.toMap(CodeManageDTO::getCode, CodeManageDTO::getStationDTOS));
            }
            Map<String, List<StationDTO>> finalStaMap = staMap;

            // 检修人
            Map<String, List<RepairTaskUser>> userMap = new HashMap<>(64);
            List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(
                    new LambdaQueryWrapper<RepairTaskUser>()
                            .in(RepairTaskUser::getRepairTaskCode, taskCodes)
                            .eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (CollUtil.isNotEmpty(repairTaskUsers)) {
                 userMap = repairTaskUsers.stream().collect(Collectors.groupingBy(RepairTaskUser::getRepairTaskCode));
            }
            Map<String, List<RepairTaskUser>> finalUserMap = userMap;

            // 并行流处理
            result.parallelStream().forEach(inspectionDTO -> {
                // 填充组织机构
                if (MapUtil.isNotEmpty(finalOrgMap)) {
                    inspectionDTO.setTeamName(manager.translateOrg(finalOrgMap.get(inspectionDTO.getCode())));
                }

                // 填充站点
                inspectionDTO.setStationName(manager.translateStation(finalStaMap.get(inspectionDTO.getCode())));

                // 翻译状态
                if (ObjectUtil.isNotEmpty(inspectionDTO.getStatus())) {
                    inspectionDTO.setStatusName(taskStateMap.get(String.valueOf(inspectionDTO.getStatus())));
                }

                // 填充检修任务
                if (ObjectUtil.isNotEmpty(inspectionDTO.getWeeks())) {
                    inspectionDTO.setInspectionTask(String.format("第%d周检修", inspectionDTO.getWeeks()));
                }

                // 如果状态是已完成，并且存在检修单异常需要将状态改成结果异常
                if (StrUtil.isNotEmpty(inspectionDTO.getCode())) {
                    if (InspectionConstant.COMPLETED.equals(inspectionDTO.getStatus())) {
                        // 查询该任务是否有检修单存在异常项
                        Integer num = repairTaskMapper.getTaskExceptionItem(inspectionDTO.getCode());
                        if (num > 0) {
                            inspectionDTO.setStatusName("结果异常");
                        }
                    }

                    // 填充检修时间，无审核拿提交时间，有审核拿审核时间
                    List<Date> inspectionTime = repairTaskMapper.getTaskInspectionTime(inspectionDTO.getCode());

                    // 29日 12：23
                    inspectionDTO.setTime(CollUtil.isNotEmpty(inspectionTime) ? DateUtil.format(inspectionTime.get(0), "dd日 HH:mm") : "");

                    // 填充检修人
                    List<LoginUser> loginUsers = sysBaseAPI.queryAllUserByIds(Optional.ofNullable(finalUserMap.get(inspectionDTO.getCode())).orElse(CollUtil.newArrayList()).stream().map(RepairTaskUser::getUserId).toArray(String[]::new));
                    if (CollUtil.isNotEmpty(loginUsers)) {
                        inspectionDTO.setRealName(loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining("；")));
                    }
                }
            });
        }
    }

    /**
     * 功能：巡检修数据分析->检修任务完成情况
     * 检修任务完成情况默认只是查询的时间范围是本周
     *
     * @param lineCode 线路code
     * @return
     */
    public List<PlanIndexDTO> getTaskCompletion(String lineCode) {
        List<PlanIndexDTO> result = Collections.synchronizedList(new ArrayList<>());

        // 默认是本周的时间范围
        Date[] time = getTimeByType(InspectionConstant.THIS_WEEK_1);

        if (time.length > 0) {
            // 通过传入线路和自身专业过滤出班组详细信息
            List<SysDepartModel> teamBylineAndMajors = sysBaseAPI.getTeamBylineAndMajors(lineCode);

            if (CollUtil.isNotEmpty(teamBylineAndMajors)) {
                teamBylineAndMajors.parallelStream().forEach(teamBylineAndMajor -> {
                    PlanIndexDTO planIndexDTO = new PlanIndexDTO();

                    // 查询已完成数量、未完成数量
                    planIndexDTO = repairPoolMapper.getNumByTimeAndOrgCode(teamBylineAndMajor.getOrgCode(), time[0], time[1]);

                    // 填充班组名称
                    planIndexDTO.setTeamName(teamBylineAndMajor.getDepartName());

                    // 计算已检占比
                    if (planIndexDTO.getSum() <= 0 || planIndexDTO.getFinish() <= 0) {
                        planIndexDTO.setFinishRate("0%");
                    } else {
                        double d = new BigDecimal((double) planIndexDTO.getFinish() * 100 / planIndexDTO.getSum()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        planIndexDTO.setFinishRate(d + "%");
                    }

                    // 计算未检占比
                    if (planIndexDTO.getSum() <= 0 || planIndexDTO.getUnfinish() <= 0) {
                        planIndexDTO.setUnfinishRate("0%");
                    } else {
                        double d = new BigDecimal((double) planIndexDTO.getUnfinish() * 100 / planIndexDTO.getSum()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                        planIndexDTO.setUnfinishRate(d + "%");
                    }

                    result.add(planIndexDTO);
                });
            }
        }
        return result;
    }


    /**
     * 根据类型计算开始时间和结束时间
     *
     * @param type
     * @return
     */
    private Date[] getTimeByType(String type) {
        if (StrUtil.isNotEmpty(type)) {

            // 本周
            if (InspectionConstant.THIS_WEEK_1.equals(type)) {
                Date date = new Date();
                DateTime beginDate = DateUtil.beginOfWeek(date);
                DateTime endDate = DateUtil.endOfWeek(date);
                return new Date[]{beginDate, endDate};
            }

            // 上周
            if (InspectionConstant.LAST_WEEK_2.equals(type)) {
                DateTime dateTime = DateUtil.lastWeek();
                DateTime beginDate = DateUtil.beginOfWeek(dateTime);
                DateTime endDate = DateUtil.endOfWeek(dateTime);
                return new Date[]{beginDate, endDate};
            }

            // 本月
            if (InspectionConstant.THIS_MONTH_3.equals(type)) {
                Date date = new Date();
                DateTime beginDate = DateUtil.beginOfMonth(date);
                DateTime endDate = DateUtil.endOfMonth(date);
                return new Date[]{beginDate, endDate};
            }

            // 上月
            if (InspectionConstant.LAST_MONTH_4.equals(type)) {
                DateTime dateTime = DateUtil.lastMonth();
                DateTime beginDate = DateUtil.beginOfMonth(dateTime);
                DateTime endDate = DateUtil.endOfMonth(dateTime);
                return new Date[]{beginDate, endDate};
            }
        }
        return new Date[0];
    }

    /**
     * 功能：班组画像
     *
     * @param type 类型:1：本周，2：上周，3：本月， 4：上月
     * @return
     */
    public List<TeamPortraitDTO> getTeamPortrait(Integer type) {
        //获取所有班组
        List<TeamPortraitDTO> allSysDepart = bigScreenPlanMapper.getAllSysDepart();
        List<TeamPortraitDTO> teamPortraitDTOS = new ArrayList<>();
        if (CollUtil.isNotEmpty(allSysDepart)) {
            for (TeamPortraitDTO sysDepartModel : allSysDepart) {
                //找到没有作为父节点的组织结构
                Optional<TeamPortraitDTO> first = allSysDepart.stream().filter(a -> a.getTeamId().equals(sysDepartModel.getParentId())).findFirst();
                if (!first.isPresent()) {
                    teamPortraitDTOS.add(sysDepartModel);
                }
            }
        }

        if (CollUtil.isNotEmpty(teamPortraitDTOS)) {
            int i = 0;
            for (TeamPortraitDTO teamPortraitDTO : teamPortraitDTOS) {
              //找到当前班组关联的工区信息
                List<TeamPortraitDTO> workAreaById = bigScreenPlanMapper.getWorkAreaByCode(teamPortraitDTO.getTeamCode());
                if (CollUtil.isNotEmpty(workAreaById)) {
                    List<String> teamLineName = workAreaById.stream().map(TeamPortraitDTO::getTeamLeaderName).collect(Collectors.toList());
                    List<String> position = workAreaById.stream().map(TeamPortraitDTO::getPosition).collect(Collectors.toList());
                    int num = 0;
                    for (TeamPortraitDTO portraitDTO : workAreaById) {
                        num = num + portraitDTO.getStationNum();
                    }
                    teamPortraitDTO.setTeamLineName(CollUtil.join(teamLineName,","));
                    teamPortraitDTO.setPosition(CollUtil.join(position, ","));
                    teamPortraitDTO.setStationNum(num);
                }
                //获取当前值班人员
                // 班组的人员
                List<LoginUser> userList = sysBaseAPI.getUserPersonnel(teamPortraitDTO.getTeamId());
                String today= DateUtil.today();
                List<String> onDuty = bigScreenPlanMapper.getOnDuty(today, userList);
                if (CollUtil.isNotEmpty(onDuty)) {
                    teamPortraitDTO.setStaffOnDuty(CollUtil.join(onDuty,","));
                }

                Date[] timeByType = getTimeByType(String.valueOf(type));
                if (timeByType.length > 0) {
                    //获取一周内的班组平均维修响应时间
                    List<RepairRecordDetailDTO> repairDuration = bigScreenPlanMapper.getRepairDuration(userList,timeByType[0],timeByType[1]);
                    long l = 0;
                    for (RepairRecordDetailDTO repairRecordDetailDTO : repairDuration) {
                        // 响应时长： 接收到任务，开始维修时长
                        Date receviceTime = repairRecordDetailDTO.getReceviceTime();
                        Date startTime = repairRecordDetailDTO.getStartTime();
                        Date time = repairRecordDetailDTO.getEndTime();
                        if (Objects.nonNull(startTime) && Objects.nonNull(receviceTime)) {
                            long between = DateUtil.between(receviceTime, startTime, DateUnit.MINUTE);
                            between = between == 0 ? 1: between;
                            l = l + between;
                        }
                        if (Objects.nonNull(startTime) && Objects.nonNull(time)) {
                            long between = DateUtil.between(time, startTime, DateUnit.MINUTE);
                            between = between == 0 ? 1: between;
                            l = l + between;
                        }
                    }
                    int size = repairDuration.size();
                    BigDecimal bigDecimal = new BigDecimal(l);
                    if (size != 0) {
                        BigDecimal bigDecimal1 = new BigDecimal(size);
                        String s = bigDecimal.divide(bigDecimal1, 0, BigDecimal.ROUND_HALF_UP).toString();
                        teamPortraitDTO.setAverageTime(s);
                    } else {
                        teamPortraitDTO.setAverageTime("0");
                    }

                    //获取维修工时

                    //获取巡检工时
                    Map<String, BigDecimal> patrolUserHours = patrolApi.getPatrolUserHours(type, teamPortraitDTO.getTeamId());
                    if (CollUtil.isNotEmpty(patrolUserHours)) {
                        BigDecimal sum = new BigDecimal("0.00");
                        for (Map.Entry<String, BigDecimal> vo : patrolUserHours.entrySet()) {
                            BigDecimal value = vo.getValue();
                            sum = sum.add(value);
                        }
                        teamPortraitDTO.setPatrolTotalTime(sum.setScale(0, BigDecimal.ROUND_HALF_UP));
                    } else {
                        teamPortraitDTO.setPatrolTotalTime(new BigDecimal(0));
                    }


                    //获取所有检修任务人员总工时和所有同行人总工时
                    Long faultTotalTime1 = bigScreenPlanMapper.getInspecitonTotalTime(userList, timeByType[0], timeByType[1]);
                    Long faultTotalTime2 = bigScreenPlanMapper.getInspecitonTotalTimeByPeer(userList, timeByType[0], timeByType[1]);
                    long time = 0L;
                    if (faultTotalTime1 != null) {
                         time = time + faultTotalTime1;
                    }
                     if (faultTotalTime2 != null) {
                         time = time + faultTotalTime2;
                    }
                    BigDecimal decimal = new BigDecimal(1.0 * time / 3600).setScale(0, BigDecimal.ROUND_HALF_UP);
                    teamPortraitDTO.setInspecitonTotalTime(decimal);
                }
            }
        }



        return teamPortraitDTOS;
    }

    /**
     * 功能：班组画像-详情
     *
     * @param type 类型:1：本周，2：上周，3：本月， 4：上月
     * @return
     */
    public TeamWorkingHourDTO getTeamPortraitDetails(Integer type, String teamId, Integer pageNo, Integer pageSize) {
        TeamWorkingHourDTO teamWorkingHourDTO = new TeamWorkingHourDTO();
        //找到当前班组关联的工区信息
        List<TeamPortraitDTO> workAreaById = bigScreenPlanMapper.getWorkAreaById(teamId);
        if (CollUtil.isNotEmpty(workAreaById)) {
            List<String> position = workAreaById.stream().map(TeamPortraitDTO::getPosition).collect(Collectors.toList());
            List<String> siteName = workAreaById.stream().map(TeamPortraitDTO::getSiteName).collect(Collectors.toList());
            int num = 0;
            StringBuilder jurisdiction = new StringBuilder();
            for (TeamPortraitDTO portraitDTO : workAreaById) {
                num = num + portraitDTO.getStationNum();
                jurisdiction.append(portraitDTO.getSiteName()).append(":");
                //获取工区管辖范围
                List<TeamWorkAreaDTO> stationDetails = bigScreenPlanMapper.getStationDetails(portraitDTO.getWorkAreaCode());
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
                        if (jurisdiction.length() > 0)
                        {
                            // 截取字符，去调最后一个，
                            jurisdiction.deleteCharAt(jurisdiction.length() - 1);
                        }
                    }
                }
                jurisdiction.append("共").append(stationDetails.size()).append("站；");
            }
            if (jurisdiction.length() > 0)
            {
                // 截取字符,去掉最后一个；
                jurisdiction.deleteCharAt(jurisdiction.length() - 1);
            }
            teamWorkingHourDTO.setPositionName(CollUtil.join(position, ","));
            teamWorkingHourDTO.setSiteName(CollUtil.join(siteName, ","));
            teamWorkingHourDTO.setStationNum(num);
            teamWorkingHourDTO.setJurisdiction(jurisdiction.toString());
        }

        // 班组的人员
        Page<TeamUserDTO> page = new Page<>(pageNo, pageSize);
        List<TeamUserDTO> userList = bigScreenPlanMapper.getUserList(page, teamId);

        if (CollUtil.isNotEmpty(userList)) {

            //获取巡检任务人员个人总工时和同行人个人总工时
            Map<String, BigDecimal> patrolUserHours = patrolApi.getPatrolUserHours(type, teamId);
            //获取检修任务人员个人总工时和同行人个人总工时
            Map<String, BigDecimal> map = new HashMap<>();
            Date[] timeByType = getTimeByType(String.valueOf(type));
            Map<String, Long> collect1 = new HashMap<>();
            Map<String, Long> collect2 = new HashMap<>();

            if (timeByType.length > 0) {
                List<TeamUserDTO> reconditionTime = bigScreenPlanMapper.getReconditionTime(userList, timeByType[0], timeByType[1]);
                List<TeamUserDTO> reconditionTimeByPeer = bigScreenPlanMapper.getReconditionTimeByPeer(userList, timeByType[0], timeByType[1]);
                collect1 = reconditionTime.stream().collect(Collectors.toMap(TeamUserDTO::getUserId,
                        v -> ObjectUtil.isEmpty(v.getInspecitonTotalTime()) ? 0L : v.getInspecitonTotalTime(), (a, b) -> a));

                collect2 = reconditionTimeByPeer.stream().collect(Collectors.toMap(TeamUserDTO::getUserId,
                        v -> ObjectUtil.isEmpty(v.getInspecitonTotalTime()) ? 0L : v.getInspecitonTotalTime(), (a, b) -> a));

            }

            for (TeamUserDTO teamUserDTO : userList) {
                //获取工作年限
                Date workingTime = teamUserDTO.getWorkingTime();
                LocalDate startDate = workingTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Date date = DateUtil.date();
                LocalDate endDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Period p = Period.between(startDate, endDate);
                teamUserDTO.setWorkingYears(p.getYears() +"年"+p.getMonths()+"个月");
                //获取维修总工时

                //获取检修总工时
                Long hours = collect1.get(teamUserDTO.getUserId());
                Long peerHours = collect2.get(teamUserDTO.getUserId());
                long time = 0L;
                if (hours != null) {
                    time = time + hours;
                }
                if (peerHours != null) {
                    time = time + peerHours;
                }
                BigDecimal decimal = new BigDecimal(1.0 * time / 3600).setScale(0, BigDecimal.ROUND_HALF_UP);
                teamUserDTO.setFaultTotalTime(decimal);

                //获取巡检总工时
                BigDecimal bigDecimal = patrolUserHours.get(teamUserDTO.getUserId());
                teamUserDTO.setPatrolTotalTime(bigDecimal);

            }
        }
        page.setRecords(userList);
        teamWorkingHourDTO.setTeamUserDTOS(page);
        return teamWorkingHourDTO;
    }


}
