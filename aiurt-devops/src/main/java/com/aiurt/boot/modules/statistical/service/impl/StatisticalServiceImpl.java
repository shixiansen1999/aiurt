package com.aiurt.boot.modules.statistical.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.boot.common.constant.CommonConstant;
import com.aiurt.boot.common.util.DateUtils;
import com.aiurt.boot.modules.fault.mapper.FaultMapper;
import com.aiurt.boot.modules.fault.service.IFaultService;
import com.aiurt.boot.modules.manage.entity.Line;
import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.service.ILineService;
import com.aiurt.boot.modules.manage.service.IStationService;
import com.aiurt.boot.modules.patrol.constant.PatrolConstant;
import com.aiurt.boot.modules.patrol.entity.Patrol;
import com.aiurt.boot.modules.patrol.entity.PatrolPool;
import com.aiurt.boot.modules.patrol.entity.PatrolTask;
import com.aiurt.boot.modules.patrol.mapper.PatrolTaskMapper;
import com.aiurt.boot.modules.patrol.param.PatrolPoolParam;
import com.aiurt.boot.modules.patrol.param.PatrolTaskDetailParam;
import com.aiurt.boot.modules.patrol.param.TreeParam;
import com.aiurt.boot.modules.patrol.service.IPatrolPoolService;
import com.aiurt.boot.modules.patrol.service.IPatrolService;
import com.aiurt.boot.modules.patrol.service.IPatrolTaskReportService;
import com.aiurt.boot.modules.patrol.service.IPatrolTaskService;
import com.aiurt.boot.modules.patrol.vo.PatrolPoolContentTreeVO;
import com.aiurt.boot.modules.patrol.vo.PatrolTaskVO;
import com.aiurt.boot.modules.patrol.vo.TaskDetailVO;
import com.aiurt.boot.modules.repairManage.mapper.RepairTaskMapper;
import com.aiurt.boot.modules.schedule.mapper.ScheduleRecordMapper;
import com.aiurt.boot.modules.statistical.service.StatisticalService;
import com.aiurt.boot.modules.statistical.vo.*;
import com.aiurt.boot.modules.system.entity.SysDepart;
import com.aiurt.boot.modules.system.entity.SysUser;
import com.aiurt.boot.modules.system.model.DepartScheduleModel;
import com.aiurt.boot.modules.system.service.ISysDepartService;
import com.aiurt.boot.modules.system.service.ISysUserService;
import com.aiurt.boot.modules.system.util.TimeUtil;
import com.aiurt.boot.modules.system.vo.SysDepartScheduleVo;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qian
 * @version 1.0
 * @date 2021/11/20 11:24
 */
@Service
@RequiredArgsConstructor
public class StatisticalServiceImpl extends ServiceImpl<PatrolTaskMapper, PatrolTask> implements StatisticalService {

    private final RepairTaskMapper repairTaskMapper;

    private final ISysUserService sysUserService;

    private final IFaultService faultService;

    private final IPatrolTaskService patrolTaskService;

    private final PatrolTaskMapper patrolTaskMapperp;

    private final ScheduleRecordMapper scheduleRecordMapper;

    private final IPatrolTaskReportService patrolTaskReportService;

    private final IPatrolPoolService patrolPoolService;

    private final IPatrolService patrolService;

    @Resource
    private final FaultMapper faultMapper;
    @Autowired
    private final IStationService stationService;
    @Autowired
    private final ISysDepartService sysDepartService;
    @Resource
    private final PatrolTaskMapper patrolTaskMapper;
    @Autowired
    private final ILineService lineService;

    @Override
    public Result<List<StatisticsResultVO>> getCount(StatisticsVO statisticsVO) {

//        final ArrayList<StatisticsResultVO> result = new ArrayList<>();
//
//
//
//
//        //根据班组ID和姓名查询下面的用户
//        final String teamId = statisticsVO.getTeamId();
//        List<SysUser> sysUserList = sysUserService.list(new QueryWrapper<SysUser>().eq("org_id", teamId).like("username",statisticsVO.getUserName()));
//
//        //总检修数
//        final List<String> userNameList = sysUserList.stream().map(SysUser::getRealname).collect(Collectors.toList());
//        final List<UserAndAmountVO> repairTaskMapperStatistical = repairTaskMapper.getStatistical(statisticsVO.getStartTime(), statisticsVO.getEndTime(), statisticsVO.getUserName(), userNameList);
//        //总巡检数
//        final Map<String, Integer> map = patrolTaskService.getUserNameMap(statisticsVO);
//        //总巡检数
//        if (repairTaskMapperStatistical.size() > 0) {
//            for (StatisticsResultVO vo : result) {
//                for (UserAndAmountVO userAndAmountVO : repairTaskMapperStatistical) {
//                    if (StrUtil.equals(userAndAmountVO.getUserName(), vo.getUserName())) {
//                        vo.setRepairAmount(userAndAmountVO.getAmount());
//                    }
//                }
//            }
//        }
//        //故障处理时长
//        final Map<String, Long> faultDuration = faultService.getFaultDuration(statisticsVO);
//        if (faultDuration.size() > 0) {
//            for (StatisticsResultVO vo : result) {
//                for (Map.Entry<String, Long> m : faultDuration.entrySet()) {
//                    if (StrUtil.equals(vo.getUserName(), m.getKey())) {
//                        vo.setFaultHandleTimeAmount(Math.toIntExact(m.getValue()));
//                    }
//                }
//            }
//        }
//        //配合施工人次
//        final Map<String, Integer> assortNum = faultService.getAssortNum(statisticsVO);
////        if (faultDuration.size() > 0) {
////            for (StatisticsResultVO vo : result) {
////                for (Map.Entry<String, Integer> m : assortNum.entrySet()) {
////                    if (StrUtil.equals(vo.getUserName(), m.getKey())) {
////                        vo.setFaultHandleTimeAmount(m.getValue());
////                    }
////                }
////            }
////        }
//
//        for (Map.Entry<String, Integer> m : map.entrySet()) {
//            final StatisticsResultVO vo = new StatisticsResultVO();
//            vo.setRepairAmount(0);
//            vo.setFaultHandleAmount(0);
//            vo.setFaultHandleTimeAmount(0);
//            vo.setCooperateBuildAmount(0);
//            vo.setUserName(m.getKey());
//            vo.setPatrolAmount(m.getValue());//总巡检数
//            result.add(vo);
//        }
//        //故障
////        final List<UserAndAmountVO> faultPersonDetail = faultService.getFaultPersonDetail(statisticsVO);
////        if (faultPersonDetail.size() > 0) {
////            for (StatisticsResultVO vo : result) {
////                for (UserAndAmountVO userAndAmountVO : repairTaskMapperStatistical) {
////                    if (StrUtil.equals(userAndAmountVO.getUserName(), vo.getUserName())) {
////                        vo.setFaultHandleAmount(userAndAmountVO.getAmount());
////                    }
////                }
////            }
////        }
//        //故障处理时长


        return Result.ok(patrolTaskMapper.getCount(statisticsVO.getStartTime(),statisticsVO.getEndTime(),
                statisticsVO.getTeamId(),statisticsVO.getUserName()));
    }

    @Override
    public Result<List<StatisticsPatrolVO>> getPatrolCountGroupByOrg(String lineCode) {
        Date startTime = DateUtil.beginOfWeek(new Date());
        Date endTime = DateUtil.endOfWeek(new Date());
        List<StatisticsPatrolVO> patrolCountGroupByOrg = patrolTaskMapperp.getPatrolCountGroupByOrg(startTime, endTime, lineCode);
        return Result.ok(patrolCountGroupByOrg);
    }

    @Override
    public Result<List<StatisticsRepairVO>> getRepairCountGroupByOrg(String lineCode) {
        Date startTime = DateUtil.beginOfWeek(new Date());
        Date endTime = DateUtil.endOfWeek(new Date());
        List<StatisticsRepairVO> repairCountList = repairTaskMapper.getRepairCountGroupByOrg(startTime, endTime, lineCode);
        return Result.ok(repairCountList);
    }

    @Override
    public PageVo getWeeklyFaultStatistics(String lineCode, Integer pageNo, Integer pageSize, DateTime startTime, DateTime endTime) {
        IPage<FaultStatisticsModal> iPage = new Page<>(pageNo, pageSize);
        FaultStatisticsVo vo = new FaultStatisticsVo();
        PageVo<FaultStatisticsVo> pageVo = new PageVo<>();

        iPage = faultMapper.selectFaultStatisticsModal(iPage, startTime, endTime, lineCode);
        pageVo.setPage(iPage);
        vo.setUnCompleteNum(faultMapper.countUnCompleteNumByLineCode(startTime, endTime, lineCode));
        vo.setFaultNum(Long.valueOf(iPage.getTotal()).intValue());
        List<FaultStatisticsModal> modalList = iPage.getRecords();
        modalList.forEach(model -> {
            List<String> userIds = new ArrayList<>();
            String maintainer;
            if (ObjectUtil.isNotEmpty(model.getAppointUserId())) {
                userIds.add(model.getAppointUserId());
            }
            if (ObjectUtil.isNotEmpty(model.getParticipateIds())) {
                userIds.addAll(Arrays.stream(model.getParticipateIds().split(",")).collect(Collectors.toList()));
            }
            if (ObjectUtil.isNotEmpty(userIds)) {
                Collection<SysUser> sysUsers = sysUserService.listByIds(userIds);
                maintainer = sysUsers.stream().map(SysUser::getRealname).distinct().collect(Collectors.joining(","));
                model.setMaintainer(maintainer);
            }
        });
        vo.setModalList(modalList);
        pageVo.setRecords(vo);
        return pageVo;
    }

    @Override
    public PageVo<RepairStatisticVo> getRepairStatisticByLineCodeAndTime(String lineCode, Integer pageNo, Integer pageSize, DateTime startTime, DateTime endTime) {
        IPage<RepairTaskVo> iPage = new Page<RepairTaskVo>(pageNo, pageSize);
        PageVo<RepairStatisticVo> pageVo = new PageVo<>();
        RepairStatisticVo repairStatisticVo = new RepairStatisticVo();
        iPage = repairTaskMapper.getRepairTaskVos(iPage, lineCode, startTime, endTime);
        pageVo.setPage(iPage);
        DateTime now = DateUtil.date();
        iPage.getRecords().forEach(repairTaskVo -> {
            Integer status = repairTaskVo.getStatus();
            if (status > 0 ) {//状态为验收时为已完成
                repairTaskVo.setRepairStatus(1);//1 已完成
            } else if (now.getTime() > repairTaskVo.getEndTime().getTime() && status == 0) {
                repairTaskVo.setRepairStatus(2);//2 漏检
            }  else if (now.getTime() < repairTaskVo.getEndTime().getTime() && status == 0) {
                repairTaskVo.setRepairStatus(3);//待执行
            }
        });
        repairStatisticVo.setRepairTaskList(iPage.getRecords());
        repairStatisticVo.setRepairNum(Long.valueOf(iPage.getTotal()).intValue());
        repairStatisticVo.setCompleteNum(repairTaskMapper.getCompleteRepairNum(lineCode, startTime, endTime));
        pageVo.setRecords(repairStatisticVo);
        return pageVo;
    }

    @Override
    public SysDepartScheduleVo getSysDepartSchedulePageVo(String lineCode, Integer pageNo, Integer pageSize, DateTime now) {
        SysDepartScheduleVo vo = new SysDepartScheduleVo();
        String date = DateUtils.format(now, "yyyy-MM-dd");
        int dutyUserNum = 0;
        int userNum = 0;
        List<String> orgIdList = new ArrayList<>();
        if (StringUtils.isBlank(lineCode)) {
            LambdaQueryWrapper<SysDepart> qw = new LambdaQueryWrapper<SysDepart>().eq(SysDepart::getDelFlag, 0)
                    .orderByAsc(SysDepart::getDepartName);
            List<SysDepart> departList = sysDepartService.list(qw);
            orgIdList = departList.stream().map(SysDepart::getId).collect(Collectors.toList());
        }
        if (StringUtils.isNotBlank(lineCode)) {
            Integer lineId = lineService.getOne(new LambdaQueryWrapper<Line>().eq(Line::getLineCode, lineCode)).getId();
            LambdaQueryWrapper<Station> qw = new LambdaQueryWrapper<Station>().eq(Station::getLineId, lineId)
                    .eq(Station::getDelFlag, 0).ne(Station::getTeamId, "").groupBy(Station::getTeamId);
            orgIdList = stationService.list(qw).stream().map(Station::getTeamId).distinct().collect(Collectors.toList());

        }
        vo.setDepartNum(orgIdList.size());
        List<DepartScheduleModel> list = sysUserService.selectUserScheduleByOrgIdsAndDate(orgIdList, date);
        for (DepartScheduleModel model : list) {
            userNum += model.getNum();
            dutyUserNum += model.getDutyUsers().size();
        }
        vo.setDutyUserNum(dutyUserNum);
        vo.setUserNum(userNum);
        vo.setDepartScheduleModelList(list);
        return vo;
    }

    @Override
    public PageVo getWeeklyPatrolStatisticPageVo(Integer pageNo, Integer pageSize, List<Integer> lineIds,String lineCode, DateTime now) {
        PageVo pageVo = new PageVo();
        PatrolStatisticVo vo = new PatrolStatisticVo();
        //1.获取本周初始和结束时间
        DateTime startTime = DateUtil.beginOfWeek(now);
        Calendar calendar = startTime.toCalendar();
        calendar.add(Calendar.DATE, -1);
        DateTime start = DateUtil.date(calendar.getTime());
        DateTime endTime = DateUtil.endOfWeek(now);
        PatrolPoolParam param = new PatrolPoolParam();
        param.setStartTime(startTime).setEndTime(endTime).setPageNo(pageNo).setPageSize(pageSize);
        param.setStationIds(lineIds);
        IPage<PatrolTaskVO> page = new Page<>(param.getPageNo(), param.getPageSize());
        page = patrolTaskMapper.selectPageList2(page, param);
        pageVo.setPage(page);
//        vo.setWeeklyIgnoreNum(patrolTaskMapper.countIgnoreNumByTimeAndLineIds(lineIds, startTime, endTime));
        List<PatrolTaskVO> list = page.getRecords();
        for (PatrolTaskVO record : list) {
            if ((ObjectUtil.isNotEmpty(record.getIgnoreStatus()) && record.getIgnoreStatus() == 1) && !(Integer.valueOf(record.getTactics())==2&&StringUtils.isNotBlank(record.getIgnoreContent()))) {
                record.setPatrolFlag(3);//漏检
            } else if (ObjectUtil.isNotEmpty(record.getWarningStatus()) && record.getWarningStatus() == 1) {
                record.setPatrolFlag(0);//异常状态
            } else if ((ObjectUtil.isNotEmpty(record.getTaskStatus()) && record.getTaskStatus() == 1)||((Integer.valueOf(record.getTactics())==2)&&StringUtils.isNotEmpty(record.getIgnoreContent()))) {
                record.setPatrolFlag(2);//已完成
            } else if (now.getTime() <= record.getExecutionTime().getTime()) {
                record.setPatrolFlag(1);//待执行
            } else if (now.getTime() > record.getExecutionTime().getTime() && record.getTaskStatus() != 1) {
                record.setPatrolFlag(3);//漏检
            } else {
                record.setPatrolFlag(1);//待执行
            }
        }
        List<SysDepart> sysDepartByLineCode = sysDepartService.getSysDepartByLineCode(lineCode);
        Integer weeklyPatrolNum = 0;
        Integer weeklyCompleteNum = 0;
        Integer weeklyIgnoreNum = 0;
        for (SysDepart sysDepart : sysDepartByLineCode) {
            //查询每个班组的计划数，完成数，漏检数
            Integer planNum = patrolTaskMapper.countPatrolNumByOrgIdAndTime(sysDepart.getId(),startTime,endTime);
            weeklyPatrolNum+=planNum;
            //完成数= 本周的完成任务数+本周处理过的一周两次任务数
            Integer completedNum = patrolTaskService.countCompletedPatrolNumByOrgIdAndTime(sysDepart.getId(),startTime,endTime);
            weeklyCompleteNum+=completedNum;
            //本周漏检 = 漏检任务数-处理过的一周两次任务数
            Integer ignoreNum = patrolTaskService.countIgnoredPatrolNumByOrgIdAndTime(sysDepart.getId(), start, endTime);
            weeklyIgnoreNum+=ignoreNum;
        }
        vo.setWeeklyCompleteNum(weeklyCompleteNum);
        vo.setWeeklyPatrolNum(weeklyPatrolNum);
        vo.setWeeklyIgnoreNum(weeklyIgnoreNum);
        vo.setPatrolTaskVoList(list);
        pageVo.setRecords(vo);
        return pageVo;
    }


    @Override
    public PageVo getPatrolStatisticPageVo(Integer pageNo, Integer pageSize, String lineCode,DateTime now) {
        //lineIds其实是stationIds，t_patrol_pool中的line_id字段复用了，实际是station_id
        List<Integer> lineIds = stationService.getIdsByLineCode(lineCode);
        PageVo<PatrolStatisticVo> pageVo = getWeeklyPatrolStatisticPageVo(pageNo, pageSize, lineIds,lineCode,now);
        PatrolStatisticVo vo = pageVo.getRecords();
        DateTime startTime = DateUtil.beginOfWeek(now);
        DateTime endTime = DateUtil.endOfWeek(now);
        //计算本周巡视异常数
        vo.setExceptionPatrolNum(patrolTaskMapper.countExceptionNumByTimeAndLineIds(lineIds, startTime, endTime));
        //计算今日巡视数
        vo.setIntradayPatrolNum(patrolTaskMapper.countPatrolNumByTimeAndLineIds(lineIds, DateUtil.beginOfDay(now), DateUtil.endOfDay(now)));
        //计算今日巡视完成数
        vo.setIntradayCompleteNum(patrolTaskMapper.countCompleteNumByTimeAndLineIds(lineIds, DateUtil.beginOfDay(now), DateUtil.endOfDay(now)));
        return pageVo;
    }

    @Override
    public Result<List<StatisticsFaultWayVO>> getFaultCountGroupByWay(String lineCode) {
        Date startTime = DateUtil.beginOfWeek(new Date());
        Date endTime = DateUtil.endOfWeek(new Date());
        List<StatisticsFaultWayVO> repairCountList = faultService.getFaultCountGroupByWay(startTime, endTime, lineCode);
        int ziNum = 0;
        int baoNum = 0;
        List<StatisticsFaultWayVO> voList = new ArrayList<>();
        StatisticsFaultWayVO ziVo = new StatisticsFaultWayVO();
        ziVo.setRepairWay("自检");
        StatisticsFaultWayVO baoVo = new StatisticsFaultWayVO();
        baoVo.setRepairWay("报修");
        for (StatisticsFaultWayVO vo : repairCountList) {
            if (vo.getRepairWay().equals("自检")){
                ziNum = vo.getCount();
            }else {
                baoNum = vo.getCount();
            }
        }
        ziVo.setCount(ziNum);
        baoVo.setCount(baoNum);
        voList.add(ziVo);
        voList.add(baoVo);
        return Result.ok(voList);
    }

    @Override
    public Result<List<StatisticsFaultStatusVO>> getFaultCountGroupByStatus(String lineCode) {
        Date startTime = DateUtil.beginOfWeek(new Date());
        Date endTime = DateUtil.endOfWeek(new Date());
        List<StatisticsFaultStatusVO> faultStatusList = faultService.getFaultCountGroupByStatus(startTime, endTime, lineCode);
        int noComplete = 0;
        int complete = 0;
        List<StatisticsFaultStatusVO> faultStatusVOList = new ArrayList();
        for (StatisticsFaultStatusVO statisticsFaultStatusVO : faultStatusList) {
            if (statisticsFaultStatusVO.getStatus().equals("1") || statisticsFaultStatusVO.getStatus().equals("0")) {
                noComplete = noComplete + statisticsFaultStatusVO.getCount();
            } else {
                complete = statisticsFaultStatusVO.getCount();
            }
        }
        StatisticsFaultStatusVO noCompleteFault = new StatisticsFaultStatusVO();
        noCompleteFault.setStatus("未完成");
        noCompleteFault.setCount(noComplete);
        faultStatusVOList.add(noCompleteFault);
        StatisticsFaultStatusVO completeFault = new StatisticsFaultStatusVO();
        completeFault.setStatus("已完成");
        completeFault.setCount(complete);
        faultStatusVOList.add(completeFault);
        return Result.ok(faultStatusVOList);
    }

    @Override
    public Result<Map<String, List<StatisticsFaultLevelVO>>> getFaultGroupByLevel(String lineCode) {
        Date startTime = DateUtil.beginOfWeek(new Date());
        Date endTime =  DateUtil.endOfWeek(new Date());
        List<StatisticsFaultLevelVO> faultLevelList = faultService.getFaultGroupByLevel(startTime, endTime, lineCode);
        Map<String, List<StatisticsFaultLevelVO>> faultMap = faultLevelList.stream().collect(Collectors.groupingBy(StatisticsFaultLevelVO::getLevel));
        return Result.ok(faultMap);
    }

    @Override
    public Result<List<StatisticsFaultMonthVO>> getFaultCountGroupByMonth(String lineCode) {
        Date startTime = DateUtil.beginOfYear(new Date());
        Date endTime = DateUtil.endOfYear(new Date());
        List<StatisticsFaultMonthVO> faultMonthList = faultService.getFaultCountGroupByMonth(startTime, endTime, lineCode);
        return Result.ok(faultMonthList);
    }

    @Override
    public Result<List<StatisticsFaultSystemVO>> getFaultCountGroupBySystem(String lineCode, Integer month) {
        Date startTime = new Date();
        Date endTime = new Date();
        if (ObjectUtil.isNotEmpty(month) && month != 0) {
            startTime = TimeUtil.getFirstDay(month);
            endTime = TimeUtil.getLastDay(month);
        } else {
            startTime = TimeUtil.getCurrentYearStartTime();
            endTime = TimeUtil.getCurrentYearEndTime();
        }
        List<StatisticsFaultSystemVO> faultMonthList = faultService.getFaultCountGroupBySystem(startTime, endTime, lineCode);
        return Result.ok(faultMonthList);
    }

    @Override
    public PageVo getRepairStatistic(String lineCode, Integer pageNo, Integer pageSize, DateTime now) {
        DateTime startTime = DateUtil.beginOfWeek(now);
        DateTime endTime = DateUtil.endOfWeek(now);
        PageVo<RepairStatisticVo> pageVo = this.getRepairStatisticByLineCodeAndTime(lineCode, pageNo, pageSize, startTime, endTime);
        RepairStatisticVo vo = pageVo.getRecords();
        int ignoreNum = 0;
        for (RepairTaskVo taskVo : vo.getRepairTaskList()) {
            if (taskVo.getRepairStatus() == 2) {
                ignoreNum += 1;
            }
        }
        vo.setWeeklyIgnoreNum(ignoreNum);
        vo.setIntradayRepairNum(repairTaskMapper.getRepairTaskNum(DateUtil.beginOfDay(now), DateUtil.endOfDay(now)));
        return pageVo;
    }

    @Override
    public Result<StatisticsFaultCountVO> getFaultCountAndDetails(String lineCode) {
        StatisticsFaultCountVO faultCountVO = faultService.getFaultCountAndDetails(lineCode);
        return Result.ok(faultCountVO);
    }

    @Override
    public List<SysUser> getDutyUsers(String lineCode, String orgId, DateTime dateTime) {
        String date = DateUtils.format(dateTime, "yyyy-MM-dd");
        List<String> orgIds = new ArrayList<>();
        if (StringUtils.isNotBlank(orgId)){
            orgIds.add(orgId);
            List<SysUser> dutyUsers = scheduleRecordMapper.getDutyUserListByOrgIdsAndDate(date, orgIds);
            return dutyUsers;
        }
        List<SysDepart> departList = sysDepartService.getSysDepartByLineCode(lineCode);
        orgIds = departList.stream().map(SysDepart::getId).collect(Collectors.toList());
        List<SysUser> dutyUsers = scheduleRecordMapper.getDutyUserListByOrgIdsAndDate(date, orgIds);
        return dutyUsers;
    }

    @Override
    public List<FaultStatisticsModal> getUncompletedFault(String lineCode, DateTime startTime, DateTime endTime) {

        List<FaultStatisticsModal> modalList = faultMapper.getUncompletedFault(startTime, endTime, lineCode);
        this.addFaultMaintainer(modalList);
        return modalList;
    }

    private List<FaultStatisticsModal> addFaultMaintainer(List<FaultStatisticsModal> modalList){
        modalList.forEach(model -> {
            List<String> userIds = new ArrayList<>();
            String maintainer;
            if (ObjectUtil.isNotEmpty(model.getAppointUserId())) {
                userIds.add(model.getAppointUserId());
            }
            if (ObjectUtil.isNotEmpty(model.getParticipateIds())) {
                userIds.addAll(Arrays.stream(model.getParticipateIds().split(",")).collect(Collectors.toList()));
            }
            if (ObjectUtil.isNotEmpty(userIds)) {
                Collection<SysUser> sysUsers = sysUserService.listByIds(userIds);
                maintainer = sysUsers.stream().map(SysUser::getRealname).distinct().collect(Collectors.joining(","));
                model.setMaintainer(maintainer);
            }
        });
        return modalList;
    }

    @Override
    public List<RepairTaskVo> getCompletedRepair(String lineCode, DateTime startTime, DateTime endTime) {
        List<RepairTaskVo> list = repairTaskMapper.getCompletedRepair(lineCode, startTime, endTime);
        list.forEach(repairTaskVo -> {
            repairTaskVo.setRepairStatus(1);
        });
        return list;
    }

    @Override
    public List<RepairTaskVo> getTodayRepair(String lineCode, DateTime now) {
        List<RepairTaskVo> list = repairTaskMapper.getRepairTaskVosByTime(lineCode, DateUtil.beginOfDay(now), DateUtil.endOfDay(now));
        return this.setStatus(list);

    }

    @Override
    public Result<?> detail(HttpServletRequest req, PatrolTaskDetailParam param) {

        Long id = param.getId();
        String code = param.getCode();
        Long poolId = param.getPoolId();


        if (id == null && StringUtils.isBlank(code) && param.getPoolId() == null) {
            return Result.error("id与code不能同时为空");
        }

        PatrolTask patrolTask = null;
        if (id != null) {
            patrolTask = this.getById(id);
        } else if (param.getPoolId() != null) {
            patrolTask = this.lambdaQuery()
                    .eq(PatrolTask::getPatrolPoolId, poolId)
                    .eq(PatrolTask::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .last("limit 1").one();
        } else {
            patrolTask = this.lambdaQuery()
                    .eq(PatrolTask::getCode, code.trim())
                    .eq(PatrolTask::getDelFlag, CommonConstant.DEL_FLAG_0)
                    .last("limit 1").one();
        }

        //返回vo
        TaskDetailVO vo = new TaskDetailVO();

        vo.setTaskStatus(patrolTask != null ? patrolTask.getStatus() : 0);
        if (patrolTask != null) {
            vo.setSpotTestUser(patrolTask.getSpotTestUser());
            vo.setSpotTest(patrolTask.getSpotTest());
            vo.setSpotTestTechnician(patrolTask.getSpotTestTechnician());
            vo.setSpotTestTechnicianId(patrolTask.getSpotTestTechnicianId());
        }
        PatrolPool pool = null;
        if (poolId != null) {
            pool = patrolPoolService.getById(poolId);
        } else {
            pool = patrolPoolService.getById(patrolTask.getPatrolPoolId());
        }

        if (patrolTask != null) {
            //任务表id
            vo.setTaskId(patrolTask.getId());
        }
        if (pool.getStatus() == 1 && patrolTask != null
                && (patrolTask.getStatus() == null || patrolTask.getStatus() == 0)
                && (patrolTask.getIgnoreStatus() == null
                || patrolTask.getIgnoreStatus() == 0)
                ) {
            vo.setFlag(1);
        } else {
            vo.setFlag(0);
        }
        Patrol patrol =null;
        if (ObjectUtil.isNotEmpty(pool)){
            if (StringUtils.isNotEmpty(pool.getPatrolName())){
                LambdaQueryWrapper<Patrol>wrapper= new LambdaQueryWrapper<Patrol>().eq(Patrol::getTitle,pool.getPatrolName()).last("limit 1");
                patrol = patrolService.getOne(wrapper);

            }
        }
        //标题
        vo.setTitle(pool.getPatrolName());
        //巡检表说明
        if (ObjectUtil.isNotEmpty(patrol)){
            vo.setNote(patrol.getNote());
        }
        //车站名称
        vo.setStationName(pool.getLineName());
        //工单编号
        vo.setCode(pool.getCode());
        if (patrolTask != null) {
            //提交时间
            vo.setSubmitTime(patrolTask.getSubmitTime());

            //巡检人及部门
            if (StringUtils.isNotBlank(patrolTask.getStaffIds())) {
                List<SysUser> sysUsers = sysUserService.lambdaQuery()
                        .eq(SysUser::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .in(SysUser::getId, Arrays.asList(patrolTask.getStaffIds().trim().split(PatrolConstant.SPL)))
                        .list();
                if (sysUsers != null && sysUsers.size() > 0) {
                    vo.setStaffName(StringUtils.join(
                            sysUsers.stream().map(SysUser::getRealname).collect(Collectors.toList()),
                            PatrolConstant.SPL));
                }
            }

        }

        //树查询参数
        TreeParam treeParam = new TreeParam();
        treeParam.setPoolId(poolId)
                .setTitle(param.getTitle())
                .setFlag(vo.getFlag());

        //树状查询
        Result<List<PatrolPoolContentTreeVO>> tree = patrolTaskReportService.tree(req, id, treeParam);

        List<PatrolPoolContentTreeVO> list = tree.getResult();
        vo.setList(list);

        SysDepart sysDepart = sysDepartService.getById(pool.getOrganizationId());
        if (sysDepart != null) {
            vo.setOrganizationName(sysDepart.getDepartName());
        }

        return Result.ok(vo);
    }

    @Override
    public List<RepairTaskVo> getWeeklyIgnoreRepair(String lineCode, DateTime now) {
        DateTime startTime = DateUtil.beginOfWeek(now);
        DateTime endTime = DateUtil.endOfWeek(now);
        DateTime today = DateTime.now();
        //查询出未完成的任务
        List<RepairTaskVo> vos = repairTaskMapper.getUncompletedRepair(lineCode, startTime, endTime);
        if (vos.size()<1){
            return null;
        }
        //从中筛选出漏检的
        List<RepairTaskVo> ignoreRepairList = new ArrayList<>();
        vos.forEach(vo->{
            if (today.getTime() > vo.getEndTime().getTime() && vo.getStatus()==0)
                vo.setRepairStatus(2);
                ignoreRepairList.add(vo);
        });
        return ignoreRepairList;
    }

    @Override
    public PatrolStatisticVo getWeeklyPatrolStatistic(String lineCode, DateTime now) {
        DateTime startTime = DateUtil.beginOfWeek(now);
        Calendar calendar = startTime.toCalendar();
        calendar.add(Calendar.DATE, -1);
        DateTime start = DateUtil.date(calendar.getTime());
        DateTime endTime = DateUtil.endOfWeek(now);
        //获取当前线路所包含班组
        List<SysDepart> sysDepartByLineCode = sysDepartService.getSysDepartByLineCode(lineCode);
        List<PatrolTaskStatisticVo>list = new ArrayList<>();
        Integer weeklyPatrolNum = 0;
        Integer weeklyCompleteNum = 0;
        Integer weeklyIgnoreNum = 0;
        for (SysDepart sysDepart : sysDepartByLineCode) {
            //查询每个班组的计划数，完成数，漏检数
            PatrolTaskStatisticVo vo = new PatrolTaskStatisticVo();
            vo.setDepartName(sysDepart.getDepartName());
            vo.setDepartId(sysDepart.getId());
            Integer planNum = patrolTaskMapper.countPatrolNumByOrgIdAndTime(sysDepart.getId(),startTime,endTime);
            weeklyPatrolNum+=planNum;
            vo.setPlanNum(planNum);
            //完成数= 本周的完成任务数+本周处理过的一周两次任务数
            Integer completedNum = patrolTaskService.countCompletedPatrolNumByOrgIdAndTime(sysDepart.getId(),startTime,endTime);
            weeklyCompleteNum+=completedNum;
            vo.setCompleteNum(completedNum);
            //本周漏检 = 漏检任务数-处理过的一周两次任务数
            Integer ignoreNum = patrolTaskService.countIgnoredPatrolNumByOrgIdAndTime(sysDepart.getId(), start, endTime);
            weeklyIgnoreNum+=ignoreNum;
            vo.setIgnoreNum(ignoreNum);
            list.add(vo);
        }
        PatrolStatisticVo statisticVo = new PatrolStatisticVo();
        statisticVo.setPatrolTaskStatisticVoList(list);
        statisticVo.setWeeklyPatrolNum(weeklyPatrolNum);
        statisticVo.setWeeklyCompleteNum(weeklyCompleteNum);
        statisticVo.setWeeklyIgnoreNum(weeklyIgnoreNum);
        return statisticVo;
    }

    @Override
    public List<PatrolTaskVO> getCompletedPatrol(String lineCode, String departId,DateTime now) {
        DateTime startTime = DateUtil.beginOfWeek(now);
        DateTime endTime = DateUtil.endOfWeek(now);
        List<PatrolTaskVO> records = getPatrolTask(lineCode,departId,startTime,endTime);
        List<PatrolTaskVO> vos = new ArrayList<>();
        records.forEach(record -> {
            if ((ObjectUtil.isNotEmpty(record.getTaskStatus()) && record.getTaskStatus() == 1)||((Integer.valueOf(record.getTactics())==2)&&StringUtils.isNotEmpty(record.getIgnoreContent()))) {
                record.setPatrolFlag(2);//已完成
                vos.add(record);
            }
        });
        return vos;
    }

    @Override
    public List<PatrolTaskVO> getIgnoredPatrol(String lineCode, String departId, DateTime now) {
        DateTime startTime = DateUtil.beginOfWeek(now);
        DateTime endTime = DateUtil.endOfWeek(now);
        List<PatrolTaskVO> records = getPatrolTask(lineCode,departId,startTime,endTime);
        List<PatrolTaskVO> vos = new ArrayList<>();
        records.forEach(record -> {
            if ((ObjectUtil.isNotEmpty(record.getIgnoreStatus()) && record.getIgnoreStatus() == 1) && !(Integer.valueOf(record.getTactics())==2&&StringUtils.isNotBlank(record.getIgnoreContent()))) {
                record.setPatrolFlag(3);//漏检
                vos.add(record);
            }
        });
        return vos;
    }

    @Override
    public List<PatrolTaskVO> getExceptionPatrol(String lineCode, DateTime now) {
        DateTime startTime = DateUtil.beginOfWeek(now);
        DateTime endTime = DateUtil.endOfWeek(now);
        List<PatrolTaskVO> records = getPatrolTask(lineCode,null,startTime,endTime);
        List<PatrolTaskVO> vos = new ArrayList<>();
        records.forEach(record -> {
            if (ObjectUtil.isNotEmpty(record.getWarningStatus()) && record.getWarningStatus() == 1) {
                record.setPatrolFlag(0);//异常状态
                vos.add(record);
            }
        });
        return vos;
    }

    @Override
    public List<PatrolTaskVO> getTodayPatrol(String lineCode, DateTime now) {
        DateTime startTime =DateUtil.beginOfDay(now);
        DateTime endTime = DateUtil.endOfDay(now);
        return this.getPatrolByLineCodeAndTime(lineCode,null,startTime,endTime);
    }

    @Override
    public List<RepairTaskVo> getWeeklyPlanRepair(String lineCode, DateTime now) {
        DateTime startTime = DateUtil.beginOfWeek(now);
        DateTime endTime = DateUtil.endOfWeek(now);
        List<RepairTaskVo> list = repairTaskMapper.getRepairTaskVos2(lineCode, startTime, endTime);
        return this.setStatus(list);
    }

    private List<RepairTaskVo>setStatus( List<RepairTaskVo> list){
        //4-11不予验收和不予确认状态归为已完成
        DateTime now = DateTime.now();
        list.forEach(repairTaskVo -> {
            Integer status = repairTaskVo.getStatus();
            if (status > 0 ) {//状态为验收时为已完成
                repairTaskVo.setRepairStatus(1);//1 已完成
            } else if (now.getTime() > repairTaskVo.getEndTime().getTime() && status == 0) {
                repairTaskVo.setRepairStatus(2);//2 漏检
            }  else if (now.getTime() < repairTaskVo.getEndTime().getTime() && status == 0) {
                repairTaskVo.setRepairStatus(3);//待执行
            }
        });
        return list;
    }

    @Override
    public List<PatrolTaskVO> getPlanPatrol(String lineCode, String departId, DateTime now) {
        DateTime startTime = DateUtil.beginOfWeek(now);
        DateTime endTime = DateUtil.endOfWeek(now);
        return this.getPatrolByLineCodeAndTime(lineCode,departId,startTime,endTime);
    }

    private List<PatrolTaskVO> getPatrolByLineCodeAndTime(String lineCode,String departId,DateTime startTime,DateTime endTime){
        DateTime now = DateTime.now();
        List<PatrolTaskVO> records = getPatrolTask(lineCode,departId,startTime,endTime);
        for (PatrolTaskVO record : records) {
            if ((ObjectUtil.isNotEmpty(record.getIgnoreStatus()) && record.getIgnoreStatus() == 1) && !(Integer.valueOf(record.getTactics())==2&&StringUtils.isNotBlank(record.getIgnoreContent()))) {
                record.setPatrolFlag(3);//漏检
            } else if (ObjectUtil.isNotEmpty(record.getWarningStatus()) && record.getWarningStatus() == 1) {
                record.setPatrolFlag(0);//异常状态
            } else if ((ObjectUtil.isNotEmpty(record.getTaskStatus()) && record.getTaskStatus() == 1)||((Integer.valueOf(record.getTactics())==2)&&StringUtils.isNotEmpty(record.getIgnoreContent()))) {
                record.setPatrolFlag(2);//已完成
            } else if (now.getTime() <= record.getExecutionTime().getTime()) {
                record.setPatrolFlag(1);//待执行
            } else if (now.getTime() > record.getExecutionTime().getTime() && record.getTaskStatus() != 1) {
                record.setPatrolFlag(3);//漏检
            } else {
                record.setPatrolFlag(1);//待执行
            }
        }
        return records;
    }

    public  List<PatrolTaskVO> getPatrolTask(String lineCode,String departId, DateTime startTime, DateTime endTime){
        //其实是stationId
        List<Integer> lineIds = stationService.getIdsByLineCode(lineCode);
        PatrolPoolParam param = new PatrolPoolParam();
        param.setStartTime(startTime).setEndTime(endTime).setPageNo(1).setPageSize(5000);
        param.setStationIds(lineIds);
        if (StringUtils.isNotBlank(departId)){
            param.setOrganizationId(departId);
        }
        IPage<PatrolTaskVO> page = new Page<>(param.getPageNo(), param.getPageSize());
        page = patrolTaskMapper.selectPageList2(page, param);
        List<PatrolTaskVO> records = page.getRecords();
        return records;
    }


    @Override
    public List<PatrolTaskVO> getTodayCompletedPatrol(String lineCode, DateTime now) {
        DateTime startTime = DateUtil.beginOfDay(now);
        DateTime endTime = DateUtil.endOfDay(now);
        List<PatrolTaskVO> records = getPatrolTask(lineCode,null,startTime,endTime);
        List<PatrolTaskVO> vos = new ArrayList<>();
        records.forEach(record -> {
            if ((ObjectUtil.isNotEmpty(record.getTaskStatus()) && record.getTaskStatus() == 1)||((Integer.valueOf(record.getTactics())==2)&&StringUtils.isNotEmpty(record.getIgnoreContent()))) {
                record.setPatrolFlag(2);//已完成
                vos.add(record);
            }
        });
        return vos;
    }

    @Override
    public IPage getFaultTotalDetail(String lineCode, Integer pageNo, Integer pageSize, Date startTime, Date endTime) {
        IPage<FaultStatisticsModal> iPage = new Page<>(pageNo,pageSize);
        iPage = faultMapper.selectFaultStatisticsModal(iPage, startTime, endTime, lineCode);
        this.addFaultMaintainer(iPage.getRecords());
        return iPage;
    }

    @Override
    public List<FaultStatisticsModal> getCompletedFault(String lineCode, DateTime startTime, DateTime endTime) {
        List<FaultStatisticsModal> list = faultMapper.getCompletedFault(lineCode, startTime, endTime);
        return list;
    }
}
