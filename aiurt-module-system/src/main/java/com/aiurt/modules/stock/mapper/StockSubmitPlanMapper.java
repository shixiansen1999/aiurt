package com.aiurt.modules.stock.mapper;

import com.aiurt.common.aspect.annotation.EnableDataPerm;
import com.aiurt.modules.stock.entity.StockSubmitPlan;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@EnableDataPerm
public interface StockSubmitPlanMapper extends BaseMapper<StockSubmitPlan> {
    /**
     * 查询组织
     * @return
     */
    List<StockSubmitPlan> getOrgSelect();
}
