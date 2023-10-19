package com.aiurt.boot.task.service.impl;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.task.dto.RepairPrintMessage;
import com.aiurt.boot.task.dto.RepairTaskUserNameDTO;
import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.common.util.DateUtils;
import com.aiurt.common.util.TimeUtil;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zwl
 * @Title:
 * @Description: 线程池处理类
 * @date 2023/3/15:05
 */
public class RepairTaskThreadService implements Callable<RepairTask> {
    private RepairTask repairTask;
    private InspectionManager manager;
    private Map<String, String> taskStateMap;
    private Map<String, String> taskTypeMap;
    private Map<String, String> isConfirmMap;
    private Map<String, String> sourceMap;
    private Map<String, String> workTypeMap;
    private Map<String, String> ecmStatusMap;
    private Map<String, RepairTaskUserNameDTO> overhaulNameMap;
    private Map<String, String> peerNameMap;
    private Map<String, String> sampNameMap;
    private Map<String, RepairPrintMessage> printMessage;
    private Map<String, RepairTask> allCodeMap;

    /**
     * 构造方法
     *
     * @param repairTask    检修任务实例
     * @param manager       巡检管理实例
     * @param taskStateMap  检修任务状态映射
     * @param taskTypeMap   检修周期类型映射
     * @param isConfirmMap  是否需要审核映射
     * @param sourceMap     任务来源映射
     * @param workTypeMap   作业类型映射
     * @param ecmStatusMap  检修归档状态映射
     * @param overhaulNameMap 检修人名称映射（根据检修任务ID）
     * @param peerNameMap   同行人名称映射（根据检修任务ID）
     * @param sampNameMap   抽检人名称映射（根据检修任务ID）
     * @param printMessage  打印信息映射
     * @param allCodeMap  编码映射
     */
    public RepairTaskThreadService(RepairTask repairTask, InspectionManager manager,
                                   Map<String, String> taskStateMap, Map<String, String> taskTypeMap,
                                   Map<String, String> isConfirmMap, Map<String, String> sourceMap,
                                   Map<String, String> workTypeMap, Map<String, String> ecmStatusMap,
                                   Map<String, RepairTaskUserNameDTO> overhaulNameMap,
                                   Map<String, String> peerNameMap, Map<String, String> sampNameMap,
                                   Map<String, RepairPrintMessage> printMessage,
                                   Map<String, RepairTask> allCodeMap) {
        this.repairTask = repairTask;
        this.manager = manager;
        this.taskStateMap = taskStateMap;
        this.taskTypeMap = taskTypeMap;
        this.isConfirmMap = isConfirmMap;
        this.sourceMap = sourceMap;
        this.workTypeMap = workTypeMap;
        this.ecmStatusMap = ecmStatusMap;
        this.overhaulNameMap = overhaulNameMap;
        this.peerNameMap = peerNameMap;
        this.sampNameMap = sampNameMap;
        this.printMessage = printMessage;
        this.allCodeMap = allCodeMap;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return computed result
     * @throws Exception if unable to compute a result
     */
    @Override
    public RepairTask call() {
        Lock lock = new ReentrantLock();
        lock.lock();
        try {
            // 更新检修任务的相关信息
            updateRepairTaskInfo(repairTask, manager);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while updating repair task info", e);
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
        if(ObjectUtil.isEmpty(repairTask)){
            return;
        }
        // 对组织机构、站点、专业、子系统进行编码处理并设置相应的属性
        repairTask.setSiteCode(allCodeMap.get(repairTask.getId()).getSiteCode());
        repairTask.setOrgCode(allCodeMap.get(repairTask.getId()).getOrgCode());
        repairTask.setMajorCode(allCodeMap.get(repairTask.getId()).getMajorCode());
        repairTask.setSystemCode(allCodeMap.get(repairTask.getId()).getSystemCode());
        setTranslatedCode(repairTask, manager);

        // 的检修时长检修时间转化
        repairTask.setDurationString(TimeUtil.translateTime(repairTask.getDuration()));

        //repairTask.setContent(repairTask.getErrorContent());
        repairTask.setPath(repairTask.getUrl());
        repairTask.setTitle(repairTask.getSiteName() + "检修记录表");

        repairTask.setStatusName(taskStateMap.get(String.valueOf(repairTask.getStatus())));
        repairTask.setTypeName(taskTypeMap.get(String.valueOf(repairTask.getType())));
        repairTask.setIsConfirmName(isConfirmMap.get(String.valueOf(repairTask.getIsConfirm())));
        repairTask.setIsReceiptName(isConfirmMap.get(String.valueOf(repairTask.getIsReceipt())));
        repairTask.setSourceName(sourceMap.get(String.valueOf(repairTask.getSource())));
        repairTask.setWorkTypeName(workTypeMap.get(repairTask.getWorkType()));
        repairTask.setEcmStatusName(ecmStatusMap.get(String.valueOf(repairTask.getEcmStatus())));
        repairTask.setPeerName(peerNameMap.get(repairTask.getId()));
        repairTask.setSamplingName(sampNameMap.get(repairTask.getId()));

        RepairPrintMessage repairPrintMessage = printMessage.get(repairTask.getId());
        if(ObjectUtil.isNotEmpty(repairPrintMessage)){
            BeanUtils.copyProperties(repairPrintMessage,repairTask);
        }

        RepairTaskUserNameDTO repairTaskUserNameDTO = overhaulNameMap.get(repairTask.getId());
        if(ObjectUtil.isNotEmpty(repairTaskUserNameDTO)){
            repairTask.setOverhaulName(repairTaskUserNameDTO.getUserNames());
            repairTask.setOverhaulId(repairTaskUserNameDTO.getUserIds());
        }

    }

    /**
     * 设置检修任务对象的组织机构、站点、专业和子系统属性
     *
     * @param repairTask 要设置属性的检修任务对象
     * @param manager    用于翻译编码的管理器
     */
    private void setTranslatedCode(RepairTask repairTask, InspectionManager manager) {
        setOrganizational(repairTask, manager);
        setSiteName(repairTask, manager);
        setLineName(repairTask, manager);
        setMajorName(repairTask, manager);
        setSystemName(repairTask, manager);
        setRepairTaskWeekName(repairTask);
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
     * 设置检修任务的线路名称，根据站点信息设置的线路名称
     * @param repairTask
     * @param manager
     */
    private void setLineName(RepairTask repairTask, InspectionManager manager) {
        if (repairTask.getSiteCode() != null) {
            List<String> stationCodeList = Arrays.asList(repairTask.getSiteCode().split(","));
            repairTask.setLineName(manager.translateLineListByStationCodeList(stationCodeList));
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

    /**
     * 设置检修任务的周名称。
     * 周名称包含所属周次、周开始日期和结束日期。
     *
     * @param repairTask 检修任务对象
     */
    private void setRepairTaskWeekName(RepairTask repairTask) {
        if (repairTask.getYear() != null && repairTask.getWeeks() != null) {
            Date[] dateByWeek = DateUtils.getDateByWeek(repairTask.getYear(), repairTask.getWeeks());
            if (dateByWeek.length != 0) {
                String weekName = String.format("第%d周(%s~%s)", repairTask.getWeeks(), DateUtil.format(dateByWeek[0], "yyyy/MM/dd"), DateUtil.format(dateByWeek[1], "yyyy/MM/dd"));
                repairTask.setWeekName(weekName);
            }
        }
    }
}
