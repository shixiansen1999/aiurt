package com.aiurt.boot.modules.repairManage.service;

import com.swsc.copsms.common.api.vo.Result;
import com.swsc.copsms.common.system.vo.LoginUser;
import com.swsc.copsms.modules.repairManage.entity.RepairTask;
import com.baomidou.mybatisplus.extension.service.IService;
import com.swsc.copsms.modules.repairManage.vo.ReTaskDetailVO;

/**
 * @Description: 检修单列表
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
public interface IRepairTaskService extends IService<RepairTask> {

    Result confirmById(String id, Integer confirmStatus, String errorContent);

    Result checkById(String id, Integer receiptStatus, String errorContent);

    Result<ReTaskDetailVO> queryDetailById(String id);

    Result<ReTaskDetailVO> getDetailByUser(LoginUser user, String startTime, String endTime);

    Result receiveByUser(LoginUser user, String ids);
}
