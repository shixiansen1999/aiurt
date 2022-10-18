package com.aiurt.modules.robot.mapper;


import com.aiurt.modules.robot.dto.TaskFinishDTO;
import com.aiurt.modules.robot.entity.TaskFinishInfo;
import com.aiurt.modules.robot.vo.TaskFinishInfoVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: task_finish_info
 * @Author: aiurt
 * @Date: 2022-09-28
 * @Version: V1.0
 */
public interface TaskFinishInfoMapper extends BaseMapper<TaskFinishInfo> {
    /**
     * 机器人巡检任务列表查询
     *
     * @param page
     * @param taskFinishDTO
     * @return
     */
    IPage<TaskFinishInfoVO> queryPageList(Page<TaskFinishInfoVO> page, @Param("condition") TaskFinishDTO taskFinishDTO);
}
