package com.aiurt.boot.task.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.manager.PatrolManager;
import com.aiurt.boot.standard.dto.StationDTO;
import com.aiurt.boot.task.dto.PatrolTaskDTO;
import com.aiurt.boot.task.dto.PatrolTaskStandardDTO;
import com.aiurt.boot.task.mapper.PatrolTaskMapper;
import com.aiurt.boot.task.mapper.PatrolTaskStandardMapper;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;

import java.util.List;
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
    private ISysBaseAPI sysBaseApi;

    public AppPatrolTaskThreadService(PatrolTaskDTO patrolTaskDTO,PatrolTaskMapper patrolTaskMapper,PatrolTaskStandardMapper patrolTaskStandardMapper,PatrolManager manager,ISysBaseAPI sysBaseApi) {
        this.patrolTaskDTO = patrolTaskDTO;
        this.patrolTaskMapper = patrolTaskMapper;
        this.patrolTaskStandardMapper = patrolTaskStandardMapper;
        this.manager = manager;
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
            String sysName = patrolTaskStandard.stream().map(PatrolTaskStandardDTO::getSysName).distinct().collect(Collectors.joining("；"));
            List<String> orgCodes = patrolTaskMapper.getOrgCode(patrolTaskDTO.getCode());
            patrolTaskDTO.setOrganizationName(manager.translateOrg(orgCodes));
            List<StationDTO> stationName = patrolTaskMapper.getStationName(patrolTaskDTO.getCode());
            patrolTaskDTO.setStationName(manager.translateStation(stationName));
            if(ObjectUtil.isNotEmpty(patrolTaskDTO.getEndUserId())){
                LoginUser userById = sysBaseApi.getUserById(patrolTaskDTO.getEndUserId());
                patrolTaskDTO.setEndUserName(userById.getRealname());
            }else {
                patrolTaskDTO.setEndUserName("-");
            }
            patrolTaskDTO.setSubmitTime(patrolTaskDTO.getSubmitTime() == null ? "-" : patrolTaskDTO.getSubmitTime());
            patrolTaskDTO.setPeriod(patrolTaskDTO.getPeriod() == null ? "-" : patrolTaskDTO.getPeriod());
            patrolTaskDTO.setSysName(sysName);
            patrolTaskDTO.setMajorName(majorName);
            patrolTaskDTO.setOrgCodeList(orgCodes);
            patrolTaskDTO.setPatrolUserName(manager.spliceUsername(patrolTaskDTO.getCode()));
            patrolTaskDTO.setPatrolReturnUserName(userName == null ? "-" : userName);
        }
        catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }

        return patrolTaskDTO;

    }
}
