package com.aiurt.boot.task.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.task.dto.RepairTaskDTO;
import com.aiurt.boot.task.entity.*;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.common.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    public RepairTaskThreadService(RepairTask repairTask, RepairTaskMapper repairTaskMapper, InspectionManager manager, RepairTaskPeerRelMapper repairTaskPeerRelMapper,
                                   RepairTaskSamplingMapper repairTaskSamplingMapper,ISysBaseAPI sysBaseApi,RepairTaskEnclosureMapper repairTaskEnclosureMapper,
                                   RepairTaskUserMapper repairTaskUserMapper) {
        this.repairTask = repairTask;
        this.repairTaskMapper = repairTaskMapper;
        this.manager = manager;
        this.repairTaskPeerRelMapper = repairTaskPeerRelMapper;
        this.repairTaskSamplingMapper = repairTaskSamplingMapper;
        this.sysBaseApi = sysBaseApi;
        this.repairTaskEnclosureMapper = repairTaskEnclosureMapper;
        this.repairTaskUserMapper = repairTaskUserMapper;
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

        }
        catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }
        return repairTask;
    }
}
