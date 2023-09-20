package com.aiurt.modules.multideal.service;

import com.aiurt.modules.multideal.dto.MultiDealDTO;

/**
 * @author fgw
 * @desc 多实例任务执行模块
 */
public interface IMultiInstanceDealService {

    /**
     * 多实例任务执行
     * @param multiDealDTO
     */
    void multiInstanceDeal(MultiDealDTO multiDealDTO);
}
