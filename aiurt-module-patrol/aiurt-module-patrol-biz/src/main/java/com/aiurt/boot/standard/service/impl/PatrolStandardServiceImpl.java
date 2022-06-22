package com.aiurt.boot.standard.service.impl;

import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.mapper.PatrolStandardMapper;
import com.aiurt.boot.standard.service.IPatrolStandardService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: patrol_standard
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class PatrolStandardServiceImpl extends ServiceImpl<PatrolStandardMapper, PatrolStandard> implements IPatrolStandardService {
    @Autowired
    private PatrolStandardMapper patrolStandardMapper;
    @Override
    public IPage<PatrolStandard> pageList(Page page, PatrolStandard patrolStandard) {
        List<PatrolStandard> page1 = patrolStandardMapper.pageList(page,patrolStandard);
        return page.setRecords(page1);
    }
}
