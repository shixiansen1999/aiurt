package com.aiurt.boot.standard.mapper;


import com.aiurt.boot.standard.entity.PatrolStandardDeviceType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @Description: patrol_standard_device_type
 * @Author: aiurt
 * @Date:   2023-08-23
 * @Version: V1.0
 */
public interface PatrolStandardDeviceTypeMapper extends BaseMapper<PatrolStandardDeviceType> {

    /**
     * @param code
     * @return
     */
    List<PatrolStandardDeviceType> queryByPatrolStandardCode(String code);

}
