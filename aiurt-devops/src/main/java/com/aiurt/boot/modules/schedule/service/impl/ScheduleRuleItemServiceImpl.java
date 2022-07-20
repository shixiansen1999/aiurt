package com.aiurt.modules.schedule.service.impl;

import com.aiurt.modules.schedule.entity.ScheduleRuleItem;
import com.aiurt.modules.schedule.mapper.ScheduleRuleItemMapper;
import com.aiurt.modules.schedule.service.IScheduleRuleItemService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: schedule_rule_item
 * @Author: swsc
 * @Date: 2021-09-23
 * @Version: V1.0
 */
@Service
public class ScheduleRuleItemServiceImpl extends ServiceImpl<ScheduleRuleItemMapper, ScheduleRuleItem> implements IScheduleRuleItemService {
    @Override
    public List<ScheduleRuleItem> getDetailRuleItems(Integer ruleId) {
        return this.baseMapper.getDetailRuleItems(ruleId);
    }
    @Override
    public List<ScheduleRuleItem> getByRuleId(Integer ruleId) {
        return this.baseMapper.getByRuleId(ruleId);
    }
}
