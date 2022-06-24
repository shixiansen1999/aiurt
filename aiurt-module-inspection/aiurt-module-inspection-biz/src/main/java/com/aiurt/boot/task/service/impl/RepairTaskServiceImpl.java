package com.aiurt.boot.task.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.manager.dto.MajorDTO;
import com.aiurt.boot.manager.dto.SubsystemDTO;
import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.boot.task.dto.RepairTaskDTO;
import com.aiurt.boot.task.mapper.RepairTaskMapper;
import com.aiurt.boot.task.service.IRepairTaskService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: repair_task
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Service
public class RepairTaskServiceImpl extends ServiceImpl<RepairTaskMapper, RepairTask> implements IRepairTaskService {

    @Autowired
    private  RepairTaskMapper repairTaskMapper;

    @Resource
    private ISysBaseAPI sysBaseAPI;

    @Resource
    private InspectionManager manager;

    @Override
    public Page<RepairTask> selectables(Page<RepairTask> pageList, RepairTask condition) {
        List<RepairTask> lists = repairTaskMapper.selectables(pageList,condition);
        lists.forEach(e->{

            //组织机构
            if (e.getOrgCode()!=null) {
                String[] split1 = e.getOrgCode().split(",");
                List<String> list1 = Arrays.asList(split1);
                e.setOrganizational(manager.translateOrg(list1));
            }
            //站点
            if (e.getSiteCode()!=null){
                String[] split2 = e.getSiteCode().split(",");
                List<String> list2 = Arrays.asList(split2);
                list2.forEach(q->{
                    List<StationDTO> dtoList = repairTaskMapper.selectStationList(q);
                    e.setSiteName(manager.translateStation(dtoList));
                });
            }

            //专业
            if (e.getMajorCode()!=null){
                String[] split3 = e.getMajorCode().split(",");
                List<String> list3 = Arrays.asList(split3);
                e.setMajorName(manager.translateMajor(list3,InspectionConstant.MAJOR));
            }

            //子系统
            if (e.getSystemCode()!=null){
                String[] split4 = e.getSystemCode().split(",");
                List<String> list4 = Arrays.asList(split4);
                e.setSystemName(manager.translateMajor(list4,InspectionConstant.SUBSYSTEM));
            }

            //检修周期类型
            e.setTypeName(sysBaseAPI.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(e.getType())));

            //检修任务状态
            e.setStatusName(sysBaseAPI.translateDict(DictConstant.INSPECTION_TASK_STATE, String.valueOf(e.getStatus())));

            //是否需要审核
            e.setIsConfirmName(sysBaseAPI.translateDict(DictConstant.INSPECTION_IS_CONFIRM, String.valueOf(e.getIsConfirm())));

            //是否需要验收
            e.setIsReceiptName(sysBaseAPI.translateDict(DictConstant.INSPECTION_IS_CONFIRM, String.valueOf(e.getIsReceipt())));

            //任务来源
            e.setSourceName(sysBaseAPI.translateDict(DictConstant.PATROL_TASK_ACCESS, String.valueOf(e.getSource())));

            //作业类型
            e.setWorkTypeName(sysBaseAPI.translateDict(DictConstant.WORK_TYPE, String.valueOf(e.getWorkType())));

        });
        return pageList.setRecords(lists);
    }

    @Override
    public Page<RepairTaskDTO> selectTasklet(Page<RepairTaskDTO> pageList, RepairTaskDTO condition) {
        List<RepairTaskDTO> repairTasks = repairTaskMapper.selectTasklet(pageList, condition);
        repairTasks.forEach(e->{
            //检修结果
            e.setMaintenanceResultsName(sysBaseAPI.translateDict(DictConstant.OVERHAUL_RESULT, String.valueOf(e.getMaintenanceResults())));

            //专业
            e.setMajorName(manager.translateMajor(Arrays.asList(e.getMajorCode()),InspectionConstant.MAJOR));

            //子系统
            e.setSystemName(manager.translateMajor(Arrays.asList(e.getSystemCode()),InspectionConstant.SUBSYSTEM));

            //检修人名称
            if (e.getOverhaulId()!=null){
                LoginUser userById = sysBaseAPI.getUserById(e.getOverhaulId());
                e.setOverhaulName(userById.getUsername());
            }
        });
        return pageList.setRecords(repairTasks);
    }

    @Override
    public List<MajorDTO> selectMajorCodeList(String id) {
        //根据检修任务id查询专业
        List<RepairTaskDTO> repairTaskDTOList = repairTaskMapper.selectCodeList(id);
        List<String> majorCodes1 = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(repairTaskDTOList)){
            repairTaskDTOList.forEach(e->{
                String majorCode = e.getMajorCode();
                majorCodes1.add(majorCode);
            });
        }
        //根据专业编码查询对应的专业子系统
        List<MajorDTO> majorDTOList = repairTaskMapper.translateMajor(majorCodes1);
        if (CollectionUtil.isNotEmpty(majorDTOList)){
            majorDTOList.forEach(q -> {
                List<SubsystemDTO> subsystemDTOList = repairTaskMapper.translateSubsystem(q.getMajorCode());
                q.setSubsystemDTOList(subsystemDTOList);
            });
        }
        return majorDTOList;
    }
}
