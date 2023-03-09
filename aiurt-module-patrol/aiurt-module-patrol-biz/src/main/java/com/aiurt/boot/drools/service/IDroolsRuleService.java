package com.aiurt.boot.drools.service;

import com.aiurt.boot.drools.entity.DroolsRule;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: drools_rule
 * @Author: aiurt
 * @Date:   2023-03-09
 * @Version: V1.0
 */
public interface IDroolsRuleService extends IService<DroolsRule> {

    DroolsRule queryByName(String name);

}
