package com.aiurt.modules.schedule.service.impl;

import cn.hutool.core.date.DateUtil;
import com.aiurt.modules.schedule.entity.ScheduleRule;
import com.aiurt.modules.schedule.entity.ScheduleRuleItem;
import com.aiurt.modules.schedule.mapper.ScheduleRuleMapper;
import com.aiurt.modules.schedule.service.IScheduleRuleItemService;
import com.aiurt.modules.schedule.service.IScheduleRuleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        LambdaQueryWrapper<ScheduleRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ScheduleRule::getDelFlag, 0);
        List<ScheduleRule> rules = this.baseMapper.selectList(wrapper);
        rules.forEach(rule -> {
            List<ScheduleRuleItem> detailRuleItems = scheduleRuleItemService.getDetailRuleItems(rule.getId());
            String temp = "";
            if (detailRuleItems != null && detailRuleItems.size() > 0) {
                List<Integer> collect = detailRuleItems.stream().map(ScheduleRuleItem::getItemId).collect(Collectors.toList());
                rule.setItemIds(collect);
                for (ScheduleRuleItem item : detailRuleItems) {
                    if (StringUtils.isNotEmpty(temp)) {
                        temp += "|";
                    }
                    StringBuilder stringBuffer = new StringBuilder();
                    stringBuffer.append("(").append(DateUtil.format(item.getStartTime(), "HH:mm")).append("-");
                    String nextDay = "1";
                    if (nextDay.equals(item.getTimeId())) {
                        stringBuffer.append("次日");
                    }
                    stringBuffer.append(DateUtil.format(item.getEndTime(), "HH:mm")).append(")");
                    temp += item.getItemName()+"-"+stringBuffer.toString();
                }
            }
            rule.setItemNames(temp);
        });
        return rules;
    }
}
