package com.aiurt.boot.plan.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.plan.dto.DeviceListDTO;
import com.aiurt.boot.plan.dto.PatrolPlanDto;
import com.aiurt.boot.plan.dto.QuerySiteDto;
import com.aiurt.boot.plan.dto.StandardDTO;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.mapper.*;
import com.aiurt.boot.plan.service.IPatrolPlanService;
import com.aiurt.boot.standard.dto.PatrolStandardDto;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.mapper.PatrolStandardMapper;
import com.aiurt.boot.task.dto.MajorDTO;
import com.aiurt.boot.task.dto.SubsystemDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private   PatrolPlanDeviceMapper patrolPlanDeviceMapper;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Override
    public IPage<PatrolPlanDto> pageList(Page<PatrolPlanDto> page, PatrolPlanDto patrolPlan) {
      IPage<PatrolPlanDto> list = baseMapper.list(page,patrolPlan);
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PatrolPlanDto patrolPlanDto) {
        this.check(patrolPlanDto);
         PatrolPlan patrolPlan =new PatrolPlan();patrolPlan.setCode(patrolPlanDto.getCode());
         patrolPlan.setName(patrolPlanDto.getName());patrolPlan.setEndDate(patrolPlanDto.getEndDate());
         patrolPlan.setStartDate(patrolPlanDto.getStartDate());patrolPlan.setRemark(patrolPlanDto.getRemark());
         patrolPlan.setType(patrolPlanDto.getType());patrolPlan.setOutsource(patrolPlanDto.getOutsource());
         patrolPlan.setConfirm(patrolPlanDto.getConfirm());patrolPlan.setPeriod(patrolPlanDto.getPeriod());
         patrolPlan.setStatus(0);baseMapper.insert(patrolPlan);
        PatrolPlan id = baseMapper.selectByCode(patrolPlanDto.getCode());
        if (patrolPlanDto.getPeriod()!=null){
        if (patrolPlanDto.getPeriod()==1){
            PatrolPlanStrategy patrolPlanStrategy = new PatrolPlanStrategy();
            patrolPlanStrategy.setPlanId(id.getId());patrolPlanStrategy.setType(0);
            patrolPlanStrategy.setEndTime(patrolPlanDto.getStrategyEndTime());
            patrolPlanStrategy.setStartTime(patrolPlanDto.getStrategyStartTime());
            patrolPlanStrategyMapper.insert(patrolPlanStrategy);
        }else if (patrolPlanDto.getPeriod()==2 || patrolPlanDto.getPeriod()==3){
            List<Integer>week = patrolPlanDto.getWeek();
            for (Integer f:week) {
                PatrolPlanStrategy patrolPlanStrategy = new PatrolPlanStrategy();
                patrolPlanStrategy.setPlanId(id.getId());
                patrolPlanStrategy.setType(1);patrolPlanStrategy.setWeek(f);
                patrolPlanStrategy.setEndTime(patrolPlanDto.getStrategyEndTime());
                patrolPlanStrategy.setStartTime(patrolPlanDto.getStrategyStartTime());
                patrolPlanStrategyMapper.insert(patrolPlanStrategy);
            }
        }else if (patrolPlanDto.getPeriod()==4||patrolPlanDto.getPeriod()==5){
            List<Integer> week = patrolPlanDto.getWeek();
            for (Integer f:week) {
                PatrolPlanStrategy patrolPlanStrategy = new PatrolPlanStrategy();
                patrolPlanStrategy.setPlanId(id.getId());
                patrolPlanStrategy.setType(2);patrolPlanStrategy.setTime((int)Math.ceil(1.0 *f)/7);
                int s= f % 7;
                if (s==0){
                    s=7;
                }patrolPlanStrategy.setWeek(s);
                patrolPlanStrategy.setEndTime(patrolPlanDto.getStrategyEndTime());
                patrolPlanStrategy.setStartTime(patrolPlanDto.getStrategyStartTime());
                patrolPlanStrategyMapper.insert(patrolPlanStrategy);
            }
          }
        }
        List<PatrolStandardDto> list = patrolPlanDto.getPatrolStandards();
        if(CollectionUtils.isNotEmpty(list)){
        for (PatrolStandard r:list) {
            PatrolPlanStandard patrolPlanStandard =new PatrolPlanStandard();patrolPlanStandard.setPlanId(id.getId());
             patrolPlanStandard.setPlanId(id.getId());patrolPlanStandard.setStandardCode(r.getCode());
             patrolPlanStandard.setProfessionCode(r.getProfessionCode()); patrolPlanStandard.setDeviceTypeCode(r.getDeviceTypeCode());
             patrolPlanStandard.setSubsystemCode(r.getSubsystemCode());
             patrolPlanStandardMapper.insert(patrolPlanStandard);
           }
        }
        List<String> mechanismCodes = patrolPlanDto.getMechanismCodes();
        if (CollectionUtils.isNotEmpty(mechanismCodes)){
        for (String m:mechanismCodes){
            PatrolPlanOrganization patrolPlanOrganization =new PatrolPlanOrganization();
            patrolPlanOrganization.setPlanCode(patrolPlanDto.getCode());
            patrolPlanOrganization.setOrganizationCode(m);
            patrolPlanOrganizationMapper.insert(patrolPlanOrganization);
        }
        }
        List<String> siteCodes = patrolPlanDto.getSiteCodes();
        if (CollectionUtils.isNotEmpty(siteCodes)){
        for (String s:siteCodes){
            PatrolPlanStation patrolPlanStation =new PatrolPlanStation();
            patrolPlanStation.setPlanCode(patrolPlanDto.getCode());
            patrolPlanStation.setStationCode(s);
            patrolPlanStationMapper.insert(patrolPlanStation);}
        }
        List<Device> devices=patrolPlanDto.getDevices();
        if (CollectionUtils.isNotEmpty(devices)){
            for (Device p:devices){
                PatrolPlanDevice patrolPlanDevice= new PatrolPlanDevice();
                patrolPlanDevice.setPlanId(id.getId());
                String standardId= baseMapper.byCode(p.getPlanStandardCode(),id.getId());
                patrolPlanDevice.setPlanStandardId(standardId);patrolPlanDevice.setDeviceCode(p.getCode());
                patrolPlanDeviceMapper.insert(patrolPlanDevice);
            }
        }
    }

    @Override
    public void delete(String id) {
        baseMapper.updates(id);
    }

    public void check(PatrolPlanDto patrolPlanDto){
       List<PatrolStandardDto>  patrolStandardDto = patrolPlanDto.getPatrolStandards();
       List<Device> devices = patrolPlanDto.getDevices();
       patrolStandardDto.forEach(p->{
           if (p.getDeviceType().equals(1)){
               boolean i = devices.stream().anyMatch(d -> p.getCode().equals(d.getPlanStandardCode()));
               if (!i){
                   throw new AiurtBootException("请指定设备!");
               }
           }
       });
    }
    @Override
    public PatrolPlanDto selectId(String id,String code) {
        PatrolPlanDto patrolPlanDto = baseMapper.selectId(id,code);
        if(ObjectUtil.isNotNull(patrolPlanDto.getSiteCode())){
        patrolPlanDto.setSiteCodes(Arrays.asList(patrolPlanDto.getSiteCode().split(",")));
        }
        if (ObjectUtil.isNotNull(patrolPlanDto.getMechanismCode())){
        patrolPlanDto.setMechanismCodes(Arrays.asList(patrolPlanDto.getMechanismCode().split(",")));
        }
        if (ObjectUtil.isNotNull(patrolPlanDto.getIds())){
        List<String>ids= Arrays.asList(patrolPlanDto.getIds().split(","));
        patrolPlanDto.setPatrolStandards(patrolStandardMapper.selectbyIds(ids));
        }
        List<Integer> week =baseMapper.selectWeek(id,code);
        if (CollUtil.isNotEmpty(week)) {
        if (ObjectUtil.isNotNull(week.get(0))) {
                patrolPlanDto.setWeek(week);
                List<Integer> time = baseMapper.selectTime(id, code);
            if (CollUtil.isNotEmpty(time)) {
                if (ObjectUtil.isNotNull(time.get(0))) {
                    patrolPlanDto.setTime(time);
                    List<Integer> number = new ArrayList<>();
                    for (int i = 0; i < week.size(); i++) {
                        Integer w = week.get(i);
                        Integer t = time.get(i);
                        number.add(7 * (t - 1) + w);
                     }
                    patrolPlanDto.setNumber(number);
                  }
                }
            }
        }
        return patrolPlanDto;
    }

    @Override
    public List<QuerySiteDto> querySited() {
        List<QuerySiteDto> list = baseMapper.querySite();
        return list;
    }

    @Override
    public void updateId(PatrolPlanDto patrolPlanDto) {
        baseMapper.deleteIdorCode(patrolPlanDto.getId());
        this.add(patrolPlanDto);
    }

    @Override
    public IPage<Device> viewDetails(Page<Device> page,String standardCode, String planId) {
        IPage<Device> deviceIPage = baseMapper.viewDetails(page, standardCode, planId);
        List<Device> records = deviceIPage.getRecords();
        if(records != null && records.size()>0){
            for(Device d : records){
                //线路
                String lineCode = d.getLineCode()==null?"":d.getLineCode();
                //站点
                String stationCode = d.getStationCode()==null?"":d.getStationCode();
                //位置
                String positionCode = d.getPositionCode()==null?"":d.getPositionCode();
                String lineCodeName = sysBaseApi.translateDictFromTable("cs_line", "line_name", "line_code", lineCode);
                String stationCodeName = sysBaseApi.translateDictFromTable("cs_station", "station_name", "station_code", stationCode);
                String positionCodeName = sysBaseApi.translateDictFromTable("cs_station_position", "position_name", "position_code", positionCode);
                String positionCodeCcName = lineCodeName ;
                if(stationCodeName != null && !"".equals(stationCodeName)){
                    positionCodeCcName +=  CommonConstant.SYSTEM_SPLIT_STR + stationCodeName  ;
                }
                if(!"".equals(positionCodeName) && positionCodeName != null){
                    positionCodeCcName += CommonConstant.SYSTEM_SPLIT_STR + positionCodeName;
                }
                d.setPositionCodeCcName(positionCodeCcName);
            }
        }
        return deviceIPage;
    }
    @Override
    public List<MajorDTO> selectMajorCodeList(String planId) {
        List<PatrolPlanDto> patrolPlanDtos = baseMapper.selectCodeList(planId, null, null);
        List<PatrolPlanDto> collect = patrolPlanDtos.stream().distinct().collect(Collectors.toList());
        List<String> majorCodes1 = new ArrayList<>();
        List<String> systemCode = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(collect)) {
            collect.forEach(e -> {
                String majorCode = e.getProfessionCode();
                String systemCode1 = e.getSubsystemCode();
                majorCodes1.add(majorCode);
                systemCode.add(systemCode1);
            });
        }
        //根据专业编码查询对应的专业子系统
        List<MajorDTO> majorDTOList = baseMapper.translateMajor(majorCodes1);
        if (CollectionUtil.isNotEmpty(majorDTOList)) {
          majorDTOList.forEach(a->{
              List<SubsystemDTO> subsystemDTOList = baseMapper.translateSubsystem(a.getMajorCode(), systemCode);
              a.setSubsystemInfo(subsystemDTOList);
          });
        }
        return majorDTOList;
    }

    @Override
    public List<StandardDTO> selectPlanStandard(String planId, String majorCode, String subsystemCode) {
        List<StandardDTO> standardDtos = baseMapper.selectStandardList(planId, majorCode, subsystemCode);
        return standardDtos;
    }

    @Override
    public IPage<Device> deviceList(Page<Device> page, DeviceListDTO deviceListDTO) {
        IPage<Device> deviceIPage = baseMapper.deviceList(page,deviceListDTO.getSiteCodes(),deviceListDTO.getSubsystemCode(),deviceListDTO.getMajorCode(),deviceListDTO.getDeviceTypeCode());
        return deviceIPage;
    }

}
