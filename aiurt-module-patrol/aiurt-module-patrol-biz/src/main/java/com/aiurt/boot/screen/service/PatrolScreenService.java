package com.aiurt.boot.screen.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.constant.PatrolDictCode;
import com.aiurt.boot.screen.constant.ScreenConstant;
import com.aiurt.boot.screen.model.*;
import com.aiurt.boot.screen.utils.ScreenDateUtil;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskStandard;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.aiurt.boot.task.mapper.PatrolTaskStandardMapper;
import com.aiurt.boot.task.mapper.PatrolTaskStationMapper;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Select;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatrolScreenService {
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private IPatrolTaskService patrolTaskService;
    @Autowired
    private PatrolTaskMapper patrolTaskMapper;

    /**
     * 大屏巡视模块-重要数据展示
     *
     * @param timeType
     * @param lineCode
     * @return
     */
    public ScreenImportantData getImportantData(Integer timeType, String lineCode) {
        // 默认本周
        if (ObjectUtil.isEmpty(timeType)) {
            timeType = ScreenConstant.THIS_WEEK;
        }
        List<String> lines = StrUtil.splitTrim(lineCode, ',');
        String dateTime = ScreenDateUtil.getDateTime(timeType);
        String[] split = dateTime.split("~");
        Date startTime = DateUtil.parse(split[0]);
        Date endTime = DateUtil.parse(split[1]);
        // 获取当前登录人的专业编号
        List<String> majors = this.getCurrentLoginUserMajors();
        if (CollectionUtil.isEmpty(majors)) {
            return new ScreenImportantData();
        }

        ScreenModule module = new ScreenModule();
        module.setLines(lines);
        module.setMajors(majors);
        module.setStartTime(startTime);
        module.setEndTime(endTime);

        List<PatrolTask> list = patrolTaskMapper.getScreenDataCount(module);
//        List<PatrolTask> list = patrolTaskService.lambdaQuery().eq(PatrolTask::getDelFlag, 0)
//                .between(PatrolTask::getPatrolDate, startTime, endTime)
//                .list();

        String omitStartTime = this.getOmitDateScope(startTime).split("~")[0];
        String omitEndTime = this.getOmitDateScope(endTime).split("~")[1];

        module.setStartTime(DateUtil.parse(omitStartTime));
        module.setEndTime(DateUtil.parse(omitEndTime));
        module.setOmit(PatrolConstant.OMIT_STATUS);

        ScreenImportantData data = new ScreenImportantData();
        long planNum = list.stream().count();
        long finishNum = list.stream().filter(l -> PatrolConstant.TASK_COMPLETE.equals(l.getStatus())).count();
//        long omitNum = patrolTaskService.lambdaQuery().eq(PatrolTask::getDelFlag, 0)
//                .eq(PatrolTask::getOmitStatus, PatrolConstant.OMIT_STATUS)
//                .between(PatrolTask::getPatrolDate, DateUtil.parse(omitStartTime), DateUtil.parse(omitEndTime))
//                .count();
        long omitNum = patrolTaskMapper.getScreenDataCount(module).stream().count();
        data.setPatrolNumber(planNum);
        data.setFinishNumber(finishNum);
        data.setOmitNumber(omitNum);
        return data;
    }

    /**
     * 大屏巡视模块-巡视数据统计
     *
     * @param timeType
     * @param lineCode
     * @return
     */
    public ScreenStatistics getStatisticsData(Integer timeType, String lineCode) {
        // 默认本周
        if (ObjectUtil.isEmpty(timeType)) {
            timeType = ScreenConstant.THIS_WEEK;
        }
        String dateTime = ScreenDateUtil.getDateTime(timeType);
        String[] split = dateTime.split("~");
        Date startTime = DateUtil.parse(split[0]);
        Date endTime = DateUtil.parse(split[1]);

        List<String> lines = StrUtil.splitTrim(lineCode, ',');
        // 获取当前登录人的专业编号
        List<String> majors = this.getCurrentLoginUserMajors();
        if (CollectionUtil.isEmpty(majors)) {
            return new ScreenStatistics();
        }
        ScreenModule module = new ScreenModule();
        module.setLines(lines);
        module.setMajors(majors);
        module.setStartTime(startTime);
        module.setEndTime(endTime);
//        List<PatrolTask> list = patrolTaskService.lambdaQuery().eq(PatrolTask::getDelFlag, 0)
//                .between(PatrolTask::getPatrolDate, startTime, endTime)
//                .list();
        List<PatrolTask> list = patrolTaskMapper.getScreenDataCount(module);

        String omitStartTime = this.getOmitDateScope(startTime).split("~")[0];
        String omitEndTime = this.getOmitDateScope(endTime).split("~")[1];
        module.setStartTime(DateUtil.parse(omitStartTime));
        module.setEndTime(DateUtil.parse(omitEndTime));
        module.setOmit(PatrolConstant.OMIT_STATUS);

        List<PatrolTask> todayList = list.stream()
                .filter(l -> DateUtil.format(new Date(), "yyyy-MM-dd").equals(DateUtil.format(l.getPatrolDate(), "yyyy-MM-dd")))
                .collect(Collectors.toList());
        if (!ScreenConstant.THIS_WEEK.equals(timeType) && !ScreenConstant.THIS_MONTH.equals(timeType)) {
//            todayList = patrolTaskService.lambdaQuery().eq(PatrolTask::getDelFlag, 0)
//                    .eq(PatrolTask::getOmitStatus, PatrolConstant.OMIT_STATUS)
//                    .between(PatrolTask::getPatrolDate, DateUtil.parse(DateUtil.format(new Date(), "yyyy-MM-dd 00:00:00")),
//                            DateUtil.parse(DateUtil.format(new Date(), "yyyy-MM-dd 23:59:59"))).list();
            ScreenModule todayModule = new ScreenModule();
            BeanUtils.copyProperties(module, todayModule);
            Date taday = new Date();
            todayModule.setStartTime(DateUtil.parse(DateUtil.format(taday, "yyyy-MM-dd 00:00:00")));
            todayModule.setEndTime(DateUtil.parse(DateUtil.format(taday, "yyyy-MM-dd 23:59:59")));
            todayList = patrolTaskMapper.getScreenDataCount(todayModule);
        }
        long planNum = list.stream().count();
        long finishNum = list.stream().filter(l -> PatrolConstant.TASK_COMPLETE.equals(l.getStatus())).count();
//        long omitNum = patrolTaskService.lambdaQuery().eq(PatrolTask::getDelFlag, 0)
//                .eq(PatrolTask::getOmitStatus, PatrolConstant.OMIT_STATUS)
//                .between(PatrolTask::getPatrolDate, DateUtil.parse(omitStartTime), DateUtil.parse(omitEndTime))
//                .count();
        long omitNum = patrolTaskMapper.getScreenDataCount(module).stream().count();
        long abnormalNum = list.stream().filter(l -> PatrolConstant.TASK_ABNORMAL.equals(l.getAbnormalState())).count();
        long todayNum = todayList.stream().count();
        long todayFinishNum = todayList.stream().filter(l -> PatrolConstant.TASK_COMPLETE.equals(l.getStatus())).count();

        ScreenStatistics data = new ScreenStatistics();
        data.setPlanNum(planNum);
        data.setFinishNum(finishNum);
        data.setOmitNum(omitNum);
        data.setAbnormalNum(abnormalNum);
        data.setTodayNum(todayNum);
        data.setTodayFinishNum(todayFinishNum);
        return data;
    }

    /**
     * 大屏巡视模块-巡视数据统计任务列表
     *
     * @param timeType
     * @param lineCode
     * @return
     */
    public List<ScreenStatisticsTask> getStatisticsTaskInfo(Integer timeType, String lineCode) {
        // 默认本周
        if (ObjectUtil.isEmpty(timeType)) {
            timeType = ScreenConstant.THIS_WEEK;
        }
        String dateTime = ScreenDateUtil.getDateTime(timeType);
        String[] split = dateTime.split("~");
        Date startTime = DateUtil.parse(split[0]);
        Date endTime = DateUtil.parse(split[1]);
        List<String> lines = null;
        if (StrUtil.isNotEmpty(lineCode)) {
            lines = StrUtil.splitTrim(lineCode, ',');
        }
        // 当前登录人的专业编号
        List<String> majors = this.getCurrentLoginUserMajors();
        if (CollectionUtil.isEmpty(majors)) {
            return new ArrayList<>();
        }

        ScreenTran tran = new ScreenTran();
        tran.setStartTime(startTime);
        tran.setEndTime(endTime);
        tran.setLines(lines);
        tran.setMajors(majors);

        List<ScreenStatisticsTask> list = patrolTaskMapper.getScreenTask(tran);
        list.stream().forEach(l -> {
            // 字典翻译
            String statusName = sysBaseApi.getDictItems(PatrolDictCode.TASK_STATUS).stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getStatus())))
                    .map(DictModel::getText).collect(Collectors.joining());
            String omitStatusName = sysBaseApi.getDictItems(PatrolDictCode.OMIT_STATUS).stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getOmitStatus())))
                    .map(DictModel::getText).collect(Collectors.joining());
            String abnormalName = sysBaseApi.getDictItems(PatrolDictCode.ABNORMAL_STATE).stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getAbnormalState())))
                    .map(DictModel::getText).collect(Collectors.joining());
            l.setStatusName(statusName);
            l.setOmitStatusName(omitStatusName);
            l.setAbnormalStateName(abnormalName);
        });
        return list;
    }

    /**
     * 大屏巡视模块-巡视任务完成情况
     *
     * @param lineCode
     * @return
     */
    public List<ScreenStatisticsGraph> getStatisticsGraph(String lineCode) {
        String dateTime = ScreenDateUtil.getDateTime(ScreenConstant.THIS_WEEK);
        String[] split = dateTime.split("~");
        Date startTime = DateUtil.parse(split[0]);
        Date endTime = DateUtil.parse(split[1]);
        List<String> lines = null;
        if (StrUtil.isNotEmpty(lineCode)) {
            lines = StrUtil.splitTrim(lineCode, ',');
        }
        // 当前登录人的专业编号
        List<String> majors = this.getCurrentLoginUserMajors();
        if (CollectionUtil.isEmpty(majors)) {
            return new ArrayList<>();
        }
        ScreenTran tran = new ScreenTran();
        tran.setStartTime(startTime);
        tran.setEndTime(endTime);
        tran.setLines(lines);
        tran.setMajors(majors);

        List<ScreenStatisticsGraph> list = patrolTaskMapper.getScreenGraph(tran);
        list.stream().forEach(l -> {
            Long total = l.getTotal();
            String finishRate = String.format("%.1f", (1.0 * l.getFinish() / total) * 100);
            String unfinishRate = String.format("%.1f", (1.0 * l.getUnfinish() / total) * 100);
            l.setFinishRate(finishRate + "%");
            l.setUnfinishRate(unfinishRate + "%");
        });
        return list;
    }

    /**
     * 如果参数日期是周一至周四，则返回上周五00时00分00秒和周日23时59分59秒，否则返回周一00时00分00秒和周四23时59分59秒
     *
     * @param date
     * @return
     */
    public String getOmitDateScope(Date date) {
        // 参数日期所在周的周一
        Date monday = DateUtils.getWeekStartTime(date);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDate localDate = monday.toInstant().atZone(zoneId).toLocalDate();
        if (Calendar.FRIDAY == DateUtil.dayOfWeek(date) || Calendar.SATURDAY == DateUtil.dayOfWeek(date)
                || Calendar.SUNDAY == DateUtil.dayOfWeek(date)) {
            // 周一往后3天，星期四
            Date thursday = Date.from(localDate.plusDays(3).atStartOfDay().atZone(zoneId).toInstant());
            return DateUtil.format(monday, "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(thursday, "yyyy-MM-dd 23:59:59");
        } else {
            // 周一往前3天，星期五
            Date friday = Date.from(localDate.minusDays(3).atStartOfDay().atZone(zoneId).toInstant());
            // 周一往前1天，星期天
            Date sunday = Date.from(localDate.minusDays(1).atStartOfDay().atZone(zoneId).toInstant());
            return DateUtil.format(friday, "yyyy-MM-dd 00:00:00") + "~" + DateUtil.format(sunday, "yyyy-MM-dd 23:59:59");
        }
    }

    /**
     * 大屏巡视模块-巡视数据统计详情列表
     *
     * @param page
     * @param screenModule
     * @param lineCode
     * @return
     */
    public IPage<ScreenStatisticsTask> getStatisticsDataList(Page<ScreenStatisticsTask> page, Integer timeType,
                                                             Integer screenModule, String lineCode) {
        // 默认本周
        if (ObjectUtil.isEmpty(timeType)) {
            timeType = ScreenConstant.THIS_WEEK;
        }
        // 模块参数未传直接返回空
        if (ObjectUtil.isEmpty(screenModule)) {
            return page;
        }

        ScreenModule moduleType = new ScreenModule();
        List<String> lines = null;
        if (StrUtil.isNotEmpty(lineCode)) {
            lines = StrUtil.splitTrim(lineCode, ',');
        }
        // 当前登录人的专业编号
        List<String> majors = this.getCurrentLoginUserMajors();
        if (CollectionUtil.isEmpty(majors)) {
            return page;
        }
        moduleType.setLines(lines);
        moduleType.setMajors(majors);

        String dateTime = ScreenDateUtil.getDateTime(timeType);
        String[] split = dateTime.split("~");
        Date startTime = DateUtil.parse(split[0]);
        Date endTime = DateUtil.parse(split[1]);
        switch (screenModule) {
            // 计划数
            case 1:
                moduleType.setStartTime(startTime);
                moduleType.setEndTime(endTime);
                break;
            // 完成数
            case 2:
                moduleType.setStartTime(startTime);
                moduleType.setEndTime(endTime);
                moduleType.setStatus(PatrolConstant.TASK_COMPLETE);
                break;
            // 漏巡数
            case 3:
                String omitStartTime = this.getOmitDateScope(startTime).split("~")[0];
                String omitEndTime = this.getOmitDateScope(endTime).split("~")[1];
                moduleType.setStartTime(DateUtil.parse(omitStartTime));
                moduleType.setEndTime(DateUtil.parse(omitEndTime));
                moduleType.setOmit(PatrolConstant.OMIT_STATUS);
                break;
            // 巡视异常数
            case 4:
                moduleType.setStartTime(startTime);
                moduleType.setEndTime(endTime);
                moduleType.setAbnormal(PatrolConstant.TASK_ABNORMAL);
                break;
            // 今日巡视数
            case 5:
                moduleType.setToday(new Date());
                break;
            // 今日巡视完成数
            case 6:
                moduleType.setToday(new Date());
                moduleType.setStatus(PatrolConstant.TASK_COMPLETE);
                break;
        }
        IPage<ScreenStatisticsTask> pageList = patrolTaskMapper.getStatisticsDataList(page, moduleType);
        pageList.getRecords().stream().forEach(l -> {
            // 字典翻译
            String statusName = sysBaseApi.getDictItems(PatrolDictCode.TASK_STATUS).stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getStatus())))
                    .map(DictModel::getText).collect(Collectors.joining());
            String omitStatusName = sysBaseApi.getDictItems(PatrolDictCode.OMIT_STATUS).stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getOmitStatus())))
                    .map(DictModel::getText).collect(Collectors.joining());
            String abnormalName = sysBaseApi.getDictItems(PatrolDictCode.ABNORMAL_STATE).stream()
                    .filter(item -> item.getValue().equals(String.valueOf(l.getAbnormalState())))
                    .map(DictModel::getText).collect(Collectors.joining());
            l.setStatusName(statusName);
            l.setOmitStatusName(omitStatusName);
            l.setAbnormalStateName(abnormalName);
        });
        return pageList;
    }

    /**
     * 获取当前登录用户的专业编号
     *
     * @return
     */
    public List<String> getCurrentLoginUserMajors() {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (ObjectUtil.isEmpty(loginUser)) {
            throw new AiurtBootException("检测到未登录系统，请登录后操作！");
        }
        List<CsUserMajorModel> majorList = sysBaseApi.getMajorByUserId(loginUser.getId());
        List<String> majors = majorList.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
        return majors;
    }
}
