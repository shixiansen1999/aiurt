package com.aiurt.boot.modules.schedule.service;

import com.aiurt.boot.modules.schedule.entity.ScheduleRule;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: schedule_rule
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface IScheduleRuleService extends IService<ScheduleRule> {
    List<ScheduleRule> getAllDetailRules();
}
