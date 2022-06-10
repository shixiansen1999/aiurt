package com.aiurt.boot.modules.schedule.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.aiurt.boot.modules.schedule.entity.ScheduleRule;
import com.aiurt.boot.modules.schedule.entity.ScheduleRuleItem;
import com.aiurt.boot.modules.schedule.mapper.ScheduleRuleMapper;
import com.aiurt.boot.modules.schedule.service.IScheduleRuleItemService;
import com.aiurt.boot.modules.schedule.service.IScheduleRuleService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: schedule_rule
 * @Author: swsc
 * @Date: 2021-09-23
 * @Version: V1.0
 */
@Service
public class ScheduleRuleServiceImpl extends ServiceImpl<ScheduleRuleMapper, ScheduleRule> implements IScheduleRuleService {
    @Autowired
    private IScheduleRuleItemService scheduleRuleItemService;

    @Override
    public List<ScheduleRule> getAllDetailRules() {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("del_flag", 0);
        List<ScheduleRule> rules = this.baseMapper.selectList(wrapper);
        rules.forEach(rule -> {
            List<ScheduleRuleItem> detailRuleItems = scheduleRuleItemService.getDetailRuleItems(rule.getId());
            String temp = "";
            if (detailRuleItems != null && detailRuleItems.size() > 0) {
                for (ScheduleRuleItem item : detailRuleItems) {
                    if (StringUtils.isNotEmpty(temp)) {
                        temp += "|";
                    }
                    temp += item.getItemName();
                }
            }
            rule.setItemNames(temp);
        });
        return rules;
    }
}
