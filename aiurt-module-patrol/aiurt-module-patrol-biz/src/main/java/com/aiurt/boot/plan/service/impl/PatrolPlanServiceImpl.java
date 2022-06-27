package com.aiurt.boot.plan.service.impl;

import com.aiurt.boot.plan.dto.PatrolPlanDto;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.mapper.*;
import com.aiurt.boot.plan.service.IPatrolPlanService;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.mapper.PatrolStandardMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Arrays;
import java.util.List;

/**
 * @Description: patrol_plan
 * @Author: aiurt
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Service
public class PatrolPlanServiceImpl extends ServiceImpl<PatrolPlanMapper, PatrolPlan> implements IPatrolPlanService {

    @Autowired
    private PatrolPlanStrategyMapper patrolPlanStrategyMapper;
    @Autowired
    private PatrolPlanStandardMapper patrolPlanStandardMapper;
    @Autowired
    private PatrolPlanOrganizationMapper patrolPlanOrganizationMapper;
    @Autowired
    private PatrolPlanStationMapper patrolPlanStationMapper;
    @Autowired
    private PatrolStandardMapper patrolStandardMapper;
    @Override
    public IPage<PatrolPlanDto> pageList(Page<PatrolPlanDto> page, PatrolPlanDto patrolPlan) {
        List<PatrolPlanDto> list = baseMapper.list(page,patrolPlan);
        return page.setRecords(list);
    }

    @Override
    public void add(PatrolPlanDto patrolPlanDto) {
         PatrolPlan patrolPlan =new PatrolPlan();
         patrolPlan.setCode(patrolPlanDto.getCode());
         patrolPlan.setName(patrolPlanDto.getName());
         patrolPlan.setEndDate(patrolPlanDto.getEndDate());
         patrolPlan.setStartDate(patrolPlanDto.getStartDate());
         patrolPlan.setRemark(patrolPlanDto.getRemark());
         patrolPlan.setType(patrolPlanDto.getType());
         patrolPlan.setOutsource(patrolPlanDto.getOutsource());
         patrolPlan.setConfirm(patrolPlanDto.getConfirm());
         patrolPlan.setPeriod(patrolPlanDto.getPeriod());
         baseMapper.insert(patrolPlan);
        PatrolPlan id = baseMapper.selectOne(new QueryWrapper<PatrolPlan>().eq("code",patrolPlan.getCode()));
        PatrolPlanStrategy patrolPlanStrategy = new PatrolPlanStrategy();
        patrolPlanStrategy.setPlanId(id.getId());
        patrolPlanStrategy.setType(patrolPlanDto.getStrategyType());
        patrolPlanStrategy.setWeek(patrolPlanDto.getWeek());
        patrolPlanStrategy.setTime(patrolPlanDto.getTime());
        patrolPlanStrategy.setEndTime(patrolPlanDto.getStrategyEndTime());
        patrolPlanStrategy.setStartTime(patrolPlanDto.getStrategyStartTime());
        patrolPlanStrategyMapper.insert(patrolPlanStrategy);
        List<PatrolStandard> list = patrolPlanDto.getPatrolStandards();
        PatrolPlanStandard patrolPlanStandard =new PatrolPlanStandard();
        patrolPlanStandard.setPlanId(id.getId());
        for (PatrolStandard r:list) {
             patrolPlanStandard.setStandardCode(r.getCode());
             patrolPlanStandard.setProfessionCode(r.getProfessionCode());
             patrolPlanStandard.setSubsystemCode(r.getSubsystemCode());
             patrolPlanStandardMapper.insert(patrolPlanStandard);
        }
        List<String> mechanismCodes = patrolPlanDto.getMechanismCodes();
        PatrolPlanOrganization patrolPlanOrganization =new PatrolPlanOrganization();
        patrolPlanOrganization.setPlanCode(patrolPlanDto.getCode());
        for (String m:mechanismCodes){
            patrolPlanOrganization.setOrganizationCode(m);
            patrolPlanOrganizationMapper.insert(patrolPlanOrganization);
        }
        List<String> siteCodes = patrolPlanDto.getSiteCodes();
        PatrolPlanStation patrolPlanStation =new PatrolPlanStation();
        patrolPlanStation.setPlanCode(patrolPlanDto.getCode());
        for (String s:siteCodes){
         patrolPlanStation.setStationCode(s);
         patrolPlanStationMapper.insert(patrolPlanStation);
        }
    }

    @Override
    public void delete(String id) {
        baseMapper.updates(id);
    }

    @Override
    public PatrolPlanDto selectById(String id) {
        PatrolPlanDto patrolPlanDto = baseMapper.selectId(id);
        patrolPlanDto.setSiteCodes(Arrays.asList(patrolPlanDto.getSiteCode().split(",")));
        patrolPlanDto.setMechanismCodes(Arrays.asList(patrolPlanDto.getMechanismCode().split(",")));
        List<String> ids = Arrays.asList(patrolPlanDto.getIds().split(","));
        patrolPlanDto.setPatrolStandards(patrolStandardMapper.selectBatchIds(ids));
        return patrolPlanDto;
    }
}
