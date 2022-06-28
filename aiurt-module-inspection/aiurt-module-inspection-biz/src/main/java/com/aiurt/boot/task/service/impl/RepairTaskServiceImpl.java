package com.aiurt.boot.task.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.manager.dto.*;
import com.aiurt.boot.plan.dto.RepairDeviceDTO;
import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.plan.entity.RepairPoolCodeContent;
import com.aiurt.boot.task.dto.CheckListDTO;
import com.aiurt.boot.task.entity.RepairTask;
import com.aiurt.boot.task.dto.RepairTaskDTO;
import com.aiurt.boot.task.entity.RepairTaskResult;
import com.aiurt.boot.task.mapper.RepairTaskMapper;
import com.aiurt.boot.task.service.IRepairTaskService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.*;

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

            //根据设备编码翻译设备名称和设备类型名称
            List<RepairDeviceDTO> repairDeviceDTOList = manager.queryDeviceByCodes(Arrays.asList(e.getEquipmentCode()));
            repairDeviceDTOList.forEach(q->{
                //设备名称
                e.setEquipmentName(q.getName());
                //设备类型名称
                e.setDeviceTypeName(q.getDeviceTypeName());
            });
            //检修人名称
            if (e.getOverhaulId()!=null){
                LoginUser userById = sysBaseAPI.getUserById(e.getOverhaulId());
                e.setOverhaulName(userById.getUsername());
            }
            if (e.getDeviceId()!=null && CollectionUtil.isNotEmpty(repairTasks)){
                //正常项
                List<RepairTaskResult> repairTaskResults = repairTaskMapper.selectSingle(e.getDeviceId(), 1);
                e.setNormal(repairTaskResults.size());
                //异常项
                List<RepairTaskResult> repairTaskResults1 = repairTaskMapper.selectSingle(e.getDeviceId(), 2);
                e.setAbnormal(repairTaskResults1.size());
            }
        });
        return pageList.setRecords(repairTasks);
    }

    @Override
    public List<MajorDTO> selectMajorCodeList(String taskId) {
        //根据检修任务id查询专业
        List<RepairTaskDTO> repairTaskDTOList = repairTaskMapper.selectCodeList(taskId);
        List<String> majorCodes1 = new ArrayList<>();
        List<String> systemCode = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(repairTaskDTOList)){
            repairTaskDTOList.forEach(e->{
                String majorCode = e.getMajorCode();
                String systemCode1 = e.getSystemCode();
                majorCodes1.add(majorCode);
                systemCode.add(systemCode1);
            });
        }
        //根据专业编码查询对应的专业子系统
        List<MajorDTO> majorDTOList = repairTaskMapper.translateMajor(majorCodes1);
        if (CollectionUtil.isNotEmpty(majorDTOList)){
            majorDTOList.forEach(q -> {
                systemCode.forEach(o->{
                    List<SubsystemDTO> subsystemDTOList = repairTaskMapper.translateSubsystem(q.getMajorCode(),o);
                    q.setSubsystemDTOList(subsystemDTOList);
                });
            });
        }
        return majorDTOList;
    }

    @Override
    public EquipmentOverhaulDTO selectEquipmentOverhaulList(String taskId) {
        //根据检修任务id查询设备
        List<RepairTaskDTO> repairTaskDTOList = repairTaskMapper.selectCodeList(taskId);
        List<String> deviceCodeList = new ArrayList<>();
        List<OverhaulDTO> overhaulDTOList = new ArrayList<>();
        repairTaskDTOList.forEach(e->{
            String deviceTypeCode = e.getDeviceTypeCode();
            deviceCodeList.add(deviceTypeCode);
            OverhaulDTO overhaulDTO = new OverhaulDTO();
            overhaulDTO.setStandardId(e.getStandardId());
            overhaulDTO.setOverhaulStandardName(e.getOverhaulStandardName());
            overhaulDTOList.add(overhaulDTO);
        });

        List<EquipmentDTO> equipmentDTOList = repairTaskMapper.queryNameByCode(deviceCodeList);
        EquipmentOverhaulDTO equipmentOverhaulDTO = new EquipmentOverhaulDTO();
        equipmentOverhaulDTO.setEquipmentDTOList(equipmentDTOList);
        equipmentOverhaulDTO.setOverhaulDTOList(overhaulDTOList);
        return equipmentOverhaulDTO;
    }

    @Override
    public CheckListDTO selectCheckList(String deviceId,String overhaulCode) {
        CheckListDTO checkListDTO = repairTaskMapper.selectCheckList(deviceId);
        if (checkListDTO.getDeviceId()!=null && ObjectUtil.isNotNull(checkListDTO)){
            List<RepairTaskResult> repairTaskResults = repairTaskMapper.selectSingle(checkListDTO.getDeviceId(), 1);
            checkListDTO.setNormal(repairTaskResults.size());
            List<RepairTaskResult> repairTaskResults1 = repairTaskMapper.selectSingle(checkListDTO.getDeviceId(), 2);
            checkListDTO.setAbnormal(repairTaskResults1.size());
        }
        //专业
        checkListDTO.setMajorName(manager.translateMajor(Arrays.asList(checkListDTO.getMajorCode()),InspectionConstant.MAJOR));

        //子系统
        checkListDTO.setSystemName(manager.translateMajor(Arrays.asList(checkListDTO.getSystemCode()),InspectionConstant.SUBSYSTEM));

        //根据设备编码翻译设备名称和设备类型名称
        List<RepairDeviceDTO> repairDeviceDTOList = manager.queryDeviceByCodes(Arrays.asList(checkListDTO.getEquipmentCode()));
        repairDeviceDTOList.forEach(q->{
            //设备名称
            checkListDTO.setEquipmentName(q.getName());
            //设备类型名称
            checkListDTO.setDeviceTypeName(q.getDeviceTypeName());
        });
        //检修人名称
        if (checkListDTO.getOverhaulId()!=null){
            LoginUser userById = sysBaseAPI.getUserById(checkListDTO.getOverhaulId());
            checkListDTO.setOverhaulName(userById.getUsername());
        }

        //设备位置
        if(checkListDTO.getEquipmentCode()!=null){
            List<StationDTO> stationDTOList = repairTaskMapper.selectStationList(overhaulCode);
            checkListDTO.setEquipmentLocation(manager.translateStation(stationDTOList));
        }
        //检修位置
        if(checkListDTO.getEquipmentCode()==null && checkListDTO.getSpecificLocation()!=null){
            List<StationDTO> stationDTOList = new ArrayList<>();
            stationDTOList.forEach(e->{
                e.setLineCode(checkListDTO.getStationCode());
                e.setLineCode(checkListDTO.getLineCode());
                e.setLineCode(checkListDTO.getSpecificLocation());
            });
            String station = manager.translateStation(stationDTOList);
            String string = checkListDTO.getSpecificLocation()+station;
            checkListDTO.setMaintenancePosition(string);
        }


        checkListDTO.setRepairTaskResultList(selectCodeContentList(checkListDTO.getDeviceId()));
        return checkListDTO;
    }


    /**
     * 检修单详情查询检修结果
     *
     * @param id 检修
     * @return 构造树形
     */
    private List<RepairTaskResult> selectCodeContentList(String id) {
        List<RepairTaskResult> repairTaskResults1 = repairTaskMapper.selectSingle(id,null);
        repairTaskResults1.forEach(r -> {
            //检修结果
            r.setStatusName(sysBaseAPI.translateDict(DictConstant.OVERHAUL_RESULT, String.valueOf(r.getStatus())));
            //备注
            if (r.getUnNote() ==null){
                r.setUnNote("无");
            }
            //检修值
            if(r.getStatusItem()==1){
                r.setInspeciontValueName(null);
            }
            if(r.getStatusItem()==2){
                r.setInspeciontValueName(sysBaseAPI.translateDict(r.getDictCode(), String.valueOf(r.getInspeciontValue())));
            }
            if(r.getStatusItem()==3){
                r.setInspeciontValueName(r.getNote());
            }
        });
        return treeFirst(repairTaskResults1);
    }

    /**
     * 构造树，不固定根节点
     *
     * @param list 全部数据
     * @return 构造好以后的树形
     */
    public static List<RepairTaskResult> treeFirst(List<RepairTaskResult> list) {
        //这里的Menu是我自己的实体类，参数只需要菜单id和父id即可，其他元素可任意增添
        Map<String, RepairTaskResult> map = new HashMap<>(50);
        for (RepairTaskResult treeNode : list) {
            map.put(treeNode.getId(), treeNode);
        }
        return addChildren(list, map);
    }


    /**
     * @param list
     * @param map
     * @return
     */
    private static List<RepairTaskResult> addChildren(List<RepairTaskResult> list, Map<String, RepairTaskResult> map) {
        List<RepairTaskResult> rootNodes = new ArrayList<>();
        for (RepairTaskResult treeNode : list) {
            RepairTaskResult parentHave = map.get(treeNode.getPid());
            if (ObjectUtil.isEmpty(parentHave)) {
                rootNodes.add(treeNode);
            } else {
                //当前位置显示实体类中的List元素定义的参数为null，出现空指针异常错误
                if (ObjectUtil.isEmpty(parentHave.getChildren())) {
                    parentHave.setChildren(new ArrayList<RepairTaskResult>());
                    parentHave.getChildren().add(treeNode);
                } else {
                    parentHave.getChildren().add(treeNode);
                }
            }
        }
        return rootNodes;
    }

}
