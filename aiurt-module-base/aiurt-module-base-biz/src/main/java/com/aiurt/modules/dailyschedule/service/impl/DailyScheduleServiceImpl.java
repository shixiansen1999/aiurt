package com.aiurt.modules.dailyschedule.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.dailyschedule.entity.DailySchedule;
import com.aiurt.modules.dailyschedule.mapper.DailyScheduleMapper;
import com.aiurt.modules.dailyschedule.service.IDailyScheduleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @Description: 日程安排
 * @Author: aiurt
 * @Date:   2022-09-08
 * @Version: V1.0
 */
@Slf4j
@Service
public class DailyScheduleServiceImpl extends ServiceImpl<DailyScheduleMapper, DailySchedule> implements IDailyScheduleService {

    @Autowired
    private DailyScheduleMapper dailyScheduleMapper;

    @Autowired
    private ISysBaseAPI sysBaseAPI;

    /**
     *
     * @param year 年
     * @param month 月
     * @param day 日
     * @return
     */
    @Override
    public List<DailySchedule> queryList(Integer year, Integer month, Integer day) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<DailySchedule> dailyScheduleList = dailyScheduleMapper.queryDailyScheduleList(year, month, day, loginUser.getId());
        dealUserInfo(dailyScheduleList);
        return dailyScheduleList;
    }

    /**
     *
     * @param year 年
     * @param month 月
     * @return
     */
    @Override
    public Map<String, List<DailySchedule>> queryDailyScheduleList(Integer year, Integer month) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<DailySchedule> dailyScheduleList = dailyScheduleMapper.queryDailyScheduleList(year, month, null, loginUser.getId());
        if (CollUtil.isEmpty(dailyScheduleList)) {
            return new HashMap<>(16);
        }
        dealUserInfo(dailyScheduleList);
        Map<String, List<DailySchedule>> map = dailyScheduleList.stream().collect(Collectors.groupingBy(DailySchedule::getAddTimeAlias));
        return map;
    }

    /**
     * 处理用户
     * @param dailyScheduleList
     */
    private void dealUserInfo(List<DailySchedule> dailyScheduleList) {
        dailyScheduleList.stream().forEach(dailySchedule -> {
            String addedUserId = dailySchedule.getAddedUserId();
            if (StrUtil.isNotBlank(addedUserId)) {
                LoginUser lo = sysBaseAPI.getUserById(addedUserId);
                if (Objects.nonNull(lo)) {
                    dailySchedule.setAddedUserName(lo.getRealname());
                }
            }
            String notifyUserId = dailySchedule.getNotifyUserId();

            if (StrUtil.isNotBlank(notifyUserId)) {
                List<String> userIdList = StrUtil.split(notifyUserId, ',');
                List<String> userNameList = userIdList.stream().map(id -> {
                    LoginUser lo = sysBaseAPI.getUserById(addedUserId);
                    if (Objects.nonNull(lo)) {
                        return lo.getRealname();
                    } else {
                        return "";
                    }
                }).collect(Collectors.toList());
                dailySchedule.setAddedUserName(StrUtil.join(",", userNameList));
            }
        });
    }
}
