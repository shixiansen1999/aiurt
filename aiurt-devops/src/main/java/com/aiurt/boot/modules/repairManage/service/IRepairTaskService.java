package com.aiurt.boot.modules.repairManage.service;

import com.aiurt.boot.common.system.vo.LoginUser;
import com.aiurt.boot.modules.repairManage.entity.RepairTask;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.modules.repairManage.vo.DeviceQueryVO;
import com.aiurt.boot.modules.repairManage.vo.ReTaskDetailVO;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: 检修单列表
 * @Author: swsc
 * @Date:   2021-09-16
 * @Version: V1.0
 */
public interface IRepairTaskService extends IService<RepairTask> {

    Result confirmById(String id, Integer confirmStatus, String errorContent, String url);

    Result receiptById(String id, Integer receiptStatus, String errorContent, String url);

    Result<ReTaskDetailVO> queryDetailById(String id);

    Result getDetailByUser(LoginUser user, String startTime, String endTime);

//    Result receiveByUser(LoginUser user, String ids, String workType, String planOrderCode, String planOrderCodeUrl);

    Result commit(String id, String position, String content, String urls, String deviceIds, String processContent);

    /**
     * 根据用户id和所在周的时间获取检修池内容
     * @param userId
     * @param time
     * @return
     */
    Result getRepairTaskByUserIdAndTime(String userId, String time);

    Result queryByDevice(DeviceQueryVO deviceQueryVO);


    /**
     * 回调
     *
     * @param code     回调的内容
     * @param repairId 检修任务id
     * @return boolean
     */
    boolean callback(Long repairId, String code);
}
