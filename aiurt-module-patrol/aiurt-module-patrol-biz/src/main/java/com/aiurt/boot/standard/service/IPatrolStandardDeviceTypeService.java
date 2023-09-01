package com.aiurt.boot.standard.service;


import com.aiurt.boot.standard.entity.PatrolStandardDeviceType;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: patrol_standard_device_type
 * @Author: aiurt
 * @Date:   2023-08-23
 * @Version: V1.0
 */
public interface IPatrolStandardDeviceTypeService extends IService<PatrolStandardDeviceType> {
    /**
     * 通过巡视标准code查询
     *
     * @param code
     * @return
     */
    List<PatrolStandardDeviceType> queryByPatrolStandardCode(String code);
}
