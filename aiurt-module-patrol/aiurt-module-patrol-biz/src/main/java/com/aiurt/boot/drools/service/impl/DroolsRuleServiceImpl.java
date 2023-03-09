package com.aiurt.boot.drools.service.impl;

import com.aiurt.boot.drools.entity.DroolsRule;
import com.aiurt.boot.drools.mapper.DroolsRuleMapper;
import com.aiurt.boot.drools.service.IDroolsRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: drools_rule
 * @Author: aiurt
 * @Date:   2023-03-09
 * @Version: V1.0
 */
@Service
public class DroolsRuleServiceImpl extends ServiceImpl<DroolsRuleMapper, DroolsRule> implements IDroolsRuleService {

    @Autowired
    private DroolsRuleMapper droolsRuleMapper;

    /**
     * 根据规则名称查询
     * @param name
     * @return
     */
    @Override
    public DroolsRule queryByName(String name) {
        return droolsRuleMapper.queryByName(name);
    }
}
