package com.aiurt.modules.robot.service;

import com.aiurt.modules.robot.dto.AreaPointDTO;
import com.aiurt.modules.robot.entity.TaskPointRel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * @Description: task_point_rel
 * @Author: aiurt
 * @Date: 2022-09-27
 * @Version: V1.0
 */
public interface ITaskPointRelService extends IService<TaskPointRel> {
    /**
     * 通过任务模板id查询巡检点位
     *
     * @param taskPathId 任务模板id
     * @return
     */
    List<AreaPointDTO> queryPointByTaskPathId(String taskPathId);

    /**
     * 更新任务模板对应的点位信息
     *
     * @param taskPointRelMap （key为任务模板id,value为点位id集合）
     */
    void handleTaskPointRel(Map<String, List<String>> taskPointRelMap);
}
