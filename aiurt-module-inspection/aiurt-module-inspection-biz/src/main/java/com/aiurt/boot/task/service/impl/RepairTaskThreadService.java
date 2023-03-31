package com.aiurt.boot.task.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.task.dto.CheckListDTO;
import com.aiurt.boot.task.dto.RepairTaskDTO;
import com.aiurt.boot.task.dto.RepairTaskStationDTO;
import com.aiurt.boot.task.entity.*;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.common.result.SpareResult;
import com.aiurt.common.util.DateUtils;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * @author zwl
 * @Title:
 * @Description: 线程池处理类
 * @date 2023/3/15:05
 */
public class RepairTaskThreadService implements Callable<RepairTask> {
    private RepairTask repairTask;

    private RepairTaskMapper repairTaskMapper;

    private InspectionManager manager;

    private RepairTaskPeerRelMapper repairTaskPeerRelMapper;

    private RepairTaskSamplingMapper repairTaskSamplingMapper;

    private ISysBaseAPI sysBaseApi;

    private RepairTaskEnclosureMapper repairTaskEnclosureMapper;

    private RepairTaskUserMapper repairTaskUserMapper;

    private RepairTaskServiceImpl repairTaskService;

    public RepairTaskThreadService(RepairTask repairTask, RepairTaskMapper repairTaskMapper, InspectionManager manager, RepairTaskPeerRelMapper repairTaskPeerRelMapper,
                                   RepairTaskSamplingMapper repairTaskSamplingMapper,ISysBaseAPI sysBaseApi,RepairTaskEnclosureMapper repairTaskEnclosureMapper,
                                   RepairTaskUserMapper repairTaskUserMapper,RepairTaskServiceImpl repairTaskService) {
        this.repairTask = repairTask;
        this.repairTaskMapper = repairTaskMapper;
        this.manager = manager;
        this.repairTaskPeerRelMapper = repairTaskPeerRelMapper;
        this.repairTaskSamplingMapper = repairTaskSamplingMapper;
        this.sysBaseApi = sysBaseApi;
        this.repairTaskEnclosureMapper = repairTaskEnclosureMapper;
        this.repairTaskUserMapper = repairTaskUserMapper;
        this.repairTaskService = repairTaskService;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public RepairTask call() throws Exception {
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            //组织机构
            if (repairTask.getOrgCode() != null) {
                String[] split1 = repairTask.getOrgCode().split(",");
                List<String> list1 = Arrays.asList(split1);
                repairTask.setOrganizational(manager.translateOrg(list1));
            }
            //站点
            if (repairTask.getSiteCode() != null) {
                String[] split2 = repairTask.getSiteCode().split(",");
                List<String> list2 = Arrays.asList(split2);
                repairTask.setSiteName(manager.translateStationList(list2));
            }
            //专业
            if (repairTask.getMajorCode() != null) {
                String[] split3 = repairTask.getMajorCode().split(",");
                List<String> list3 = Arrays.asList(split3);
                repairTask.setMajorName(manager.translateMajor(list3, InspectionConstant.MAJOR));
            }

            //子系统
            if (repairTask.getSystemCode() != null) {
                String[] split4 = repairTask.getSystemCode().split(",");
                List<String> list4 = Arrays.asList(split4);
                repairTask.setSystemName(manager.translateMajor(list4, InspectionConstant.SUBSYSTEM));
            }
            // TODO: 2023/3/17 同行人和抽检人代码待优化 
            List<RepairTaskDTO> repairTasks = repairTaskMapper.selectTask(repairTask.getId());
            List<String> peerList = new ArrayList<String>();
            List<String> samplingList = new ArrayList<String>();
            repairTasks.forEach(e -> {
                //查询同行人
                List<RepairTaskPeerRel> repairTaskPeer = repairTaskPeerRelMapper.selectList(
                        new LambdaQueryWrapper<RepairTaskPeerRel>()
                                .eq(RepairTaskPeerRel::getRepairTaskDeviceCode, e.getOverhaulCode()));
                //名称集合
                List<String> collect3 = repairTaskPeer.stream().distinct().map(RepairTaskPeerRel::getRealName).collect(Collectors.toList());
                peerList.addAll(collect3);

                //查询抽检人
                List<RepairTaskSampling> repairTaskSampling = repairTaskSamplingMapper.selectList(
                        new LambdaQueryWrapper<RepairTaskSampling>()
                                .eq(RepairTaskSampling::getRepairTaskDeviceCode, e.getOverhaulCode()));
                //抽检名称集合
                List<String> collect4 = repairTaskSampling.stream().map(RepairTaskSampling::getRealName).collect(Collectors.toList());
                samplingList.addAll(collect4);
            });

            peerList.removeAll (Collections.singleton (null));
            samplingList.removeAll (Collections.singleton (null));
            //查询同行人
            HashSet<String> collect33 = new HashSet<>(peerList);
            if (CollectionUtil.isNotEmpty(collect33)) {
                StringBuffer stringBuffer = new StringBuffer();
                for (String t : collect33) {
                    stringBuffer.append(t);
                    stringBuffer.append(",");
                }
                if (stringBuffer.length() > 0) {
                    stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                }
                repairTask.setPeerName(stringBuffer.toString());
            }

            //查询抽检人
            HashSet<String> collect44 = new HashSet<>(samplingList);
            if (CollectionUtil.isNotEmpty(collect44)) {
                StringBuffer stringBuffer = new StringBuffer();
                for (String t : collect44) {
                    stringBuffer.append(t);
                    stringBuffer.append(",");
                }
                if (stringBuffer.length() > 0) {
                    stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                }
                repairTask.setSamplingName(stringBuffer.toString());
            }

            //检修周期类型
            repairTask.setTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repairTask.getType())));

            //检修任务状态
            repairTask.setStatusName(sysBaseApi.translateDict(DictConstant.INSPECTION_TASK_STATE, String.valueOf(repairTask.getStatus())));

            //是否需要审核
            repairTask.setIsConfirmName(sysBaseApi.translateDict(DictConstant.INSPECTION_IS_CONFIRM, String.valueOf(repairTask.getIsConfirm())));

            //是否需要验收
            repairTask.setIsReceiptName(sysBaseApi.translateDict(DictConstant.INSPECTION_IS_CONFIRM, String.valueOf(repairTask.getIsReceipt())));

            //任务来源
            repairTask.setSourceName(sysBaseApi.translateDict(DictConstant.PATROL_TASK_ACCESS, String.valueOf(repairTask.getSource())));

            //作业类型
            repairTask.setWorkTypeName(sysBaseApi.translateDict(DictConstant.WORK_TYPE, String.valueOf(repairTask.getWorkType())));
            //检修归档状态
            repairTask.setEcmStatusName(sysBaseApi.translateDict(DictConstant.ECM_STATUS,String.valueOf(repairTask.getEcmStatus())));
            //备注
            repairTask.setContent(repairTask.getErrorContent());
            //附件
            LoginUser loginUser = manager.checkLogin();
//            String userName = repairTaskMapper.getRealName(loginUser.getId());
            List<String> enclosures = repairTaskEnclosureMapper.getByRepairTaskId(repairTask.getId());
            if (enclosures.size()!=0){
                RepairTaskEnclosure repairTaskEnclosure = repairTaskEnclosureMapper.getByResultId(enclosures.get(0),loginUser.getUsername());
                if (repairTaskEnclosure!=null){
                    repairTask.setPath(repairTaskEnclosure.getUrl());
                }
            }

            if (repairTask.getCode() != null) {
                //根据检修任务code查询
                List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(
                        new LambdaQueryWrapper<RepairTaskUser>()
                                .eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode()));
                //检修人id集合
                List<String> collect = repairTaskUsers.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(collect)) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (String t : collect) {
                        stringBuffer.append(t);
                        stringBuffer.append(",");
                    }
                    if (stringBuffer.length() > 0) {
                        stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                    }
                    repairTask.setOverhaulId(stringBuffer.toString());

                    ArrayList<String> userList = new ArrayList<>();
                    collect.forEach(o -> {
                        String realName = repairTaskMapper.getRealName(o);
                        userList.add(realName);
                    });
                    if (CollectionUtil.isNotEmpty(userList)) {
                        StringBuffer stringBuffer1 = new StringBuffer();
                        for (String t : userList) {
                            stringBuffer1.append(t);
                            stringBuffer1.append(",");
                        }
                        if (stringBuffer1.length() > 0) {
                            stringBuffer1 = stringBuffer1.deleteCharAt(stringBuffer1.length() - 1);
                        }
                        repairTask.setOverhaulName(stringBuffer1.toString());
                    }
                }

            }

            // 所属周（相对年）
            if (repairTask.getYear() != null && repairTask.getWeeks() != null) {
                Date[] dateByWeek = DateUtils.getDateByWeek(repairTask.getYear(), repairTask.getWeeks());
                if (dateByWeek.length != 0) {
                    String weekName = String.format("第%d周(%s~%s)", repairTask.getWeeks(), DateUtil.format(dateByWeek[0], "yyyy/MM/dd"), DateUtil.format(dateByWeek[1], "yyyy/MM/dd"));
                    repairTask.setWeekName(weekName);
                }
            }


            //打印详情
            // 禁用数据权限过滤-start
            boolean filter = GlobalThreadLocal.setDataFilter(false);
            List<RepairTaskResult> repairTaskResults = new ArrayList<>();
            //获取检修站点
            List<RepairTaskStationDTO> repairTaskStationDTOS = repairTaskService.repairTaskStationList(repairTask.getId());
            List<SpareResult> spareChange = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            List<String> enclosureUrl = new ArrayList<>();
            for (RepairTaskStationDTO repairTaskStationDTO : repairTaskStationDTOS) {
                //无设备
                List<RepairTaskDTO> tasks = repairTaskMapper.selectTaskList(repairTask.getId(), repairTaskStationDTO.getStationCode());
                //有设备
                List<RepairTaskDTO> repairDeviceTask = repairTaskMapper.selectDeviceTaskList(repairTask.getId());
                for (RepairTaskDTO repairTaskDTO : repairDeviceTask) {
                    String equipmentCode = repairTaskDTO.getEquipmentCode();
                    if(StrUtil.isNotBlank(equipmentCode)){
                        JSONObject deviceByCode = sysBaseApi.getDeviceByCode(equipmentCode);
                        if (ObjectUtil.isNotEmpty(deviceByCode)) {
                            String station_code = deviceByCode.getString("stationCode");
                            if((repairTaskStationDTO.getStationCode()).equals(station_code)){
                                tasks.add(repairTaskDTO);
                            }
                        }
                    }
                }
                int i = 1;
                for (RepairTaskDTO repairTaskDTO : tasks) {
                    repairTaskDTO.setSystemName(manager.translateMajor(Arrays.asList(repairTaskDTO.getSystemCode()), InspectionConstant.SUBSYSTEM));

                    String deviceId = repairTaskDTO.getDeviceId();

                    CheckListDTO checkListDTO = repairTaskMapper.selectRepairTaskInfo(repairTask.getId(), repairTaskStationDTO.getStationCode(), deviceId);

                    //判断设备code是否为空
                    if (ObjectUtil.isNotEmpty(checkListDTO.getEquipmentCode())) {
                        List<StationDTO> stationDTOList = repairTaskMapper.selectStationLists(checkListDTO.getEquipmentCode());
                        String station = manager.translateStation(stationDTOList);
                        //判断具体位置是否为空
                        if (ObjectUtil.isNotEmpty(checkListDTO.getSpecificLocation())) {
                            if (ObjectUtil.isNotEmpty(station)) {
                                String string = checkListDTO.getSpecificLocation() + station;
                                checkListDTO.setMaintenancePosition(string);
                            } else {
                                checkListDTO.setMaintenancePosition(checkListDTO.getSpecificLocation());
                            }
                        } else {
                            checkListDTO.setMaintenancePosition(station);
                        }
                    } else {
                        List<StationDTO> stationDTOList1 = new ArrayList<>();
                        StationDTO stationDto = new StationDTO();
                        stationDto.setStationCode(checkListDTO.getStationCode());
                        stationDto.setLineCode(checkListDTO.getLineCode());
                        stationDto.setPositionCode(checkListDTO.getPositionCode());
                        stationDTOList1.add(stationDto);
                        String station = manager.translateStation(stationDTOList1);
                        if (ObjectUtil.isNotEmpty(checkListDTO.getSpecificLocation())) {
                            if (ObjectUtil.isNotEmpty(station)) {
                                String string = checkListDTO.getSpecificLocation() + station;
                                checkListDTO.setMaintenancePosition(string);
                            } else {
                                checkListDTO.setMaintenancePosition(checkListDTO.getSpecificLocation());
                            }
                        } else {
                            checkListDTO.setMaintenancePosition(station);
                        }
                    }

                    String faultCode = checkListDTO.getFaultCode();

                    if (StrUtil.isNotBlank(faultCode)) {
                        //获取备件更换信息
                        List<SpareResult> change = sysBaseApi.getSpareChange(faultCode);
                        spareChange.addAll(change);

                        //处理结果
                        String faultRepairReuslt = sysBaseApi.getFaultRepairReuslt(faultCode);
                        if (StrUtil.isNotBlank(faultRepairReuslt)) {
                            stringBuilder.append(Convert.toStr(i)).append(".").append("故障编号：").append(faultCode).append(",").append(faultRepairReuslt).append(",");
                        } else {
                            stringBuilder.append(Convert.toStr(i)).append(".").append(faultCode).append(":该故障没有完成维修").append(",");
                        }
                    }


                    //获取检查项
                    List<RepairTaskResult> resultList = repairTaskMapper.selectSingle(deviceId, null);
                    resultList.forEach(r -> {
                        List<RepairTaskEnclosure> repairTaskDevice = repairTaskEnclosureMapper.selectList(
                                new LambdaQueryWrapper<RepairTaskEnclosure>()
                                        .eq(RepairTaskEnclosure::getRepairTaskResultId, r.getId()));
                        if (CollectionUtils.isNotEmpty(repairTaskDevice)) {
                            //获取检修单的检修结果的附件
                            List<String> urllist = repairTaskDevice.stream().map(RepairTaskEnclosure::getUrl).collect(Collectors.toList());
                            enclosureUrl.addAll(urllist);
                        }

                        if ("0".equals(r.getPid())) {
                            r.setName(checkListDTO.getMaintenancePosition() + "-" + repairTaskDTO.getSystemName() + ":" + (r.getName() != null ? r.getName() : ""));
                        }

                        //检修结果
                        r.setStatusName(sysBaseApi.translateDict(DictConstant.OVERHAUL_RESULT, String.valueOf(r.getStatus())));

                        //当第一次检修结果为空时，且有检修结果是正常
                        if (ObjectUtil.isEmpty(repairTask.getRepairRecord())&& r.getStatus() != null && r.getStatus() == 1) {
                            repairTask.setRepairRecord(r.getStatusName());
                        }
                        //当检修结果异常时覆盖
                        if (r.getStatus() != null && r.getStatus() == 2) {
                            repairTask.setRepairRecord(r.getStatusName());
                        }
                    });
                    List<RepairTaskResult> repairTaskResults1 = RepairTaskServiceImpl.treeFirst(resultList);
                    repairTaskResults.addAll(repairTaskResults1);
                }
            }
            if (StrUtil.isEmpty(repairTask.getRepairRecord())) {
                repairTask.setRepairRecord("无");
            }
            repairTask.setTitle(repairTask.getSiteName()+"检修记录表");
            repairTask.setRepairTaskResultList(repairTaskResults);
            repairTask.setSpareChange(spareChange);
            if (stringBuilder.length() > 0) {
                // 截取字符
                stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                repairTask.setRepairResult(stringBuilder.toString());
            }
            repairTask.setEnclosureUrl(enclosureUrl);

            // 禁用数据权限过滤-end
            GlobalThreadLocal.setDataFilter(filter);

        }
        catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }
        return repairTask;
    }
}
