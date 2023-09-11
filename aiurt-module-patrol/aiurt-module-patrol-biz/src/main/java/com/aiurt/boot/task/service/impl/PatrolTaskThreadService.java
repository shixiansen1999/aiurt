package com.aiurt.boot.task.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.task.dto.PatrolTaskOrganizationDTO;
import com.aiurt.boot.task.dto.PatrolTaskStationDTO;
import com.aiurt.boot.task.entity.PatrolTaskUser;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.boot.task.param.PatrolTaskParam;
import com.aiurt.boot.task.service.IPatrolTaskDeviceService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


/**
 * @author lkj
 * @Description: 线程池处理类
 */
public class PatrolTaskThreadService implements Callable<PatrolTaskParam> {
    private PatrolTaskParam patrolTaskParam;

    private PatrolTaskOrganizationMapper patrolTaskOrganizationMapper;

    private PatrolTaskStationMapper patrolTaskStationMapper;

    private PatrolCheckResultMapper patrolCheckResultMapper;

    private PatrolTaskMapper patrolTaskMapper;

    private PatrolTaskDeviceMapper patrolTaskDeviceMapper;

    private PatrolTaskUserMapper patrolTaskUserMapper;

    private IPatrolTaskDeviceService patrolTaskDeviceService;

    private ISysBaseAPI sysBaseApi;

    public PatrolTaskThreadService(PatrolTaskParam patrolTaskParam,PatrolTaskOrganizationMapper patrolTaskOrganizationMapper,PatrolTaskStationMapper patrolTaskStationMapper,
                                   PatrolCheckResultMapper patrolCheckResultMapper,PatrolTaskMapper patrolTaskMapper,PatrolTaskDeviceMapper patrolTaskDeviceMapper,
                                   PatrolTaskUserMapper patrolTaskUserMapper,IPatrolTaskDeviceService patrolTaskDeviceService,ISysBaseAPI sysBaseApi) {
        this.patrolTaskParam = patrolTaskParam;
        this.patrolTaskOrganizationMapper = patrolTaskOrganizationMapper;
        this.patrolTaskStationMapper = patrolTaskStationMapper;
        this.patrolCheckResultMapper = patrolCheckResultMapper;
        this.patrolTaskMapper = patrolTaskMapper;
        this.patrolTaskDeviceMapper = patrolTaskDeviceMapper;
        this.patrolTaskUserMapper = patrolTaskUserMapper;
        this.patrolTaskDeviceService = patrolTaskDeviceService;
        this.sysBaseApi = sysBaseApi;
    }

    @Override
    public PatrolTaskParam call() throws Exception {
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            // 组织机构信息
            patrolTaskParam.setDepartInfo(patrolTaskOrganizationMapper.selectOrgByTaskCode(patrolTaskParam.getCode()));
            // 设置组织机构名称
            String departInfoName = patrolTaskParam.getDepartInfo().stream().map(PatrolTaskOrganizationDTO::getDepartName).collect(Collectors.joining("；"));
            patrolTaskParam.setDepartInfoName(departInfoName);
            // 站点信息
            patrolTaskParam.setStationInfo(patrolTaskStationMapper.selectStationByTaskCode(patrolTaskParam.getCode()));
            // 设置站点名称
            String stationInfoName = patrolTaskParam.getStationInfo().stream().map(PatrolTaskStationDTO::getStationName).collect(Collectors.joining("；"));
            patrolTaskParam.setStationInfoName(stationInfoName);
            if (ObjectUtil.isNotEmpty(patrolTaskParam.getEndUserId())) {
                // 任务结束用户名称
                patrolTaskParam.setEndUsername(patrolTaskMapper.getUsername(patrolTaskParam.getEndUserId()));
            }
            if (ObjectUtil.isNotEmpty(patrolTaskParam.getAuditorId())) {
                // 审核用户名称
                patrolTaskParam.setAuditUsername(patrolTaskMapper.getUsername(patrolTaskParam.getAuditorId()));
            }
            if (ObjectUtil.isNotEmpty(patrolTaskParam.getBackId())) {
                // 退回用户名称
                patrolTaskParam.setBackUsername(patrolTaskMapper.getUsername(patrolTaskParam.getBackId()));
            }
            if (ObjectUtil.isNotEmpty(patrolTaskParam.getDisposeId())) {
                // 处置用户名称
                patrolTaskParam.setDisposeUserName(patrolTaskMapper.getUsername(patrolTaskParam.getDisposeId()));
            }
            // 巡检用户信息
            if (CollectionUtils.isEmpty(patrolTaskParam.getUserInfo())) {
                QueryWrapper<PatrolTaskUser> userWrapper = new QueryWrapper<>();
                userWrapper.lambda().eq(PatrolTaskUser::getTaskCode, patrolTaskParam.getCode());
                List<PatrolTaskUser> userInfo = Optional.ofNullable(patrolTaskUserMapper.selectList(userWrapper))
                        .orElseGet(Collections::emptyList).stream().collect(Collectors.toList());
                patrolTaskParam.setUserInfo(userInfo);
                // 设置巡检用户名称
                String userInfoName = userInfo.stream().map(PatrolTaskUser::getUserName).collect(Collectors.joining("；"));
                patrolTaskParam.setUserInfoName(userInfoName);
            }

            // 该巡视任务是否有关联故障
            Integer patrolTaskFaultNum = this.patrolTaskMapper.getPatrolTaskFaultNum(patrolTaskParam.getId());
            patrolTaskParam.setIsRelateFault(patrolTaskFaultNum != null && (patrolTaskFaultNum > 0));
            patrolTaskParam.setIsRelateFaultName(patrolTaskFaultNum != null && (patrolTaskFaultNum > 0) ? "是" : "否");

            /*//巡视单内容
            if (ObjectUtil.isNotEmpty(patrolTaskParam.getHavePrint()) && patrolTaskParam.getHavePrint()) {
                List<PatrolStationDTO> billGangedInfo = patrolTaskDeviceService.getBillGangedInfo(patrolTaskParam.getId());
                List<PrintStationDTO> stationDTOS = new ArrayList<>();

                for (PatrolStationDTO dto : billGangedInfo) {
                    PrintStationDTO printStationDTO = new PrintStationDTO();
                    printStationDTO.setStationName(dto.getStationName());
                    List<PrintSystemDTO> printSystemDTOS = new ArrayList<>();

                    //获取检修项
                    List<PatrolBillDTO> billInfo = dto.getBillInfo();
                    if (CollUtil.isNotEmpty(billInfo)) {
                        for (PatrolBillDTO patrolBillDTO : billInfo) {
                            //根据检修单号查询检修项
                            String billCode = patrolBillDTO.getBillCode();
                            PrintSystemDTO printSystemDTO = new PrintSystemDTO();
                            if (StrUtil.isNotEmpty(billCode)) {
                                PatrolTaskDeviceParam taskDeviceParam = patrolTaskDeviceMapper.getIdAndSystemName(billCode);
                                printSystemDTO.setSystemName(taskDeviceParam.getSubsystemName());
                                List<PrintDetailDTO> printDetailList = new ArrayList<>();

                                List<PatrolCheckResultDTO> checkResultList = patrolCheckResultMapper.getCheckByTaskDeviceId(taskDeviceParam.getId());
                                for (PatrolCheckResultDTO c : checkResultList) {
                                    String userName = patrolTaskMapper.getUserName(c.getUserId());
                                    c.setCheckUserName(userName);

                                    PrintDetailDTO printDetailDTO = new PrintDetailDTO();
                                    printDetailDTO.setContent(c.getContent() + ":" + c.getQualityStandard());
                                    printDetailDTO.setResult(Convert.toStr(c.getCheckResult()));
                                    printDetailDTO.setRemark(c.getRemark());
                                    printDetailList.add(printDetailDTO);
                                }

                                printSystemDTO.setPrintDetailDTOS(printDetailList);
                                printSystemDTOS.add(printSystemDTO);
                            }
                        }
                        printStationDTO.setPrintSystemDTOS(printSystemDTOS);
                        stationDTOS.add(printStationDTO);
                    }
                }
                patrolTaskParam.setPrintStationDTOList(stationDTOS);
                List<PatrolTaskStationDTO> stationInfo = patrolTaskParam.getStationInfo();
                List<String> collect = stationInfo.stream().map(PatrolTaskStationDTO::getStationName).collect(Collectors.toList());
                patrolTaskParam.setTitle(CollUtil.join(collect, ",") + patrolTaskParam.getName() + "巡视表");
            }*/
        }
        catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }

        return patrolTaskParam;

    }
}
