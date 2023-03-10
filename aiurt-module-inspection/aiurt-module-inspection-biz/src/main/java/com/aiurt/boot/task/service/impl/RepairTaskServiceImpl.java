package com.aiurt.boot.task.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.api.InspectionApi;
import com.aiurt.boot.constant.DictConstant;
import com.aiurt.boot.constant.InspectionConstant;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
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
import com.aiurt.boot.task.dto.*;
import com.aiurt.boot.task.entity.*;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.boot.task.service.IRepairTaskService;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtNoDataException;
import com.aiurt.common.util.ArchiveUtils;
import com.aiurt.common.util.DateUtils;
import com.aiurt.common.util.PdfUtil;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: repair_task
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@Service
public class RepairTaskServiceImpl extends ServiceImpl<RepairTaskMapper, RepairTask> implements IRepairTaskService, InspectionApi {

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
    @Autowired
    private ISysBaseAPI iSysBaseAPI;
    @Autowired
    private ISTodoBaseAPI isTodoBaseAPI;
    @Autowired
    private ISysParamAPI iSysParamAPI;

//    @Autowired
//    private RepairTaskService repairTaskService;
    @Autowired
    ArchiveUtils archiveUtils;

    @Value("${support.path.exportRepairTaskPath}")
    private String exportPath;

    @Override
    public Page<RepairTask> selectables(Page<RepairTask> pageList, RepairTask condition) {
        //去掉查询参数的所有空格
        if (condition.getCode() != null) {
            condition.setCode(condition.getCode().replaceAll(" ", ""));
        }

        // 数据权限过滤
        condition.setCodeList(handleDataPermission());
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
                e.setSiteName(manager.translateStationList(list2));
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
            //备注
            e.setContent(e.getErrorContent());

            //附件
            List<String> enclosures = repairTaskEnclosureMapper.getByRepairTaskId(e.getId());
            for (String enclosure:enclosures){
                RepairTaskEnclosure repairTaskEnclosure = repairTaskEnclosureMapper.getByResultId(enclosure);
                if (repairTaskEnclosure!=null){
                    if (!repairTaskEnclosure.getUrl().isEmpty()){
                        e.setPath(repairTaskEnclosure.getUrl());
                    }
                }
            }


            //备注
            e.setContent(e.getErrorContent());

            //附件
            List<String> enclosures = repairTaskEnclosureMapper.getByRepairTaskId(e.getId());
            for (String enclosure:enclosures){
                List<RepairTaskEnclosure> repairTaskEnclosure = repairTaskEnclosureMapper.getByResultId(enclosure);
                if (repairTaskEnclosure!=null){
                    for (RepairTaskEnclosure list: repairTaskEnclosure){
                        if (!list.getUrl().isEmpty()){
                            e.setPath(list.getUrl());
                        }
                    }
                }
            }

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
                        e.setOverhaulName(stringBuffer1.toString());
                    }
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

    /**
     * 数据权限处理
     * @param
     * @return
     */
    private List<String> handleDataPermission() {
        // 组织机构权限
        List<RepairTaskOrgRel> repairTaskOrgRels = repairTaskOrgRelMapper.selectList(null);
        if (CollUtil.isEmpty(repairTaskOrgRels)) {
            throw new AiurtNoDataException(InspectionConstant.NO_DATA, new ArrayList<>());
        }

        // 专业、专业子系统权限
        List<String> taskCodes = repairTaskStandardRelMapper.getRepairTaskCode();
        if (CollUtil.isEmpty(taskCodes)) {
            throw new AiurtNoDataException(InspectionConstant.NO_DATA, new ArrayList<>());
        }

        // 站点权限
        List<RepairTaskStationRel> repairTaskStationRels = repairTaskStationRelMapper.selectList(new LambdaQueryWrapper<RepairTaskStationRel>().eq(RepairTaskStationRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isEmpty(repairTaskStationRels)) {
            throw new AiurtNoDataException(InspectionConstant.NO_DATA, new ArrayList<>());
        }
        List<String> result = CollUtil.newArrayList(CollUtil.intersection(repairTaskOrgRels.stream().map(RepairTaskOrgRel::getRepairTaskCode).collect(Collectors.toList()), repairTaskStationRels.stream().map(RepairTaskStationRel::getRepairTaskCode).collect(Collectors.toList()), taskCodes));

        if(CollUtil.isEmpty(result)){
            throw new AiurtNoDataException(InspectionConstant.NO_DATA, new ArrayList<>());
        }
        return result;
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
            boolean nullSafetyPrecautions = sysBaseApi.isNullSafetyPrecautions(e.getMajorCode(), e.getSystemCode(),e.getStandardCode(),1);
            e.setIsNullSafetyPrecautions(nullSafetyPrecautions);
            //根据设备编码翻译设备名称和设备类型名称
            List<RepairDeviceDTO> repairDeviceDTOList = manager.queryDeviceByCodes(Arrays.asList(e.getEquipmentCode()));
            repairDeviceDTOList.forEach(q -> {
                //设备名称
                e.setEquipmentName(q.getName());
                //设备类型名称
                e.setDeviceTypeName(q.getDeviceTypeName());
            });
            //设备位置
            if (e.getEquipmentCode() != null) {
                List<StationDTO> stationDTOList = repairTaskMapper.selectStationLists(e.getEquipmentCode());
                e.setEquipmentLocation(manager.translateStation(stationDTOList));
            }
            //翻译线路
            if(StrUtil.isNotBlank(e.getLineCode())){
                String s = manager.translateLine(e.getLineCode());
                e.setLineName(s);
            }
            //翻译站点
            if(StrUtil.isNotBlank(e.getStationCode())){
                String s = manager.translateStation(e.getStationCode());
                e.setStationName(s);
            }
            //翻译位置
            if(StrUtil.isNotBlank(e.getPositionCode())){
                String s = manager.translatePosition(e.getPositionCode());
                e.setPositionName(s);
            }
            //提交人名称
            if (e.getOverhaulId() != null) {
                String realName = repairTaskMapper.getRealName(e.getOverhaulId());
                e.setOverhaulName(realName);
            }
            if (e.getDeviceId() != null && CollectionUtil.isNotEmpty(repairTasks)) {
                //正常项
                List<RepairTaskResult> repairTaskResults = repairTaskMapper.selectSingle(e.getDeviceId(), InspectionConstant.RESULT_STATUS);
                e.setNormal(Integer.toString(repairTaskResults.size()));
                //异常项
                List<RepairTaskResult> repairTaskResults1 = repairTaskMapper.selectSingle(e.getDeviceId(), InspectionConstant.NO_RESULT_STATUS);
                e.setAbnormal(Integer.toString(repairTaskResults1.size()));
            }
            //检修任务状态
            if (e.getStartTime() == null) {
                e.setTaskStatusName("未开始");
                e.setTaskStatus("0");
                e.setNormal("-");
                e.setAbnormal("-");
            }
            if (e.getStartTime() != null) {
                e.setTaskStatusName("进行中");
                e.setTaskStatus("1");
            }
            if (e.getIsSubmit() != null && e.getIsSubmit().equals(InspectionConstant.IS_EFFECT)) {
                e.setTaskStatusName("已提交");
                e.setTaskStatus("2");
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

            //检修位置
            if (e.getSpecificLocation() != null) {
                List<StationDTO> stationDTOList = new ArrayList<>();
                stationDTOList.forEach(q -> {
                    q.setStationCode(e.getStationCode());
                    q.setLineCode(e.getLineCode());
                    q.setPositionCode(e.getPositionCode());
                });
                String station = manager.translateStation(stationDTOList);
                if (station != null) {
                    String string = e.getSpecificLocation() + station;
                    e.setMaintenancePosition(string);
                } else {
                    e.setMaintenancePosition(e.getSpecificLocation());
                }

            }
        });
        return pageList.setRecords(repairTasks);
    }

    @Override
    public List<RepairTaskDTO> selectTaskList(String taskId, String stationCode) {
        //无设备
        List<RepairTaskDTO> repairTasks = repairTaskMapper.selectTaskList(taskId, stationCode);
        //有设备
        List<RepairTaskDTO> repairDeviceTask = repairTaskMapper.selectDeviceTaskList(taskId);
        for (RepairTaskDTO repairTaskDTO : repairDeviceTask) {
            String equipmentCode = repairTaskDTO.getEquipmentCode();
            if(StrUtil.isNotBlank(equipmentCode)){
                JSONObject deviceByCode = iSysBaseAPI.getDeviceByCode(equipmentCode);
                String station_code = deviceByCode.getString("stationCode");
                if((stationCode).equals(station_code)){
                    repairTasks.add(repairTaskDTO);
                }
            }
        }
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
            //检修单名称：检修标准title+设备名称
            if (e.getIsAppointDevice() == 1) {
                e.setResultName(e.getOverhaulStandardName() + "(" + e.getEquipmentName() + ")");
            } else {
                e.setResultName(e.getOverhaulStandardName());
            }

            //设备位置
            if (e.getEquipmentCode() != null) {
                List<StationDTO> stationDTOList = repairTaskMapper.selectStationLists(e.getEquipmentCode());
                e.setEquipmentLocation(manager.translateStation(stationDTOList));
            }
            //提交人名称
            if (e.getOverhaulId() != null) {
                String realName = repairTaskMapper.getRealName(e.getOverhaulId());
                e.setOverhaulName(realName);
            }
            if (e.getDeviceId() != null && CollectionUtil.isNotEmpty(repairTasks)) {
                //正常项
                List<RepairTaskResult> repairTaskResults = repairTaskMapper.selectSingle(e.getDeviceId(), InspectionConstant.RESULT_STATUS);
                e.setNormal(Integer.toString(repairTaskResults.size()));
                //异常项
                List<RepairTaskResult> repairTaskResults1 = repairTaskMapper.selectSingle(e.getDeviceId(), InspectionConstant.NO_RESULT_STATUS);
                e.setAbnormal(Integer.toString(repairTaskResults1.size()));
            }
            //检修任务状态
            if (e.getStartTime() == null) {
                e.setTaskStatusName("未开始");
                e.setTaskStatus("0");
                e.setNormal("-");
                e.setAbnormal("-");
            }
            if (e.getStartTime() != null) {
                e.setTaskStatusName("进行中");
                e.setTaskStatus("1");
            }
            if (e.getIsSubmit() != null && e.getIsSubmit().equals(InspectionConstant.IS_EFFECT)) {
                e.setTaskStatusName("已提交");
                e.setTaskStatus("2");
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

            //检修位置
            if (e.getSpecificLocation() != null) {
                List<StationDTO> stationDTOList = new ArrayList<>();
                stationDTOList.forEach(q -> {
                    q.setStationCode(e.getStationCode());
                    q.setLineCode(e.getLineCode());
                    q.setPositionCode(e.getPositionCode());
                });
                String station = manager.translateStation(stationDTOList);
                if (station != null) {
                    String string = e.getSpecificLocation() + station;
                    e.setMaintenancePosition(string);
                } else {
                    e.setMaintenancePosition(e.getSpecificLocation());
                }

            }

        });
        return repairTasks;
    }

    @Override
    public List<RepairTaskStationDTO> repairTaskStationList(String taskId) {
        List<RepairTaskStationDTO> repairTaskStationDtos = repairTaskMapper.repairTaskStationList(taskId);
        return repairTaskStationDtos;
    }


    @Override
    public CheckListDTO selectRepairTaskInfo(String taskId, String stationCode, String deviceId) {
        CheckListDTO checkListDTO = repairTaskMapper.selectRepairTaskInfo(taskId, stationCode, deviceId);

        // 检修时长格式化
        if (ObjectUtil.isNotEmpty(checkListDTO)) {
            Integer duration = checkListDTO.getDuration();
            if (duration != null) {
                checkListDTO.setDurationName(DateUtils.getTimeByMinute(duration));
            }
        }
        if (ObjectUtil.isNotEmpty(checkListDTO)) {
            if (checkListDTO.getDeviceId() != null && ObjectUtil.isNotNull(checkListDTO)) {
                List<RepairTaskResult> repairTaskResults = repairTaskMapper.selectSingle(checkListDTO.getDeviceId(), 1);
                if (CollUtil.isNotEmpty(repairTaskResults)) {
                    checkListDTO.setNormal(repairTaskResults.size());
                }
                List<RepairTaskResult> repairTaskResults1 = repairTaskMapper.selectSingle(checkListDTO.getDeviceId(), 2);
                if (CollUtil.isNotEmpty(repairTaskResults1)) {
                    checkListDTO.setAbnormal(repairTaskResults1.size());
                }
                //检修结果
                if (ObjectUtil.isNotEmpty(checkListDTO.getAbnormal())) {
                    //异常
                    checkListDTO.setStatusName(sysBaseApi.translateDict(DictConstant.OVERHAUL_RESULT, String.valueOf(InspectionConstant.NO_RESULT_STATUS)));
                }
                if (ObjectUtil.isEmpty(checkListDTO.getAbnormal()) & ObjectUtil.isNotEmpty(checkListDTO.getNormal())) {
                    //正常
                    checkListDTO.setStatusName(sysBaseApi.translateDict(DictConstant.OVERHAUL_RESULT, String.valueOf(InspectionConstant.RESULT_STATUS)));
                }
                if (ObjectUtil.isEmpty(checkListDTO.getAbnormal()) & ObjectUtil.isEmpty(checkListDTO.getNormal())) {
                    //都为空则显示null
                    checkListDTO.setStatusName(null);
                }

            }
            //检修单名称
            if (checkListDTO.getResultCode() != null) {
                checkListDTO.setResultName(checkListDTO.getResultCode());

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

            if (checkListDTO.getEquipmentCode() != null) {
                //根据设备编码翻译设备名称和设备类型名称
                List<RepairDeviceDTO> repairDeviceDTOList = manager.queryDeviceByCodes(Arrays.asList(checkListDTO.getEquipmentCode()));
                //设备位置
                List<StationDTO> stationDTOList = repairTaskMapper.selectStationLists(checkListDTO.getEquipmentCode());
                repairDeviceDTOList.forEach(q -> {
                    //设备名称
                    checkListDTO.setEquipmentName(q.getName());
                    //设备类型名称
                    checkListDTO.setDeviceTypeName(q.getDeviceTypeName());
                    //设备类型编码
                    checkListDTO.setDeviceTypeCode(q.getDeviceTypeCode());
                    //设备id
                    checkListDTO.setEquipmentId(q.getDeviceId());
                    //设备专业
                    checkListDTO.setDeviceMajorName(q.getMajorName());
                    //设备专业编码
                    checkListDTO.setDeviceMajorCode(q.getMajorCode());
                    //设备子系统
                    checkListDTO.setDeviceSystemName(q.getSubsystemName());
                    //设备子系统编码
                    checkListDTO.setDeviceSystemCode(q.getSubsystemCode());
                    //线路编码
                    checkListDTO.setLineCode(q.getLineCode());
                    //位置编码
                    checkListDTO.setPositionCode(q.getPositionCode());
                    //站点编码
                    checkListDTO.setSiteCode(q.getStationCode());
                });
                checkListDTO.setEquipmentLocation(manager.translateStation(stationDTOList));

                //站点位置
                List<StationDTO> stationDtoList1 = new ArrayList<>();
                stationDtoList1.forEach(e -> {
                    e.setStationCode(checkListDTO.getStationCode());
                    e.setLineCode(checkListDTO.getLineCode());
                    e.setPositionCode(checkListDTO.getPositionCode());
                });
                String station = manager.translateStation(stationDTOList);
                checkListDTO.setSitePosition(station);
            }
            if (checkListDTO.getEquipmentCode() == null) {
                //设备专业
                checkListDTO.setDeviceMajorName(manager.translateMajor(Arrays.asList(checkListDTO.getMajorCode()), InspectionConstant.MAJOR));
                //设备专业编码
                checkListDTO.setDeviceMajorCode(checkListDTO.getMajorCode());
                //设备子系统
                checkListDTO.setDeviceSystemName(manager.translateMajor(Arrays.asList(checkListDTO.getSystemCode()), InspectionConstant.SUBSYSTEM));
                //设备子系统编码
                checkListDTO.setDeviceSystemCode(checkListDTO.getSystemCode());
                //根据站点编码翻译站点名称
                if (checkListDTO.getStationCode() != null && checkListDTO.getLineCode() != null) {
                    String string1 = manager.translateLine(checkListDTO.getLineCode()) + "/" + manager.translateStation(checkListDTO.getStationCode());
                    checkListDTO.setStationsName(string1);
                    checkListDTO.setSiteCode(checkListDTO.getStationCode());
                }
            }
            //提交人名称
            if (checkListDTO.getOverhaulId() != null) {
                String realName = repairTaskMapper.getRealName(checkListDTO.getOverhaulId());
                checkListDTO.setOverhaulName(realName);
            }

            //检修位置
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

            //构造树形
            List<RepairTaskResult> repairTaskResults = selectCodeContentList(checkListDTO.getDeviceId());
            checkListDTO.setRepairTaskResultList(repairTaskResults);
            List<RepairTaskResult> repairTaskResultList = checkListDTO.getRepairTaskResultList();
            ArrayList<String> list = new ArrayList<>();
            int sum1 = InspectionConstant.NO_IS_EFFECT;
            int sum2 = InspectionConstant.NO_IS_EFFECT;
            int sum3 = InspectionConstant.NO_IS_EFFECT;
            long count1 = InspectionConstant.NO_IS_EFFECT;
            long count2 = InspectionConstant.NO_IS_EFFECT;
            long count3 = InspectionConstant.NO_IS_EFFECT;
            if (CollectionUtil.isNotEmpty(repairTaskResultList)) {
                for (RepairTaskResult r : repairTaskResultList) {
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
                    count1 = repairTaskResultList.stream().filter(repairTaskResult -> repairTaskResult.getType().equals(InspectionConstant.IS_EFFECT)).count();
                    //已检修的数量父级
                    count2 = repairTaskResultList.stream().filter(repairTaskResult -> repairTaskResult.getStatus() != null && repairTaskResult.getType().equals(InspectionConstant.IS_EFFECT)).count();
                    //待检修的数量父级
                    count3 = repairTaskResultList.stream().filter(repairTaskResult -> repairTaskResult.getStatus() == null && repairTaskResult.getType().equals(InspectionConstant.IS_EFFECT)).count();
                    checkListDTO.setMaintenanceItemsQuantity((int) count1);
                    checkListDTO.setOverhauledQuantity((int) count2);
                    checkListDTO.setToBeOverhauledQuantity((int) count3);
                    if (CollectionUtils.isNotEmpty(children)) {
                        //检查项的数量子级
                        long count11 = children.stream().filter(repairTaskResult -> repairTaskResult.getType().equals(InspectionConstant.IS_EFFECT)).count();
                        sum1 = sum1 + (int) count11;
                        //已检修的数量子级
                        long count22 = children.stream().filter(repairTaskResult -> repairTaskResult.getStatus() != null && repairTaskResult.getType().equals(InspectionConstant.IS_EFFECT)).count();
                        sum2 = sum2 + (int) count22;
                        //待检修的数量子级
                        long count33 = children.stream().filter(repairTaskResult -> repairTaskResult.getStatus() == null && repairTaskResult.getType().equals(InspectionConstant.IS_EFFECT)).count();
                        sum3 = sum3 + (int) count33;
                    }
                }
                ;
                sum1 = sum1 + (int) count1;
                sum2 = sum2 + (int) count2;
                sum3 = sum3 + (int) count3;
                checkListDTO.setMaintenanceItemsQuantity(sum1);
                checkListDTO.setOverhauledQuantity(sum2);
                checkListDTO.setToBeOverhauledQuantity(sum3);
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
        }
        return checkListDTO;
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
            if (e.getEquipmentCode() != null) {
                List<StationDTO> stationDTOList = repairTaskMapper.selectStationLists(e.getEquipmentCode());
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
                String realName = repairTaskMapper.getRealName(e.getOverhaulId());
                e.setOverhaulName(realName);
            }
            if (e.getDeviceId() != null && CollectionUtil.isNotEmpty(repairTasks)) {
                //正常项
                List<RepairTaskResult> repairTaskResults = repairTaskMapper.selectSingle(e.getDeviceId(), InspectionConstant.RESULT_STATUS);
                e.setNormal(Integer.toString(repairTaskResults.size()));
                //异常项
                List<RepairTaskResult> repairTaskResults1 = repairTaskMapper.selectSingle(e.getDeviceId(), InspectionConstant.NO_RESULT_STATUS);
                e.setAbnormal(Integer.toString(repairTaskResults1.size()));
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
        Map<String, String> map = new HashMap<>(16);
        if (CollectionUtil.isNotEmpty(repairTaskDTOList)) {
            repairTaskDTOList.forEach(e -> {
                String majorCode = e.getMajorCode();
                String systemCode1 = e.getSystemCode();
                majorCodes1.add(majorCode);
                map.put(systemCode1, majorCode);
                systemCode.add(systemCode1);
            });
        }
        //根据专业编码查询对应的专业子系统
        List<MajorDTO> majorDTOList = repairTaskMapper.translateMajor(majorCodes1);
        if (CollectionUtil.isNotEmpty(majorDTOList)) {
            majorDTOList.forEach(q -> {
                systemCode.forEach(o -> {
                    String string = map.get(o);
                    if (q.getMajorCode().equals(string)) {
                        List<SubsystemDTO> subsystemDTOList = repairTaskMapper.translateSubsystem(q.getMajorCode(), o);
                        q.setSubsystemDTOList(subsystemDTOList);
                    }
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

        // 检修时长格式化
        if (ObjectUtil.isNotEmpty(checkListDTO)) {
            Integer duration = checkListDTO.getDuration();
            if (duration != null) {
                checkListDTO.setDurationName(DateUtils.getTimeByMinute(duration));
            }
        }

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

        if (checkListDTO.getEquipmentCode() != null) {
            //根据设备编码翻译设备名称和设备类型名称
            List<RepairDeviceDTO> repairDeviceDTOList = manager.queryDeviceByCodes(Arrays.asList(checkListDTO.getEquipmentCode()));
            //设备位置
            List<StationDTO> stationDTOList = repairTaskMapper.selectStationLists(checkListDTO.getEquipmentCode());
            repairDeviceDTOList.forEach(q -> {
                //设备名称
                checkListDTO.setEquipmentName(q.getName());
                //设备类型名称
                checkListDTO.setDeviceTypeName(q.getDeviceTypeName());
                //设备类型编码
                checkListDTO.setDeviceTypeCode(q.getDeviceTypeCode());
                //设备id
                checkListDTO.setEquipmentId(q.getDeviceId());
                //设备专业
                checkListDTO.setDeviceMajorName(q.getMajorName());
                //设备专业编码
                checkListDTO.setDeviceMajorCode(q.getMajorCode());
                //设备子系统
                checkListDTO.setDeviceSystemName(q.getSubsystemName());
                //设备子系统编码
                checkListDTO.setDeviceSystemCode(q.getSubsystemCode());
                //线路编码
                checkListDTO.setLineCode(q.getLineCode());
                //位置编码
                checkListDTO.setPositionCode(q.getPositionCode());
                //站点编码
                checkListDTO.setSiteCode(q.getStationCode());
            });
            checkListDTO.setEquipmentLocation(manager.translateStation(stationDTOList));

            //站点位置
            List<StationDTO> stationDtoList1 = new ArrayList<>();
            stationDtoList1.forEach(e -> {
                e.setStationCode(checkListDTO.getStationCode());
                e.setLineCode(checkListDTO.getLineCode());
                e.setPositionCode(checkListDTO.getPositionCode());
            });
            String station = manager.translateStation(stationDTOList);
            checkListDTO.setSitePosition(station);
        }
        if (checkListDTO.getEquipmentCode() == null) {
            //设备专业
            checkListDTO.setDeviceMajorName(manager.translateMajor(Arrays.asList(checkListDTO.getMajorCode()), InspectionConstant.MAJOR));
            //设备专业编码
            checkListDTO.setDeviceMajorCode(checkListDTO.getMajorCode());
            //设备子系统
            checkListDTO.setDeviceSystemName(manager.translateMajor(Arrays.asList(checkListDTO.getSystemCode()), InspectionConstant.SUBSYSTEM));
            //设备子系统编码
            checkListDTO.setDeviceSystemCode(checkListDTO.getSystemCode());
            //根据站点编码翻译站点名称
            if (checkListDTO.getStationCode() != null && checkListDTO.getLineCode() != null) {
                String string1 = manager.translateLine(checkListDTO.getLineCode()) + "/" + manager.translateStation(checkListDTO.getStationCode());
                checkListDTO.setStationsName(string1);
                checkListDTO.setSiteCode(checkListDTO.getStationCode());
            }
        }
        //提交人名称
        if (checkListDTO.getOverhaulId() != null) {
            String realName = repairTaskMapper.getRealName(checkListDTO.getOverhaulId());
            checkListDTO.setOverhaulName(realName);
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
        //构造树形
        List<RepairTaskResult> repairTaskResults = selectCodeContentList(checkListDTO.getDeviceId());
        checkListDTO.setRepairTaskResultList(repairTaskResults);
        List<RepairTaskResult> repairTaskResultList = checkListDTO.getRepairTaskResultList();
        ArrayList<String> list = new ArrayList<>();
        int sum1 = InspectionConstant.NO_IS_EFFECT;
        int sum2 = InspectionConstant.NO_IS_EFFECT;
        int sum3 = InspectionConstant.NO_IS_EFFECT;
        long count1 = InspectionConstant.NO_IS_EFFECT;
        long count2 = InspectionConstant.NO_IS_EFFECT;
        long count3 = InspectionConstant.NO_IS_EFFECT;
        if (CollectionUtil.isNotEmpty(repairTaskResultList)) {
            for (RepairTaskResult r : repairTaskResultList) {
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
                count1 = repairTaskResultList.stream().filter(repairTaskResult -> repairTaskResult.getType().equals(InspectionConstant.IS_EFFECT)).count();
                //已检修的数量父级
                count2 = repairTaskResultList.stream().filter(repairTaskResult -> repairTaskResult.getStatus() != null && repairTaskResult.getType().equals(InspectionConstant.IS_EFFECT)).count();
                //待检修的数量父级
                count3 = repairTaskResultList.stream().filter(repairTaskResult -> repairTaskResult.getStatus() == null && repairTaskResult.getType().equals(InspectionConstant.IS_EFFECT)).count();
                checkListDTO.setMaintenanceItemsQuantity((int) count1);
                checkListDTO.setOverhauledQuantity((int) count2);
                checkListDTO.setToBeOverhauledQuantity((int) count3);
                if (CollectionUtils.isNotEmpty(children)) {
                    //检查项的数量子级
                    long count11 = children.stream().filter(repairTaskResult -> repairTaskResult.getType().equals(InspectionConstant.IS_EFFECT)).count();
                    sum1 = sum1 + (int) count11;
                    //已检修的数量子级
                    long count22 = children.stream().filter(repairTaskResult -> repairTaskResult.getStatus() != null && repairTaskResult.getType().equals(InspectionConstant.IS_EFFECT)).count();
                    sum2 = sum2 + (int) count22;
                    //待检修的数量子级
                    long count33 = children.stream().filter(repairTaskResult -> repairTaskResult.getStatus() == null && repairTaskResult.getType().equals(InspectionConstant.IS_EFFECT)).count();
                    sum3 = sum3 + (int) count33;
                }
            }
            ;
            sum1 = sum1 + (int) count1;
            sum2 = sum2 + (int) count2;
            sum3 = sum3 + (int) count3;
            checkListDTO.setMaintenanceItemsQuantity(sum1);
            checkListDTO.setOverhauledQuantity(sum2);
            checkListDTO.setToBeOverhauledQuantity(sum3);
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

            //检查值是否必填
            r.setInspectionTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_VALUE, String.valueOf(r.getInspectionType())));

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
                String realName = repairTaskMapper.getRealName(r.getStaffId());
                if (ObjectUtil.isNotNull(realName)) {
                    r.setStaffName(realName);
                }
            }
            //备注
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
        LoginUser loginUser = manager.checkLogin();
        String realName = repairTaskMapper.getRealName(loginUser.getId());
        RepairTask repairTask1 = new RepairTask();
        repairTask1.setCode(repairTask.getCode());
        // 修改审核待办任务的状态
        isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.INSPECTION_CONFIRM.getType(), repairTask.getId(), loginUser.getUsername(), CommonTodoStatus.DONE_STATUS_1);

        status(examineDTO, loginUser, realName, repairTask1, repairTask.getRepairPoolId());
        if (examineDTO.getStatus().equals(InspectionConstant.IS_EFFECT) && repairTask.getIsReceipt().equals(InspectionConstant.IS_EFFECT)) {
            //修改检修任务状态
            repairTask1.setId(examineDTO.getId());
            repairTask1.setErrorContent(examineDTO.getContent());
            repairTask1.setConfirmTime(new Date());
            repairTask1.setConfirmUserId(loginUser.getId());
            repairTask1.setConfirmUserName(realName);
            repairTask1.setStatus(InspectionConstant.PENDING_RECEIPT);
            repairTaskMapper.updateById(repairTask1);
            // 修改对应检修计划状态
            RepairPool repairPool = repairPoolMapper.selectById(repairTask.getRepairPoolId());
            if (ObjectUtil.isNotEmpty(repairPool)) {
                repairPool.setStatus(InspectionConstant.PENDING_RECEIPT);
                repairPoolMapper.updateById(repairPool);
            }
            sendAcceptanceMessage(repairTask);
        }
        if (examineDTO.getStatus().equals(InspectionConstant.IS_EFFECT) && repairTask.getIsReceipt().equals(InspectionConstant.NO_IS_EFFECT)) {
            repairTask1.setId(examineDTO.getId());
            repairTask1.setErrorContent(examineDTO.getContent());
            repairTask1.setConfirmTime(new Date());
            repairTask1.setConfirmUserId(loginUser.getId());
            repairTask1.setConfirmUserName(realName);
            repairTask1.setStatus(InspectionConstant.COMPLETED);
            repairTaskMapper.updateById(repairTask1);

            // 修改对应检修计划状态
            RepairPool repairPool = repairPoolMapper.selectById(repairTask.getRepairPoolId());
            if (ObjectUtil.isNotEmpty(repairPool)) {
                repairPool.setStatus(InspectionConstant.COMPLETED);
                repairPoolMapper.updateById(repairPool);
            }
            sendAcceptanceMessage(repairTask);
        }
        // 创建验收待办任务
        try {
            if (examineDTO.getStatus().equals(InspectionConstant.IS_EFFECT) && repairTask.getIsReceipt().equals(InspectionConstant.IS_EFFECT)) {
                String realNames = null;
                String currentUserName = getUserName(repairTask.getCode(), RoleConstant.TECHNICIAN);
                if (StrUtil.isNotEmpty(currentUserName)) {
                    List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(new LambdaQueryWrapper<RepairTaskUser>().eq(RepairTaskUser::getRepairTaskCode, repairTask1.getCode()).eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
                    if(CollUtil.isNotEmpty(repairTaskUsers)){
                        String[] userIds = repairTaskUsers.stream().map(RepairTaskUser::getUserId).toArray(String[]::new);
                        List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
                        if (CollUtil.isNotEmpty(loginUsers)) {
                            realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                        }
                    }
                    TodoDTO todoDTO = new TodoDTO();
                    todoDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE);
                    todoDTO.setTitle("检修任务-验收" + DateUtil.today());
                    todoDTO.setMsgAbstract("检修任务验收");
                    todoDTO.setPublishingContent("检修任务审核待验收");
                    createTodoTask(currentUserName, TodoBusinessTypeEnum.INSPECTION_RECEIPT.getType(), repairTask.getId(), "检修任务验收", "", "", todoDTO, repairTask, realNames, null);

                    /*MessageDTO messageDTO = new MessageDTO(manager.checkLogin().getUsername(),currentUserName, "检修任务-验收" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_5);
                    RepairTaskMessageDTO repairTaskMessageDTO = new RepairTaskMessageDTO();
                    BeanUtil.copyProperties(repairTask,repairTaskMessageDTO);
                    //业务类型，消息类型，消息模板编码，摘要，发布内容
                    repairTaskMessageDTO.setBusType(SysAnnmentTypeEnum.INSPECTION.getType());
                    messageDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE);
                    messageDTO.setMsgAbstract("检修任务验收");
                    messageDTO.setPublishingContent("检修任务审核待验收");
                    sendMessage(messageDTO,realNames,null,repairTaskMessageDTO);*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送审核通过消息
     * @param repairTask1
     */
    public void sendAcceptanceMessage(RepairTask repairTask1) {
        // 审核通过，消息通知检修人
        List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(new LambdaQueryWrapper<RepairTaskUser>().eq(RepairTaskUser::getRepairTaskCode, repairTask1.getCode()).eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
        if(CollUtil.isNotEmpty(repairTaskUsers)){
            String[] userIds = repairTaskUsers.stream().map(RepairTaskUser::getUserId).toArray(String[]::new);
            List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
            if (CollUtil.isNotEmpty(loginUsers)) {
                String usernames = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.joining(","));
                String realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                //发送通知
                try {
                    MessageDTO messageDTO = new MessageDTO(manager.checkLogin().getUsername(),usernames, "检修任务-审核" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_5);
                    RepairTaskMessageDTO repairTaskMessageDTO = new RepairTaskMessageDTO();
                    BeanUtil.copyProperties(repairTask1,repairTaskMessageDTO);
                    //业务类型，消息类型，消息模板编码，摘要，发布内容
                    repairTaskMessageDTO.setBusType(SysAnnmentTypeEnum.INSPECTION.getType());
                    messageDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE);
                    messageDTO.setMsgAbstract("检修任务审核");
                    messageDTO.setPublishingContent("检修任务审核通过");
                    sendMessage(messageDTO,realNames,null,repairTaskMessageDTO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
            throw new AiurtBootException("该任务没有对应的检修人");
        } else {
            List<String> userList = repairTaskUserss.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
            if (!userList.contains(manager.checkLogin().getId())) {
                throw new AiurtBootException("只有该任务的检修人才能执行");
            }
        }

        if (ObjectUtil.isEmpty(repairTask)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        // 待执行状态才可以执行
        if (InspectionConstant.PENDING.equals(repairTask.getStatus())) {
            repairTask.setStatus(InspectionConstant.IN_EXECUTION);
            repairTask.setBeginTime(new Date());
            repairTaskMapper.updateById(repairTask);

            // 修改对应检修计划状态
            RepairPool repairPool = repairPoolMapper.selectById(repairTask.getRepairPoolId());
            if (ObjectUtil.isNotEmpty(repairPool)) {
                repairPool.setStatus(InspectionConstant.IN_EXECUTION);
                repairPoolMapper.updateById(repairPool);
            }
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
            throw new AiurtBootException("该任务没有对应的检修人");
        } else {
            List<String> userList = repairTaskUserss.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
            if (!userList.contains(manager.checkLogin().getId())) {
                throw new AiurtBootException("只有该任务的检修人才能提交");
            }
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (InspectionConstant.IS_CONFIRM_1.equals(repairTask.getIsConfirm())) {
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

        // 更新待办任务状态为已完成
        isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.INSPECTION_EXECUTE.getType(), repairTask.getId(), sysUser.getUsername(), CommonTodoStatus.DONE_STATUS_1);

        // 创建审核待办任务
        try {
            if (InspectionConstant.IS_CONFIRM_1.equals(repairTask.getIsConfirm())) {
                String currentUserName = getUserName(repairTask.getCode(), RoleConstant.FOREMAN);
                if (StrUtil.isNotEmpty(currentUserName)) {
                    String realNames = null;
                    List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(new LambdaQueryWrapper<RepairTaskUser>().eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode()).eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
                    if(CollUtil.isNotEmpty(repairTaskUsers)){
                        String[] userIds = repairTaskUsers.stream().map(RepairTaskUser::getUserId).toArray(String[]::new);
                        List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
                        if (CollUtil.isNotEmpty(loginUsers)) {
                            realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                        }
                    }
                    TodoDTO todoDTO = new TodoDTO();
                    todoDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE);
                    todoDTO.setTitle("检修任务-审核" + DateUtil.today());
                    todoDTO.setMsgAbstract("检修任务审核");
                    todoDTO.setPublishingContent("您有一条检修任务审核");
                    createTodoTask(currentUserName, TodoBusinessTypeEnum.INSPECTION_CONFIRM.getType(), repairTask.getId(), "检修任务审核", "", "", todoDTO, repairTask, realNames, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取检修任务组织机构对应的角色编码的人员账号信息
     *
     * @param code
     * @param roleCode
     * @return
     */
    public String getUserName(String code, String roleCode) {
        List<RepairTaskOrgRel> repairTaskOrgRels = repairTaskOrgRelMapper.selectList(new LambdaQueryWrapper<RepairTaskOrgRel>().eq(RepairTaskOrgRel::getRepairTaskCode, code).eq(RepairTaskOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isNotEmpty(repairTaskOrgRels)) {
            List<String> orgs = repairTaskOrgRels.stream().map(RepairTaskOrgRel::getOrgCode).collect(Collectors.toList());
            String currentUserName = sysBaseApi.getUserNameByOrgCodeAndRoleCode(orgs, Arrays.asList(roleCode));
            return currentUserName;
        }
        return "";
    }


    @Override
    public void acceptance(ExamineDTO examineDTO) {
        RepairTask repairTask = repairTaskMapper.selectById(examineDTO.getId());
        if (ObjectUtil.isEmpty(repairTask)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }
        RepairTask repairTask1 = new RepairTask();
        repairTask1.setCode(repairTask.getCode());
        LoginUser loginUser = manager.checkLogin();
        String realName = repairTaskMapper.getRealName(loginUser.getId());
        // 修改验收待办任务的状态
        isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.INSPECTION_RECEIPT.getType(), repairTask.getId(), loginUser.getUsername(), CommonTodoStatus.DONE_STATUS_1);

        //添加附件
        List<String> enclosures = repairTaskEnclosureMapper.getByRepairTaskId(examineDTO.getId());
        for (String enclosure : enclosures){
            List<RepairTaskEnclosure> repairTaskEnclosure = repairTaskEnclosureMapper.getByResultId(enclosure);
            RepairTaskEnclosure taskEnclosure = new RepairTaskEnclosure();
            if (repairTaskEnclosure.size()==0 ){
                taskEnclosure.setUrl(examineDTO.getPath());
                taskEnclosure.setRepairTaskResultId(enclosure);
                repairTaskEnclosureMapper.insert(taskEnclosure);
            } else {
                for (RepairTaskEnclosure list:repairTaskEnclosure){
                    if(list.getUrl().isEmpty()){
                        list.setUrl(examineDTO.getPath());
                        repairTaskEnclosureMapper.updateById(list);
                    }
                }
            }
        }
        status(examineDTO, loginUser, realName, repairTask1, repairTask.getRepairPoolId());
        if (examineDTO.getStatus().equals(InspectionConstant.IS_EFFECT)) {
            setId(examineDTO, repairTask1, loginUser, realName, repairTask.getRepairPoolId());
        }
       }

    private void status(ExamineDTO examineDTO, LoginUser loginUser, String realName, RepairTask repairTask1, String id) {
        if (examineDTO.getStatus().equals(InspectionConstant.NO_IS_EFFECT)) {
            //修改检修任务状态
            repairTask1.setId(examineDTO.getId());
            repairTask1.setErrorContent(examineDTO.getContent());
            repairTask1.setConfirmTime(new Date());
            repairTask1.setConfirmUserId(loginUser.getId());
            repairTask1.setConfirmUserName(realName);
            repairTask1.setStatus(InspectionConstant.REJECTED);
            repairTask1.setErrorContent(examineDTO.getContent());
            repairTaskMapper.updateById(repairTask1);

            // 修改对应检修计划状态
            RepairPool repairPool = repairPoolMapper.selectById(id);
            if (ObjectUtil.isNotEmpty(repairPool)) {
                repairPool.setStatus(InspectionConstant.REJECTED);
                repairPoolMapper.updateById(repairPool);
            }
            List<String> enclosures = repairTaskEnclosureMapper.getByRepairTaskId(examineDTO.getId());
                for (String enclosure : enclosures){
                    RepairTaskEnclosure repairTaskEnclosure = repairTaskEnclosureMapper.getByResultId(enclosure);
                    RepairTaskEnclosure taskEnclosure = new RepairTaskEnclosure();
                    if (repairTaskEnclosure==null ){
                        taskEnclosure.setUrl(examineDTO.getPath());
                        taskEnclosure.setRepairTaskResultId(enclosure);
                        repairTaskEnclosureMapper.insert(taskEnclosure);
                    } else if(repairTaskEnclosure.getUrl().isEmpty()){
                        repairTaskEnclosure.setUrl(examineDTO.getPath());
                        repairTaskEnclosureMapper.updateById(repairTaskEnclosure);
                    }
                }

            // 给检修人驳回发消息
            sendBackMessage(repairTask1,examineDTO.getAcceptanceRemark());
        }
    }

    /**
     * 发送消息
     * @param repairTask1
     */
    private void sendBackMessage(RepairTask repairTask1,Integer remark) {
        List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(new LambdaQueryWrapper<RepairTaskUser>().eq(RepairTaskUser::getRepairTaskCode, repairTask1.getCode()).eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
        if(CollUtil.isNotEmpty(repairTaskUsers)){
            String[] userIds = repairTaskUsers.stream().map(RepairTaskUser::getUserId).toArray(String[]::new);
            List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);

            if (CollUtil.isNotEmpty(loginUsers)) {
                String usernames = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.joining(","));
                String realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                //发送通知
                try {
                    String title = null;
                    if (remark == 1) {
                        title = "检修任务-审核驳回" + DateUtil.today();
                    } else {
                        title = "检修任务-验收驳回" + DateUtil.today();
                    }
                    MessageDTO messageDTO = new MessageDTO(manager.checkLogin().getUsername(), usernames, title, null, CommonConstant.MSG_CATEGORY_5);
                    RepairTaskMessageDTO repairTaskMessageDTO = new RepairTaskMessageDTO();
                    RepairTask repairTask = repairTaskMapper.selectById(repairTask1.getId());
                    if (ObjectUtil.isEmpty(repairTask)) {
                        throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
                    }
                    BeanUtil.copyProperties(repairTask,repairTaskMessageDTO);
                    //构建消息模板
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("errorContent",repairTask1.getErrorContent());
                    /*messageDTO.setData(map);
                    //业务类型，消息类型，消息模板编码，摘要，发布内容
                    repairTaskMessageDTO.setBusType(SysAnnmentTypeEnum.INSPECTION.getType());
                    messageDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE_REJECT);
                    messageDTO.setMsgAbstract("检修任务审核驳回");
                    messageDTO.setPublishingContent("检修任务审核驳回，请重新处理");
                    sendMessage(messageDTO,realNames,null,repairTaskMessageDTO);*/

                    TodoDTO todoDTO = new TodoDTO();
                    todoDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE_REJECT);
                    todoDTO.setTitle("检修任务-审核驳回" + DateUtil.today());
                    todoDTO.setMsgAbstract("检修任务审核驳回");
                    todoDTO.setPublishingContent("检修任务审核驳回，请重新处理");
                    todoDTO.setData(map);
                    createTodoTask(usernames, TodoBusinessTypeEnum.INSPECTION_CONFIRM.getType(),repairTask.getId(), "检修任务审核驳回", "", "",todoDTO,repairTask,realNames,null);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void setId(ExamineDTO examineDTO, RepairTask repairTask1, LoginUser loginUser, String realName, String id) {
        //修改检修任务状态
        repairTask1.setId(examineDTO.getId());
        repairTask1.setErrorContent(examineDTO.getContent());
        repairTask1.setReceiptTime(new Date());
        repairTask1.setReceiptUserId(loginUser.getId());
        repairTask1.setReceiptUserName(realName);
        repairTask1.setStatus(InspectionConstant.COMPLETED);
        repairTaskMapper.updateById(repairTask1);

        // 修改对应检修计划状态
        RepairPool repairPool = repairPoolMapper.selectById(id);
        if (ObjectUtil.isNotEmpty(repairPool)) {
            repairPool.setStatus(InspectionConstant.COMPLETED);
            repairPoolMapper.updateById(repairPool);
        }

        // 验收通过，消息通知检修人
        List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(new LambdaQueryWrapper<RepairTaskUser>().eq(RepairTaskUser::getRepairTaskCode, repairTask1.getCode()).eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
        if(CollUtil.isNotEmpty(repairTaskUsers)){
            String[] userIds = repairTaskUsers.stream().map(RepairTaskUser::getUserId).toArray(String[]::new);
            List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
            if (CollUtil.isNotEmpty(loginUsers)) {
                String usernames = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.joining(","));
                String realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                //发送通知
                try {
                    MessageDTO messageDTO = new MessageDTO(manager.checkLogin().getUsername(), usernames, "检修任务-验收" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_5);
                    RepairTaskMessageDTO repairTaskMessageDTO = new RepairTaskMessageDTO();
                    RepairTask repairTask = repairTaskMapper.selectById(repairTask1.getId());
                    if (ObjectUtil.isEmpty(repairTask)) {
                        throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
                    }
                    BeanUtil.copyProperties(repairTask,repairTaskMessageDTO);
                    //业务类型，消息类型，消息模板编码，摘要，发布内容
                    repairTaskMessageDTO.setBusType(SysAnnmentTypeEnum.INSPECTION.getType());
                    messageDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE);
                    messageDTO.setMsgAbstract("检修任务审核");
                    messageDTO.setPublishingContent("检修任务审核通过");
                    sendMessage(messageDTO,realNames,null,repairTaskMessageDTO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<RepairTaskEnclosure> selectEnclosure(String resultId) {
        return repairTaskMapper.selectEnclosure(resultId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmedDelete(ExamineDTO examineDTO) {
        if (StrUtil.isBlank(examineDTO.getContent())) {
            throw new AiurtBootException("退回理由不能为空！");
        }
        RepairTask repairTask = repairTaskMapper.selectById(examineDTO.getId());
        if (ObjectUtil.isEmpty(repairTask)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }
        // 是任务的检修人才可以退回
        List<RepairTaskUser> repairTaskUserss = repairTaskUserMapper.selectList(
                new LambdaQueryWrapper<RepairTaskUser>()
                        .eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode())
                        .eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
        //保留检修人的id
        List<String> userList = repairTaskUserss.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
        if (CollUtil.isEmpty(repairTaskUserss)) {
            throw new AiurtBootException("该任务没有对应的检修人");
        } else {
            if (!userList.contains(manager.checkLogin().getId())) {
                throw new AiurtBootException("只有该任务的检修人才能退回");
            }
        }

        // 如果该任务是被指派的，则发消息提醒指派人
        if (StrUtil.isNotEmpty(repairTask.getAssignUserId())) {
            LoginUser user = sysBaseApi.getUserById(repairTask.getAssignUserId());
            if (ObjectUtil.isNotEmpty(user) && StrUtil.isNotEmpty(user.getUsername())) {
                //发送通知
                try {
                    MessageDTO messageDTO = new MessageDTO(manager.checkLogin().getUsername(), user.getUsername(), "检修任务-退回"+DateUtil.today(), null, CommonConstant.MSG_CATEGORY_5);
                    RepairTaskMessageDTO repairTaskMessageDTO = new RepairTaskMessageDTO();
                    BeanUtil.copyProperties(repairTask,repairTaskMessageDTO);
                    repairTaskMessageDTO.setId(repairTask.getId());
                    //构建消息模板
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("returnReason",examineDTO.getContent());
                    messageDTO.setData(map);
                    //业务类型，消息类型，消息模板编码，摘要，发布内容
                    repairTaskMessageDTO.setBusType(SysAnnmentTypeEnum.INSPECTION.getType());
                    messageDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE_RETURN);
                    messageDTO.setMsgAbstract("检修任务退回");
                    messageDTO.setPublishingContent("检修任务退回，请重新安排");
                    List<String> userNames = repairTaskUserss.stream().map(RepairTaskUser::getName).collect(Collectors.toList());
                    sendMessage(messageDTO,CollUtil.join(userNames,","),null,repairTaskMessageDTO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

        // 更新检修计划状态
        RepairPool repairPool = new RepairPool();
        repairPool.setId(repairTask.getRepairPoolId());
        repairPool.setStatus(InspectionConstant.GIVE_BACK);
        repairPool.setRemark(examineDTO.getContent());
        repairPoolMapper.updateById(repairPool);

        // 将接收检修待办任务改为已完成
        isTodoBaseAPI.updateTodoTaskState(TodoBusinessTypeEnum.INSPECTION_EXECUTE.getType(), repairTask.getId(), manager.checkLogin().getUsername(), CommonTodoStatus.DONE_STATUS_1);
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

        // 生成待办任务
        try {
            String currentUserName = manager.checkLogin().getUsername();
            if (StrUtil.isNotEmpty(currentUserName)) {
                String realNames = null;
                List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(new LambdaQueryWrapper<RepairTaskUser>().eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode()).eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
                if(CollUtil.isNotEmpty(repairTaskUsers)){
                    String[] userIds = repairTaskUsers.stream().map(RepairTaskUser::getUserId).toArray(String[]::new);
                    List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
                    if (CollUtil.isNotEmpty(loginUsers)) {
                        realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                    }
                }
                TodoDTO todoDTO = new TodoDTO();
                todoDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE);
                todoDTO.setTitle("检修任务-领取" + DateUtil.today());
                todoDTO.setMsgAbstract("领取检修任务");
                todoDTO.setPublishingContent("您领取了一条检修任务，请尽快检修");

                createTodoTask(currentUserName, TodoBusinessTypeEnum.INSPECTION_EXECUTE.getType(),repairTask.getId(), "执行检修任务", "", "",todoDTO,repairTask,realNames,null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            throw new AiurtBootException("该检修任务已被指派或已被领取");
        }

        // 当前登录人所属部门是在检修任务的指派部门范围内才可以领取
        List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(
                new LambdaQueryWrapper<RepairPoolOrgRel>()
                        .eq(RepairPoolOrgRel::getRepairPoolCode, repairPool.getCode())
                        .eq(RepairPoolOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
            List<String> orgList = repairPoolOrgRels.stream().map(RepairPoolOrgRel::getOrgCode).collect(Collectors.toList());
            if (!orgList.contains(manager.checkLogin().getOrgCode())) {
                throw new AiurtBootException("该检修任务不在您的领取范围之内哦");
            }
        }

        // 现在的时间大于任务的开始时间才可以进行领取
        if (repairPool.getStartTime() != null && DateUtil.compare(new Date(), repairPool.getStartTime()) < 0) {
            throw new AiurtBootException("未到检修任务开始时间，暂时无法领取");
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
        if(StrUtil.isNotBlank(monadDTO.getNote()) && StrUtil.isNotBlank(result.getDataCheck())){
            boolean matches = monadDTO.getNote().matches(result.getDataCheck());
            if (!matches){
                String regex = sysBaseApi.translateDict("regex", result.getDataCheck());
                throw new AiurtBootException(regex);
            }
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
                        throw new AiurtBootException("未到检修任务开始时间");
                    }

                    // 只有任务状态是执行中或已驳回才可以改
                    if (!InspectionConstant.IN_EXECUTION.equals(repairTask.getStatus())
                            && !InspectionConstant.REJECTED.equals(repairTask.getStatus())) {
                        throw new AiurtBootException("只有任务被驳回或者执行中才可以操作");
                    }

                    // 是任务的检修人才可以填写
                    List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(
                            new LambdaQueryWrapper<RepairTaskUser>()
                                    .eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode())
                                    .eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
                    if (CollUtil.isEmpty(repairTaskUsers)) {
                        throw new AiurtBootException("该任务没有对应的检修人");
                    } else {
                        List<String> userList = repairTaskUsers.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
                        if (!userList.contains(manager.checkLogin().getId())) {
                            throw new AiurtBootException("您不是该检修任务的检修人");
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
            // TODO 暂默认非必填，此处校验后期需求有调整
//            // 选择项
//            if (InspectionConstant.STATUS_ITEM_CHOICE.equals(repair.getStatusItem()) && repair.getInspeciontValue() == null) {
//                throw new AiurtBootException("有检修值未填写");
//            }
//            // 输入项
//            if (InspectionConstant.STATUS_ITEM_INPUT.equals(repair.getStatusItem()) && StrUtil.isEmpty(repair.getNote())) {
//                throw new AiurtBootException("有检修值未填写");
//            }
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

        LoginUser loginUser = manager.checkLogin();
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

        // 查询任务的检修人
        List<String> userIds = null;
        List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(
                new LambdaQueryWrapper<RepairTaskUser>()
                        .eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode()));
        if (CollUtil.isNotEmpty(repairTaskUsers)) {
            userIds = repairTaskUsers.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
        }

        // 查询登录人部门下所有的人员
        orgDto = manager.queryUserByOrdCode(loginUser.getOrgCode());

        // 过滤掉检修人
        if (CollUtil.isNotEmpty(orgDto)) {
            for (OrgDTO orgDTO : orgDto) {
                if (ObjectUtil.isNotEmpty(orgDTO)) {
                    if (orgDTO.getOrgCode().equals(loginUser.getOrgCode())) {
                        List<LoginUser> users = orgDTO.getUsers();
                        if (CollUtil.isNotEmpty(users) && CollUtil.isNotEmpty(userIds)) {
                            List<String> finalUserIds = userIds;
                            orgDTO.setUsers(users.stream().filter(u -> !finalUserIds.contains(u.getId())).collect(Collectors.toList()));
                        }
                    }
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
            throw new AiurtBootException("该任务没有对应的检修人");
        } else {
            List<String> userList = repairTaskUsers.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
            if (!userList.contains(manager.checkLogin().getId())) {
                throw new AiurtBootException("只有该任务的检修人才能确认");
            }
        }

        // 待确认状态才可以确认
        if (InspectionConstant.TO_BE_CONFIRMED.equals(repairTask.getStatus())) {
            repairTask.setStatus(InspectionConstant.PENDING);
            repairTaskMapper.updateById(repairTask);

            // 修改对应检修计划状态
            RepairPool repairPool = repairPoolMapper.selectById(repairTask.getRepairPoolId());
            if (ObjectUtil.isNotEmpty(repairPool)) {
                repairPool.setStatus(InspectionConstant.PENDING);
                repairPoolMapper.updateById(repairPool);
            }

            // 新建待办任务
            try {
                String currentUserName = getCurrentUserName(repairTask);
                if (StrUtil.isNotEmpty(currentUserName)) {
                    String realNames = null;
                    String[] userIds = repairTaskUsers.stream().map(RepairTaskUser::getUserId).toArray(String[]::new);
                    List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
                    if (CollUtil.isNotEmpty(loginUsers)) {
                        realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                    }
                    TodoDTO todoDTO = new TodoDTO();
                    todoDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE);
                    todoDTO.setTitle("检修任务-待执行" + DateUtil.today());
                    todoDTO.setMsgAbstract("检修任务待执行");
                    todoDTO.setPublishingContent("您有一条检修任务待执行");
                    createTodoTask(currentUserName, TodoBusinessTypeEnum.INSPECTION_EXECUTE.getType(), repairTask.getId(), "执行检修任务", "", "", todoDTO, repairTask, realNames, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }
    }

    /**
     * 获取检修任务对应的检修人账号信息
     *
     * @param repairTask
     * @return
     */
    public String getCurrentUserName(RepairTask repairTask) {
        List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(new LambdaQueryWrapper<RepairTaskUser>().eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode()).eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isEmpty(repairTaskUsers)) {
            return "";
        }
        String[] userNames = repairTaskUsers.stream().map(RepairTaskUser::getUserId).toArray(String[]::new);
        List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userNames);
        if (CollUtil.isNotEmpty(loginUsers)) {
            return loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.joining(","));
        }
        return "";
    }

    /**
     * 创建待办任务
     *
     * @param currentUserName 办理人账号
     * @param businessKey     检修任务编码
     * @param businessType    业务类型
     * @param taskName        任务名称
     * @param url             pc跳转前端路径
     * @param appUrl          app跳转前端路径
     */
    private void createTodoTask(String currentUserName,String businessType, String businessKey, String taskName, String url, String appUrl, TodoDTO todoDTO,RepairTask repairTask1,String realNames, String realName) {
       //构建消息模板
        HashMap<String, Object> map = new HashMap<>();
        if (CollUtil.isNotEmpty(todoDTO.getData())) {
            map.putAll(todoDTO.getData());
        }
        map.put("code",repairTask1.getCode());
        String typeName = sysBaseApi.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repairTask1.getType()));
        map.put("repairTaskName",typeName+repairTask1.getCode());
        List<String> codes = repairTaskMapper.getRepairTaskStation(repairTask1.getId());
        Map<String, String> stationNameByCode = iSysBaseAPI.getStationNameByCode(codes);
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entry : stationNameByCode.entrySet()) {
            stringBuilder.append(entry.getValue());
            stringBuilder.append(",");
        }
        if (stringBuilder.length() > 0) {
            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        map.put("repairStation",stringBuilder.toString());
        if (repairTask1.getEndTime() != null) {
            map.put("repairTaskTime",DateUtil.format(repairTask1.getStartTime(),"yyyy-MM-dd HH:mm")+"-"+DateUtil.format(repairTask1.getEndTime(),"yyyy-MM-dd HH:mm"));
        }else {
            map.put("repairTaskTime",DateUtil.format(repairTask1.getStartTime(),"yyyy-MM-dd HH:mm"));
        }
        if (StrUtil.isNotEmpty(realNames)) {
            map.put("repairName", realNames);
        } else {
            map.put("repairName",realName);
        }
        todoDTO.setData(map);
        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.REPAIR_MESSAGE_PROCESS);
        todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
        todoDTO.setTaskName(taskName);
        todoDTO.setBusinessKey(businessKey);
        todoDTO.setBusinessType(businessType);
        todoDTO.setCurrentUserName(currentUserName);
        todoDTO.setTaskType(TodoTaskTypeEnum.INSPECTION.getType());
        todoDTO.setTodoType(CommonTodoStatus.TODO_STATUS_0);
        todoDTO.setProcessDefinitionName("检修管理");
        todoDTO.setUrl(url);
        todoDTO.setAppUrl(appUrl);
        isTodoBaseAPI.createTodoTask(todoDTO);
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

    /**
     * 故障回调
     *
     * @param faultCallbackDTO
     */
    @Override
    public void editFaultCallback(FaultCallbackDTO faultCallbackDTO) {
        RepairTaskDeviceRel repairTaskDeviceRel = new RepairTaskDeviceRel();
        String id = repairTaskDeviceRelMapper.getId(faultCallbackDTO.getSingleCode());
        if (id != null) {
            repairTaskDeviceRel.setId(id);
            repairTaskDeviceRel.setFaultCode(faultCallbackDTO.getFaultCode());
            repairTaskDeviceRelMapper.updateById(repairTaskDeviceRel);
        }
    }

    @Override
    public HashMap<String, String> getInspectionTaskDevice(DateTime startTime, DateTime endTime) {
        HashMap<String, String> map = new HashMap<>();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<LoginUser> sysUsers = iSysBaseAPI.getUserPersonnel(sysUser.getOrgId());
        List<String> userIds = Optional.ofNullable(sysUsers).orElse(Collections.emptyList()).stream().map(LoginUser::getId).collect(Collectors.toList());
        //获取当前班组用户的检修任务编号
        List<RepairTaskUser> taskUsers = repairTaskUserMapper.selectList(new LambdaQueryWrapper<RepairTaskUser>().in(RepairTaskUser::getUserId, userIds).eq(RepairTaskUser::getDelFlag, 0));
        if (CollUtil.isNotEmpty(taskUsers)) {
            //根据任务编号，获取检修任务信息
            List<RepairTaskDeviceRel> taskDeviceRelList = new ArrayList<>();
            List<RepairTaskDeviceRel> oldTaskDeviceRelList = new ArrayList<>();
            List<String> pairTaskCodes = taskUsers.stream().map(RepairTaskUser::getRepairTaskCode).distinct().collect(Collectors.toList());
            List<RepairTask> taskList = repairTaskMapper.selectList(new LambdaQueryWrapper<RepairTask>().in(RepairTask::getCode, pairTaskCodes));

            List<String> repairTaskIds = taskList.stream().map(RepairTask::getId).distinct().collect(Collectors.toList());
            for (String repairTaskId : repairTaskIds) {
                //获取当前用户作为领取/指派人，当天，已提交的工单
                List<RepairTaskDeviceRel> deviceRelList = repairTaskDeviceRelMapper.getTodaySubmit(startTime, endTime, repairTaskId, null);
                if (ObjectUtil.isNotEmpty(deviceRelList)) {
                    taskDeviceRelList.addAll(deviceRelList);
                }

            }

            //获取当前用户作为同行人参与的单号
            List<RepairTaskPeerRel> relList = repairTaskPeerRelMapper.selectList(new LambdaQueryWrapper<RepairTaskPeerRel>().eq(RepairTaskPeerRel::getUserId, sysUser.getId()));
            //获取单号信息
            if (CollUtil.isNotEmpty(relList)) {
                for (RepairTaskPeerRel taskPeerRel : relList) {
                    List<RepairTaskDeviceRel> deviceRelList = repairTaskDeviceRelMapper.getTodaySubmit(startTime, endTime, null, taskPeerRel.getRepairTaskDeviceCode());
                    if (ObjectUtil.isNotEmpty(deviceRelList)) {
                        oldTaskDeviceRelList.addAll(deviceRelList);
                    }
                }
            }

            taskDeviceRelList.addAll(oldTaskDeviceRelList);


            if (CollUtil.isNotEmpty(taskDeviceRelList)) {
                //去重
                Set<RepairTaskDeviceRel> list = new HashSet<>(taskDeviceRelList);
                StringBuilder content = new StringBuilder();
                StringBuilder code = new StringBuilder();

                if (CollUtil.isNotEmpty(list)) {
                    HashMap<String, Map<StringBuilder, StringBuilder>> hashMap = new HashMap<>();

                    for (RepairTaskDeviceRel deviceRel : list) {
                        HashMap<StringBuilder, StringBuilder> map1 = new HashMap<>();
                        StringBuilder lineStation = new StringBuilder();
                        StringBuilder staffName = new StringBuilder();

                        String stationName = iSysBaseAPI.getPosition(deviceRel.getStationCode());
                        String lineName = iSysBaseAPI.getPosition(deviceRel.getLineCode());
                        //如果工单中不存在线路站点，则从设备中拿
                        if (StrUtil.isEmpty(stationName) && StrUtil.isEmpty(lineName)) {
                            String deviceCode = deviceRel.getDeviceCode();
                            JSONObject deviceByCode = iSysBaseAPI.getDeviceByCode(deviceCode);
                            stationName = iSysBaseAPI.getPosition(deviceByCode.getString("lineCode"));
                            lineName = iSysBaseAPI.getPosition(deviceByCode.getString("stationCode"));
                        }
                        LoginUser userById = iSysBaseAPI.getUserById(deviceRel.getStaffId());
                        if (deviceRel.getWeeks() == null) {
                            Calendar c = Calendar.getInstance();
                            c.setFirstDayOfWeek(Calendar.MONDAY);
                            c.setTime(deviceRel.getStartTime());
                            c.setMinimalDaysInFirstWeek(7);
                            int i = c.get(Calendar.WEEK_OF_YEAR);
                            deviceRel.setWeeks(i);
                        }

                        StringBuilder append1 = lineStation.append(lineName).append("-").append(stationName).append(" ").append("第").append(deviceRel.getWeeks()).append("周检修任务").append(" ").append(" 检修人:");
                        StringBuilder append2 = staffName.append(userById.getRealname());
                        //同检修任务下的，不同工单中的，同线路站点的不同检修人要合并起来
                        Map<StringBuilder, StringBuilder> mapList = hashMap.get(deviceRel.getTaskCode());
                        if (CollUtil.isNotEmpty(mapList)) {
                            for (Map.Entry<StringBuilder, StringBuilder> n : mapList.entrySet()) {
                                StringBuilder stringBuilder = n.getValue();
                                if (ObjectUtil.isNotEmpty(stringBuilder)&&!stringBuilder.toString().contains(staffName)) {
                                    n.setValue(append2.append(",").append(stringBuilder));
                                } else {
                                    map1.put(append1, append2);
                                }

                            }

                        } else {
                            append2.append("。").append('\n');
                            map1.put(append1, append2);
                            hashMap.put(deviceRel.getTaskCode(), map1);
                        }
                    }

                    for (Map.Entry<String, Map<StringBuilder, StringBuilder>> m : hashMap.entrySet()) {
                        code.append(m.getKey()).append(",");
                        Map<StringBuilder, StringBuilder> value = m.getValue();
                        for (Map.Entry<StringBuilder, StringBuilder> n : value.entrySet()) {
                            content.append(n.getKey()).append(n.getValue());
                        }
                    }

                    if (content.length() > 1) {
                        // 截取字符
                        content = content.deleteCharAt(content.length() - 1);
                        map.put("content", content.toString());
                    }
                    if (code.length() > 1) {
                        // 截取字符
                        code = code.deleteCharAt(code.length() - 1);
                        map.put("code", code.toString());
                    }

                }
            }
        }

        return map;
    }

    /**
     * 检修消息发送
     *
     * @param messageDTO
     * @param realNames
     * @param realNames
     * @param repairTaskMessageDTO
     */
    @Override
    public void sendMessage(MessageDTO messageDTO, String realNames, String realName, RepairTaskMessageDTO repairTaskMessageDTO) {
        //发送通知
        //构建消息模板
        HashMap<String, Object> map = new HashMap<>();
        if (CollUtil.isNotEmpty(messageDTO.getData())) {
            map.putAll(messageDTO.getData());
        }
        map.put("code",repairTaskMessageDTO.getCode());
        String typeName = sysBaseApi.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repairTaskMessageDTO.getType()));
        map.put("repairTaskName",typeName+repairTaskMessageDTO.getCode());
        List<String> codes = repairTaskMapper.getRepairTaskStation(repairTaskMessageDTO.getId());
        if (CollUtil.isNotEmpty(codes)) {
            Map<String, String> stationNameByCode = iSysBaseAPI.getStationNameByCode(codes);
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<String, String> entry : stationNameByCode.entrySet()) {
                stringBuilder.append(entry.getValue());
                stringBuilder.append(",");
            }
            if (stringBuilder.length() > 0) {
                stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            }
            map.put("repairStation",stringBuilder.toString());
        }
        if (repairTaskMessageDTO.getEndTime() != null) {
            map.put("repairTaskTime",DateUtil.format(repairTaskMessageDTO.getStartTime(),"yyyy-MM-dd HH:mm")+"-"+DateUtil.format(repairTaskMessageDTO.getEndTime(),"yyyy-MM-dd HH:mm"));
        }else {
            map.put("repairTaskTime",DateUtil.format(repairTaskMessageDTO.getStartTime(),"yyyy-MM-dd HH:mm"));
        }
        if (StrUtil.isNotEmpty(realNames)) {
            map.put("repairName", realNames);
        } else {
            map.put("repairName",realName);
        }
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, repairTaskMessageDTO.getId());
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, repairTaskMessageDTO.getBusType());
        messageDTO.setData(map);
        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.REPAIR_MESSAGE);
        messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
        iSysBaseAPI.sendTemplateMessage(messageDTO);
    }

    @Override
    public IPage<SystemInformationDTO> getSystemInformation(SystemInformationDTO systemInformationDTO) {
        Page<SystemInformationDTO> pageList = new Page<>(systemInformationDTO.getPageNo(),systemInformationDTO.getPageSize());

        //查询所有线路
        List<SystemInformationDTO> systemInformation = repairTaskMapper.getSystemInformation(pageList);
        systemInformation.forEach(e->{
            e.setSystemTyp("通信");
            String lineCode = e.getLineCode();
            if(StrUtil.isNotBlank(lineCode)){
                //根据线路Code查询站点Code
                List<String> stationCodeByLineCode = sysBaseApi.getStationCodeByLineCode(lineCode);
                if (CollectionUtil.isNotEmpty(stationCodeByLineCode)){
                    //检修总数
                    Long maintenanceQuantity = repairTaskMapper.getMaintenanceQuantity(stationCodeByLineCode,null);
                    //巡检总数
                    Long inspection = repairTaskMapper.getInspection(stationCodeByLineCode, null);
                    e.setIplanSum(maintenanceQuantity+inspection);

                    //检修已完成总数
                    Long maintenanceQuantity1 = repairTaskMapper.getMaintenanceQuantity(stationCodeByLineCode, CommonConstant.REPAIR_POOL_ACCOMPLISH);
                    //巡检已完成总数
                    Long inspection1 = repairTaskMapper.getInspection(stationCodeByLineCode, CommonConstant.PATROL_TASK);
                    e.setIplanComplete(maintenanceQuantity1+inspection1);


                    List<String> faultCodeList = repairTaskMapper.getFaultCodeList(stationCodeByLineCode);
                    if (CollectionUtil.isNotEmpty(faultCodeList)){
                        //故障总数
                        e.setFaultSum((long) faultCodeList.size());

                        //故障完成总数
                        Long faultQuantity = repairTaskMapper.getFaultQuantity(faultCodeList);
                        e.setFaultComplete(faultQuantity);
                    }
                }else {
                    e.setIplanSum(CommonConstant.ASSIGNMENT);
                    e.setIplanComplete(CommonConstant.ASSIGNMENT);
                    e.setFaultSum(CommonConstant.ASSIGNMENT);
                    e.setFaultComplete(CommonConstant.ASSIGNMENT);

                }
            }
        });
        pageList.setRecords(systemInformation);
        return pageList;
    }

    @Override
    public void archRepairTask(RepairTask repairTask, String token, String archiveUserId, String refileFolderId, String realname, String sectId) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = repairTask.getSiteName() + "检修记录表" + sdf.format(repairTask.getStartTime());
            Date date = new Date();
            //传入档案系统
            //创建文件夹
            String foldername = fileName + "_" + date.getTime();
            String refileFolderIdNew = archiveUtils.createFolder(token, refileFolderId, foldername);
            //上传文件
            String fileType = "pdf";
            File file = new File(exportPath + fileName + "." + fileType);
            Long size = file.length();
            InputStream in = new FileInputStream(file);
            JSONObject res = archiveUtils.upload(token, refileFolderIdNew, fileName + "." + fileType, size, fileType, in);
            String fileId = res.getString("fileId");
            Map<String, String> fileInfo = new HashMap<>();
            fileInfo.put("fileId", fileId);
            fileInfo.put("operateType", "upload");
            ArrayList<Object> fileList = new ArrayList<>();
            fileList.add(fileInfo);
            Map values = new HashMap();
            values.put("archiver", archiveUserId);
            values.put("username", realname);
            values.put("duration", repairTask.getSecertduration());
            values.put("secert", repairTask.getSecert());
            values.put("secertduration",  repairTask.getSecertduration());
            values.put("name", fileName);
            values.put("fileList", fileList);
            values.put("number", values.get("number"));
            values.put("refileFolderId", refileFolderIdNew);
            values.put("sectid", sectId);
            Map result = archiveUtils.arch(values, token);
            Map<String, String> obj = JSON.parseObject((String) result.get("obj"), new TypeReference<HashMap<String, String>>() {
            });

            //更新归档状态
            if (result.get("result").toString() == "true" && "新增".equals(obj.get("rs"))) {
                UpdateWrapper<RepairTask> uwrapper = new UpdateWrapper<>();
                uwrapper.eq("id", repairTask.getId()).set("ecm_status", 1);
                update(uwrapper);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void exportPdf(HttpServletRequest request, RepairTask repairTask, HttpServletResponse response) throws IOException {
        String path = "templates/repairTaskTemplate.xlsx";
        TemplateExportParams params = new TemplateExportParams(path, true);
        Map<String, Object> map = new HashMap<String, Object>();

//        检修任务单号
        map.put("code", repairTask.getCode());
//        任务来源
        map.put("sourceName",repairTask.getSourceName() );
//        适用专业
        map.put("majorName", repairTask.getMajorName());
//        适用系统
        map.put("systemName", repairTask.getSystemName());
//        适用站点
        map.put("siteName", repairTask.getSiteName());
//        组织机构
        map.put("organizational",repairTask.getOrganizational());
//        检修周期类型
        map.put("typeName", repairTask.getTypeName());
//        所属周
        map.put("weekName", repairTask.getWeekName());
//        作业类型
        map.put("workType", repairTask.getWorkType());
//        作业令
        map.put("planOrderCodeUrl", repairTask.getPlanOrderCodeUrl());
//        同行人
        map.put("peerName", repairTask.getPeerName());
//        计划开始时间
        map.put("startTime", DateUtil.format(repairTask.getStartTime(), "YYYY-MM-dd HH:mm:ss"));
//        计划结束时间vwv
        map.put("endTime", DateUtil.format(repairTask.getEndTime(), "YYYY-MM-dd HH:mm:ss"));
//        开始检修任务时间
        map.put("overhaulTime", DateUtil.format(repairTask.getStartOverhaulTime(), "YYYY-MM-dd HH:mm:ss"));
//        结束检修任务时间
        map.put("endOverhaulTime", DateUtil.format(repairTask.getEndOverhaulTime(), "YYYY-MM-dd HH:mm:ss"));
//        任务状态
        map.put("statusName", repairTask.getStatusName());
//        检修任务提交人
        map.put("sumitUserName",repairTask.getSumitUserName());

        String fileName = repairTask.getSiteName() + "检修记录表" ;
        String exportRepairTaskPath = exportPath +"/" +fileName + ".xlsx";
        Workbook workbook = ExcelExportUtil.exportExcel(params,map);
        FileOutputStream fos = new FileOutputStream(exportRepairTaskPath);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        workbook.write(bos);
        bos.close();
        fos.close();
        workbook.close();
        PdfUtil.excel2pdf(exportRepairTaskPath);
    }
}
