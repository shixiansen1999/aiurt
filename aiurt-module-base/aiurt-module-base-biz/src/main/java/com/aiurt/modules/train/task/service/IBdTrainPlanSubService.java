package com.aiurt.modules.train.task.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.modules.train.task.entity.BdTrainPlanSub;
import com.aiurt.modules.train.task.entity.BdTrainTask;

import java.util.List;

/**
 * @Description: 年子计划
 * @Author: jeecg-boot
 * @Date: 2022-04-20
 * @Version: V1.0
 */
public interface IBdTrainPlanSubService extends IService<BdTrainPlanSub> {

    //
    void deleteByPlanId(String id);

    //查询子计划
    List<BdTrainPlanSub> getByPlanId(String id);

    /**
     * 筛选未使用的子计划
     * @param bdTrainPlanSub
     * @param pageList
     * @return
     */
    Page<BdTrainPlanSub> filterPlanSub(Page<BdTrainPlanSub> pageList, BdTrainPlanSub bdTrainPlanSub);
}
