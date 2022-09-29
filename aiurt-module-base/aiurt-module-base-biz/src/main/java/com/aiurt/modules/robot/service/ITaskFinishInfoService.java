package com.aiurt.modules.robot.service;

import com.aiurt.modules.robot.dto.TaskFinishDTO;
import com.aiurt.modules.robot.entity.TaskFinishInfo;
import com.aiurt.modules.robot.vo.TaskFinishInfoVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;

/**
 * @Description: task_finish_info
 * @Author: aiurt
 * @Date: 2022-09-28
 * @Version: V1.0
 */
public interface ITaskFinishInfoService extends IService<TaskFinishInfo> {

    /**
     * 刷新同步巡检任务数据
     *
     * @param startTime
     * @param endTime
     */
    void synchronizeRobotTask(Date startTime, Date endTime);

    /**
     * 机器人巡检任务列表查询
     *
     * @param page
     * @param taskFinishDTO
     * @return
     */
    IPage<TaskFinishInfoVO> queryPageList(Page<TaskFinishInfoVO> page, TaskFinishDTO taskFinishDTO);

    /**
     * 机器人巡检任务处置
     *
     * @param id
     * @param handleExplain
     */
    void taskDispose(String id, String handleExplain);
}
