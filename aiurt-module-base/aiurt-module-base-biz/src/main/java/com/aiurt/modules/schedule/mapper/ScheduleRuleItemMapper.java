package com.aiurt.modules.schedule.mapper;

import java.util.List;

import com.aiurt.modules.schedule.entity.ScheduleRuleItem;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: schedule_rule_item
 * @Author: swsc
 * @Date: 2021-09-23
 * @Version: V1.0
 */
public interface ScheduleRuleItemMapper extends BaseMapper<ScheduleRuleItem> {
    public List<ScheduleRuleItem> getDetailRuleItems(@Param("ruleId") Integer ruleId);
    @Select("select a.*,b.name as itemName,b.start_time as startTime,b.end_time as endTime,b.time_id as timeId from schedule_rule_item a left join schedule_item b on a.item_id=b.id where a.rule_id=#{ruleId} ")
    List<ScheduleRuleItem> getByRuleId(Integer ruleId);
}
