package com.aiurt.boot.modules.repairManage.service;

import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.modules.repairManage.entity.RepairPool;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 检修计划池
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
public interface IRepairPoolService extends IService<RepairPool> {

    Result assigned(String ids, String userIds, String userNames);

    Result updateTime(String ids, String startTime, String endTime);

    Result getRepairTask(String userId, String startTime, String endTime);
}
