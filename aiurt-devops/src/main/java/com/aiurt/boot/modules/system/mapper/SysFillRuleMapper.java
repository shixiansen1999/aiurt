package com.aiurt.boot.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.aiurt.boot.modules.system.entity.SysFillRule;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @Description: 填值规则
 * @Author: swsc
 * @Date: 2019-11-07
 * @Version: V1.0
 */
@Mapper
@Repository
public interface SysFillRuleMapper extends BaseMapper<SysFillRule> {

}
