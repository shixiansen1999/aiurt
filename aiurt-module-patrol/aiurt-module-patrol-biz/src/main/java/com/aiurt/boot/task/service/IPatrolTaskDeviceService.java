package com.aiurt.boot.task.service;

import com.aiurt.boot.task.dto.PatrolCheckResultDTO;
import com.aiurt.boot.task.dto.PatrolTaskDeviceDTO;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.param.PatrolTaskDeviceParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: patrol_task_device
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface IPatrolTaskDeviceService extends IService<PatrolTaskDevice> {

    /**
     * PC巡检任务池详情-巡检工单
     *
     * @param patrolTaskDeviceParam
     * @return
     */
    IPage<PatrolTaskDeviceParam> selectBillInfo(Page<PatrolTaskDeviceParam> page, PatrolTaskDeviceParam patrolTaskDeviceParam);

    /**
     * app-巡检清单列表
     *
     * @param pageList
     * @param code
     * @return
     */
    Page<PatrolTaskDeviceDTO> getPatrolTaskDeviceList(Page<PatrolTaskDeviceDTO> pageList, String code);

    /**
     * PC巡检任务池详情-巡检工单详情
     *
     * @param patrolNumber
     * @return
     */
    Map<String, Object> selectBillInfoByNumber(String patrolNumber);

    /**
     * 开始巡检时复制巡检标准项目到检查结果表中
     *
     * @param patrolTaskDevice
     * @return
     */
    int copyItems(PatrolTaskDevice patrolTaskDevice);

    /**
     * app巡检任务执行中-检查
     * @param patrolTaskDevice
     * @return
     */
    List<PatrolCheckResultDTO> getPatrolTaskCheck(PatrolTaskDevice patrolTaskDevice);
}
