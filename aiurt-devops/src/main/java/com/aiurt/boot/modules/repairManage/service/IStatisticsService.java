package com.aiurt.boot.modules.repairManage.service;

import com.aiurt.boot.modules.repairManage.vo.StatisticsQueryVO;
import com.aiurt.boot.modules.repairManage.vo.TimeVO;

/**
 * @author qian
 * @version 1.0
 * @date 2021/9/27 17:05
 */
public interface IStatisticsService {
    Result workload(StatisticsQueryVO workLoadVO);

    Result repairItem();

    Result compareToTeam(TimeVO timeVO);

}
