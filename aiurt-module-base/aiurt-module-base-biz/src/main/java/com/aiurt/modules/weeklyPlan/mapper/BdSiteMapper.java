package com.aiurt.modules.weeklyPlan.mapper;

import com.aiurt.modules.weeklyPlan.entity.BdSite;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 工区表，存储工区包含工作场所及对应工班信息
 * @Author: wgp
 * @Date:   2021-03-31
 * @Version: V1.0
 */
@Component
public interface BdSiteMapper extends BaseMapper<BdSite> {
}
