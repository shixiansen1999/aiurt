package com.aiurt.boot.task.service;

import com.aiurt.boot.task.dto.PatrolTaskDeviceDTO;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.param.PatrolTaskDeviceParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: patrol_task_device
 * @Author: aiurt
 * @Date: 2022-06-21
 * @Version: V1.0
 */
public interface IPatrolTaskDeviceService extends IService<PatrolTaskDevice> {

    /**
     * PC巡检任务池详情-巡检工单
     * @param patrolTaskDeviceParam
     * @return
     */
    IPage<PatrolTaskDeviceParam> selectBillInfo(Page<PatrolTaskDeviceParam> page, PatrolTaskDeviceParam patrolTaskDeviceParam);

    /**
     * app-巡检清单列表
     * @param pageList
     * @param id
     * @return
     */
    Page<PatrolTaskDeviceDTO> getPatrolTaskDeviceList(Page<PatrolTaskDeviceDTO> pageList, String id);
}
