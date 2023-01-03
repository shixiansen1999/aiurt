package com.aiurt.boot.plan.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.PatrolConstant;
import com.aiurt.boot.manager.PatrolManager;
import com.aiurt.boot.plan.dto.DeviceListDTO;
import com.aiurt.boot.plan.dto.PatrolPlanDto;
import com.aiurt.boot.plan.dto.QuerySiteDto;
import com.aiurt.boot.plan.dto.StandardDTO;
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.mapper.*;
import com.aiurt.boot.plan.service.IPatrolPlanService;
import com.aiurt.boot.standard.dto.PatrolStandardDto;
import com.aiurt.boot.standard.dto.StationDTO;
import com.aiurt.boot.standard.entity.PatrolStandard;
import com.aiurt.boot.standard.mapper.PatrolStandardMapper;
import com.aiurt.boot.task.dto.MajorDTO;
import com.aiurt.boot.task.dto.SubsystemDTO;
import com.aiurt.boot.task.service.IPatrolTaskService;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.device.entity.Device;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.CsUserMajorModel;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: patrol_plan
 * @Author: aiurt
 * @Date: 2022-06-21
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
    private PatrolPlanDeviceMapper patrolPlanDeviceMapper;
    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private PatrolPlanMapper patrolPlanMapper;
    @Autowired
    private PatrolManager patrolManager;
    @Override
    public IPage<PatrolPlanDto> pageList(Page<PatrolPlanDto> page, PatrolPlanDto patrolPlan) {
        if (Objects.nonNull(patrolPlan.getSiteCode())){
            List<String> strings = baseMapper.selectBySite(patrolPlan.getSiteCode());
            if (CollUtil.isNotEmpty(strings)){
                patrolPlan.setSiteCode(String.join("|",strings));
            }
        }
//        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        if (ObjectUtil.isEmpty(loginUser)) {
//            throw new AiurtBootException("检测到未登录，请登录后操作！");
//        }
//        // 按照专业过滤
//        List<CsUserMajorModel> majorInfo = sysBaseApi.getMajorByUserId(loginUser.getId());
//        List<String> majorCodes = majorInfo.stream().map(CsUserMajorModel::getMajorCode).collect(Collectors.toList());
//        List<CsUserDepartModel> orgCodes = sysBaseApi.getDepartByUserId(loginUser.getId());
//        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
//        Set<String> userRoleSet = sysBaseApi.getUserRoleSet(sysUser.getUsername());
//        if (CollectionUtil.isNotEmpty(userRoleSet)){
//            //没有管理员权限查看自己的权限
//            String a = "admin";
//            if (!userRoleSet.contains(a)){
//                patrolPlan.setMajorCodes(majorCodes);
//                patrolPlan.setOrgCodes(orgCodes.stream().map(s->s.getOrgCode()).collect(Collectors.toList()));
//            }
//        }

        // 数据权限过滤-begin
        try {
            List<String> planCodes = this.planDataPermissionFilter();
            patrolPlan.setPlanCodes(planCodes);
        } catch (Exception e) {
            return page;
        }
        // 数据权限过滤-end

        IPage<PatrolPlanDto> list = baseMapper.list(page, patrolPlan);
        // 局部禁用数据权限-begin
        boolean filter = GlobalThreadLocal.setDataFilter(true);
        List <PatrolPlanDto> list1 =list.getRecords();
        list1.forEach(l->{
            List<String> strings = Arrays.asList(l.getSiteCode().split(";"));
            List<StationDTO> stationDtos = baseMapper.selectStations(strings);
            l.setSiteName(patrolManager.translateStation(stationDtos));
        });
        // 局部禁用数据权限-end
        GlobalThreadLocal.setDataFilter(filter);
        return list;
    }

    /**
     * 巡视计划数据权限过滤
     * @return
     * @throws AiurtBootException
     */
    private List<String> planDataPermissionFilter() throws AiurtBootException {
        List<String> planCodeByOrg = patrolPlanOrganizationMapper.getPlanCodeByUserOrg();
        List<String> planCodeByMajorSystem = patrolPlanStandardMapper.getPlanCodeByMajorSystem();
        List<String> planCodeByUserStation = patrolPlanStationMapper.getPlanCodeByUserStation();
        List<String> planCodes = CollectionUtil.intersection(planCodeByOrg, planCodeByMajorSystem, planCodeByUserStation).stream().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(planCodes)) {
            throw new AiurtBootException("暂无任务！");
        }
        return planCodes;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PatrolPlanDto patrolPlanDto) {
        this.check(patrolPlanDto);
        PatrolPlan patrolPlan = new PatrolPlan();
        patrolPlan.setCode(patrolPlanDto.getCode());
        patrolPlan.setName(patrolPlanDto.getName());
        patrolPlan.setEndDate(patrolPlanDto.getEndDate());
        patrolPlan.setStartDate(patrolPlanDto.getStartDate());
        patrolPlan.setRemark(patrolPlanDto.getRemark());
        patrolPlan.setType(patrolPlanDto.getType());
        patrolPlan.setOutsource(patrolPlanDto.getOutsource());
        patrolPlan.setConfirm(patrolPlanDto.getConfirm());
        patrolPlan.setPeriod(patrolPlanDto.getPeriod());
        patrolPlan.setStatus(0);
        baseMapper.insert(patrolPlan);
        PatrolPlan id = baseMapper.selectByCode(patrolPlanDto.getCode());
        if (patrolPlanDto.getPeriod() != null) {
            if (patrolPlanDto.getPeriod() == 1) {
                PatrolPlanStrategy patrolPlanStrategy = new PatrolPlanStrategy();
                patrolPlanStrategy.setPlanId(id.getId());
                patrolPlanStrategy.setType(0);
                Date strategyStartTime = patrolPlanDto.getStrategyStartTime();
                Date strategyEndTime = patrolPlanDto.getStrategyEndTime();
                if (ObjectUtil.isNotEmpty(strategyEndTime) && ObjectUtil.isNotEmpty(strategyEndTime)) {
                    Date startTime = DateUtil.parse(DateUtil.format(strategyStartTime, "HH:mm"), "HH:mm");
                    Date endTime = DateUtil.parse(DateUtil.format(strategyEndTime, "HH:mm"), "HH:mm");
                    int compare = DateUtil.compare(startTime, endTime);
//                    if (compare > 0) {
//                        throw new AiurtBootException("巡检策略设置的开始时间不能晚于结束时间！");
//                    } else
                    if (compare == 0) {
                        throw new AiurtBootException("巡检策略设置的开始和结束时间不能相等！");
                    }
                }
                patrolPlanStrategy.setEndTime(strategyEndTime);
                patrolPlanStrategy.setStartTime(strategyStartTime);
                patrolPlanStrategyMapper.insert(patrolPlanStrategy);
            } else if (patrolPlanDto.getPeriod().equals(PatrolConstant.PLAN_PERIOD_TWO) || patrolPlanDto.getPeriod().equals(PatrolConstant.PLAN_PERIOD_THREE)) {
                List<Integer> week = patrolPlanDto.getWeek();
                for (Integer f : week) {
                    PatrolPlanStrategy patrolPlanStrategy = new PatrolPlanStrategy();
                    patrolPlanStrategy.setPlanId(id.getId());
                    patrolPlanStrategy.setType(1);
                    patrolPlanStrategy.setWeek(f);
                    patrolPlanStrategy.setEndTime(patrolPlanDto.getStrategyEndTime());
                    patrolPlanStrategy.setStartTime(patrolPlanDto.getStrategyStartTime());
                    patrolPlanStrategyMapper.insert(patrolPlanStrategy);
                }
            } else if (patrolPlanDto.getPeriod().equals(PatrolConstant.PLAN_PERIOD_FOUR) || patrolPlanDto.getPeriod().equals(PatrolConstant.PLAN_PERIOD_FIVE)) {
                List<Integer> week = patrolPlanDto.getWeek();
                for (Integer f : week) {
                    PatrolPlanStrategy patrolPlanStrategy = new PatrolPlanStrategy();
                    patrolPlanStrategy.setPlanId(id.getId());
                    patrolPlanStrategy.setType(2);
                    patrolPlanStrategy.setTime((int) Math.ceil(1.0 * f / 7));
                    int s = f % 7;
                    if (s == 0) {
                        s = 7;
                    }
                    patrolPlanStrategy.setWeek(s);
                    patrolPlanStrategy.setEndTime(patrolPlanDto.getStrategyEndTime());
                    patrolPlanStrategy.setStartTime(patrolPlanDto.getStrategyStartTime());
                    patrolPlanStrategyMapper.insert(patrolPlanStrategy);
                }
            }
        }
        List<PatrolStandardDto> list = patrolPlanDto.getPatrolStandards();
        if (CollectionUtils.isNotEmpty(list)) {
            for (PatrolStandard r : list) {
                PatrolPlanStandard patrolPlanStandard = new PatrolPlanStandard();
                patrolPlanStandard.setPlanId(id.getId());
                patrolPlanStandard.setPlanId(id.getId());
                patrolPlanStandard.setStandardCode(r.getCode());
                patrolPlanStandard.setProfessionCode(r.getProfessionCode());
                patrolPlanStandard.setDeviceTypeCode(r.getDeviceTypeCode());
                patrolPlanStandard.setSubsystemCode(r.getSubsystemCode());
                patrolPlanStandardMapper.insert(patrolPlanStandard);
            }
        }
        List<String> mechanismCodes = patrolPlanDto.getMechanismCodes();
        if (CollectionUtils.isNotEmpty(mechanismCodes)) {
            for (String m : mechanismCodes) {
                PatrolPlanOrganization patrolPlanOrganization = new PatrolPlanOrganization();
                patrolPlanOrganization.setPlanCode(patrolPlanDto.getCode());
                patrolPlanOrganization.setOrganizationCode(m);
                patrolPlanOrganizationMapper.insert(patrolPlanOrganization);
            }
        }
        List<String> siteCodes = patrolPlanDto.getSiteCodes();
        if (CollectionUtils.isNotEmpty(siteCodes)) {
            for (String s : siteCodes) {
                PatrolPlanStation patrolPlanStation = new PatrolPlanStation();
                patrolPlanStation.setPlanCode(patrolPlanDto.getCode());
                patrolPlanStation.setStationCode(s);
                patrolPlanStationMapper.insert(patrolPlanStation);
            }
        }
        List<Device> devices = patrolPlanDto.getDevices();
        if (CollectionUtils.isNotEmpty(devices)) {
            for (Device p : devices) {
                PatrolPlanDevice patrolPlanDevice = new PatrolPlanDevice();
                patrolPlanDevice.setPlanId(id.getId());
                String standardId = baseMapper.byCode(p.getPlanStandardCode(), id.getId());
                patrolPlanDevice.setPlanStandardId(standardId);
                patrolPlanDevice.setDeviceCode(p.getCode());
                patrolPlanDeviceMapper.insert(patrolPlanDevice);
            }
        }
    }

    @Override
    public void delete(String id) {
        baseMapper.updates(id);
    }

    public void check(PatrolPlanDto patrolPlanDto) {
        if (CollUtil.isNotEmpty(patrolPlanDto.getPatrolStandards())) {
            List<PatrolStandardDto> patrolStandardDto = patrolPlanDto.getPatrolStandards();
            List<Device> devices = patrolPlanDto.getDevices();
            patrolStandardDto.forEach(p -> {
                if (p.getDeviceType().equals(1)) {
                    boolean i = devices.stream().anyMatch(d -> p.getCode().equals(d.getPlanStandardCode()));
                    if (!i) {
                        throw new AiurtBootException("标准表名为：" + p.getName() + "暂未指定设备,请指定设备!");
                    }
                }
            });
          }
        }

    @Override
    public PatrolPlanDto selectId(String id, String code) {
        PatrolPlanDto patrolPlanDto = baseMapper.selectId(id, code);
        if (ObjectUtil.isNotNull(patrolPlanDto.getSiteCode())) {
            patrolPlanDto.setSiteCodes(Arrays.asList(patrolPlanDto.getSiteCode().split(",")));
            List<StationDTO>stationDtos=baseMapper.selectStations(patrolPlanDto.getSiteCodes());
            patrolPlanDto.setSiteName(patrolManager.translateStation(stationDtos));
        }
        if (ObjectUtil.isNotNull(patrolPlanDto.getMechanismCode())) {
            patrolPlanDto.setMechanismCodes(Arrays.asList(patrolPlanDto.getMechanismCode().split(",")));
        }
        if (ObjectUtil.isNotNull(patrolPlanDto.getIds())) {
            List<String> ids = Arrays.asList(patrolPlanDto.getIds().split(","));
            List<PatrolStandardDto> patrolStandardDtos = patrolStandardMapper.selectbyIds(ids);
            patrolStandardDtos.forEach(p -> {
                PatrolPlanStandard patrolPlanStandard = patrolPlanStandardMapper.selectOne(
                        new LambdaQueryWrapper<PatrolPlanStandard>()
                                .eq(PatrolPlanStandard::getStandardCode, p.getCode())
                                .eq(PatrolPlanStandard::getPlanId,id));
                List<PatrolPlanDevice> patrolPlanDevices = patrolPlanDeviceMapper.selectList(
                        new LambdaQueryWrapper<PatrolPlanDevice>()
                                .eq(PatrolPlanDevice::getPlanStandardId, patrolPlanStandard.getId()));
                if (CollUtil.isNotEmpty(patrolPlanDevices)) {
                    p.setSpecifyDevice(1);
                    List<Device>devices = viewDetails(patrolPlanStandard.getStandardCode(),id);
                    devices.forEach(object -> object.setPlanStandardCode(patrolPlanStandard.getStandardCode()));
                    p.setDevicesSs(devices);
                }
            });
            patrolPlanDto.setPatrolStandards(patrolStandardDtos);
        }
        List<Integer> week = baseMapper.selectWeek(id, code);
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
    @Transactional(rollbackFor = Exception.class)
    public void updateId(PatrolPlanDto patrolPlanDto) {
        baseMapper.deleteIdorCode(patrolPlanDto.getId());
         List<String> is = patrolPlanDto.getSiteCodes();
        List<Device> devices = patrolPlanDto.getDevices();
        List<Device> result = null;
        result = devices.stream()
                .filter((Device s) -> is.contains(s.getStationCode()))
                .collect(Collectors.toList());
        patrolPlanDto.setDevices(result);
        this.add(patrolPlanDto);
    }

    @Override
    public IPage<Device> viewDetails(Page<Device> page, String standardCode, String planId) {
        IPage<Device> deviceIpage = baseMapper.viewDetails(page, standardCode, planId);
        List<Device> records = deviceIpage.getRecords();
        if (records != null && records.size() > 0) {
            for (Device d : records) {
                //线路
                String lineCode = d.getLineCode() == null ? "" : d.getLineCode();
                //站点
                String stationCode = d.getStationCode() == null ? "" : d.getStationCode();
                //位置
                String positionCode = d.getPositionCode() == null ? "" : d.getPositionCode();
                String lineCodeName = sysBaseApi.translateDictFromTable("cs_line", "line_name", "line_code", lineCode);
                String stationCodeName = sysBaseApi.translateDictFromTable("cs_station", "station_name", "station_code", stationCode);
                String positionCodeName = sysBaseApi.translateDictFromTable("cs_station_position", "position_name", "position_code", positionCode);
                String positionCodeCcName = lineCodeName;
                if (stationCodeName != null && !"".equals(stationCodeName)) {
                    positionCodeCcName += CommonConstant.SYSTEM_SPLIT_STR + stationCodeName;
                }
                if (!"".equals(positionCodeName) && positionCodeName != null) {
                    positionCodeCcName += CommonConstant.SYSTEM_SPLIT_STR + positionCodeName;
                }
                d.setPositionCodeCcName(positionCodeCcName);
            }
        }
        return deviceIpage;
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
            majorDTOList.forEach(a -> {
                List<SubsystemDTO> subsystemDTOList = baseMapper.translateSubsystem(a.getMajorCode(), systemCode);
                a.setSubsystemInfo(subsystemDTOList);
            });
        }
        return majorDTOList;
    }

    @Override
    public List<StandardDTO> selectPlanStandard(String planId, String majorCode, String subsystemCode) {
        List<StandardDTO> standardDtos = new ArrayList<StandardDTO>();
        if (CollUtil.isNotEmpty(baseMapper.selectStandardList(planId, majorCode, subsystemCode))){
           standardDtos = baseMapper.selectStandardList(planId, majorCode, subsystemCode);
        }
        return standardDtos;
    }

    @Override
    public IPage<Device> deviceList(Page<Device> page, DeviceListDTO deviceListDTO) {
        IPage<Device> deviceIpage = baseMapper.deviceList(page, deviceListDTO.getSiteCodes(), deviceListDTO.getSubsystemCode(), deviceListDTO.getMajorCode(), deviceListDTO.getDeviceTypeCode(), deviceListDTO.getDeviceCode(), deviceListDTO.getDeviceName());
        List<Device> records = deviceIpage.getRecords();
        if (records != null && records.size() > 0) {
            for (Device d : records) {
                //线路
                String lineCode = d.getLineCode() == null ? "" : d.getLineCode();
                //站点
                String stationCode = d.getStationCode() == null ? "" : d.getStationCode();
                //位置
                String positionCode = d.getPositionCode() == null ? "" : d.getPositionCode();
                String lineCodeName = sysBaseApi.translateDictFromTable("cs_line", "line_name", "line_code", lineCode);
                String stationCodeName = sysBaseApi.translateDictFromTable("cs_station", "station_name", "station_code", stationCode);
                String positionCodeName = sysBaseApi.translateDictFromTable("cs_station_position", "position_name", "position_code", positionCode);
                String positionCodeCcName = lineCodeName;
                if (stationCodeName != null && !"".equals(stationCodeName)) {
                    positionCodeCcName += CommonConstant.SYSTEM_SPLIT_STR + stationCodeName;
                }
                if (!"".equals(positionCodeName) && positionCodeName != null) {
                    positionCodeCcName += CommonConstant.SYSTEM_SPLIT_STR + positionCodeName;
                }
                d.setPositionCodeCcName(positionCodeCcName);
            }
        }
        if(CollUtil.isEmpty(deviceListDTO.getSiteCodes())){
            records = new ArrayList<>();
        }
        return deviceIpage;
    }

    @Override
    public int modefy(String planId, Integer status) {
        if (ObjectUtil.isEmpty(planId)) {
            throw new AiurtBootException("计划主键ID为空！");
        }
        PatrolPlan patrolPlan = patrolPlanMapper.selectById(planId);

        if (0 == status) {
            // 判断计划的策略是否为空
            List<PatrolPlanStrategy> planStrategy = patrolPlanStrategyMapper.selectList(
                    new LambdaQueryWrapper<PatrolPlanStrategy>().eq(PatrolPlanStrategy::getPlanId, planId));
            if (CollectionUtil.isEmpty(planStrategy)) {
                throw new AiurtBootException("计划暂未设置巡检策略，不允许启用！");
            }

            // 判断计划是否选择标准表
            List<PatrolPlanStandard> patrolPlanStandard = patrolPlanStandardMapper.selectList(
                    new LambdaQueryWrapper<PatrolPlanStandard>().eq(PatrolPlanStandard::getPlanId, planId));
            if (CollectionUtil.isEmpty(patrolPlanStandard)) {
                throw new AiurtBootException("计划暂未挑选巡检标准表，不允许启用！");
            }

            // 判断标准表中如果与设备类型相关是否选定了设备
            Optional.ofNullable(patrolPlanStandard).orElseGet(Collections::emptyList).stream().forEach(l -> {
                LambdaQueryWrapper<PatrolStandard> standardWrapper = new LambdaQueryWrapper<>();
                standardWrapper.eq(PatrolStandard::getCode, l.getStandardCode());
                PatrolStandard standard = Optional.ofNullable(patrolStandardMapper.selectOne(standardWrapper)).orElseGet(PatrolStandard::new);
                if (1 == standard.getDeviceType()) {
                    LambdaQueryWrapper<PatrolPlanDevice> deviceWrapper = new LambdaQueryWrapper<>();
                    deviceWrapper.eq(PatrolPlanDevice::getPlanId, planId);
                    deviceWrapper.eq(PatrolPlanDevice::getPlanStandardId, l.getId());
                    List<PatrolPlanDevice> deviceList = patrolPlanDeviceMapper.selectList(deviceWrapper);
                    if (CollectionUtil.isEmpty(deviceList)) {
                        throw new AiurtBootException("标准表名为:【" + standard.getName() + "】暂未指定设备，不允许启用！");
                    }
                }
            });
            // 更新计划启用状态
            status = 1;
        } else if (1 == status) {
            status = 0;
        }
        patrolPlan.setStatus(status);
        int updateById = patrolPlanMapper.updateById(patrolPlan);
        return updateById;

    }

    public List<Device> viewDetails(String standardCode, String planId) {
        List<Device> records =   baseMapper.viewDetails(standardCode, planId);
        if (records != null && records.size() > 0) {
            for (Device d : records) {
                //线路
                String lineCode = d.getLineCode() == null ? "" : d.getLineCode();
                //站点
                String stationCode = d.getStationCode() == null ? "" : d.getStationCode();
                //位置
                String positionCode = d.getPositionCode() == null ? "" : d.getPositionCode();
                String lineCodeName = sysBaseApi.translateDictFromTable("cs_line", "line_name", "line_code", lineCode);
                String stationCodeName = sysBaseApi.translateDictFromTable("cs_station", "station_name", "station_code", stationCode);
                String positionCodeName = sysBaseApi.translateDictFromTable("cs_station_position", "position_name", "position_code", positionCode);
                String positionCodeCcName = lineCodeName;
                if (stationCodeName != null && !"".equals(stationCodeName)) {
                    positionCodeCcName += CommonConstant.SYSTEM_SPLIT_STR + stationCodeName;
                }
                if (!"".equals(positionCodeName) && positionCodeName != null) {
                    positionCodeCcName += CommonConstant.SYSTEM_SPLIT_STR + positionCodeName;
                }
                d.setPositionCodeCcName(positionCodeCcName);
                d.setStatusDesc(baseMapper.statusDesc(d.getStatus()));
                d.setTemporaryName(baseMapper.temporaryName(d.getTemporary()));
                d.setMajorCodeName(baseMapper.majorName(d.getMajorCode()));
                d.setSystemCodeName(baseMapper.systemCodeName(d.getSystemCode()));
                d.setDeviceTypeCodeName(baseMapper.deviceTypeCodeName(d.getDeviceTypeCode()));
            }
        }
        return records;
    }

}
