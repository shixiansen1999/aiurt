package com.aiurt.boot.task.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.boot.task.entity.RepairTaskUser;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.common.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.common.system.api.ISysBaseAPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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

    public RepairTaskThreadService(RepairTask repairTask,
                                   RepairTaskMapper repairTaskMapper,
                                   InspectionManager manager,
                                   RepairTaskPeerRelMapper repairTaskPeerRelMapper,
                                   RepairTaskSamplingMapper repairTaskSamplingMapper,
                                   ISysBaseAPI sysBaseApi,
                                   RepairTaskEnclosureMapper repairTaskEnclosureMapper,
                                   RepairTaskUserMapper repairTaskUserMapper,
                                   RepairTaskServiceImpl repairTaskService) {
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
            // 更新检修任务的相关信息
            updateRepairTaskInfo(repairTask,manager);

            // TODO: 2023/3/17 同行人和抽检人代码待优化 
//            List<RepairTaskDTO> repairTasks = repairTaskMapper.selectTask(repairTask.getId());
//            List<String> peerList = new ArrayList<String>();
//            List<String> samplingList = new ArrayList<String>();
//            repairTasks.forEach(e -> {
//                // 查询同行人
//                List<RepairTaskPeerRel> repairTaskPeer = repairTaskPeerRelMapper.selectList(
//                        new LambdaQueryWrapper<RepairTaskPeerRel>()
//                                .eq(RepairTaskPeerRel::getRepairTaskDeviceCode, e.getOverhaulCode()));
//                //名称集合
//                List<String> collect3 = repairTaskPeer.stream().distinct().map(RepairTaskPeerRel::getRealName).collect(Collectors.toList());
//                peerList.addAll(collect3);
//
//                //查询抽检人
//                List<RepairTaskSampling> repairTaskSampling = repairTaskSamplingMapper.selectList(
//                        new LambdaQueryWrapper<RepairTaskSampling>()
//                                .eq(RepairTaskSampling::getRepairTaskDeviceCode, e.getOverhaulCode()));
//                //抽检名称集合
//                List<String> collect4 = repairTaskSampling.stream().map(RepairTaskSampling::getRealName).collect(Collectors.toList());
//                samplingList.addAll(collect4);
//            });
//
//            peerList.removeAll (Collections.singleton (null));
//            samplingList.removeAll (Collections.singleton (null));
//            //查询同行人
//            HashSet<String> collect33 = new HashSet<>(peerList);
//            if (CollectionUtil.isNotEmpty(collect33)) {
//                StringBuffer stringBuffer = new StringBuffer();
//                for (String t : collect33) {
//                    stringBuffer.append(t);
//                    stringBuffer.append(",");
//                }
//                if (stringBuffer.length() > 0) {
//                    stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
//                }
//                repairTask.setPeerName(stringBuffer.toString());
//            }
//
//            //查询抽检人
//            HashSet<String> collect44 = new HashSet<>(samplingList);
//            if (CollectionUtil.isNotEmpty(collect44)) {
//                StringBuffer stringBuffer = new StringBuffer();
//                for (String t : collect44) {
//                    stringBuffer.append(t);
//                    stringBuffer.append(",");
//                }
//                if (stringBuffer.length() > 0) {
//                    stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
//                }
//                repairTask.setSamplingName(stringBuffer.toString());
//            }
//
//            //检修周期类型
//            repairTask.setTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repairTask.getType())));
//
//            //检修任务状态
//            repairTask.setStatusName(sysBaseApi.translateDict(DictConstant.INSPECTION_TASK_STATE, String.valueOf(repairTask.getStatus())));
//
//            //是否需要审核
//            repairTask.setIsConfirmName(sysBaseApi.translateDict(DictConstant.INSPECTION_IS_CONFIRM, String.valueOf(repairTask.getIsConfirm())));
//
//            //是否需要验收
//            repairTask.setIsReceiptName(sysBaseApi.translateDict(DictConstant.INSPECTION_IS_CONFIRM, String.valueOf(repairTask.getIsReceipt())));
//
//            //任务来源
//            repairTask.setSourceName(sysBaseApi.translateDict(DictConstant.PATROL_TASK_ACCESS, String.valueOf(repairTask.getSource())));
//
//            //作业类型
//            repairTask.setWorkTypeName(sysBaseApi.translateDict(DictConstant.WORK_TYPE, String.valueOf(repairTask.getWorkType())));
//            //检修归档状态
//            repairTask.setEcmStatusName(sysBaseApi.translateDict(DictConstant.ECM_STATUS,String.valueOf(repairTask.getEcmStatus())));
//            //备注
            repairTask.setContent(repairTask.getErrorContent());
            //附件
            repairTask.setPath(repairTask.getUrl());

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

            repairTask.setTitle(repairTask.getSiteName()+"检修记录表");
        }
        catch (Exception e) {
            throw e;
        } finally {
            lock.unlock();
        }
        return repairTask;
    }

    /**
     * 更新检修任务的相关信息
     *
     * @param repairTask 要更新的检修任务对象
     * @param manager    用于翻译编码的管理器
     */
    public void updateRepairTaskInfo(RepairTask repairTask, InspectionManager manager) {
        // 对组织机构、站点、专业、子系统进行编码处理并设置相应的属性
        setTranslatedCode(repairTask, manager);
    }

    /**
     * 设置检修任务对象的组织机构、站点、专业和子系统属性
     *
     * @param repairTask 要设置属性的检修任务对象
     * @param manager    用于翻译编码的管理器
     */
    private void setTranslatedCode(RepairTask repairTask, InspectionManager manager) {
        // 组织机构
        setOrganizational(repairTask, manager);

        // 站点
        setSiteName(repairTask, manager);

        // 专业
        setMajorName(repairTask, manager);

        // 子系统
        setSystemName(repairTask, manager);
    }

    /**
     * 设置检修任务对象的组织机构属性
     *
     * @param repairTask 要设置属性的检修任务对象
     * @param manager    用于翻译编码的管理器
     */
    private void setOrganizational(RepairTask repairTask, InspectionManager manager) {
        if (repairTask.getOrgCode() != null) {
            List<String> list1 = Arrays.asList(repairTask.getOrgCode().split(","));
            repairTask.setOrganizational(manager.translateOrg(list1));
        }
    }

    /**
     * 设置检修任务对象的站点属性
     *
     * @param repairTask 要设置属性的检修任务对象
     * @param manager    用于翻译编码的管理器
     */
    private void setSiteName(RepairTask repairTask, InspectionManager manager) {
        if (repairTask.getSiteCode() != null) {
            List<String> list2 = Arrays.asList(repairTask.getSiteCode().split(","));
            repairTask.setSiteName(manager.translateStationList(list2));
        }
    }

    /**
     * 设置检修任务对象的专业属性
     *
     * @param repairTask 要设置属性的检修任务对象
     * @param manager    用于翻译编码的管理器
     */
    private void setMajorName(RepairTask repairTask, InspectionManager manager) {
        if (repairTask.getMajorCode() != null) {
            List<String> list3 = Arrays.asList(repairTask.getMajorCode().split(","));
            repairTask.setMajorName(manager.translateMajor(list3, InspectionConstant.MAJOR));
        }
    }

    /**
     * 设置检修任务对象的子系统属性
     *
     * @param repairTask 要设置属性的检修任务对象
     * @param manager    用于翻译编码的管理器
     */
    private void setSystemName(RepairTask repairTask, InspectionManager manager) {
        if (repairTask.getSystemCode() != null) {
            List<String> list4 = Arrays.asList(repairTask.getSystemCode().split(","));
            repairTask.setSystemName(manager.translateMajor(list4, InspectionConstant.SUBSYSTEM));
        }
    }
}
