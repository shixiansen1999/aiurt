package com.aiurt.boot.task.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.manager.PatrolManager;
import com.aiurt.boot.standard.dto.StationDTO;
import com.aiurt.boot.task.dto.PatrolTaskDTO;
import com.aiurt.boot.task.dto.PatrolTaskStandardDTO;
import com.aiurt.boot.task.entity.PatrolAccompany;
import com.aiurt.boot.task.entity.PatrolSamplePerson;
import com.aiurt.boot.task.entity.PatrolTaskDevice;
import com.aiurt.boot.task.mapper.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


/**
 * @author hlq
 * @Description: app巡视-任务池-线程池处理类
 */
public class AppPatrolTaskThreadService implements Callable<PatrolTaskDTO> {
    private PatrolTaskDTO patrolTaskDTO;
    private PatrolTaskMapper patrolTaskMapper;
    private PatrolTaskStandardMapper patrolTaskStandardMapper;
    private PatrolManager manager;
    private PatrolTaskDeviceMapper patrolTaskDeviceMapper;
    private PatrolAccompanyMapper accompanyMapper;
    private PatrolSamplePersonMapper patrolSamplePersonMapper;
    private ISysBaseAPI sysBaseApi;


    public AppPatrolTaskThreadService(PatrolTaskDTO patrolTaskDTO, PatrolTaskMapper patrolTaskMapper, PatrolTaskStandardMapper patrolTaskStandardMapper, PatrolManager manager,
                                      PatrolTaskDeviceMapper patrolTaskDeviceMapper,PatrolAccompanyMapper accompanyMapper,PatrolSamplePersonMapper patrolSamplePersonMapper, ISysBaseAPI sysBaseApi) {
        this.patrolTaskDTO = patrolTaskDTO;
        this.patrolTaskMapper = patrolTaskMapper;
        this.patrolTaskStandardMapper = patrolTaskStandardMapper;
        this.manager = manager;
        this.patrolTaskDeviceMapper = patrolTaskDeviceMapper;
        this.accompanyMapper = accompanyMapper;
        this.patrolSamplePersonMapper = patrolSamplePersonMapper;
        this.sysBaseApi = sysBaseApi;
    }

    @Override
    public PatrolTaskDTO call() throws Exception {
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            String userName = patrolTaskMapper.getUserName(patrolTaskDTO.getBackId());
            List<PatrolTaskStandardDTO> patrolTaskStandard = patrolTaskStandardMapper.getMajorSystemName(patrolTaskDTO.getId());
            String majorName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getMajorName).distinct().collect(Collectors.joining("；"));
            String sysName = patrolTaskStandard.stream().filter(e->ObjectUtil.isNotEmpty(e.getSysName())).map(PatrolTaskStandardDTO::getSysName).distinct().collect(Collectors.joining("；"));
            List<String> orgCodes = patrolTaskMapper.getOrgCode(patrolTaskDTO.getCode());
            patrolTaskDTO.setOrganizationName(manager.translateOrg(orgCodes));
            List<StationDTO> stationName = patrolTaskMapper.getStationName(patrolTaskDTO.getCode());
            List<PatrolTaskDevice> taskDeviceList = patrolTaskDeviceMapper.selectList(new LambdaQueryWrapper<PatrolTaskDevice>().eq(PatrolTaskDevice::getTaskId, patrolTaskDTO.getId()));
            List<PatrolAccompany> accompanyList = new ArrayList<>();
            List<PatrolSamplePerson> samplePersonList = new ArrayList<>();
            for (PatrolTaskDevice patrolTaskDevice : taskDeviceList) {
                List<PatrolAccompany> patrolAccompanies = accompanyMapper.selectList(new LambdaQueryWrapper<PatrolAccompany>().eq(PatrolAccompany::getTaskDeviceCode, patrolTaskDevice.getPatrolNumber()));
                if (CollUtil.isNotEmpty(patrolAccompanies)) {
                    accompanyList.addAll(patrolAccompanies);
                }
                List<PatrolSamplePerson> patrolSamplePeoples = patrolSamplePersonMapper.selectList(new LambdaQueryWrapper<PatrolSamplePerson>().eq(PatrolSamplePerson::getTaskDeviceCode, patrolTaskDevice.getPatrolNumber()));
                if (CollUtil.isNotEmpty(patrolSamplePeoples)) {
                    samplePersonList.addAll(patrolSamplePeoples);
                }
            }
            if (CollUtil.isNotEmpty(accompanyList)) {
                accompanyList = accompanyList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PatrolAccompany::getUserId))), ArrayList::new));
                String peerPeople = accompanyList.stream().map(PatrolAccompany::getUsername).collect(Collectors.joining(";"));
                patrolTaskDTO.setPeerPeople(peerPeople);
            }
            if (CollUtil.isNotEmpty(samplePersonList)) {
                samplePersonList = samplePersonList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(PatrolSamplePerson::getUserId))), ArrayList::new));
                String samplePersonName = samplePersonList.stream().map(PatrolSamplePerson::getUsername).collect(Collectors.joining("；"));
                patrolTaskDTO.setSamplePersonName(samplePersonName);
            }
            if(ObjectUtil.isNotEmpty(patrolTaskDTO.getEndUserId())){
                LoginUser userById = sysBaseApi.getUserById(patrolTaskDTO.getEndUserId());
                patrolTaskDTO.setEndUserName(userById.getRealname());
            }else {
                patrolTaskDTO.setEndUserName("-");
            }
            patrolTaskDTO.setStationName(manager.translateStation(stationName));
            patrolTaskDTO.setSysName(ObjectUtil.isNotEmpty(sysName)?sysName:"-");
            patrolTaskDTO.setMajorName(majorName);
            patrolTaskDTO.setOrgCodeList(orgCodes);
            patrolTaskDTO.setPatrolUserName(manager.spliceUsername(patrolTaskDTO.getCode()));
            patrolTaskDTO.setPatrolReturnUserName(userName == null ? "-" : userName);
            patrolTaskDTO.setPeriod(patrolTaskDTO.getPeriod() == null ? "-" : patrolTaskDTO.getPeriod());
            patrolTaskDTO.setPatrolReturnUserName(patrolTaskDTO.getPeriod() == null ? "-" : patrolTaskDTO.getPeriod());
            patrolTaskDTO.setSource(patrolTaskDTO.getSource() == null ? "-" : patrolTaskDTO.getSource());
        }
        catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }

        return patrolTaskDTO;

    }
}
