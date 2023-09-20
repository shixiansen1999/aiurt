package com.aiurt.boot.standard.service.impl;


import com.aiurt.boot.standard.entity.PatrolStandardDeviceType;
import com.aiurt.boot.standard.mapper.PatrolStandardDeviceTypeMapper;
import com.aiurt.boot.standard.service.IPatrolStandardDeviceTypeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description: patrol_standard_device_type
 * @Author: aiurt
 * @Date:   2023-08-23
 * @Version: V1.0
 */
@Service
public class PatrolStandardDeviceTypeServiceImpl extends ServiceImpl<PatrolStandardDeviceTypeMapper, PatrolStandardDeviceType> implements IPatrolStandardDeviceTypeService {
    @Autowired
    private PatrolStandardDeviceTypeMapper patrolStandardDeviceTypeMapper;

    @Override
    public List<PatrolStandardDeviceType> queryByPatrolStandardCode(String code) {

        return patrolStandardDeviceTypeMapper.queryByPatrolStandardCode(code);
    }
}
