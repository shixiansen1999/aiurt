package com.aiurt.boot.screen.service;

import cn.hutool.core.date.DateUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.constant.PatrolDictCode;
import com.aiurt.boot.screen.constant.ScreenConstant;
import com.aiurt.boot.screen.model.ScreenImportantData;
import com.aiurt.boot.screen.model.ScreenStatistics;
import com.aiurt.boot.screen.model.ScreenStatisticsPieGraph;
import com.aiurt.boot.screen.model.ScreenStatisticsTask;
import com.aiurt.boot.screen.utils.ScreenDateUtil;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.aiurt.boot.task.service.IPatrolTaskService;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.util.DateUtils;
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
        String dateTime = ScreenDateUtil.getDateTime(timeType);
        String[] split = dateTime.split("~");
        Date startTime = DateUtil.parse(split[0]);
        Date endTime = DateUtil.parse(split[1]);
        List<PatrolTask> list = patrolTaskService.lambdaQuery().eq(PatrolTask::getDelFlag, 0)
                .between(PatrolTask::getPatrolDate, startTime, endTime)
                .list();

        String omitStartTime = this.getOmitDateScope(startTime).split("~")[0];
        String omitEndTime = this.getOmitDateScope(endTime).split("~")[1];

        ScreenImportantData data = new ScreenImportantData();
        long planNum = list.stream().count();
        long finishNum = list.stream().filter(l -> PatrolConstant.TASK_COMPLETE.equals(l.getStatus())).count();
        long omitNum = patrolTaskService.lambdaQuery().eq(PatrolTask::getDelFlag, 0)
                .eq(PatrolTask::getOmitStatus, PatrolConstant.OMIT_STATUS)
                .between(PatrolTask::getPatrolDate, DateUtil.parse(omitStartTime), DateUtil.parse(omitEndTime))
                .count();

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
        String dateTime = ScreenDateUtil.getDateTime(timeType);
        String[] split = dateTime.split("~");
        Date startTime = DateUtil.parse(split[0]);
        Date endTime = DateUtil.parse(split[1]);
        List<PatrolTask> list = patrolTaskService.lambdaQuery().eq(PatrolTask::getDelFlag, 0)
                .between(PatrolTask::getPatrolDate, startTime, endTime)
                .list();

        String omitStartTime = this.getOmitDateScope(startTime).split("~")[0];
        String omitEndTime = this.getOmitDateScope(endTime).split("~")[1];

        List<PatrolTask> todayList = list.stream()
                .filter(l -> DateUtil.format(new Date(), "yyyy-MM-dd").equals(DateUtil.format(l.getPatrolDate(), "yyyy-MM-dd")))
                .collect(Collectors.toList());
        if (!ScreenConstant.THIS_WEEK.equals(timeType) || !ScreenConstant.THIS_MONTH.equals(timeType)) {
            todayList = patrolTaskService.lambdaQuery().eq(PatrolTask::getDelFlag, 0)
                    .eq(PatrolTask::getOmitStatus, PatrolConstant.OMIT_STATUS)
                    .between(PatrolTask::getPatrolDate, DateUtil.parse(DateUtil.format(new Date(), "yyyy-MM-dd 00:00:00")),
                            DateUtil.parse(DateUtil.format(new Date(), "yyyy-MM-dd 23:59:59"))).list();
        }
        ScreenStatistics data = new ScreenStatistics();

        long planNum = list.stream().count();
        long finishNum = list.stream().filter(l -> PatrolConstant.TASK_COMPLETE.equals(l.getStatus())).count();
        long omitNum = patrolTaskService.lambdaQuery().eq(PatrolTask::getDelFlag, 0)
                .eq(PatrolTask::getOmitStatus, PatrolConstant.OMIT_STATUS)
                .between(PatrolTask::getPatrolDate, DateUtil.parse(omitStartTime), DateUtil.parse(omitEndTime))
                .count();
        long abnormalNum = list.stream().filter(l -> PatrolConstant.TASK_ABNORMAL.equals(l.getAbnormalState())).count();
        long todayNum = todayList.stream().count();
        long todayFinishNum = todayList.stream().filter(l -> PatrolConstant.TASK_COMPLETE.equals(l.getStatus())).count();

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
        String dateTime = ScreenDateUtil.getDateTime(timeType);
        String[] split = dateTime.split("~");
        Date startTime = DateUtil.parse(split[0]);
        Date endTime = DateUtil.parse(split[1]);
        List<ScreenStatisticsTask> list = patrolTaskMapper.getScreenTask(startTime, endTime, lineCode);
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
     * @param timeType
     * @param lineCode
     * @return
     */
    public List<ScreenStatisticsPieGraph> getStatisticsPieGraph(Integer timeType, String lineCode) {
        return new ArrayList<>();
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
}
