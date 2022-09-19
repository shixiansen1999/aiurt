package com.aiurt.boot.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.screen.model.ScreenDurationTask;
import com.aiurt.boot.screen.service.PatrolScreenService;
import com.aiurt.boot.screen.utils.ScreenDateUtil;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.mapper.PatrolStandardMapper;
import com.aiurt.boot.task.entity.PatrolAccompany;
import com.aiurt.boot.task.entity.PatrolTask;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.mapper.PatrolAccompanyMapper;
import com.aiurt.boot.task.mapper.PatrolTaskDeviceMapper;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.aiurt.boot.task.mapper.PatrolTaskUserMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
    private PatrolAccompanyMapper patrolAccompanyMapper;
    @Autowired
    private PatrolTaskUserMapper patrolTaskUserMapper;
    @Autowired
    private PatrolScreenService patrolScreenService;

    /**
     * 首页-统计日程的巡视完成数
     *
     * @param year
     * @param month
     * @return
     */
    @Override
    public Map<String, Integer> getPatrolFinishNumber(int year, int month) {
        Map<String, Integer> map = new HashMap<>();
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
    public String getUserTask() {
        //获取当前用户的巡检任务信息
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<PatrolTask> taskList = patrolTaskUserMapper.getUserTask(sysUser.getId());
        List<String> list = new ArrayList<>();
        if (CollUtil.isNotEmpty(taskList)) {
            List<PatrolTaskDevice> taskDeviceList = new ArrayList<>();
            for (PatrolTask task : taskList) {
                //获取当前用户的任务中，获取当天，已提交的所有的工单
                PatrolTaskDevice devices = patrolTaskDeviceMapper.getTodaySubmit(new Date(), task.getId(), null);
                if (ObjectUtil.isNotEmpty(devices)) {
                    taskDeviceList.add(devices);
                }
            }
            //获取当前用户作为同行人参与的单号
            List<PatrolAccompany> accompanyList = patrolAccompanyMapper.selectList(new LambdaQueryWrapper<PatrolAccompany>().eq(PatrolAccompany::getUserId, sysUser.getId()));
            //获取当前用户参与的单号，并且当天，已提交
            for (PatrolAccompany accompany : accompanyList) {
                PatrolTaskDevice devices = patrolTaskDeviceMapper.getTodaySubmit(new Date(), null, accompany.getTaskDeviceCode());
                if (ObjectUtil.isNotEmpty(devices)) {
                    taskDeviceList.add(devices);
                }
            }
            //获取这个任务下的工单所对应的站点
            for (PatrolTaskDevice patrolTaskDevice : taskDeviceList) {
                String stationName = patrolTaskDeviceMapper.getLineStationName(patrolTaskDevice.getStationCode());
                PatrolStandard standardName = patrolStandardMapper.selectById(patrolTaskDevice.getTaskStandardId());
                String submitName = patrolTaskDeviceMapper.getSubmitName(patrolTaskDevice.getUserId());
                String deviceStationName = standardName.getName() + "-" + stationName + " 巡视人：" + submitName;
                list.add(deviceStationName);
            }
        }
        return CollUtil.join(list, "。");
    }

    @Override
    public Map<String, BigDecimal> getPatrolUserHours(int type, String teamId) {
        Map<String, BigDecimal> userDurationMap = new HashMap<>();
        // 班组的人员
        List<LoginUser> userList = sysBaseApi.getUserPersonnel(teamId);
        String dateTime = ScreenDateUtil.getDateTime(type);
        Date startTime = DateUtil.parse(dateTime.split("~")[0]);
        Date endTime = DateUtil.parse(dateTime.split("~")[1]);

        // 获取巡视人员在指定时间范围内的任务时长(单位秒)
        List<ScreenDurationTask> list = patrolTaskUserMapper.getScreenUserDuration(startTime, endTime);
        Map<String, Long> durationMap = list.stream().collect(Collectors.toMap(k -> k.getUserId(),
                v -> ObjectUtil.isEmpty(v.getDuration()) ? 0L : v.getDuration(), (a, b) -> a));
        userList.stream().forEach(l -> {
            String userId = l.getId();
            Long time = durationMap.get(userId);
            if (ObjectUtil.isEmpty(time)) {
                time = 0L;
            }
            // 展示需要以小时数展示，并保留两位小数
            BigDecimal decimal = new BigDecimal(1.0 * time / 3600).setScale(2, BigDecimal.ROUND_HALF_UP);
            userDurationMap.put(userId, decimal);
        });
        return userDurationMap;
    }

}
