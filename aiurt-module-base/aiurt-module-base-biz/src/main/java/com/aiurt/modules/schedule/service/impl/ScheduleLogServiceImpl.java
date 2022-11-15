package com.aiurt.modules.schedule.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.schedule.entity.ScheduleLog;
import com.aiurt.modules.schedule.mapper.ScheduleLogMapper;
import com.aiurt.modules.schedule.service.IScheduleLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: schedule_log
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
@Service
public class ScheduleLogServiceImpl extends ServiceImpl<ScheduleLogMapper, ScheduleLog> implements IScheduleLogService {


    @Autowired
    private ScheduleLogMapper scheduleLogMapper;

    @Autowired
    private ISysBaseAPI iSysBaseAPI;


    @Override
    public IPage<ScheduleLog> queryPageList(Page<ScheduleLog> page, ScheduleLog scheduleLog) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<String> orgList = new ArrayList<>();
        List<SysDepartModel> userSysDepart = iSysBaseAPI.getUserSysDepart(loginUser.getId());
        if (CollUtil.isNotEmpty(userSysDepart)) {
            List<String> collect = userSysDepart.stream().map(SysDepartModel::getId).collect(Collectors.toList());
            orgList.addAll(collect);
            scheduleLog.setOrgList(orgList);
        }else {
            scheduleLog.setUserId(loginUser.getId());
        }

        List<ScheduleLog> scheduleLogs = scheduleLogMapper.queryPageList(page, scheduleLog);
        if (CollUtil.isNotEmpty(scheduleLogs)) {
            for (ScheduleLog log : scheduleLogs) {
                LoginUser userById = iSysBaseAPI.queryUser(log.getCreateBy());
                log.setCreateBy(userById.getRealname());
                if (StrUtil.isEmpty(log.getSourceItemName()) && ObjectUtil.isEmpty(log.getSourceItemId())) {
                    log.setShiftRecord("由休息调整为" + log.getTargetItemName());
                } else if (StrUtil.isEmpty(log.getTargetItemName()) && ObjectUtil.isEmpty(log.getTargetItemId())) {
                    log.setShiftRecord("由" + log.getSourceItemName() + "调整为休息");
                } else {
                    log.setShiftRecord("由" + log.getSourceItemName() + "调整为" + log.getTargetItemName());
                }
            }
        }
        page.setRecords(scheduleLogs);
        return page;
    }
}
