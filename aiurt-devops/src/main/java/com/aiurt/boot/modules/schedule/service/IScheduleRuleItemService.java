package com.aiurt.boot.modules.schedule.service;

import com.aiurt.boot.modules.schedule.entity.ScheduleRuleItem;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: schedule_rule_item
 * @Author: swsc
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface IScheduleRuleItemService extends IService<ScheduleRuleItem> {
    List<ScheduleRuleItem> getDetailRuleItems(Integer ruleId);
    public List<ScheduleRuleItem> getByRuleId(Integer ruleId);

}
