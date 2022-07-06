package com.aiurt.boot.task.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
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
import com.aiurt.boot.task.entity.*;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.boot.task.service.IRepairTaskService;
import com.aiurt.common.exception.AiurtBootException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private RepairTaskEnclosureMapper  repairTaskEnclosureMapper;
    @Resource
    private ISysBaseAPI sysBaseAPI;
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

            if (e.getCode() != null) {
                //根据检修任务code查询
                LambdaQueryWrapper<RepairTaskUser> repairTaskUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
                List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(repairTaskUserLambdaQueryWrapper.eq(RepairTaskUser::getRepairTaskCode, e.getCode()));
                //检修人id集合
                List<String> collect = repairTaskUsers.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
                e.setOverhaulId(collect);
                ArrayList<String> userList = new ArrayList<>();
                collect.forEach(o -> {
                    LoginUser userById = sysBaseAPI.getUserById(o);
                    userList.add(userById.getUsername());
                });
                e.setOverhaulName(userList);
            }
        });
        return pageList.setRecords(lists);
    }

    @Override
    public Page<RepairTaskDTO> selectTasklet(Page<RepairTaskDTO> pageList, RepairTaskDTO condition) {
        List<RepairTaskDTO> repairTasks = repairTaskMapper.selectTasklet(pageList, condition);
        repairTasks.forEach(e -> {
            //查询同行人
            LambdaQueryWrapper<RepairTaskPeerRel> repairTaskPeerRelLambdaQueryWrapper = new LambdaQueryWrapper<>();
            List<RepairTaskPeerRel> repairTaskPeer = repairTaskPeerRelMapper.selectList(repairTaskPeerRelLambdaQueryWrapper.eq(RepairTaskPeerRel::getRepairTaskDeviceCode, e.getOverhaulCode()));
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
            }
            if (e.getStartTime() != null) {
                e.setTaskStatusName("进行中");
            }
            if (e.getIsSubmit() != null && e.getIsSubmit() == 1) {
                e.setTaskStatusName("已提交");
            }
            //提交人名称
            if (e.getOverhaulId() != null) {
                LoginUser userById = sysBaseAPI.getUserById(e.getOverhaulId());
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
            long count3 = repairTasks.stream().filter(repairTaskDTO -> repairTaskDTO.getIsSubmit()!=null && repairTaskDTO.getIsSubmit().equals(InspectionConstant.IS_EFFECT)).count();
            e.setSubmitted((int) count3);
        });
        return pageList.setRecords(repairTasks);
    }

    @Override
    public Page<RepairTaskDTO> repairSelectTaskletForDevice(Page<RepairTaskDTO> pageList, RepairTaskDTO condition) {
        List<RepairTaskDTO> repairTasks = repairTaskMapper.selectTaskletForDevice(pageList, condition);
        repairTasks.forEach(e -> {
            //查询同行人
            LambdaQueryWrapper<RepairTaskPeerRel> repairTaskPeerRelLambdaQueryWrapper = new LambdaQueryWrapper<>();
            List<RepairTaskPeerRel> repairTaskPeer = repairTaskPeerRelMapper.selectList(repairTaskPeerRelLambdaQueryWrapper.eq(RepairTaskPeerRel::getRepairTaskDeviceCode, e.getOverhaulCode()));
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
            }
            if (e.getStartTime() != null) {
                e.setTaskStatusName("进行中");
            }
            if (e.getIsSubmit() != null && e.getIsSubmit() == 1) {
                e.setTaskStatusName("已提交");
            }
            //提交人名称
//            if (e.getOverhaulId()!=null){
////                LoginUser userById = sysBaseAPI.getUserById(e.getOverhaulId());
////                e.setOverhaulName(userById.getUsername());
////            }
            if (e.getDeviceId() != null && CollectionUtil.isNotEmpty(repairTasks)) {
                //正常项
                List<RepairTaskResult> repairTaskResults = repairTaskMapper.selectSingle(e.getDeviceId(), 1);
                e.setNormal(repairTaskResults.size());
                //异常项
                List<RepairTaskResult> repairTaskResults1 = repairTaskMapper.selectSingle(e.getDeviceId(), 2);
                e.setAbnormal(repairTaskResults1.size());
            }
            if (e.getDeviceId() != null && CollectionUtil.isNotEmpty(repairTasks)) {
                //正常项
                List<RepairTaskResult> repairTaskResults = repairTaskMapper.selectSingle(e.getDeviceId(), 1);
                e.setNormal(repairTaskResults.size());
                //异常项
                List<RepairTaskResult> repairTaskResults1 = repairTaskMapper.selectSingle(e.getDeviceId(), 2);
                e.setAbnormal(repairTaskResults1.size());
            }
            //未开始的数量
            long count1 = repairTasks.stream().filter(repairTaskDTO -> repairTaskDTO.getStartTime() == null).count();
            e.setNotStarted((int) count1);
            //进行中的数量
            long count2 = repairTasks.stream().filter(repairTaskDTO -> repairTaskDTO.getStartTime() != null).count();
            e.setHaveInHand((int) count2);
            //已提交的数量
            long count3 = repairTasks.stream().filter(repairTaskDTO -> repairTaskDTO.getIsSubmit() != null && repairTaskDTO.getIsSubmit() == 1).count();
            e.setSubmitted((int) count3);
        });
        return pageList.setRecords(repairTasks);
    }

    @Override
    public List<MajorDTO> selectMajorCodeList(String taskId) {
        //根据检修任务id查询专业
        List<RepairTaskDTO> repairTaskDTOList = repairTaskMapper.selectCodeList(taskId,null,null);
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
    public EquipmentOverhaulDTO selectEquipmentOverhaulList(String taskId,String majorCode,String subsystemCode ) {
        //根据检修任务id查询设备和
        List<RepairTaskDTO> repairTaskDTOList = repairTaskMapper.selectCodeList(taskId,majorCode,subsystemCode);
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
        });
        //提交人名称
        if (checkListDTO.getOverhaulId() != null) {
            LoginUser userById = sysBaseAPI.getUserById(checkListDTO.getOverhaulId());
            checkListDTO.setOverhaulName(userById.getUsername());
        }

        //设备位置
        if (overhaulCode != null) {
            List<StationDTO> stationDTOList = repairTaskMapper.selectStationList(overhaulCode);
            checkListDTO.setEquipmentLocation(manager.translateStation(stationDTOList));
        }
        //检修位置
        if (checkListDTO.getEquipmentCode() == null && checkListDTO.getSpecificLocation() != null) {
            List<StationDTO> stationDTOList = new ArrayList<>();
            stationDTOList.forEach(e -> {
                e.setLineCode(checkListDTO.getStationCode());
                e.setLineCode(checkListDTO.getLineCode());
                e.setLineCode(checkListDTO.getSpecificLocation());
            });
            String station = manager.translateStation(stationDTOList);
            String string = checkListDTO.getSpecificLocation() + station;
            checkListDTO.setMaintenancePosition(string);
        }

        //构造树形
        checkListDTO.setRepairTaskResultList(selectCodeContentList(checkListDTO.getDeviceId()));
        List<RepairTaskResult> repairTaskResultList = checkListDTO.getRepairTaskResultList();

        //获取检修单的检修结果
        List<String> collect1 = repairTaskResultList.stream().map(RepairTaskResult::getId).collect(Collectors.toList());

        LambdaQueryWrapper<RepairTaskEnclosure> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<RepairTaskEnclosure> repairTaskDevice = repairTaskEnclosureMapper.selectList(objectLambdaQueryWrapper.in(RepairTaskEnclosure::getRepairTaskResultId,collect1));

        //获取检修单的检修结果的附件
        checkListDTO.setEnclosureUrl(repairTaskDevice.stream().map(RepairTaskEnclosure::getUrl).collect(Collectors.toList()));

        //检查项的数量
        long count1 = repairTaskResultList.stream().filter(repairTaskResult -> repairTaskResult.getType() == 1).count();
        checkListDTO.setMaintenanceItemsQuantity((int) count1);

        //已检修的数量
        long count2 = repairTaskResultList.stream().filter(repairTaskResult -> repairTaskResult.getStatus() != null).count();
        checkListDTO.setMaintenanceItemsQuantity((int) count2);

        //待检修的数量
        long count3 = repairTaskResultList.stream().filter(repairTaskResult -> repairTaskResult.getStatus() == null).count();
        checkListDTO.setMaintenanceItemsQuantity((int) count3);

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
            r.setStatusName(sysBaseAPI.translateDict(DictConstant.OVERHAUL_RESULT, String.valueOf(r.getStatus())));

            //检查项类型
            r.setTypeName(sysBaseAPI.translateDict(DictConstant.INSPECTION_PROJECT, String.valueOf(r.getType())));

            //检修人名称
            if (r.getStaffId() != null) {
                LoginUser userById = sysBaseAPI.getUserById(r.getStaffId());
                r.setStaffName(userById.getUsername());
            }
            //备注
            if (r.getUnNote() == null) {
                r.setUnNote("无");
            }
            if (r.getStatusItem() != null) {
                //检修值
                if (r.getStatusItem() == 1) {
                    r.setInspeciontValueName(null);
                }
                if (r.getStatusItem() == 2) {
                    r.setInspeciontValueName(sysBaseAPI.translateDict(r.getDictCode(), String.valueOf(r.getInspeciontValue())));
                }
                if (r.getStatusItem() == 3) {
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
        LoginUser loginUser = manager.checkLogin();
        LoginUser userById = sysBaseAPI.getUserById(loginUser.getId());
        RepairTask repairTask1 = new RepairTask();
        status(examineDTO, loginUser, userById, repairTask1);
        if (examineDTO.getStatus().equals(InspectionConstant.IS_EFFECT) && repairTask.getIsReceipt().equals(InspectionConstant.IS_EFFECT)){
            repairTask1.setId(examineDTO.getId());
            repairTask1.setErrorContent(examineDTO.getContent());
            repairTask1.setConfirmTime(new Date());
            repairTask1.setConfirmUserId(loginUser.getId());
            repairTask1.setConfirmUserName(userById.getRealname());
            repairTask1.setStatus(InspectionConstant.PENDING_RECEIPT);
            repairTaskMapper.updateById(repairTask1);
        }if (examineDTO.getStatus().equals(InspectionConstant.IS_EFFECT) && repairTask.getIsReceipt().equals(InspectionConstant.NO_IS_EFFECT)){
            setId(examineDTO, repairTask1, loginUser, userById);
        }
    }

    @Override
    public void toBeImplement(ExamineDTO examineDTO) {
        RepairTask repairTask1 = new RepairTask();
        repairTask1.setId(examineDTO.getId());
        repairTask1.setBeginTime(new Date());
        repairTask1.setStatus(InspectionConstant.IN_EXECUTION);
        repairTaskMapper.updateById(repairTask1);
    }

    @Override
    public void inExecution(ExamineDTO examineDTO) {
        RepairTask repairTask = repairTaskMapper.selectById(examineDTO.getId());
        RepairTask repairTask1 = new RepairTask();
        RepairTaskDeviceRel repairTaskDeviceRel = new RepairTaskDeviceRel();


        LambdaQueryWrapper<RepairTaskDeviceRel> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<RepairTaskDeviceRel> repairTaskDevice1 = repairTaskDeviceRelMapper.selectList(objectLambdaQueryWrapper.eq(RepairTaskDeviceRel::getRepairTaskId, examineDTO.getId()));

        //查询检修单的主键id集合
        List<String> collect1 = repairTaskDevice1.stream().map(RepairTaskDeviceRel::getId).collect(Collectors.toList());

        //查询未提交的检修单
        List<RepairTaskDeviceRel> repairTaskDevice = repairTaskDeviceRelMapper.selectList(objectLambdaQueryWrapper
                .eq(RepairTaskDeviceRel::getRepairTaskId,examineDTO.getId())
                .eq(RepairTaskDeviceRel::getIsSubmit,InspectionConstant.NO_IS_EFFECT));

        if (CollectionUtil.isNotEmpty(collect1) && CollectionUtil.isEmpty(repairTaskDevice)) {
            collect1.forEach(e -> {
                repairTaskDeviceRel.setId(e);
                repairTaskDeviceRel.setSubmitTime(new Date());
                repairTaskDeviceRel.setIsSubmit(InspectionConstant.IS_EFFECT);
                repairTaskDeviceRel.setEndTime(new Date());
                repairTaskDeviceRelMapper.updateById(repairTaskDeviceRel);
            });
            if (repairTask.getIsConfirm() == 1) {
                repairTask1.setId(examineDTO.getId());
                repairTask1.setStatus(InspectionConstant.PENDING_REVIEW);
            }else {
                repairTask1.setId(examineDTO.getId());
                repairTask1.setStatus(InspectionConstant.COMPLETED);
            }
            repairTaskMapper.updateById(repairTask1);
        }

    }

    @Override
    public void acceptance(ExamineDTO examineDTO) {
        RepairTask repairTask1 = new RepairTask();
        LoginUser loginUser = manager.checkLogin();
        LoginUser userById = sysBaseAPI.getUserById(loginUser.getId());
        status(examineDTO, loginUser, userById, repairTask1);
        if (examineDTO.getStatus().equals(InspectionConstant.IS_EFFECT) ){
            setId(examineDTO, repairTask1, loginUser, userById);
        }
    }

    private void status(ExamineDTO examineDTO, LoginUser loginUser, LoginUser userById, RepairTask repairTask1) {
        if (examineDTO.getStatus().equals(InspectionConstant.NO_IS_EFFECT)){
            repairTask1.setId(examineDTO.getId());
            repairTask1.setErrorContent(examineDTO.getContent());
            repairTask1.setConfirmTime(new Date());
            repairTask1.setConfirmUserId(loginUser.getId());
            repairTask1.setConfirmUserName(userById.getRealname());
            repairTask1.setStatus(InspectionConstant.REJECTED);
            repairTaskMapper.updateById(repairTask1);
        }
    }


    private void setId(ExamineDTO examineDTO, RepairTask repairTask1, LoginUser loginUser, LoginUser userById) {
        repairTask1.setId(examineDTO.getId());
        repairTask1.setErrorContent(examineDTO.getContent());
        repairTask1.setConfirmTime(new Date());
        repairTask1.setConfirmUserId(loginUser.getId());
        repairTask1.setConfirmUserName(userById.getRealname());
        repairTask1.setStatus(InspectionConstant.COMPLETED);
        repairTaskMapper.updateById(repairTask1);
    }

    @Override
    public List<RepairTaskEnclosure> selectEnclosure(String resultId) {
        return repairTaskMapper.selectEnclosure(resultId);
    }

    @Override
    public void confirmedDelete(ExamineDTO examineDTO) {
        RepairTask repairTask = repairTaskMapper.selectById(examineDTO.getId());

        //根据任务id查询设备清单
        LambdaQueryWrapper<RepairTaskDeviceRel> objectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<RepairTaskDeviceRel> repairTaskDevice = repairTaskDeviceRelMapper.selectList(objectLambdaQueryWrapper.eq(RepairTaskDeviceRel::getRepairTaskId, examineDTO.getId()));
        //任务清单主键id集合
        if (CollectionUtil.isNotEmpty(repairTaskDevice)) {
            List<String> collect1 = repairTaskDevice.stream().map(RepairTaskDeviceRel::getId).collect(Collectors.toList());
            //根据设备清单查询结果
            LambdaQueryWrapper<RepairTaskResult> resultLambdaQueryWrapper = new LambdaQueryWrapper<>();
            List<RepairTaskResult> repairTaskResults = repairTaskResultMapper.selectList(resultLambdaQueryWrapper.in(RepairTaskResult::getTaskDeviceRelId, collect1));
            //任务结果主键id集合
            List<String> collect2 = repairTaskResults.stream().map(RepairTaskResult::getId).collect(Collectors.toList());

            repairTaskDeviceRelMapper.deleteBatchIds(collect1);
            repairTaskResultMapper.deleteBatchIds(collect2);
        }
        //根据任务id查询标准
        LambdaQueryWrapper<RepairTaskStandardRel> repairTaskStandardRelLambdaQueryWrapper = new LambdaQueryWrapper<>();
        List<RepairTaskStandardRel> repairTaskStandard = repairTaskStandardRelMapper.selectList(repairTaskStandardRelLambdaQueryWrapper.eq(RepairTaskStandardRel::getRepairTaskId, examineDTO.getId()));
        if (CollectionUtil.isNotEmpty(repairTaskStandard)) {
            //标准主键id集合
            List<String> collect4 = repairTaskStandard.stream().map(RepairTaskStandardRel::getId).collect(Collectors.toList());
            repairTaskStandardRelMapper.deleteBatchIds(collect4);
        }

        if (ObjectUtil.isNotNull(repairTask)) {
            //根据设备编号查询人员
            LambdaQueryWrapper<RepairTaskUser> repairTaskUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
            List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(repairTaskUserLambdaQueryWrapper.eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode()));
            //人员主键id集合
            List<String> collect3 = repairTaskUsers.stream().map(RepairTaskUser::getId).collect(Collectors.toList());

            //根据设备编号查询站所
            LambdaQueryWrapper<RepairTaskStationRel> repairTaskStationRelLambdaQueryWrapper = new LambdaQueryWrapper<>();
            List<RepairTaskStationRel> repairTaskStation = repairTaskStationRelMapper.selectList(repairTaskStationRelLambdaQueryWrapper.eq(RepairTaskStationRel::getRepairTaskCode, repairTask.getCode()));
            //站所主键id集合
            List<String> collect5 = repairTaskStation.stream().map(RepairTaskStationRel::getId).collect(Collectors.toList());

            //根据设备编号查询组织机构
            LambdaQueryWrapper<RepairTaskOrgRel> repairTaskOrgRelLambdaQueryWrapper = new LambdaQueryWrapper<>();
            List<RepairTaskOrgRel> repairTaskOrg = repairTaskOrgRelMapper.selectList(repairTaskOrgRelLambdaQueryWrapper.eq(RepairTaskOrgRel::getRepairTaskCode, repairTask.getCode()));
            //组织机构主键id集合
            List<String> collect6 = repairTaskOrg.stream().map(RepairTaskOrgRel::getId).collect(Collectors.toList());

            repairTaskUserMapper.deleteBatchIds(collect3);
            repairTaskStationRelMapper.deleteBatchIds(collect5);
            repairTaskOrgRelMapper.deleteBatchIds(collect6);
        }

        repairTaskMapper.deleteById(examineDTO.getId());

        RepairPool repairPool = new RepairPool();
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
        if (ObjectUtil.isNotEmpty(repairPool)) {
            throw new AiurtBootException("非法操作");
        }

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
                        .eq(RepairPoolOrgRel::getDelFlag, 0));
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
}
