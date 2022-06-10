package com.aiurt.boot.modules.repairManage.service;

import com.aiurt.boot.modules.repairManage.entity.RepairPool;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.repairManage.vo.AssignVO;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: 检修计划池
 * @Author: swsc
 * @Date: 2021-09-16
 * @Version: V1.0
 */
public interface IRepairPoolService extends IService<RepairPool> {

//    Result assigned(String ids, String userIds, String userNames,String stationId, String workType, String planOrderCode, String planOrderCodeUrl);

    Result updateTime(String ids, String startTime, String endTime);

    Result getRepairTask(String startTime, String endTime);

    Result assigned(AssignVO assignVO);

    Result getTimeInfo(int year);
}
