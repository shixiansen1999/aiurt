package com.aiurt.boot.task.service;

import com.aiurt.boot.task.dto.PatrolAccessorySaveDTO;
import com.aiurt.boot.task.entity.PatrolAccessory;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: patrol_accessory
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface IPatrolAccessoryService extends IService<PatrolAccessory> {

    /**
     * app-保存附件
     * @param patrolAccessory
     */
    void savePatrolTaskAccessory(PatrolAccessorySaveDTO patrolAccessory);
}
