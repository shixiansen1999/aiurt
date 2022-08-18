package com.aiurt.boot.standard.service.impl;

import com.aiurt.boot.standard.dto.InspectionStandardDto;
import com.aiurt.boot.standard.dto.PatrolStandardDto;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.mapper.PatrolStandardMapper;
import com.aiurt.boot.standard.service.IPatrolStandardService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
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
    public IPage<PatrolStandardDto> pageList(Page page, PatrolStandard patrolStandard) {
        List<PatrolStandardDto> page1 = patrolStandardMapper.pageList(page,patrolStandard);
        page1.forEach(a->{
            a.setNumber(baseMapper.number(a.getCode()));
        });
        return page.setRecords(page1);
    }

    @Override
    public IPage<PatrolStandardDto> pageLists(Page page, PatrolStandardDto patrolStandard) {
        List<PatrolStandardDto> page1 = patrolStandardMapper.pageLists(page,patrolStandard,patrolStandard.getStations());
        return page.setRecords(page1);
    }

    @Override
    public List<InspectionStandardDto> lists(String professionCode, String subsystemCode) {
        List<InspectionStandardDto> list = patrolStandardMapper.list(professionCode,subsystemCode);
        return list;
    }
}
