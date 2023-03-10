package com.aiurt.boot.drools.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import com.aiurt.boot.drools.entity.DroolsRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * @Description: drools_rule
 * @Author: aiurt
 * @Date:   2023-03-09
 * @Version: V1.0
 */
public interface DroolsRuleMapper extends BaseMapper<DroolsRule> {

    DroolsRule queryByName(String name);

}
