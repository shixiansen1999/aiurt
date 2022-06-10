package com.aiurt.boot.modules.schedule.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.modules.schedule.entity.ScheduleRuleItem;
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
    @Select("select a.*,b.name as itemName from schedule_rule_item a left join schedule_item b on a.item_id=b.id where a.rule_id=#{ruleId} ")
    List<ScheduleRuleItem> getByRuleId(Integer ruleId);
}
