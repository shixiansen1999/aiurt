package com.aiurt.boot.task.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.manager.InspectionManager;
import com.aiurt.boot.manager.dto.*;
import com.aiurt.boot.plan.dto.RepairDeviceDTO;
import com.aiurt.boot.plan.dto.StationDTO;
import com.aiurt.boot.plan.entity.RepairPool;
import com.aiurt.boot.plan.mapper.RepairPoolMapper;
import com.aiurt.boot.task.dto.CheckListDTO;
import com.aiurt.boot.task.entity.*;
import com.aiurt.boot.task.dto.RepairTaskDTO;
import com.aiurt.boot.task.mapper.*;
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
import java.util.stream.Collectors;

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
    private RepairPoolMapper  repairPoolMapper;

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

            if (e.getCode()!=null){
                //根据检修任务code查询
                LambdaQueryWrapper<RepairTaskUser> repairTaskUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
                List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(repairTaskUserLambdaQueryWrapper.eq(RepairTaskUser::getRepairTaskCode, e.getCode()));
                //检修人id集合
                List<String> collect = repairTaskUsers.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
                e.setOverhaulId(collect);
                ArrayList<String> userList = new ArrayList<>();
                collect.forEach(o->{
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
        repairTasks.forEach(e->{
            //检修结果
            e.setMaintenanceResultsName(sysBaseAPI.translateDict(DictConstant.OVERHAUL_RESULT, String.valueOf(e.getMaintenanceResults())));

            //查询同行人
            LambdaQueryWrapper<RepairTaskPeerRel> repairTaskPeerRelLambdaQueryWrapper = new LambdaQueryWrapper<>();
            List<RepairTaskPeerRel> repairTaskPeer = repairTaskPeerRelMapper.selectList(repairTaskPeerRelLambdaQueryWrapper.eq(RepairTaskPeerRel::getRepairTaskDeviceCode, e.getOverhaulCode()));
            //名称集合
            List<String> collect3 = repairTaskPeer.stream().map(RepairTaskPeerRel::getRealName).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect3)){
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
            //设备位置
            if(e.getTaskCode()!=null){
                List<StationDTO> stationDTOList = repairTaskMapper.selectStationList(e.getTaskCode());
                e.setEquipmentLocation(manager.translateStation(stationDTOList));
            }
            if (e.getTaskStatus()!=null){
                //检修任务状态
                e.setTaskStatusName(sysBaseAPI.translateDict(DictConstant.INSPECTION_TASK_STATE, String.valueOf(e.getTaskStatus())));
            }
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
        //检修单名称
        if (checkListDTO.getResultCode()!=null){
            checkListDTO.setResultName("检修单"+checkListDTO.getResultCode());
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
        //提交人名称
        if (checkListDTO.getOverhaulId()!=null){
            LoginUser userById = sysBaseAPI.getUserById(checkListDTO.getOverhaulId());
            checkListDTO.setOverhaulName(userById.getUsername());
        }

        //设备位置
        if(overhaulCode!=null){
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
            if (r.getStatusItem()!=null){
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
        RepairTask repairTask1= new RepairTask();
        status(examineDTO, loginUser, userById, repairTask1);
        if (examineDTO.getStatus()==1 && repairTask.getIsReceipt()==1){
            repairTask1.setId(examineDTO.getId());
            repairTask1.setErrorContent(examineDTO.getContent());
            repairTask1.setConfirmTime(new Date());
            repairTask1.setConfirmUserId(loginUser.getId());
            repairTask1.setConfirmUserName(userById.getRealname());
            repairTask1.setStatus(7);
            repairTaskMapper.updateById(repairTask1);
        }if (examineDTO.getStatus()==1 && repairTask.getIsReceipt()==0){
            setId(examineDTO, repairTask1, loginUser, userById);
        }
    }

    @Override
    public void toBeImplement(ExamineDTO examineDTO) {
        RepairTask repairTask1= new RepairTask();
        repairTask1.setId(examineDTO.getId());
        repairTask1.setBeginTime(new Date());
        repairTask1.setStatus(4);
        repairTaskMapper.updateById(repairTask1);
    }

    @Override
    public void inExecution(ExamineDTO examineDTO) {
        RepairTask repairTask = repairTaskMapper.selectById(examineDTO.getId());
        RepairTask repairTask1= new RepairTask();
        if (repairTask.getIsConfirm()==1){
            repairTask1.setId(examineDTO.getId());
            repairTask1.setStatus(6);
        }else {
            repairTask1.setId(examineDTO.getId());
            repairTask1.setStatus(8);
        }

        repairTaskMapper.updateById(repairTask1);

    }

    @Override
    public void acceptance(ExamineDTO examineDTO) {
        RepairTask repairTask1= new RepairTask();
        LoginUser loginUser = manager.checkLogin();
        LoginUser userById = sysBaseAPI.getUserById(loginUser.getId());
        status(examineDTO, loginUser, userById, repairTask1);
        if (examineDTO.getStatus()==1 ){
            setId(examineDTO, repairTask1, loginUser, userById);
        }
    }

    private void status(ExamineDTO examineDTO, LoginUser loginUser, LoginUser userById, RepairTask repairTask1) {
        if (examineDTO.getStatus()==0){
            repairTask1.setId(examineDTO.getId());
            repairTask1.setErrorContent(examineDTO.getContent());
            repairTask1.setConfirmTime(new Date());
            repairTask1.setConfirmUserId(loginUser.getId());
            repairTask1.setConfirmUserName(userById.getRealname());
            repairTask1.setStatus(5);
            repairTaskMapper.updateById(repairTask1);
        }
    }


    private void setId(ExamineDTO examineDTO, RepairTask repairTask1, LoginUser loginUser, LoginUser userById) {
        repairTask1.setId(examineDTO.getId());
        repairTask1.setErrorContent(examineDTO.getContent());
        repairTask1.setConfirmTime(new Date());
        repairTask1.setConfirmUserId(loginUser.getId());
        repairTask1.setConfirmUserName(userById.getRealname());
        repairTask1.setStatus(8);
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
        List<RepairTaskDeviceRel> repairTaskDevice = repairTaskDeviceRelMapper.selectList(objectLambdaQueryWrapper.eq(RepairTaskDeviceRel::getRepairTaskId,examineDTO.getId()));
        //任务清单主键id集合
        if (CollectionUtil.isNotEmpty(repairTaskDevice)){
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
        if (CollectionUtil.isNotEmpty(repairTaskStandard)){
            //标准主键id集合
            List<String> collect4 = repairTaskStandard.stream().map(RepairTaskStandardRel::getId).collect(Collectors.toList());
            repairTaskStandardRelMapper.deleteBatchIds(collect4);
        }

        if (ObjectUtil.isNotNull(repairTask)){
            //根据设备编号查询人员
            LambdaQueryWrapper<RepairTaskUser> repairTaskUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
            List<RepairTaskUser>  repairTaskUsers = repairTaskUserMapper.selectList(repairTaskUserLambdaQueryWrapper.eq(RepairTaskUser::getRepairTaskCode,repairTask.getCode()));
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
        repairPool.setStatus(3);
        repairPool.setRemark(examineDTO.getContent());
        repairPoolMapper.updateById(repairPool);

    }
}
