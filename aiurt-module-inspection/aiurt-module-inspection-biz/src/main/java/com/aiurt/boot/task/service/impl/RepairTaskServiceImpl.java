package com.aiurt.boot.task.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.manager.dto.*;
import com.aiurt.boot.plan.dto.RepairDeviceDTO;
import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.entity.RepairPoolOrgRel;
import com.aiurt.boot.plan.entity.RepairPoolStationRel;
import com.aiurt.boot.plan.mapper.RepairPoolMapper;
import com.aiurt.boot.plan.mapper.RepairPoolOrgRelMapper;
import com.aiurt.boot.plan.mapper.RepairPoolStationRelMapper;
import com.aiurt.boot.plan.service.IRepairPoolService;
import com.aiurt.boot.task.dto.CheckListDTO;
import com.aiurt.boot.task.dto.RepairTaskDTO;
import com.aiurt.boot.task.dto.WriteMonadDTO;
import com.aiurt.boot.task.entity.*;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.boot.task.service.IRepairTaskService;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.DateUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: repair_task
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@Service
public class RepairTaskServiceImpl extends ServiceImpl<RepairTaskMapper, RepairTask> implements IRepairTaskService {

    @Autowired
    private RepairTaskMapper repairTaskMapper;
    @Autowired
    private RepairTaskDeviceRelMapper repairTaskDeviceRelMapper;
    @Autowired
    private RepairTaskResultMapper repairTaskResultMapper;
    @Autowired
    private RepairTaskStandardRelMapper repairTaskStandardRelMapper;
    @Autowired
    private RepairTaskStationRelMapper repairTaskStationRelMapper;
    @Autowired
    private RepairTaskPeerRelMapper repairTaskPeerRelMapper;
    @Autowired
    private RepairTaskUserMapper repairTaskUserMapper;
    @Autowired
    private RepairTaskOrgRelMapper repairTaskOrgRelMapper;
    @Autowired
    private RepairPoolMapper repairPoolMapper;
    @Autowired
    private RepairTaskEnclosureMapper repairTaskEnclosureMapper;
    @Resource
    private ISysBaseAPI sysBaseApi;
    @Resource
    private InspectionManager manager;
    @Resource
    private IRepairPoolService repairPoolService;
    @Resource
    private RepairPoolStationRelMapper repairPoolStationRelMapper;
    @Resource
    private RepairPoolOrgRelMapper orgRelMapper;


    @Override
    public Page<RepairTask> selectables(Page<RepairTask> pageList, RepairTask condition) {
        //去掉查询参数的所有空格
        if (condition.getCode() != null) {
            condition.setCode(condition.getCode().replaceAll(" ", ""));
        }
        List<RepairTask> lists = repairTaskMapper.selectables(pageList, condition);
        lists.forEach(e -> {
            //组织机构
            if (e.getOrgCode() != null) {
                String[] split1 = e.getOrgCode().split(",");
                List<String> list1 = Arrays.asList(split1);
                e.setOrganizational(manager.translateOrg(list1));
            }
            //站点
            if (e.getSiteCode() != null) {
                String[] split2 = e.getSiteCode().split(",");
                List<String> list2 = Arrays.asList(split2);
                list2.forEach(q -> {
                    List<StationDTO> dtoList = repairTaskMapper.selectStationList(q);
                    e.setSiteName(manager.translateStation(dtoList));
                });
            }
            //专业
            if (e.getMajorCode() != null) {
                String[] split3 = e.getMajorCode().split(",");
                List<String> list3 = Arrays.asList(split3);
                e.setMajorName(manager.translateMajor(list3, InspectionConstant.MAJOR));
            }

            //子系统
            if (e.getSystemCode() != null) {
                String[] split4 = e.getSystemCode().split(",");
                List<String> list4 = Arrays.asList(split4);
                e.setSystemName(manager.translateMajor(list4, InspectionConstant.SUBSYSTEM));
            }

            //检修周期类型
            e.setTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(e.getType())));

            //检修任务状态
            e.setStatusName(sysBaseApi.translateDict(DictConstant.INSPECTION_TASK_STATE, String.valueOf(e.getStatus())));

            //是否需要审核
            e.setIsConfirmName(sysBaseApi.translateDict(DictConstant.INSPECTION_IS_CONFIRM, String.valueOf(e.getIsConfirm())));

            //是否需要验收
            e.setIsReceiptName(sysBaseApi.translateDict(DictConstant.INSPECTION_IS_CONFIRM, String.valueOf(e.getIsReceipt())));

            //任务来源
            e.setSourceName(sysBaseApi.translateDict(DictConstant.PATROL_TASK_ACCESS, String.valueOf(e.getSource())));

            //作业类型
            e.setWorkTypeName(sysBaseApi.translateDict(DictConstant.WORK_TYPE, String.valueOf(e.getWorkType())));

            if (e.getCode() != null) {
                //根据检修任务code查询
                List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(
                        new LambdaQueryWrapper<RepairTaskUser>()
                                .eq(RepairTaskUser::getRepairTaskCode, e.getCode()));
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
                    e.setOverhaulId(stringBuffer.toString());
                }
                ArrayList<String> userList = new ArrayList<>();
                collect.forEach(o -> {
                    LoginUser userById = sysBaseApi.getUserById(o);
                    userList.add(userById.getRealname());
                });
                if (CollectionUtil.isNotEmpty(userList)) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (String t : userList) {
                        stringBuffer.append(t);
                        stringBuffer.append(",");
                    }
                    if (stringBuffer.length() > 0) {
                        stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                    }
                    e.setOverhaulName(stringBuffer.toString());
                }
            }

            // 所属周（相对年）
            if (e.getYear() != null && e.getWeeks() != null) {
                Date[] dateByWeek = DateUtils.getDateByWeek(e.getYear(), e.getWeeks());
                if (dateByWeek.length != 0) {
                    String weekName = String.format("第%d周(%s~%s)", e.getWeeks(), DateUtil.format(dateByWeek[0], "yyyy/MM/dd"), DateUtil.format(dateByWeek[1], "yyyy/MM/dd"));
                    e.setWeekName(weekName);
                }
            }


        });
        return pageList.setRecords(lists);
    }

    @Override
    public Page<RepairTaskDTO> selectTasklet(Page<RepairTaskDTO> pageList, RepairTaskDTO condition) {
        List<RepairTaskDTO> repairTasks = repairTaskMapper.selectTasklet(pageList, condition);
        repairTasks.forEach(e -> {
            //查询同行人
            List<RepairTaskPeerRel> repairTaskPeer = repairTaskPeerRelMapper.selectList(
                    new LambdaQueryWrapper<RepairTaskPeerRel>()
                            .eq(RepairTaskPeerRel::getRepairTaskDeviceCode, e.getOverhaulCode()));
            //名称集合
            List<String> collect3 = repairTaskPeer.stream().map(RepairTaskPeerRel::getRealName).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect3)) {
                StringBuffer stringBuffer = new StringBuffer();
                for (String t : collect3) {
                    stringBuffer.append(t);
                    stringBuffer.append(",");
                }
                if (stringBuffer.length() > 0) {
                    stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                }
                e.setPeerName(stringBuffer.toString());
            }
            //专业
            e.setMajorName(manager.translateMajor(Arrays.asList(e.getMajorCode()), InspectionConstant.MAJOR));

            //子系统
            e.setSystemName(manager.translateMajor(Arrays.asList(e.getSystemCode()), InspectionConstant.SUBSYSTEM));

            //根据设备编码翻译设备名称和设备类型名称
            List<RepairDeviceDTO> repairDeviceDTOList = manager.queryDeviceByCodes(Arrays.asList(e.getEquipmentCode()));
            repairDeviceDTOList.forEach(q -> {
                //设备名称
                e.setEquipmentName(q.getName());
                //设备类型名称
                e.setDeviceTypeName(q.getDeviceTypeName());
            });
            //设备位置
            if (e.getTaskCode() != null) {
                List<StationDTO> stationDTOList = repairTaskMapper.selectStationList(e.getTaskCode());
                e.setEquipmentLocation(manager.translateStation(stationDTOList));
            }
            //检修任务状态
            if (e.getStartTime() == null) {
                e.setTaskStatusName("未开始");
                e.setTaskStatus("0");
            }
            if (e.getStartTime() != null) {
                e.setTaskStatusName("进行中");
                e.setTaskStatus("1");
            }
            if (e.getIsSubmit() != null && e.getIsSubmit().equals(InspectionConstant.IS_EFFECT)) {
                e.setTaskStatusName("已提交");
                e.setTaskStatus("2");
            }
            //提交人名称
            if (e.getOverhaulId() != null) {
                LoginUser userById = sysBaseApi.getUserById(e.getOverhaulId());
                e.setOverhaulName(userById.getRealname());
            }
            if (e.getDeviceId() != null && CollectionUtil.isNotEmpty(repairTasks)) {
                //正常项
                List<RepairTaskResult> repairTaskResults = repairTaskMapper.selectSingle(e.getDeviceId(), InspectionConstant.RESULT_STATUS);
                e.setNormal(repairTaskResults.size());
                //异常项
                List<RepairTaskResult> repairTaskResults1 = repairTaskMapper.selectSingle(e.getDeviceId(), InspectionConstant.NO_RESULT_STATUS);
                e.setAbnormal(repairTaskResults1.size());
            }
            //未开始的数量
            long count1 = repairTasks.stream().filter(repairTaskDTO -> repairTaskDTO.getStartTime() == null).count();
            e.setNotStarted((int) count1);
            //进行中的数量
            long count2 = repairTasks.stream().filter(repairTaskDTO -> repairTaskDTO.getStartTime() != null && repairTaskDTO.getIsSubmit() != null && repairTaskDTO.getIsSubmit().equals(InspectionConstant.NO_IS_EFFECT)).count();
            e.setHaveInHand((int) count2);
            //已提交的数量
            long count3 = repairTasks.stream().filter(repairTaskDTO -> repairTaskDTO.getIsSubmit() != null && repairTaskDTO.getIsSubmit().equals(InspectionConstant.IS_EFFECT)).count();
            e.setSubmitted((int) count3);
        });
        return pageList.setRecords(repairTasks);
    }

    @Override
    public Page<RepairTaskDTO> repairSelectTaskletForDevice(Page<RepairTaskDTO> pageList, RepairTaskDTO condition) {
        List<RepairTaskDTO> repairTasks = repairTaskMapper.selectTaskletForDevice(pageList, condition);
        repairTasks.forEach(e -> {
            //查询同行人
            List<RepairTaskPeerRel> repairTaskPeer = repairTaskPeerRelMapper.selectList(
                    new LambdaQueryWrapper<RepairTaskPeerRel>()
                            .eq(RepairTaskPeerRel::getRepairTaskDeviceCode, e.getOverhaulCode()));
            //名称集合
            List<String> collect3 = repairTaskPeer.stream().map(RepairTaskPeerRel::getRealName).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect3)) {
                StringBuffer stringBuffer = new StringBuffer();
                for (String t : collect3) {
                    stringBuffer.append(t);
                    stringBuffer.append(",");
                }
                if (stringBuffer.length() > 0) {
                    stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                }
                e.setPeerName(stringBuffer.toString());
            }
            //专业
            e.setMajorName(manager.translateMajor(Arrays.asList(e.getMajorCode()), InspectionConstant.MAJOR));

            //子系统
            e.setSystemName(manager.translateMajor(Arrays.asList(e.getSystemCode()), InspectionConstant.SUBSYSTEM));

            //根据设备编码翻译设备名称和设备类型名称
            List<RepairDeviceDTO> repairDeviceDTOList = manager.queryDeviceByCodes(Arrays.asList(e.getEquipmentCode()));
            repairDeviceDTOList.forEach(q -> {
                //设备名称
                e.setEquipmentName(q.getName());
                //设备类型名称
                e.setDeviceTypeName(q.getDeviceTypeName());
            });
            //设备位置
            if (e.getTaskCode() != null) {
                List<StationDTO> stationDTOList = repairTaskMapper.selectStationList(e.getTaskCode());
                e.setEquipmentLocation(manager.translateStation(stationDTOList));
            }
            //检修任务状态
            if (e.getStartTime() == null) {
                e.setTaskStatusName("未开始");
                e.setTaskStatus("0");
            }
            if (e.getStartTime() != null) {
                e.setTaskStatusName("进行中");
                e.setTaskStatus("1");
            }
            if (e.getIsSubmit() != null && e.getIsSubmit().equals(InspectionConstant.IS_EFFECT)) {
                e.setTaskStatusName("已提交");
                e.setTaskStatus("2");
            }
            //提交人名称
            if (e.getOverhaulId() != null) {
                LoginUser userById = sysBaseApi.getUserById(e.getOverhaulId());
                e.setOverhaulName(userById.getUsername());
            }
            if (e.getDeviceId() != null && CollectionUtil.isNotEmpty(repairTasks)) {
                //正常项
                List<RepairTaskResult> repairTaskResults = repairTaskMapper.selectSingle(e.getDeviceId(), InspectionConstant.RESULT_STATUS);
                e.setNormal(repairTaskResults.size());
                //异常项
                List<RepairTaskResult> repairTaskResults1 = repairTaskMapper.selectSingle(e.getDeviceId(), InspectionConstant.NO_RESULT_STATUS);
                e.setAbnormal(repairTaskResults1.size());
            }
            //未开始的数量
            long count1 = repairTasks.stream().filter(repairTaskDTO -> repairTaskDTO.getStartTime() == null).count();
            e.setNotStarted((int) count1);
            //进行中的数量
            long count2 = repairTasks.stream().filter(repairTaskDTO -> repairTaskDTO.getStartTime() != null).count();
            e.setHaveInHand((int) count2);
            //已提交的数量
            long count3 = repairTasks.stream().filter(repairTaskDTO -> repairTaskDTO.getIsSubmit() != null && repairTaskDTO.getIsSubmit().equals(InspectionConstant.IS_EFFECT)).count();
            e.setSubmitted((int) count3);
        });
        return pageList.setRecords(repairTasks);
    }

    @Override
    public List<MajorDTO> selectMajorCodeList(String taskId) {
        //根据检修任务id查询专业
        List<RepairTaskDTO> repairTaskDTOList = repairTaskMapper.selectCodeList(taskId, null, null);
        List<String> majorCodes1 = new ArrayList<>();
        List<String> systemCode = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(repairTaskDTOList)) {
            repairTaskDTOList.forEach(e -> {
                String majorCode = e.getMajorCode();
                String systemCode1 = e.getSystemCode();
                majorCodes1.add(majorCode);
                systemCode.add(systemCode1);
            });
        }
        //根据专业编码查询对应的专业子系统
        List<MajorDTO> majorDTOList = repairTaskMapper.translateMajor(majorCodes1);
        if (CollectionUtil.isNotEmpty(majorDTOList)) {
            majorDTOList.forEach(q -> {
                systemCode.forEach(o -> {
                    List<SubsystemDTO> subsystemDTOList = repairTaskMapper.translateSubsystem(q.getMajorCode(), o);
                    q.setSubsystemDTOList(subsystemDTOList);
                });
            });
        }
        return majorDTOList;
    }

    @Override
    public EquipmentOverhaulDTO selectEquipmentOverhaulList(String taskId, String majorCode, String subsystemCode) {
        //根据检修任务id查询设备和
        List<RepairTaskDTO> repairTaskDTOList = repairTaskMapper.selectCodeList(taskId, majorCode, subsystemCode);
        List<String> deviceCodeList = new ArrayList<>();
        List<OverhaulDTO> overhaulDTOList = new ArrayList<>();
        repairTaskDTOList.forEach(e -> {
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
    public CheckListDTO selectCheckList(String deviceId, String overhaulCode) {
        CheckListDTO checkListDTO = repairTaskMapper.selectCheckList(deviceId);
        if (checkListDTO.getDeviceId() != null && ObjectUtil.isNotNull(checkListDTO)) {
            List<RepairTaskResult> repairTaskResults = repairTaskMapper.selectSingle(checkListDTO.getDeviceId(), 1);
            checkListDTO.setNormal(repairTaskResults.size());
            List<RepairTaskResult> repairTaskResults1 = repairTaskMapper.selectSingle(checkListDTO.getDeviceId(), 2);
            checkListDTO.setAbnormal(repairTaskResults1.size());
        }
        //检修单名称
        if (checkListDTO.getResultCode() != null) {
            checkListDTO.setResultName("检修单" + checkListDTO.getResultCode());

            //同行人列表
            List<RepairTaskPeerRel> repairTaskPeer = repairTaskPeerRelMapper.selectList(
                    new LambdaQueryWrapper<RepairTaskPeerRel>()
                            .eq(RepairTaskPeerRel::getRepairTaskDeviceCode, checkListDTO.getResultCode()));
            if (CollectionUtil.isNotEmpty(repairTaskPeer)) {
                List<ColleaguesDTO> realList = new ArrayList<>();
                repairTaskPeer.forEach(p -> {
                    ColleaguesDTO colleaguesDTO = new ColleaguesDTO();
                    colleaguesDTO.setRealId(p.getUserId());
                    colleaguesDTO.setRealName(p.getRealName());
                    realList.add(colleaguesDTO);
                });
                checkListDTO.setRealList(realList);
            }

            //组织机构
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (sysUser.getOrgCode() != null) {
                String s = manager.translateOrg(Arrays.asList(sysUser.getOrgCode()));
                checkListDTO.setOrganization(s);
            }

            //同行人名称
            List<String> collect3 = repairTaskPeer.stream().map(RepairTaskPeerRel::getRealName).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect3)) {
                StringBuffer stringBuffer = new StringBuffer();
                for (String t : collect3) {
                    stringBuffer.append(t);
                    stringBuffer.append(",");
                }
                if (stringBuffer.length() > 0) {
                    stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                }
                checkListDTO.setPeer(stringBuffer.toString());
            }
        }

        //专业
        checkListDTO.setMajorName(manager.translateMajor(Arrays.asList(checkListDTO.getMajorCode()), InspectionConstant.MAJOR));

        //子系统
        checkListDTO.setSystemName(manager.translateMajor(Arrays.asList(checkListDTO.getSystemCode()), InspectionConstant.SUBSYSTEM));

        //根据设备编码翻译设备名称和设备类型名称
        List<RepairDeviceDTO> repairDeviceDTOList = manager.queryDeviceByCodes(Arrays.asList(checkListDTO.getEquipmentCode()));
        repairDeviceDTOList.forEach(q -> {
            //设备名称
            checkListDTO.setEquipmentName(q.getName());
            //设备类型名称
            checkListDTO.setDeviceTypeName(q.getDeviceTypeName());
            //设备类型编码
            checkListDTO.setDeviceTypeCode(q.getDeviceTypeCode());
            //设备id
            checkListDTO.setEquipmentId(q.getDeviceId());
            //根据站点编码翻译站点名称
            if (q.getStationCode() != null) {
                String s = manager.translateStation(q.getStationCode());
                checkListDTO.setStationsName(s);
                checkListDTO.setSiteCode(q.getStationCode());
            }
            //线路编码
            checkListDTO.setLineCode(q.getLineCode());
            //设备专业
            checkListDTO.setDeviceMajorName(q.getMajorName());
            //设备子系统
            checkListDTO.setDeviceSystemName(q.getDeviceTypeName());
        });
        //提交人名称
        if (checkListDTO.getOverhaulId() != null) {
            LoginUser userById = sysBaseApi.getUserById(checkListDTO.getOverhaulId());
            checkListDTO.setOverhaulName(userById.getRealname());
        }

        //设备位置
        if (checkListDTO.getEquipmentCode() != null) {
            List<StationDTO> stationDTOList = repairTaskMapper.selectStationLists(checkListDTO.getEquipmentCode());
            checkListDTO.setEquipmentLocation(manager.translateStation(stationDTOList));
        }
        //检修位置
        if (checkListDTO.getSpecificLocation() != null) {
            List<StationDTO> stationDTOList = new ArrayList<>();
            stationDTOList.forEach(e -> {
                e.setStationCode(checkListDTO.getStationCode());
                e.setLineCode(checkListDTO.getLineCode());
                e.setPositionCode(checkListDTO.getPositionCode());
            });
            String station = manager.translateStation(stationDTOList);
            if (station != null) {
                String string = checkListDTO.getSpecificLocation() + station;
                checkListDTO.setMaintenancePosition(string);
            } else {
                checkListDTO.setMaintenancePosition(checkListDTO.getSpecificLocation());
            }

        }
        //站点位置
        if (checkListDTO.getEquipmentCode() == null) {
            List<StationDTO> stationDTOList = new ArrayList<>();
            stationDTOList.forEach(e -> {
                e.setStationCode(checkListDTO.getStationCode());
                e.setLineCode(checkListDTO.getLineCode());
                e.setPositionCode(checkListDTO.getPositionCode());
            });
            String station = manager.translateStation(stationDTOList);
            checkListDTO.setSitePosition(station);
        }

        //构造树形
        checkListDTO.setRepairTaskResultList(selectCodeContentList(checkListDTO.getDeviceId()));
        List<RepairTaskResult> repairTaskResultList = checkListDTO.getRepairTaskResultList();
        ArrayList<String> list = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(repairTaskResultList)) {
            repairTaskResultList.forEach(r -> {
                List<RepairTaskResult> children = r.getChildren();
                //获取检修单的检修结果子
                list.add(r.getId());
                if (CollectionUtil.isNotEmpty(children)) {
                    List<String> collect = children.stream().map(RepairTaskResult::getId).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(collect)) {
                        list.addAll(collect);
                    }
                }
                //检查项的数量父级
                long count1 = repairTaskResultList.stream().filter(repairTaskResult -> repairTaskResult.getType() == 1).count();
                //已检修的数量父级
                long count2 = repairTaskResultList.stream().filter(repairTaskResult -> repairTaskResult.getStatus() != null && repairTaskResult.getType() == 1).count();
                //待检修的数量父级
                long count3 = repairTaskResultList.stream().filter(repairTaskResult -> repairTaskResult.getStatus() == null && repairTaskResult.getType() == 1).count();
                if (CollectionUtils.isNotEmpty(children)) {
                    //检查项的数量子级
                    long count11 = children.stream().filter(repairTaskResult -> repairTaskResult.getType() == 1).count();
                    checkListDTO.setMaintenanceItemsQuantity((int) count1 + (int) count11);
                    //已检修的数量子级
                    long count22 = children.stream().filter(repairTaskResult -> repairTaskResult.getStatus() != null && repairTaskResult.getType() == 1).count();
                    checkListDTO.setOverhauledQuantity((int) count2 + (int) count22);
                    //待检修的数量子级
                    long count33 = children.stream().filter(repairTaskResult -> repairTaskResult.getStatus() == null && repairTaskResult.getType() == 1).count();
                    checkListDTO.setToBeOverhauledQuantity((int) count3 + (int) count33);
                } else {
                    checkListDTO.setMaintenanceItemsQuantity((int) count1);
                    checkListDTO.setOverhauledQuantity((int) count2);
                    checkListDTO.setToBeOverhauledQuantity((int) count3);
                }
            });
            if (CollectionUtils.isNotEmpty(list)) {
                List<RepairTaskEnclosure> repairTaskDevice = repairTaskEnclosureMapper.selectList(
                        new LambdaQueryWrapper<RepairTaskEnclosure>()
                                .in(RepairTaskEnclosure::getRepairTaskResultId, list));
                if (CollectionUtils.isNotEmpty(repairTaskDevice)) {
                    //获取检修单的检修结果的附件
                    checkListDTO.setEnclosureUrl(repairTaskDevice.stream().map(RepairTaskEnclosure::getUrl).collect(Collectors.toList()));
                }
            }

        }
        return checkListDTO;
    }

    /**
     * 检修单详情查询检修结果
     *
     * @param id 检修
     * @return 构造树形
     */
    private List<RepairTaskResult> selectCodeContentList(String id) {
        List<RepairTaskResult> repairTaskResults1 = repairTaskMapper.selectSingle(id, null);
        repairTaskResults1.forEach(r -> {
            //检修结果
            r.setStatusName(sysBaseApi.translateDict(DictConstant.OVERHAUL_RESULT, String.valueOf(r.getStatus())));

            //检查项类型
            r.setTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_PROJECT, String.valueOf(r.getType())));

            //父级名称
            if (r.getPid() != null) {
                RepairTaskResult repairTaskResult = repairTaskResultMapper.selectById(r.getPid());
                if (ObjectUtil.isNotNull(repairTaskResult)) {
                    r.setParentName(repairTaskResult.getName());
                }
            }
            //附件url
            if (r.getId() != null) {
                LambdaQueryWrapper<RepairTaskEnclosure> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
                List<RepairTaskEnclosure> repairTaskDevice = repairTaskEnclosureMapper.selectList(objectLambdaQueryWrapper.eq(RepairTaskEnclosure::getRepairTaskResultId, r.getId()));
                if (CollectionUtils.isNotEmpty(repairTaskDevice)) {
                    List<String> stringList = repairTaskDevice.stream().map(RepairTaskEnclosure::getUrl).collect(Collectors.toList());
                    if (CollectionUtils.isNotEmpty(stringList)) {
                        r.setUrl(stringList);
                    }
                }
            }
            //检修人名称
            if (r.getStaffId() != null) {
                LoginUser userById = sysBaseApi.getUserById(r.getStaffId());
                r.setStaffName(userById.getRealname());
            }
            //备注
            if (r.getUnNote() == null) {
                r.setUnNote("无");
            }
            if (r.getStatusItem() != null) {
                //检修值
                if (r.getStatusItem().equals(InspectionConstant.NO_STATUS_ITEM)) {
                    r.setInspeciontValueName(null);
                }
                if (r.getStatusItem().equals(InspectionConstant.STATUS_ITEM_CHOICE)) {
                    r.setInspeciontValueName(sysBaseApi.translateDict(r.getDictCode(), String.valueOf(r.getInspeciontValue())));
                }
                if (r.getStatusItem().equals(InspectionConstant.STATUS_ITEM_INPUT)) {
                    r.setInspeciontValueName(r.getNote());
                }
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

    @Override
    public void toExamine(ExamineDTO examineDTO) {
        RepairTask repairTask = repairTaskMapper.selectById(examineDTO.getId());
        if (ObjectUtil.isEmpty(repairTask)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }
        // 是任务的检修人才可以审核
        List<RepairTaskUser> repairTaskUserss = repairTaskUserMapper.selectList(
                new LambdaQueryWrapper<RepairTaskUser>()
                        .eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode())
                        .eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isEmpty(repairTaskUserss)) {
            throw new AiurtBootException("小主，该任务没有对应的检修人");
        } else {
            List<String> userList = repairTaskUserss.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
            if (!userList.contains(manager.checkLogin().getId())) {
                throw new AiurtBootException("小主，只有该任务的检修人才能审核");
            }
        }
        LoginUser loginUser = manager.checkLogin();
        LoginUser userById = sysBaseApi.getUserById(loginUser.getId());
        RepairTask repairTask1 = new RepairTask();
        status(examineDTO, loginUser, userById, repairTask1, repairTask.getRepairPoolId());
        if (examineDTO.getStatus().equals(InspectionConstant.IS_EFFECT) && repairTask.getIsReceipt().equals(InspectionConstant.IS_EFFECT)) {
            //修改检修任务状态
            repairTask1.setId(examineDTO.getId());
            repairTask1.setErrorContent(examineDTO.getContent());
            repairTask1.setConfirmTime(new Date());
            repairTask1.setConfirmUserId(loginUser.getId());
            repairTask1.setConfirmUserName(userById.getRealname());
            repairTask1.setStatus(InspectionConstant.PENDING_RECEIPT);
            repairTaskMapper.updateById(repairTask1);
            // 修改对应检修计划状态
            RepairPool repairPool = repairPoolMapper.selectById(repairTask.getRepairPoolId());
            if (ObjectUtil.isNotEmpty(repairPool)) {
                repairPool.setStatus(InspectionConstant.PENDING_RECEIPT);
                repairPoolMapper.updateById(repairPool);
            }
        }
        if (examineDTO.getStatus().equals(InspectionConstant.IS_EFFECT) && repairTask.getIsReceipt().equals(InspectionConstant.NO_IS_EFFECT)) {
            setId(examineDTO, repairTask1, loginUser, userById, repairTask.getRepairPoolId());
        }
    }

    @Override
    public void toBeImplement(ExamineDTO examineDTO) {
        RepairTask repairTask = repairTaskMapper.selectById(examineDTO.getId());
        if (ObjectUtil.isEmpty(repairTask)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }
        // 是任务的检修人才可以执行
        List<RepairTaskUser> repairTaskUserss = repairTaskUserMapper.selectList(
                new LambdaQueryWrapper<RepairTaskUser>()
                        .eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode())
                        .eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isEmpty(repairTaskUserss)) {
            throw new AiurtBootException("小主，该任务没有对应的检修人");
        } else {
            List<String> userList = repairTaskUserss.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
            if (!userList.contains(manager.checkLogin().getId())) {
                throw new AiurtBootException("小主，只有该任务的检修人才能执行");
            }
        }

        if (ObjectUtil.isEmpty(repairTask)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        // 待执行状态才可以执行
        if (InspectionConstant.PENDING.equals(repairTask.getStatus())) {
            repairTask.setConfirmTime(new Date());
            repairTask.setStatus(InspectionConstant.IN_EXECUTION);
            repairTaskMapper.updateById(repairTask);

            // 修改对应检修计划状态
            RepairPool repairPool = repairPoolMapper.selectById(repairTask.getRepairPoolId());
            if (ObjectUtil.isNotEmpty(repairPool)) {
                repairPool.setStatus(InspectionConstant.IN_EXECUTION);
                repairPoolMapper.updateById(repairPool);
            }

        } else {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        repairTaskMapper.updateById(repairTask);
    }

    @Override
    public void inExecution(ExamineDTO examineDTO) {
        RepairTask repairTask = repairTaskMapper.selectById(examineDTO.getId());
        if (ObjectUtil.isEmpty(repairTask)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }
        // 是任务的检修人才可以提交
        List<RepairTaskUser> repairTaskUserss = repairTaskUserMapper.selectList(
                new LambdaQueryWrapper<RepairTaskUser>()
                        .eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode())
                        .eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isEmpty(repairTaskUserss)) {
            throw new AiurtBootException("小主，该任务没有对应的检修人");
        } else {
            List<String> userList = repairTaskUserss.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
            if (!userList.contains(manager.checkLogin().getId())) {
                throw new AiurtBootException("小主，只有该任务的检修人才能提交");
            }
        }
        RepairTaskDeviceRel repairTaskDeviceRel = new RepairTaskDeviceRel();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        LambdaQueryWrapper<RepairTaskDeviceRel> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<RepairTaskDeviceRel> repairTaskDevice1 = repairTaskDeviceRelMapper.selectList(objectLambdaQueryWrapper.eq(RepairTaskDeviceRel::getRepairTaskId, examineDTO.getId()));

        //查询检修单的主键id集合
        List<String> collect1 = repairTaskDevice1.stream().map(RepairTaskDeviceRel::getId).collect(Collectors.toList());

        //查询未提交的检修单
        List<RepairTaskDeviceRel> repairTaskDevice = repairTaskDeviceRelMapper.selectList(objectLambdaQueryWrapper
                .eq(RepairTaskDeviceRel::getRepairTaskId, examineDTO.getId())
                .eq(RepairTaskDeviceRel::getIsSubmit, InspectionConstant.NO_IS_EFFECT));

        if (CollectionUtil.isNotEmpty(collect1) && CollectionUtil.isEmpty(repairTaskDevice)) {
            collect1.forEach(e -> {
                repairTaskDeviceRel.setId(e);
                repairTaskDeviceRel.setSubmitTime(new Date());
                repairTaskDeviceRel.setIsSubmit(InspectionConstant.IS_EFFECT);
                repairTaskDeviceRel.setEndTime(new Date());
                repairTaskDeviceRelMapper.updateById(repairTaskDeviceRel);
            });
            if (repairTask.getIsConfirm() == 1) {
                //修改检修任务状态
                repairTask.setSubmitUserId(sysUser.getId());
                repairTask.setSumitUserName(sysUser.getRealname());
                repairTask.setSubmitTime(new Date());
                repairTask.setConfirmUrl(examineDTO.getConfirmUrl());
                repairTask.setStatus(InspectionConstant.PENDING_REVIEW);
                // 修改对应检修计划状态
                RepairPool repairPool = repairPoolMapper.selectById(repairTask.getRepairPoolId());
                if (ObjectUtil.isNotEmpty(repairPool)) {
                    repairPool.setStatus(InspectionConstant.PENDING_REVIEW);
                    repairPoolMapper.updateById(repairPool);
                }
            } else {
                //修改检修任务状态
                repairTask.setSubmitUserId(sysUser.getId());
                repairTask.setSumitUserName(sysUser.getRealname());
                repairTask.setSubmitTime(new Date());
                repairTask.setConfirmUrl(examineDTO.getConfirmUrl());
                repairTask.setStatus(InspectionConstant.COMPLETED);
                // 修改对应检修计划状态
                RepairPool repairPool = repairPoolMapper.selectById(repairTask.getRepairPoolId());
                if (ObjectUtil.isNotEmpty(repairPool)) {
                    repairPool.setStatus(InspectionConstant.COMPLETED);
                    repairPoolMapper.updateById(repairPool);
                }
            }
            repairTaskMapper.updateById(repairTask);
        } else {
            throw new AiurtBootException("小主，该检修任务的检修单还没有提交完成哦！");
        }

    }

    @Override
    public void acceptance(ExamineDTO examineDTO) {
        RepairTask repairTask = repairTaskMapper.selectById(examineDTO.getId());
        if (ObjectUtil.isEmpty(repairTask)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }
        // 是任务的检修人才可以验收
        List<RepairTaskUser> repairTaskUserss = repairTaskUserMapper.selectList(
                new LambdaQueryWrapper<RepairTaskUser>()
                        .eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode())
                        .eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isEmpty(repairTaskUserss)) {
            throw new AiurtBootException("小主，该任务没有对应的检修人");
        } else {
            List<String> userList = repairTaskUserss.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
            if (!userList.contains(manager.checkLogin().getId())) {
                throw new AiurtBootException("小主，只有该任务的检修人才能验收");
            }
        }
        RepairTask repairTask1 = new RepairTask();
        LoginUser loginUser = manager.checkLogin();
        LoginUser userById = sysBaseApi.getUserById(loginUser.getId());
        status(examineDTO, loginUser, userById, repairTask1, repairTask.getRepairPoolId());
        if (examineDTO.getStatus().equals(InspectionConstant.IS_EFFECT)) {
            setId(examineDTO, repairTask1, loginUser, userById, repairTask.getRepairPoolId());
        }
    }

    private void status(ExamineDTO examineDTO, LoginUser loginUser, LoginUser userById, RepairTask repairTask1, String id) {
        if (examineDTO.getStatus().equals(InspectionConstant.NO_IS_EFFECT)) {
            //修改检修任务状态
            repairTask1.setId(examineDTO.getId());
            repairTask1.setErrorContent(examineDTO.getContent());
            repairTask1.setConfirmTime(new Date());
            repairTask1.setConfirmUserId(loginUser.getId());
            repairTask1.setConfirmUserName(userById.getRealname());
            repairTask1.setStatus(InspectionConstant.REJECTED);
            repairTaskMapper.updateById(repairTask1);

            // 修改对应检修计划状态
            RepairPool repairPool = repairPoolMapper.selectById(id);
            if (ObjectUtil.isNotEmpty(repairPool)) {
                repairPool.setStatus(InspectionConstant.REJECTED);
                repairPoolMapper.updateById(repairPool);
            }
        }
    }


    private void setId(ExamineDTO examineDTO, RepairTask repairTask1, LoginUser loginUser, LoginUser userById, String id) {
        //修改检修任务状态
        repairTask1.setId(examineDTO.getId());
        repairTask1.setErrorContent(examineDTO.getContent());
        repairTask1.setReceiptTime(new Date());
        repairTask1.setReceiptUserId(loginUser.getId());
        repairTask1.setReceiptUserName(userById.getRealname());
        repairTask1.setStatus(InspectionConstant.COMPLETED);
        repairTaskMapper.updateById(repairTask1);

        // 修改对应检修计划状态
        RepairPool repairPool = repairPoolMapper.selectById(id);
        if (ObjectUtil.isNotEmpty(repairPool)) {
            repairPool.setStatus(InspectionConstant.COMPLETED);
            repairPoolMapper.updateById(repairPool);
        }
    }

    @Override
    public List<RepairTaskEnclosure> selectEnclosure(String resultId) {
        return repairTaskMapper.selectEnclosure(resultId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmedDelete(ExamineDTO examineDTO) {
        RepairTask repairTask = repairTaskMapper.selectById(examineDTO.getId());
        if (ObjectUtil.isEmpty(repairTask)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }
        // 是任务的检修人才可以退回
        List<RepairTaskUser> repairTaskUserss = repairTaskUserMapper.selectList(
                new LambdaQueryWrapper<RepairTaskUser>()
                        .eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode())
                        .eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isEmpty(repairTaskUserss)) {
            throw new AiurtBootException("小主，该任务没有对应的检修人");
        } else {
            List<String> userList = repairTaskUserss.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
            if (!userList.contains(manager.checkLogin().getId())) {
                throw new AiurtBootException("小主，只有该任务的检修人才能退回");
            }
        }

        //根据任务id查询设备清单
        List<RepairTaskDeviceRel> repairTaskDevice = repairTaskDeviceRelMapper.selectList(
                new LambdaQueryWrapper<RepairTaskDeviceRel>()
                        .eq(RepairTaskDeviceRel::getRepairTaskId, examineDTO.getId()));
        //任务清单主键id集合
        if (CollectionUtil.isNotEmpty(repairTaskDevice)) {
            List<String> collect1 = repairTaskDevice.stream().map(RepairTaskDeviceRel::getId).collect(Collectors.toList());
            //根据设备清单查询结果
            List<RepairTaskResult> repairTaskResults = repairTaskResultMapper.selectList(
                    new LambdaQueryWrapper<RepairTaskResult>()
                            .in(RepairTaskResult::getTaskDeviceRelId, collect1));
            //任务结果主键id集合
            List<String> collect2 = repairTaskResults.stream().map(RepairTaskResult::getId).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect1)) {
                repairTaskDeviceRelMapper.deleteBatchIds(collect1);
            }
            if (CollectionUtil.isNotEmpty(collect2)) {
                repairTaskResultMapper.deleteBatchIds(collect2);
            }
        }
        //根据任务id查询标准
        List<RepairTaskStandardRel> repairTaskStandard = repairTaskStandardRelMapper.selectList(new LambdaQueryWrapper<RepairTaskStandardRel>()
                .eq(RepairTaskStandardRel::getRepairTaskId, examineDTO.getId()));
        if (CollectionUtil.isNotEmpty(repairTaskStandard)) {
            //标准主键id集合
            List<String> collect4 = repairTaskStandard.stream().map(RepairTaskStandardRel::getId).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect4)) {
                repairTaskStandardRelMapper.deleteBatchIds(collect4);
            }
        }

        if (ObjectUtil.isNotNull(repairTask)) {
            //根据设备编号查询人员
            List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(
                    new LambdaQueryWrapper<RepairTaskUser>()
                            .eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode()));
            //人员主键id集合
            List<String> collect3 = repairTaskUsers.stream().map(RepairTaskUser::getId).collect(Collectors.toList());

            //根据设备编号查询站所
            List<RepairTaskStationRel> repairTaskStation = repairTaskStationRelMapper.selectList(
                    new LambdaQueryWrapper<RepairTaskStationRel>()
                            .eq(RepairTaskStationRel::getRepairTaskCode, repairTask.getCode()));
            //站所主键id集合
            List<String> collect5 = repairTaskStation.stream().map(RepairTaskStationRel::getId).collect(Collectors.toList());

            //根据设备编号查询组织机构
            List<RepairTaskOrgRel> repairTaskOrg = repairTaskOrgRelMapper.selectList(
                    new LambdaQueryWrapper<RepairTaskOrgRel>()
                            .eq(RepairTaskOrgRel::getRepairTaskCode, repairTask.getCode()));
            //组织机构主键id集合
            List<String> collect6 = repairTaskOrg.stream().map(RepairTaskOrgRel::getId).collect(Collectors.toList());

            if (ObjectUtil.isNotNull(collect3)) {
                repairTaskUserMapper.deleteBatchIds(collect3);
            }
            if (ObjectUtil.isNotNull(collect5)) {
                repairTaskStationRelMapper.deleteBatchIds(collect5);
            }
            if (ObjectUtil.isNotNull(collect6)) {
                repairTaskOrgRelMapper.deleteBatchIds(collect6);
            }
        }

        repairTaskMapper.deleteById(examineDTO.getId());

        RepairPool repairPool = new RepairPool();
        repairPool.setId(repairTask.getRepairPoolId());
        repairPool.setStatus(InspectionConstant.GIVE_BACK);
        repairPool.setRemark(examineDTO.getContent());
        repairPoolMapper.updateById(repairPool);

    }

    /**
     * 领取检修任务
     *
     * @param id
     * @return
     */
    @Override
    public void receiveTask(String id) {
        RepairPool repairPool = repairPoolMapper.selectById(id);
        if (ObjectUtil.isEmpty(repairPool)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        // 校验领取资格
        checkReceiveTask(repairPool);

        // 更新检修计划状态，待执行
        repairPool.setStatus(InspectionConstant.PENDING);
        repairPoolMapper.updateById(repairPool);

        // 添加任务
        RepairTask repairTask = new RepairTask();
        repairTask.setRepairPoolId(id);
        repairTask.setYear(DateUtil.year(repairPool.getStartTime()));
        repairTask.setType(repairPool.getType());
        repairTask.setIsOutsource(repairPool.getIsOutsource());
        repairTask.setSource(InspectionConstant.PICK_UP_MANUALLY);
        repairTask.setCode(repairPool.getCode());
        repairTask.setWeeks(repairPool.getWeeks());
        repairTask.setStartTime(new Date());
        repairTask.setStatus(InspectionConstant.PENDING);
        repairTask.setIsConfirm(repairPool.getIsConfirm());
        repairTask.setIsReceipt(repairPool.getIsReceipt());
        repairTask.setWorkType(String.valueOf(repairPool.getWorkType()));
        // todo 计划令信息

        // 保存检修任务信息
        repairTaskMapper.insert(repairTask);

        // 保存站点关联信息
        List<RepairPoolStationRel> repairPoolStationRels = repairPoolStationRelMapper.selectList(
                new LambdaQueryWrapper<RepairPoolStationRel>()
                        .eq(RepairPoolStationRel::getRepairPoolCode, repairPool.getCode())
                        .eq(RepairPoolStationRel::getDelFlag, 0));
        if (CollUtil.isNotEmpty(repairPoolStationRels)) {
            for (RepairPoolStationRel repairPoolStationRel : repairPoolStationRels) {
                RepairTaskStationRel repairTaskStationRel = new RepairTaskStationRel();
                repairTaskStationRel.setLineCode(repairPoolStationRel.getLineCode());
                repairTaskStationRel.setStationCode(repairPoolStationRel.getStationCode());
                repairTaskStationRel.setPositionCode(repairPoolStationRel.getPositionCode());
                repairTaskStationRel.setRepairTaskCode(repairPool.getCode());
                repairTaskStationRelMapper.insert(repairTaskStationRel);
            }
        }

        // 保存检修人信息
        RepairTaskUser repairTaskUser = new RepairTaskUser();
        repairTaskUser.setRepairTaskCode(repairPool.getCode());
        LoginUser loginUser = manager.checkLogin();
        repairTaskUser.setUserId(loginUser.getId());
        repairTaskUser.setName(loginUser.getRealname());
        repairTaskUserMapper.insert(repairTaskUser);

        // 保存组织机构信息
        List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(
                new LambdaQueryWrapper<RepairPoolOrgRel>()
                        .eq(RepairPoolOrgRel::getRepairPoolCode, repairPool.getCode())
                        .eq(RepairPoolOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
            for (RepairPoolOrgRel repairPoolOrgRel : repairPoolOrgRels) {
                RepairTaskOrgRel repairTaskOrgRel = new RepairTaskOrgRel();
                repairTaskOrgRel.setRepairTaskCode(repairPool.getCode());
                repairTaskOrgRel.setOrgCode(repairPoolOrgRel.getOrgCode());
                repairTaskOrgRelMapper.insert(repairTaskOrgRel);
            }
        }

        // 生成检修标准关联、检修设备清单、检修结果信息
        repairPoolService.generate(repairPool, repairTask.getId(), repairPool.getCode());

    }

    /**
     * 校验领取资格
     *
     * @param repairPool
     */
    private void checkReceiveTask(RepairPool repairPool) {
        // 计划状态是待指派和已退回才能领取
        if (!InspectionConstant.TO_BE_ASSIGNED.equals(repairPool.getStatus())
                && !InspectionConstant.GIVE_BACK.equals(repairPool.getStatus())) {
            throw new AiurtBootException("小主，该检修任务已被指派或已被领取");
        }

        // 当前登录人所属部门是在检修任务的指派部门范围内才可以领取
        List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(
                new LambdaQueryWrapper<RepairPoolOrgRel>()
                        .eq(RepairPoolOrgRel::getRepairPoolCode, repairPool.getCode())
                        .eq(RepairPoolOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
            List<String> orgList = repairPoolOrgRels.stream().map(RepairPoolOrgRel::getOrgCode).collect(Collectors.toList());
            if (!orgList.contains(manager.checkLogin().getOrgCode())) {
                throw new AiurtBootException("小主，该检修任务不在您的领取范围之内哦");
            }
        }

        // 现在的时间大于任务的开始时间才可以进行领取
        if (repairPool.getStartTime() != null && DateUtil.compare(new Date(), repairPool.getStartTime()) < 0) {
            throw new AiurtBootException("小主莫急，未到检修任务开始时间，暂时无法领取");
        }
    }

    /**
     * 填写检修工单
     *
     * @param monadDTO
     */
    @Override
    public void writeMonad(WriteMonadDTO monadDTO) {
        RepairTaskResult result = repairTaskResultMapper.selectOne(
                new LambdaQueryWrapper<RepairTaskResult>()
                        .eq(RepairTaskResult::getId, monadDTO.getItemId())
                        .eq(RepairTaskResult::getTaskDeviceRelId, monadDTO.getOrdId())
                        .eq(RepairTaskResult::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (ObjectUtil.isEmpty(result)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }
        // 什么情况才能填写检修单
        this.check(result.getTaskDeviceRelId());

        // 什么情况下需要更新该项检修人
        if (isNeedUpdateStaffId(monadDTO, result)) {
            result.setStaffId(manager.checkLogin().getId());
        }

        result.setStatus(monadDTO.getStatus());
        result.setNote(monadDTO.getNote());
        result.setInspeciontValue(monadDTO.getInspeciontValue());
        result.setUnNote(monadDTO.getUnNote());

        // 更新检修结果
        repairTaskResultMapper.updateById(result);

        // 保存上传的附件
        if (StrUtil.isNotEmpty(monadDTO.getAppendix())) {
            repairTaskEnclosureMapper.delete(
                    new LambdaQueryWrapper<RepairTaskEnclosure>()
                            .eq(RepairTaskEnclosure::getRepairTaskResultId, result.getId())
                            .eq(RepairTaskEnclosure::getDelFlag, CommonConstant.DEL_FLAG_0));

            List<String> appendixList = StrUtil.split(monadDTO.getAppendix(), ',');
            appendixList.forEach(ap -> {
                RepairTaskEnclosure repairTaskEnclosure = new RepairTaskEnclosure();
                repairTaskEnclosure.setUrl(ap);
                repairTaskEnclosure.setRepairTaskResultId(result.getId());
                repairTaskEnclosureMapper.insert(repairTaskEnclosure);
            });
        }
    }

    /**
     * 什么情况下才可以填写检修单
     *
     * @param taskDeviceRelId
     */
    private void check(String taskDeviceRelId) {
        if (StrUtil.isNotEmpty(taskDeviceRelId)) {

            RepairTaskDeviceRel repairTaskDeviceRel = repairTaskDeviceRelMapper.selectById(taskDeviceRelId);
            if (ObjectUtil.isNotEmpty(repairTaskDeviceRel)) {
                RepairTask repairTask = repairTaskMapper.selectById(repairTaskDeviceRel.getRepairTaskId());
                if (ObjectUtil.isNotEmpty(repairTask)) {

                    // 现在的时间大于任务的开始时间才可以进行填单
                    if (DateUtil.compare(new Date(), repairTask.getStartTime()) < 0) {
                        throw new AiurtBootException("小主莫急，未到检修任务开始时间");
                    }

                    // 只有任务状态是执行中或已驳回才可以改
                    if (!InspectionConstant.IN_EXECUTION.equals(repairTask.getStatus())
                            && !InspectionConstant.REJECTED.equals(repairTask.getStatus())) {
                        throw new AiurtBootException("小主，只有任务被驳回或者执行中才可以操作");
                    }

                    // 是任务的检修人才可以填写
                    List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(
                            new LambdaQueryWrapper<RepairTaskUser>()
                                    .eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode())
                                    .eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
                    if (CollUtil.isEmpty(repairTaskUsers)) {
                        throw new AiurtBootException("小主，该任务没有对应的检修人");
                    } else {
                        List<String> userList = repairTaskUsers.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
                        if (!userList.contains(manager.checkLogin().getId())) {
                            throw new AiurtBootException("小主，您不是该检修任务的检修人");
                        }
                    }
                }
            }
        }
    }

    /**
     * 什么情况下需要更新该项检修人
     * 1、该项检修人为空
     * 2、检修结果发生改变
     * 3、检修值发生变化
     *
     * @param monadDTO
     * @param result
     * @return
     */
    public boolean isNeedUpdateStaffId(WriteMonadDTO monadDTO, RepairTaskResult result) {
        return StrUtil.isEmpty(result.getStaffId())
                || (monadDTO.getStatus() != null && !monadDTO.getStatus().equals(result.getStatus()))
                || (monadDTO.getInspeciontValue() != null && !monadDTO.getInspeciontValue().equals(result.getInspeciontValue()))
                || (StrUtil.isNotEmpty(monadDTO.getNote()) && !monadDTO.getNote().equals(result.getNote()));
    }

    /**
     * 填写检修单上的同行人
     *
     * @param code   检修单code
     * @param peerId 同行人ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writePeerPeople(String code, String peerId) {
        RepairTaskDeviceRel repairTaskDeviceRel = repairTaskDeviceRelMapper.selectOne(
                new LambdaQueryWrapper<RepairTaskDeviceRel>()
                        .eq(RepairTaskDeviceRel::getCode, code)
                        .eq(RepairTaskDeviceRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (ObjectUtil.isEmpty(repairTaskDeviceRel)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        // 校验什么情况下可以填写同行人
        check(repairTaskDeviceRel.getId());

        repairTaskPeerRelMapper.delete(
                new LambdaQueryWrapper<RepairTaskPeerRel>()
                        .eq(RepairTaskPeerRel::getRepairTaskDeviceCode, repairTaskDeviceRel.getCode()));

        // 更新同行人
        if (StrUtil.isNotEmpty(peerId)) {
            List<String> userIdS = StrUtil.split(peerId, ',');
            userIdS.forEach(userId -> {
                RepairTaskPeerRel rel = new RepairTaskPeerRel();
                rel.setUserId(userId);
                rel.setRealName(ObjectUtil.isNotEmpty(sysBaseApi.getUserById(userId)) ? sysBaseApi.getUserById(userId).getRealname() : "");
                rel.setRepairTaskDeviceCode(repairTaskDeviceRel.getCode());
                repairTaskPeerRelMapper.insert(rel);
            });
        }
    }

    /**
     * 填写检修单上的检修位置
     *
     * @param id               检修单id
     * @param specificLocation 检修位置
     * @return
     */
    @Override
    public void writeLocation(String id, String specificLocation) {
        RepairTaskDeviceRel repairTaskDeviceRel = repairTaskDeviceRelMapper.selectById(id);
        if (ObjectUtil.isEmpty(repairTaskDeviceRel)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }
        // 校验什么情况下可以上传检修位置
        check(id);

        repairTaskDeviceRel.setSpecificLocation(specificLocation);
        repairTaskDeviceRelMapper.updateById(repairTaskDeviceRel);
    }

    /**
     * 提交检修工单
     *
     * @param id 检修单id
     * @return
     */
    @Override
    public void submitMonad(String id) {
        // 查询检修工单
        RepairTaskDeviceRel repairTaskDeviceRel = repairTaskDeviceRelMapper.selectById(id);
        if (ObjectUtil.isEmpty(repairTaskDeviceRel)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        // 校验什么时候才可以提交检修单
        this.check(id);

        // 检修工单对应的检修项
        List<RepairTaskResult> repairTaskResults = repairTaskResultMapper.selectList(
                new LambdaQueryWrapper<RepairTaskResult>()
                        .eq(RepairTaskResult::getTaskDeviceRelId, repairTaskDeviceRel.getId())
                        .eq(RepairTaskResult::getType, InspectionConstant.CHECKPROJECT)
                        .eq(RepairTaskResult::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isEmpty(repairTaskResults)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        // 校验检修项是否存在未填写的检修结果和检修值
        repairTaskResults.forEach(repair -> {
            // 检修结果和检修值存在空值的就不能进行提交
            if (repair.getStatus() == null) {
                throw new AiurtBootException("有检修结果未填写");
            }
            // 选择项
            if (InspectionConstant.STATUS_ITEM_CHOICE.equals(repair.getStatusItem()) && repair.getInspeciontValue() == null) {
                throw new AiurtBootException("有检修值未填写");
            }
            // 输入项
            if (InspectionConstant.STATUS_ITEM_INPUT.equals(repair.getStatusItem()) && StrUtil.isEmpty(repair.getNote())) {
                throw new AiurtBootException("有检修值未填写");
            }
        });

        // 检修结束时间为空则修改
        Date submitTime = new Date();
        if (ObjectUtil.isEmpty(repairTaskDeviceRel.getEndTime())) {
            repairTaskDeviceRel.setEndTime(submitTime);
            // 检修时长
            if (ObjectUtil.isNotEmpty(repairTaskDeviceRel.getStartTime()) && ObjectUtil.isNotEmpty(repairTaskDeviceRel.getEndTime())) {
                repairTaskDeviceRel.setDuration(DateUtil.between(repairTaskDeviceRel.getStartTime(), repairTaskDeviceRel.getEndTime(), DateUnit.MINUTE));
            }
        }

        // 修改检修单的状态，已提交
        repairTaskDeviceRel.setSubmitTime(submitTime);
        repairTaskDeviceRel.setStaffId(manager.checkLogin().getId());
        repairTaskDeviceRel.setIsSubmit(InspectionConstant.SUBMITTED);
        repairTaskDeviceRelMapper.updateById(repairTaskDeviceRel);
    }

    /**
     * 检修单同行人下拉
     *
     * @param id
     */
    @Override
    public List<OrgDTO> queryPeerList(String id) {
        List<OrgDTO> orgDto = new ArrayList<>();
        // 检修单
        RepairTaskDeviceRel repairTaskDeviceRel = repairTaskDeviceRelMapper.selectById(id);
        if (ObjectUtil.isEmpty(repairTaskDeviceRel)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        // 检修任务
        RepairTask repairTask = baseMapper.selectById(repairTaskDeviceRel.getRepairTaskId());
        if (ObjectUtil.isEmpty(repairTask)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        List<RepairTaskOrgRel> repairTaskOrgRels = repairTaskOrgRelMapper.selectList(
                new LambdaQueryWrapper<RepairTaskOrgRel>()
                        .eq(RepairTaskOrgRel::getRepairTaskCode, repairTask.getCode())
                        .eq(RepairTaskOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (CollUtil.isNotEmpty(repairTaskOrgRels)) {
            List<String> orgList = repairTaskOrgRels.stream().map(RepairTaskOrgRel::getOrgCode).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(orgList)) {
                List<String> list = manager.handleMixedOrgCode(orgList);
                if (CollUtil.isNotEmpty(list)) {
                    orgDto = manager.queryUserByOrdCode(StrUtil.join(",", list));
                }
            }
        }
        return orgDto;
    }

    /**
     * 确认检修任务
     *
     * @param examineDTO
     */
    @Override
    public void confirmTask(ExamineDTO examineDTO) {
        RepairTask repairTask = repairTaskMapper.selectById(examineDTO.getId());
        if (ObjectUtil.isEmpty(repairTask)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        // 是任务的检修人才可以确认
        List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(
                new LambdaQueryWrapper<RepairTaskUser>()
                        .eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode())
                        .eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isEmpty(repairTaskUsers)) {
            throw new AiurtBootException("小主，该任务没有对应的检修人");
        } else {
            List<String> userList = repairTaskUsers.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
            if (!userList.contains(manager.checkLogin().getId())) {
                throw new AiurtBootException("小主，只有该任务的检修人才能确认");
            }
        }

        // 待确认状态才可以确认
        if (InspectionConstant.TO_BE_CONFIRMED.equals(repairTask.getStatus())) {
            repairTask.setConfirmTime(new Date());
            LoginUser loginUser = manager.checkLogin();
            repairTask.setConfirmUserId(loginUser.getId());
            repairTask.setConfirmUserName(loginUser.getRealname());
            repairTask.setStatus(InspectionConstant.PENDING);
            repairTaskMapper.updateById(repairTask);

            // 修改对应检修计划状态
            RepairPool repairPool = repairPoolMapper.selectById(repairTask.getRepairPoolId());
            if (ObjectUtil.isNotEmpty(repairPool)) {
                repairPool.setStatus(InspectionConstant.PENDING);
                repairPoolMapper.updateById(repairPool);
            }

        } else {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }
    }

    /**
     * 扫码设备查询检修单
     *
     * @param taskId     检修任务id
     * @param deviceCode 设备编码
     * @return
     */
    @Override
    public List<RepairTaskDeviceRel> scanCodeDevice(String taskId, String deviceCode) {
        if (StrUtil.isEmpty(taskId) || StrUtil.isEmpty(deviceCode)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }
        List<RepairTaskDeviceRel> repairTaskDeviceRels = repairTaskDeviceRelMapper.selectList(
                new LambdaQueryWrapper<RepairTaskDeviceRel>()
                        .eq(RepairTaskDeviceRel::getRepairTaskId, taskId)
                        .eq(RepairTaskDeviceRel::getDeviceCode, deviceCode)
                        .eq(RepairTaskDeviceRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        return repairTaskDeviceRels;
    }
}
