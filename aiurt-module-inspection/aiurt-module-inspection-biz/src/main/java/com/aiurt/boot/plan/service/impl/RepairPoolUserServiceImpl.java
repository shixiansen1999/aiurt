package com.aiurt.boot.plan.service.impl;

import com.aiurt.boot.plan.entity.RepairPoolUser;
import com.aiurt.boot.plan.mapper.RepairPoolUserMapper;
import com.aiurt.boot.plan.service.IRepairPoolUserService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: repair_pool_user
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Service
public class RepairPoolUserServiceImpl extends ServiceImpl<RepairPoolUserMapper, RepairPoolUser> implements IRepairPoolUserService {

    @Override
    public List<RepairPoolUser> findAll() {
        return baseMapper.findAll();
    }
}
