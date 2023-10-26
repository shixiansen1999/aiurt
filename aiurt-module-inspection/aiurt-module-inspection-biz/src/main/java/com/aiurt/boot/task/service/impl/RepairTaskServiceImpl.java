package com.aiurt.boot.task.service.impl;

import cn.afterturn.easypoi.excel.ExcelExportUtil;
import cn.afterturn.easypoi.excel.entity.TemplateExportParams;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.IoUtil;
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
import com.aiurt.boot.plan.entity.*;
import com.aiurt.boot.plan.mapper.*;
import com.aiurt.boot.plan.service.IRepairPoolService;
import com.aiurt.boot.standard.entity.InspectionCode;
import com.aiurt.boot.standard.mapper.InspectionCodeMapper;
import com.aiurt.boot.task.CustomCellMergeHandler;
import com.aiurt.boot.task.dto.*;
import com.aiurt.boot.task.entity.*;
import com.aiurt.boot.task.mapper.*;
import com.aiurt.boot.task.service.IRepairTaskService;
import com.aiurt.boot.task.service.IRepairTaskSignUserService;
import com.aiurt.boot.task.service.IRepairTaskStandardRelService;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.api.dto.quartz.QuartzJobDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.CommonTodoStatus;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtNoDataException;
import com.aiurt.common.result.SpareResult;
import com.aiurt.common.util.*;
import com.aiurt.config.datafilter.object.GlobalThreadLocal;
import com.aiurt.modules.basic.entity.SysAttachment;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.enums.WriteDirectionEnum;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.util.IoUtils;
import com.alibaba.excel.util.MapUtils;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.excel.write.metadata.fill.FillConfig;
import com.alibaba.excel.write.metadata.fill.FillWrapper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.xiaoymin.knife4j.core.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.ImageUtils;
import org.apache.poi.util.Units;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.DictModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysDepartModel;
import org.jeecg.common.system.vo.SysParamModel;
import org.jetbrains.annotations.NotNull;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.net.ssl.TrustManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @Description: repair_task
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@Service
@Slf4j
public class RepairTaskServiceImpl extends ServiceImpl<RepairTaskMapper, RepairTask> implements IRepairTaskService, InspectionApi {
    @Value("${jeecg.path.upload:/opt/upFiles}")
    private String path;
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
    private RepairTaskSamplingMapper repairTaskSamplingMapper;
    @Autowired
    private RepairTaskUserMapper repairTaskUserMapper;
    @Autowired
    private RepairTaskOrgRelMapper repairTaskOrgRelMapper;
    @Autowired
    private RepairPoolMapper repairPoolMapper;
    @Autowired
    private RepairTaskEnclosureMapper repairTaskEnclosureMapper;
    @Autowired
    private InspectionCodeMapper inspectionCodeMapper;
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
    @Autowired
    private RepairPoolRelMapper relMapper;
    @Autowired
    private RepairPoolCodeMapper repairPoolCodeMapper;
    @Autowired
    private RepairPoolCodeContentMapper repairPoolCodeContentMapper;
    @Autowired
    ArchiveUtils archiveUtils;

    @Autowired
    private IRepairTaskSignUserService repairTaskSignUserService;
    @Autowired
    private IRepairTaskStandardRelService repairTaskStandardRelService;

    @Value("${support.path.exportRepairTaskPath}")
    private String exportPath;
    @Value("${jeecg.minio.bucketName}")
    private String bucketName;
    @Value(value = "${jeecg.path.upload}")
    private String uploadpath;
    /**
     * 检修任务列表查询
     *
     * @param pageList  分页查询条件
     * @param condition 查询条件
     * @return 分页的检修任务列表
     */
    @Override
    public Page<RepairTask> selectables(Page<RepairTask> pageList, RepairTask condition) {
        // 去掉查询参数的所有空格
        removeSpacesFromQueryCondition(condition);

        List<RepairTask> lists;

        if (CollUtil.isNotEmpty(condition.getSelections())){
            // 只根据id查询，page就不设置了
            lists = repairTaskMapper.selectablesByIds(condition.getSelections());
        }else{
            lists = repairTaskMapper.selectables(pageList, condition);
        }

        boolean filter = GlobalThreadLocal.setDataFilter(false);

        // 获取所有检修任务的id列表
        List<String> repairTaskIds = lists.stream().map(RepairTask::getId).collect(Collectors.toList());

        //判空处理
        if (CollUtil.isEmpty(repairTaskIds)) {
            return pageList.setRecords(lists);
        }

        // 站台门四期、有多同行人签名的情况的话，预览要是多人的签名图片，给RepairTask的confirmUrl赋值，多个以","分隔
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.INSPECTION_SIGN_MULTI);
        boolean value = "1".equals(paramModel.getValue());
        // value为true，则检修任务展示签名是包括同行人在内的多签名
        if(value){
            // 查询lists的所有task任务的所有签名
            LambdaQueryWrapper<RepairTaskSignUser> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.in(RepairTaskSignUser::getRepairTaskId, repairTaskIds);
            queryWrapper.eq(RepairTaskSignUser::getDelFlag, CommonConstant.DEL_FLAG_0);
            List<RepairTaskSignUser> taskSignUserList = repairTaskSignUserService.list(queryWrapper);
            // 将所有签名做成一个map,key是任务id，value是使用”,“分隔的多个签名url
            Map<String, String> taskSignUrlMap = taskSignUserList.stream().filter(taskSignUser -> StrUtil.isNotEmpty(taskSignUser.getSignUrl()))
                    .collect(Collectors.toMap(RepairTaskSignUser::getRepairTaskId, RepairTaskSignUser::getSignUrl, (oldValue, newValue) -> oldValue + "," + newValue));
            // 给任务列表的签名url重新赋值为多签名的url
            lists.forEach(task->{
                String signUrl = taskSignUrlMap.get(task.getId());
                if (StrUtil.isNotEmpty(signUrl)) {
                    task.setConfirmUrl(signUrl);
                }
            });
        }

        Map<String, String> taskStateMap = getTaskStateMap();
        Map<String, String> taskTypeMap = getTaskTypeMap();
        Map<String, String> isConfirmMap = getIsConfirmMap();
        Map<String, String> sourceMap = getSourceMap();
        Map<String, String> workTypeMap = getWorkTypeMap();
        Map<String, String> ecmStatusMap = getEcmStatusMap();
        Map<String, RepairTaskUserNameDTO> overhaulNameMap = getOverhaulNameMap(repairTaskIds);
        Map<String, String> peerNameMap = getPeerNameMap(repairTaskIds);
        Map<String, String> sampNameMap = getSampNameMap(repairTaskIds);
        Map<String, RepairTask> allCodeMap = getAllCodes(repairTaskIds);
        Map<String, RepairPrintMessage> printMessage = new HashMap<>();
//        Map<String, RepairPrintMessage> printMessage = getPrintMessage(repairTaskIds);

        lists.parallelStream().forEach(repairTask -> {
            RepairTaskThreadService repairTaskThreadService = new RepairTaskThreadService(repairTask, manager, taskStateMap, taskTypeMap, isConfirmMap, sourceMap, workTypeMap, ecmStatusMap, overhaulNameMap, peerNameMap, sampNameMap, printMessage,allCodeMap);
            try {
                repairTaskThreadService.call();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        GlobalThreadLocal.setDataFilter(filter);
        return pageList.setRecords(lists);
    }
    /**
     * 获取检修任务ID与任务映射。
     *
     * @param repairTaskIds 检修任务ID列表
     * @return 获取检修任务ID与任务映射
     */
    private Map<String, RepairTask> getAllCodes(List<String> repairTaskIds) {
        if (CollUtil.isEmpty(repairTaskIds)) {
            return CollUtil.newHashMap();
        }
        return Optional.ofNullable(repairTaskMapper.getAllCodes(repairTaskIds))
                .orElse(Collections.emptyList())
                .stream()
                .collect(Collectors.toMap(RepairTask::getId, r->r, (v1, v2) -> v1));
    }

    /**
     * 获取打印详情所需要的信息
     */
    private Map<String, RepairPrintMessage> getPrintMessage(List<String> repairTaskIds) {
        HashMap<String, RepairPrintMessage> hashMap = new HashMap<>();
        for (String id : repairTaskIds) {
            RepairPrintMessage repairPrintMessage = new RepairPrintMessage();
            //打印详情
            List<RepairTaskResult> repairTaskResults = new ArrayList<>();
            //获取检修站点
            List<RepairTaskStationDTO> repairTaskStationDTOS = this.repairTaskStationList(id);
            List<SpareResult> spareChange = new ArrayList<>();
            StringBuilder stringBuilder = new StringBuilder();
            List<String> enclosureUrl = new ArrayList<>();
            for (RepairTaskStationDTO repairTaskStationDTO : repairTaskStationDTOS) {
                //无设备
                List<RepairTaskDTO> tasks = repairTaskMapper.selectTaskList(id, repairTaskStationDTO.getStationCode());
                //有设备
                List<RepairTaskDTO> repairDeviceTask = repairTaskMapper.selectDeviceTaskList(id);
                for (RepairTaskDTO repairTaskDTO : repairDeviceTask) {
                    String equipmentCode = repairTaskDTO.getEquipmentCode();
                    if (StrUtil.isNotBlank(equipmentCode)) {
                        JSONObject deviceByCode = sysBaseApi.getDeviceByCode(equipmentCode);
                        if (ObjectUtil.isNotEmpty(deviceByCode)) {
                            String station_code = deviceByCode.getString("stationCode");
                            if ((repairTaskStationDTO.getStationCode()).equals(station_code)) {
                                tasks.add(repairTaskDTO);
                            }
                        }
                    }
                }
                int i = 1;
                for (RepairTaskDTO repairTaskDTO : tasks) {
                    repairTaskDTO.setSystemName(manager.translateMajor(Arrays.asList(repairTaskDTO.getSystemCode()), InspectionConstant.SUBSYSTEM));

                    String deviceId = repairTaskDTO.getDeviceId();

                    CheckListDTO checkListDTO = repairTaskMapper.selectRepairTaskInfo(id, repairTaskStationDTO.getStationCode(), deviceId);

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
                        if (ObjectUtil.isEmpty(repairPrintMessage.getRepairRecord()) && r.getStatus() != null && r.getStatus() == 1) {
                            repairPrintMessage.setRepairRecord(r.getStatusName());
                        }
                        //当检修结果异常时覆盖
                        if (r.getStatus() != null && r.getStatus() == 2) {
                            repairPrintMessage.setRepairRecord(r.getStatusName());
                        }
                    });
                    List<RepairTaskResult> repairTaskResults1 = RepairTaskServiceImpl.treeFirst(resultList);
                    repairTaskResults.addAll(repairTaskResults1);
                }
            }
            if (StrUtil.isEmpty(repairPrintMessage.getRepairRecord())) {
                repairPrintMessage.setRepairRecord("无");
            }

            repairPrintMessage.setRepairTaskResultList(repairTaskResults);
            repairPrintMessage.setSpareChange(spareChange);
            if (stringBuilder.length() > 0) {
                // 截取字符
                stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
                repairPrintMessage.setRepairResult(stringBuilder.toString());
            }
            repairPrintMessage.setEnclosureUrl(enclosureUrl);
            hashMap.put(id, repairPrintMessage);
        }
        return hashMap;

    }

    /**
     * 数据权限处理
     *
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

        if (CollUtil.isEmpty(result)) {
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
            //查询抽检人
            List<RepairTaskSampling> repairTaskSampling = repairTaskSamplingMapper.selectList(
                    new LambdaQueryWrapper<RepairTaskSampling>()
                            .eq(RepairTaskSampling::getRepairTaskDeviceCode, e.getOverhaulCode()));
            //抽检名称集合
            List<String> collect4 = repairTaskSampling.stream().map(RepairTaskSampling::getRealName).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect4)) {
                StringBuffer stringBuffer = new StringBuffer();
                for (String t : collect4) {
                    stringBuffer.append(t);
                    stringBuffer.append(",");
                }
                if (stringBuffer.length() > 0) {
                    stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                }
                e.setSamplingName(stringBuffer.toString());
            }
            //专业
            e.setMajorName(manager.translateMajor(Arrays.asList(e.getMajorCode()), InspectionConstant.MAJOR));

            //子系统
            e.setSystemName(manager.translateMajor(Arrays.asList(e.getSystemCode()), InspectionConstant.SUBSYSTEM));
            boolean nullSafetyPrecautions = sysBaseApi.isNullSafetyPrecautions(e.getMajorCode(), e.getSystemCode(), e.getStandardCode(), 1);
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
            if (StrUtil.isNotBlank(e.getLineCode())) {
                String s = manager.translateLine(e.getLineCode());
                e.setLineName(s);
            }
            //翻译站点
            if (StrUtil.isNotBlank(e.getStationCode())) {
                String s = manager.translateStation(e.getStationCode());
                e.setStationName(s);
            }
            //翻译位置
            if (StrUtil.isNotBlank(e.getPositionCode())) {
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
        List<RepairTaskDTO> collect = repairTasks.stream().sorted(Comparator.comparing(RepairTaskDTO::getTaskStatus)).collect(Collectors.toList());
        return pageList.setRecords(collect);
    }

    @Override
    public List<RepairTaskDTO> selectTaskList(String taskId, String stationCode) {
        //无设备
        List<RepairTaskDTO> repairTasks = repairTaskMapper.selectTaskList(taskId, stationCode);
        //有设备
        List<RepairTaskDTO> repairDeviceTask = repairTaskMapper.selectDeviceTaskList(taskId);
        for (RepairTaskDTO repairTaskDTO : repairDeviceTask) {
            String equipmentCode = repairTaskDTO.getEquipmentCode();
            if (StrUtil.isNotBlank(equipmentCode)) {
                JSONObject deviceByCode = iSysBaseAPI.getDeviceByCode(equipmentCode);
                if (ObjectUtil.isNotNull(deviceByCode)) {
                    String station_code = deviceByCode.getString("stationCode");
                    if ((stationCode).equals(station_code)) {
                        repairTasks.add(repairTaskDTO);
                    }
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
            //查询抽检人
            List<RepairTaskSampling> repairTaskSampling = repairTaskSamplingMapper.selectList(
                    new LambdaQueryWrapper<RepairTaskSampling>()
                            .eq(RepairTaskSampling::getRepairTaskDeviceCode, e.getOverhaulCode()));

            //抽检名称集合
            List<String> collect4 = repairTaskSampling.stream().map(RepairTaskSampling::getRealName).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect4)) {
                StringBuffer stringBuffer = new StringBuffer();
                for (String t : collect4) {
                    stringBuffer.append(t);
                    stringBuffer.append(",");
                }
                if (stringBuffer.length() > 0) {
                    stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                }
                e.setSamplingName(stringBuffer.toString());
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
            //通信十一期修改关联设备类型之后，没用检修单没用设备
            SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.MULTIPLE_DEVICE_TYPES);
            if (e.getIsAppointDevice() == 1 && "0".equals(paramModel.getValue())) {
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

                //抽检人列表
                List<RepairTaskSampling> repairTaskSampling = repairTaskSamplingMapper.selectList(
                        new LambdaQueryWrapper<RepairTaskSampling>()
                                .eq(RepairTaskSampling::getRepairTaskDeviceCode, checkListDTO.getResultCode()));
                if (CollectionUtil.isNotEmpty(repairTaskSampling)) {
                    List<ColleaguesDTO> samplingList = new ArrayList<>();
                    repairTaskSampling.forEach(p -> {
                        ColleaguesDTO colleaguesDTO = new ColleaguesDTO();
                        colleaguesDTO.setRealId(p.getUserId());
                        colleaguesDTO.setRealName(p.getRealName());
                        samplingList.add(colleaguesDTO);
                    });
                    String sampling = repairTaskSampling.stream().map(RepairTaskSampling::getRealName).collect(Collectors.joining(","));
                    checkListDTO.setSampling(sampling);
                    checkListDTO.setSamplingList(samplingList);
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
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.MULTIPLE_DEVICE_TYPES);
        List<RepairTaskDTO> repairTasks = repairTaskMapper.selectTaskletForDevice(pageList, condition,paramModel.getValue());
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

            //查询抽检人
            List<RepairTaskSampling> repairTaskSampling = repairTaskSamplingMapper.selectList(
                    new LambdaQueryWrapper<RepairTaskSampling>()
                            .eq(RepairTaskSampling::getRepairTaskDeviceCode, e.getOverhaulCode()));

            //抽检名称集合
            List<String> collect4 = repairTaskSampling.stream().map(RepairTaskSampling::getRealName).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect4)) {
                StringBuffer stringBuffer = new StringBuffer();
                for (String t : collect4) {
                    stringBuffer.append(t);
                    stringBuffer.append(",");
                }
                if (stringBuffer.length() > 0) {
                    stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                }
                e.setSamplingName(stringBuffer.toString());
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

            //抽查人列表
            List<RepairTaskSampling> repairTaskSampling = repairTaskSamplingMapper.selectList(
                    new LambdaQueryWrapper<RepairTaskSampling>()
                            .eq(RepairTaskSampling::getRepairTaskDeviceCode, checkListDTO.getResultCode()));
            if (CollectionUtil.isNotEmpty(repairTaskSampling)) {
                List<ColleaguesDTO> samplingList = new ArrayList<>();
                repairTaskSampling.forEach(p -> {
                    ColleaguesDTO colleaguesDTO = new ColleaguesDTO();
                    colleaguesDTO.setRealId(p.getUserId());
                    colleaguesDTO.setRealName(p.getRealName());
                    samplingList.add(colleaguesDTO);
                });
                checkListDTO.setSamplingList(samplingList);
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

            //抽捡人名称
            List<String> collect4 = repairTaskSampling.stream().map(RepairTaskSampling::getRealName).collect(Collectors.toList());
            if (CollectionUtil.isNotEmpty(collect4)) {
                StringBuffer stringBuffer = new StringBuffer();
                for (String t : collect3) {
                    stringBuffer.append(t);
                    stringBuffer.append(",");
                }
                if (stringBuffer.length() > 0) {
                    stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                }
                checkListDTO.setSampling(stringBuffer.toString());
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
                if (r.getStatusItem().equals(InspectionConstant.STATUS_ITEM_SPECIALCHAR_INPUT)) {
                    if (StrUtil.isNotBlank(r.getSpecialCharactersResult()) && !r.getSpecialCharacters().equals(r.getSpecialCharactersResult())) {
                        r.setInspeciontValueName(r.getSpecialCharactersResult());
                        //app详情页面使用specialCharacters展示，因此赋值结果回给这个字段
                        r.setSpecialCharacters(r.getSpecialCharactersResult());
                    }
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
                    if (CollUtil.isNotEmpty(repairTaskUsers)) {
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
     *
     * @param repairTask1
     */
    public void sendAcceptanceMessage(RepairTask repairTask1) {
        // 审核通过，消息通知检修人
        List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(new LambdaQueryWrapper<RepairTaskUser>().eq(RepairTaskUser::getRepairTaskCode, repairTask1.getCode()).eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isNotEmpty(repairTaskUsers)) {
            String[] userIds = repairTaskUsers.stream().map(RepairTaskUser::getUserId).toArray(String[]::new);
            List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
            if (CollUtil.isNotEmpty(loginUsers)) {
                String usernames = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.joining(","));
                String realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                //发送通知
                try {
                    MessageDTO messageDTO = new MessageDTO(manager.checkLogin().getUsername(), usernames, "检修任务-验收" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_5);
                    RepairTaskMessageDTO repairTaskMessageDTO = new RepairTaskMessageDTO();
                    BeanUtil.copyProperties(repairTask1, repairTaskMessageDTO);
                    //业务类型，消息类型，消息模板编码，摘要，发布内容
                    repairTaskMessageDTO.setBusType(SysAnnmentTypeEnum.INSPECTION.getType());
                    messageDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE);
                    messageDTO.setMsgAbstract("检修任务审核");
                    messageDTO.setPublishingContent("检修任务审核通过");
                    HashMap<String, Object> map = new HashMap<>();
                    map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, repairTaskMessageDTO.getId());
                    messageDTO.setData(map);
                    sendMessage(messageDTO, realNames, null, repairTaskMessageDTO);
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
            //维保任务执行中延时提醒
            processRepairInExecutionToRemind(repairTask);
        }
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
                        .select(RepairTaskUser::getUserId)
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
        LoginUser user = sysBaseApi.getUserById(sysUser.getId());

        // 站台门四期，多同行人签名，保存签名到repair_task_sign_user表里
        List<SignUserDTO> signUserDTOList = examineDTO.getSignUserDTOList();
        if (CollUtil.isNotEmpty(signUserDTOList)) {
            List<RepairTaskSignUser> repairTaskSignUserList = signUserDTOList.stream().map(signUserDTO -> {
                RepairTaskSignUser signUser = new RepairTaskSignUser();
                BeanUtils.copyProperties(signUserDTO, signUser);
                signUser.setRepairTaskId(repairTask.getId());

                // 多签名时，examineDTO.getConfirmUrl() 可能为空，从signUserDTOList中筛选赋值
                if(StrUtil.isEmpty(examineDTO.getConfirmUrl()) && sysUser.getId().equals(signUserDTO.getUserId())){
                    examineDTO.setConfirmUrl(signUserDTO.getSignUrl());
                }

                return signUser;
            }).collect(Collectors.toList());
            repairTaskSignUserService.saveBatch(repairTaskSignUserList);
        }

        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.INSPECTION_SUBMIT_SIGNATURE);
        boolean value = "1".equals(paramModel.getValue());
        if (InspectionConstant.IS_CONFIRM_1.equals(repairTask.getIsConfirm())) {
            //修改检修任务状态
            repairTask.setSubmitUserId(sysUser.getId());
            repairTask.setSumitUserName(sysUser.getRealname());
            repairTask.setSubmitTime(new Date());
            repairTask.setConfirmUrl(value?user.getSignatureUrl():examineDTO.getConfirmUrl());
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
            repairTask.setConfirmUrl(value?user.getSignatureUrl():examineDTO.getConfirmUrl());
            repairTask.setStatus(InspectionConstant.COMPLETED);
            // 修改对应检修计划状态
            RepairPool repairPool = repairPoolMapper.selectById(repairTask.getRepairPoolId());
            if (ObjectUtil.isNotEmpty(repairPool)) {
                repairPool.setStatus(InspectionConstant.COMPLETED);
                repairPoolMapper.updateById(repairPool);
            }
        }

        // 提交检修任务就更新检修任务的检修时长
        // 2023-06 通信6期 检修任务的检修时长是提交时间-开始时间，且单位秒
        if (ObjectUtil.isNotNull(repairTask.getBeginTime())) {
            repairTask.setDuration((int) DateUtil.between(repairTask.getBeginTime(), new Date(), DateUnit.SECOND));
        }else {
            repairTask.setDuration(0);
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
                    if (CollUtil.isNotEmpty(repairTaskUsers)) {
                        String[] userIds = repairTaskUsers.stream().map(RepairTaskUser::getUserId).toArray(String[]::new);
                        List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
                        if (CollUtil.isNotEmpty(loginUsers)) {
                            realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                        }
                    }
                    TodoDTO todoDTO = new TodoDTO();
                    todoDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE);
                    todoDTO.setTitle("检修任务-审核" + DateUtil.today());
                    todoDTO.setMsgAbstract("检修任务完成");
                    todoDTO.setPublishingContent("检修任务已完成，请确认");
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
        repairTask.setUrl(examineDTO.getPath());
        repairTaskMapper.updateById(repairTask);
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


            // 给检修人驳回发消息
            sendBackMessage(repairTask1, examineDTO.getAcceptanceRemark());
        }
    }

    /**
     * 发送消息
     *
     * @param repairTask1
     */
    private void sendBackMessage(RepairTask repairTask1, Integer remark) {
        List<RepairTaskUser> repairTaskUsers = repairTaskUserMapper.selectList(new LambdaQueryWrapper<RepairTaskUser>().eq(RepairTaskUser::getRepairTaskCode, repairTask1.getCode()).eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isNotEmpty(repairTaskUsers)) {
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
                    BeanUtil.copyProperties(repairTask, repairTaskMessageDTO);
                    //构建消息模板
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("errorContent", repairTask1.getErrorContent());
                    /*messageDTO.setData(map);
                    //业务类型，消息类型，消息模板编码，摘要，发布内容
                    repairTaskMessageDTO.setBusType(SysAnnmentTypeEnum.INSPECTION.getType());
                    messageDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE_REJECT);
                    messageDTO.setMsgAbstract("检修任务审核驳回");
                    messageDTO.setPublishingContent("检修任务审核驳回，请重新处理");
                    sendMessage(messageDTO,realNames,null,repairTaskMessageDTO);*/

                    TodoDTO todoDTO = new TodoDTO();
                    todoDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE_REJECT);
                    todoDTO.setTitle(title);
                    todoDTO.setMsgAbstract("检修任务审核驳回");
                    todoDTO.setPublishingContent("检修任务审核驳回，请重新处理");
                    todoDTO.setData(map);
                    createTodoTask(usernames, TodoBusinessTypeEnum.INSPECTION_CONFIRM.getType(), repairTask.getId(), "检修任务审核驳回", "", "", todoDTO, repairTask, realNames, null);

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
        if (CollUtil.isNotEmpty(repairTaskUsers)) {
            String[] userIds = repairTaskUsers.stream().map(RepairTaskUser::getUserId).toArray(String[]::new);
            List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
            if (CollUtil.isNotEmpty(loginUsers)) {
                String usernames = loginUsers.stream().map(LoginUser::getUsername).collect(Collectors.joining(","));
                String realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                //发送通知
                try {
                    MessageDTO messageDTO = new MessageDTO(manager.checkLogin().getUsername(), usernames, "检修任务-验收通过" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_5);
                    RepairTaskMessageDTO repairTaskMessageDTO = new RepairTaskMessageDTO();
                    RepairTask repairTask = repairTaskMapper.selectById(repairTask1.getId());
                    if (ObjectUtil.isEmpty(repairTask)) {
                        throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
                    }
                    BeanUtil.copyProperties(repairTask, repairTaskMessageDTO);
                    //业务类型，消息类型，消息模板编码，摘要，发布内容
                    repairTaskMessageDTO.setBusType(SysAnnmentTypeEnum.INSPECTION.getType());
                    messageDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE);
                    messageDTO.setMsgAbstract("检修任务审核");
                    messageDTO.setPublishingContent("检修任务审核通过");
                    HashMap<String, Object> map = new HashMap<>();
                    map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, repairTaskMessageDTO.getId());
                    messageDTO.setData(map);
                    sendMessage(messageDTO, realNames, null, repairTaskMessageDTO);
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
                    MessageDTO messageDTO = new MessageDTO(manager.checkLogin().getUsername(), user.getUsername(), "检修任务-退回" + DateUtil.today(), null, CommonConstant.MSG_CATEGORY_5);
                    RepairTaskMessageDTO repairTaskMessageDTO = new RepairTaskMessageDTO();
                    BeanUtil.copyProperties(repairTask, repairTaskMessageDTO);
                    //构建消息模板
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("returnReason", examineDTO.getContent());
                    messageDTO.setData(map);
                    //业务类型，消息类型，消息模板编码，摘要，发布内容
                    repairTaskMessageDTO.setBusType(SysAnnmentTypeEnum.INSPECTION_RETURN.getType());
                    messageDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE_RETURN);
                    messageDTO.setMsgAbstract("检修任务退回");
                    messageDTO.setPublishingContent("检修任务退回，请重新安排");
                    map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, repairTask.getRepairPoolId());
                    messageDTO.setData(map);
                    List<String> userNames = repairTaskUserss.stream().map(RepairTaskUser::getName).collect(Collectors.toList());
                    sendMessage(messageDTO, CollUtil.join(userNames, ","), null, repairTaskMessageDTO);
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
        repairTask.setTaskConfirmationTime(new Date());
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
                if (CollUtil.isNotEmpty(repairTaskUsers)) {
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

                createTodoTask(currentUserName, TodoBusinessTypeEnum.INSPECTION_EXECUTE.getType(), repairTask.getId(), "执行检修任务", "", "", todoDTO, repairTask, realNames, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String receiveTask(ExamineDTO examineDTO) {
        RepairPool repairPool = new RepairPool();
        if (examineDTO.getInspectionStatus() == 1) {
            RepairTask repairTask = repairTaskMapper.selectById(examineDTO.getId());
            if (ObjectUtil.isEmpty(repairTask)) {
                throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
            }
        } else {
            repairPool = repairPoolMapper.selectById(examineDTO.getId());
            if (ObjectUtil.isEmpty(repairPool)) {
                throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
            }
            // 校验领取资格
            checkReceiveTask(repairPool);
        }


        //个人领取：将待指派或退回之后重新领取改为待执行，变为个人领取（传任务主键id,状态）
        /*if (InspectionConstant.TASK_INIT.equals(examineDTO.getInspectionStatus()) || InspectionConstant.TASK_RETURNED.equals(examineDTO.getInspectionStatus())){

        }*/

        //确认：将待确认改为执行中
        if (InspectionConstant.TO_BE_CONFIRMED.equals(examineDTO.getInspectionStatus())) {
            System.out.println(examineDTO.getId() + "将待确认改为执行中");
            confirmInspectionTask(examineDTO);
//            return examineDTO.getId();
        }
        //执行：将待指派改为执行中
        if (InspectionConstant.TO_BE_ASSIGNED.equals(examineDTO.getInspectionStatus())) {
            System.out.println(examineDTO.getId() + "将待指派改为执行中");
            return excuteInspectionTask(repairPool);

        }
        //待执行：将待执行或被退回改为执行中
        if (InspectionConstant.PENDING.equals(examineDTO.getInspectionStatus())) {
            System.out.println(examineDTO.getId() + "将待执行改为执行中");
            RepairTask repairTask = repairTaskMapper.selectById(examineDTO.getId());
            repairTask.setStatus(InspectionConstant.IN_EXECUTION);
            repairTask.setBeginTime(new Date());
            repairTaskMapper.updateById(repairTask);
            // 修改对应检修计划状态
            if (ObjectUtil.isNotEmpty(repairPool)) {
                repairPool.setStatus(InspectionConstant.IN_EXECUTION);
                repairPoolMapper.updateById(repairPool);
            }
            //维保任务执行中延时提醒
            processRepairInExecutionToRemind(repairTask);
        }
        if (InspectionConstant.GIVE_BACK.equals(examineDTO.getInspectionStatus())) {
            System.out.println(examineDTO.getId() + "将被退回改为执行中");
//            // 修改对应检修计划状态
//            if (ObjectUtil.isNotEmpty(repairPool)) {
//                repairPool.setStatus(InspectionConstant.IN_EXECUTION);
//                repairPoolMapper.updateById(repairPool);
//            }
            return excuteInspectionTask(repairPool);
        }
        examineDTO.setInspectionStatus(InspectionConstant.IN_EXECUTION);
        return examineDTO.getId();
    }

    private void confirmInspectionTask(ExamineDTO examineDTO) {
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
            repairTask.setStatus(InspectionConstant.IN_EXECUTION);
            repairTask.setTaskConfirmationTime(new Date());
            repairTask.setBeginTime(new Date());
            repairTaskMapper.updateById(repairTask);

            // 修改对应检修计划状态
            RepairPool repairPool = repairPoolMapper.selectById(repairTask.getRepairPoolId());
            if (ObjectUtil.isNotEmpty(repairPool)) {
                repairPool.setStatus(InspectionConstant.IN_EXECUTION);
                repairTask.setTaskConfirmationTime(new Date());
                repairPoolMapper.updateById(repairPool);
            }

            //维保任务执行中延时提醒
            processRepairInExecutionToRemind(repairTask);

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
                    todoDTO.setTitle("检修任务-执行中" + DateUtil.today());
                    todoDTO.setMsgAbstract("检修任务执行中");
                    todoDTO.setPublishingContent("您有一条检修任务执行中");
                    createTodoTask(currentUserName, TodoBusinessTypeEnum.INSPECTION_EXECUTE.getType(), repairTask.getId(), "执行检修任务", "", "", todoDTO, repairTask, realNames, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }
    }

    private String excuteInspectionTask(RepairPool repairPool) {

        // 更新检修计划状态，待执行
        repairPool.setStatus(InspectionConstant.PENDING);
        repairPoolMapper.updateById(repairPool);

        System.out.println("正在执行excuteInspectionTask");
        RepairTask repairTask = new RepairTask();
        repairTask.setRepairPoolId(repairPool.getId());
        repairTask.setYear(DateUtil.year(repairPool.getStartTime()));
        repairTask.setType(repairPool.getType());
        repairTask.setIsOutsource(repairPool.getIsOutsource());
        repairTask.setSource(InspectionConstant.PICK_UP_MANUALLY);
        repairTask.setCode(repairPool.getCode());
        repairTask.setWeeks(repairPool.getWeeks());
        repairTask.setBeginTime(new Date());
        repairTask.setStartTime(new Date());
        repairTask.setTaskConfirmationTime(new Date());
        repairTask.setStatus(InspectionConstant.PENDING);
        repairTask.setIsConfirm(repairPool.getIsConfirm());
        repairTask.setIsReceipt(repairPool.getIsReceipt());
        repairTask.setWorkType(String.valueOf(repairPool.getWorkType()));
        // todo 计划令信息

        // 保存检修任务信息
        repairTaskMapper.insert(repairTask);
        System.out.println(repairTask.getId() + "已插入repair_task表");
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
                if (CollUtil.isNotEmpty(repairTaskUsers)) {
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

                createTodoTask(currentUserName, TodoBusinessTypeEnum.INSPECTION_EXECUTE.getType(), repairTask.getId(), "执行检修任务", "", "", todoDTO, repairTask, realNames, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ExamineDTO examineDTO = new ExamineDTO();
        examineDTO.setId(repairTask.getId());
        toBeImplement(examineDTO);
        return repairTask.getId();
    }

    /**
     * 校验领取资格
     *
     * @param repairPool
     */
    private void checkReceiveTask(RepairPool repairPool) {
        // 计划状态是待指派和已退回或待确认才能领取
        if (!InspectionConstant.TO_BE_ASSIGNED.equals(repairPool.getStatus())
                && !InspectionConstant.GIVE_BACK.equals(repairPool.getStatus())
                && !InspectionConstant.TO_BE_CONFIRMED.equals(repairPool.getStatus())) {
            throw new AiurtBootException("该检修任务已被指派或已被领取或已被确认");
        }

        // 当前登录人所属部门是在检修任务的指派部门范围内才可以领取
        List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(
                new LambdaQueryWrapper<RepairPoolOrgRel>()
                        .eq(RepairPoolOrgRel::getRepairPoolCode, repairPool.getCode())
                        .eq(RepairPoolOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
            List<String> orgList = repairPoolOrgRels.stream().map(RepairPoolOrgRel::getOrgCode).collect(Collectors.toList());
            System.out.println(manager.checkLogin().getOrgCode());
            if (!orgList.contains(manager.checkLogin().getOrgCode())) {
                throw new AiurtBootException("该检修任务不在您的领取范围之内哦");
            }
        }
        //信号三期根据配置不需要在指定时间内才能领取
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.INSPECTION_RECEIVE_ANYTIME);
        boolean value = "1".equals(paramModel.getValue());
        // 现在的时间大于任务的开始时间才可以进行领取
        if (repairPool.getStartTime() != null && DateUtil.compare(new Date(), repairPool.getStartTime()) < 0 && !value) {
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
        if (StrUtil.isNotBlank(monadDTO.getNote()) && StrUtil.isNotBlank(result.getDataCheck())) {
            Pattern pattern = Pattern.compile(result.getDataCheck());
            Matcher matcher = pattern.matcher(monadDTO.getNote());
            if (!matcher.find()) {
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
        result.setSpecialCharactersResult(monadDTO.getSpecialCharactersResult());

        // 更新检修结果
        repairTaskResultMapper.updateById(result);

        // 保存上传的附件
        repairTaskEnclosureMapper.delete(
                new LambdaQueryWrapper<RepairTaskEnclosure>()
                        .eq(RepairTaskEnclosure::getRepairTaskResultId, result.getId())
                        .eq(RepairTaskEnclosure::getDelFlag, CommonConstant.DEL_FLAG_0));
        if (StrUtil.isNotEmpty(monadDTO.getAppendix())) {
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
                || (StrUtil.isNotEmpty(monadDTO.getNote()) && !monadDTO.getNote().equals(result.getNote()))
                || (StrUtil.isNotEmpty(monadDTO.getSpecialCharactersResult()) && !monadDTO.getSpecialCharactersResult().equals(result.getSpecialCharacters()));
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
                rel.setDelFlag(0);
                repairTaskPeerRelMapper.insert(rel);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void writeSampling(String code, String samplingId) {
        RepairTaskDeviceRel repairTaskDeviceRel = repairTaskDeviceRelMapper.selectOne(
                new LambdaQueryWrapper<RepairTaskDeviceRel>()
                        .eq(RepairTaskDeviceRel::getCode, code)
                        .eq(RepairTaskDeviceRel::getDelFlag, CommonConstant.DEL_FLAG_0));

        if (ObjectUtil.isEmpty(repairTaskDeviceRel)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }

        // 校验什么情况下可以填写抽检人
        check(repairTaskDeviceRel.getId());

        repairTaskSamplingMapper.delete(
                new LambdaQueryWrapper<RepairTaskSampling>()
                        .eq(RepairTaskSampling::getRepairTaskDeviceCode, repairTaskDeviceRel.getCode()));

        // 更新抽检人
        if (StrUtil.isNotEmpty(samplingId)) {
            List<String> userIdS = StrUtil.split(samplingId, ',');
            userIdS.forEach(userId -> {
                RepairTaskSampling sampling = new RepairTaskSampling();
                sampling.setUserId(userId);
                sampling.setRealName(ObjectUtil.isNotEmpty(sysBaseApi.getUserById(userId)) ? sysBaseApi.getUserById(userId).getRealname() : "");
                sampling.setRepairTaskDeviceCode(repairTaskDeviceRel.getCode());
                sampling.setDelFlag(0);
                repairTaskSamplingMapper.insert(sampling);
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
    public void submitMonad(String id, String samplingSignUrl) {
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
            // if (ObjectUtil.isNotEmpty(repairTaskDeviceRel.getStartTime()) && ObjectUtil.isNotEmpty(repairTaskDeviceRel.getEndTime())) {
            //     repairTaskDeviceRel.setDuration(DateUtil.between(repairTaskDeviceRel.getStartTime(), repairTaskDeviceRel.getEndTime(), DateUnit.MINUTE));
            // }
        }
        // 2023-06 通信6期 检修工单的检修时长是提交时间-开始时间，且单位秒
        if (ObjectUtil.isNotNull(repairTaskDeviceRel.getStartTime())) {
            repairTaskDeviceRel.setDuration(DateUtil.between(repairTaskDeviceRel.getStartTime(), submitTime, DateUnit.SECOND));
        }else {
            repairTaskDeviceRel.setDuration(0L);
        }

        // 修改检修单的状态，已提交
        repairTaskDeviceRel.setSubmitTime(submitTime);
        repairTaskDeviceRel.setStaffId(manager.checkLogin().getId());
        repairTaskDeviceRel.setIsSubmit(InspectionConstant.SUBMITTED);
        repairTaskDeviceRel.setSamplingSignUrl(samplingSignUrl);
        repairTaskDeviceRelMapper.updateById(repairTaskDeviceRel);
        //是否需要自动提交工单，并写入签名
        //未驳回，检查是否是最后工单提交
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.INSPECTION_SUBMIT_SIGNATURE);
        boolean value = "1".equals(paramModel.getValue());
        if(value){
            List<RepairTaskDeviceRel> deviceRels = repairTaskDeviceRelMapper.selectList(new LambdaQueryWrapper<RepairTaskDeviceRel>().eq(RepairTaskDeviceRel::getRepairTaskId, repairTaskDeviceRel.getRepairTaskId()));
            List<RepairTaskDeviceRel> noSubmitDeviceList = deviceRels.stream().filter(d -> d.getIsSubmit() != 1).collect(Collectors.toList());
            RepairTask repairTask = repairTaskMapper.selectById(repairTaskDeviceRel.getRepairTaskId());
            if (!repairTask.getStatus().equals(InspectionConstant.REJECTED)) {
                if (CollUtil.isEmpty(noSubmitDeviceList)) {
                    repairTaskDeviceCheck(repairTask);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void repairTaskDeviceCheck(RepairTask repairTask) {
        if (ObjectUtil.isEmpty(repairTask)) {
            throw new AiurtBootException(InspectionConstant.ILLEGAL_OPERATION);
        }
//        // 是任务的检修人才可以提交
//        List<RepairTaskUser> repairTaskUserss = repairTaskUserMapper.selectList(
//                new LambdaQueryWrapper<RepairTaskUser>()
//                        .eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode())
//                        .eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0));
//        if (CollUtil.isEmpty(repairTaskUserss)) {
//            throw new AiurtBootException("该任务没有对应的检修人");
//        } else {
//            List<String> userList = repairTaskUserss.stream().map(RepairTaskUser::getUserId).collect(Collectors.toList());
//            if (!userList.contains(manager.checkLogin().getId())) {
//                throw new AiurtBootException("只有该任务的检修人才能提交");
//            }
//        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        LoginUser user = sysBaseApi.getUserById(sysUser.getId());
        if (InspectionConstant.IS_CONFIRM_1.equals(repairTask.getIsConfirm())) {
            //修改检修任务状态
            repairTask.setSubmitUserId(sysUser.getId());
            repairTask.setSumitUserName(sysUser.getRealname());
            repairTask.setSubmitTime(new Date());
            repairTask.setConfirmUrl(user.getSignatureUrl());
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
            repairTask.setConfirmUrl(user.getSignatureUrl());
            repairTask.setStatus(InspectionConstant.COMPLETED);
            // 修改对应检修计划状态
            RepairPool repairPool = repairPoolMapper.selectById(repairTask.getRepairPoolId());
            if (ObjectUtil.isNotEmpty(repairPool)) {
                repairPool.setStatus(InspectionConstant.COMPLETED);
                repairPoolMapper.updateById(repairPool);
            }
        }

        // 提交检修任务就更新检修任务的检修时长
        // 2023-06 通信6期 检修任务的检修时长是提交时间-开始时间，且单位秒
        if (ObjectUtil.isNotNull(repairTask.getBeginTime())) {
            repairTask.setDuration((int) DateUtil.between(repairTask.getBeginTime(), new Date(), DateUnit.SECOND));
        }else {
            repairTask.setDuration(0);
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
                    if (CollUtil.isNotEmpty(repairTaskUsers)) {
                        String[] userIds = repairTaskUsers.stream().map(RepairTaskUser::getUserId).toArray(String[]::new);
                        List<LoginUser> loginUsers = sysBaseApi.queryAllUserByIds(userIds);
                        if (CollUtil.isNotEmpty(loginUsers)) {
                            realNames = loginUsers.stream().map(LoginUser::getRealname).collect(Collectors.joining(","));
                        }
                    }
                    TodoDTO todoDTO = new TodoDTO();
                    todoDTO.setTemplateCode(CommonConstant.REPAIR_SERVICE_NOTICE);
                    todoDTO.setTitle("检修任务-审核" + DateUtil.today());
                    todoDTO.setMsgAbstract("检修任务完成");
                    todoDTO.setPublishingContent("检修任务已完成，请确认");
                    createTodoTask(currentUserName, TodoBusinessTypeEnum.INSPECTION_CONFIRM.getType(), repairTask.getId(), "检修任务审核", "", "", todoDTO, repairTask, realNames, null);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.INSPECTION_PEER_ALL_USER);
        boolean value = "1".equals(paramModel.getValue());
        // value为true，则选择同行人时可以选择全部班组的成员
        if(value){
            // 获取全部部门
            List<SysDepartModel> allDepartList = sysBaseApi.getAllSysDepart();
            String allOrgCodes = allDepartList.stream().map(SysDepartModel::getOrgCode).collect(Collectors.joining(","));
            // 查询全部部门的人员
            orgDto = manager.queryUserByOrdCode(allOrgCodes);
        }else{
            // 查询登录人部门下所有的人员
            orgDto = manager.queryUserByOrdCode(loginUser.getOrgCode());
        }

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
            repairTask.setTaskConfirmationTime(new Date());
            repairTaskMapper.updateById(repairTask);

            // 修改对应检修计划状态
            RepairPool repairPool = repairPoolMapper.selectById(repairTask.getRepairPoolId());
            if (ObjectUtil.isNotEmpty(repairPool)) {
                repairPool.setStatus(InspectionConstant.PENDING);
                repairTask.setTaskConfirmationTime(new Date());
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
    private void createTodoTask(String currentUserName, String businessType, String businessKey, String taskName, String url, String appUrl, TodoDTO todoDTO, RepairTask repairTask1, String realNames, String realName) {
        //构建消息模板
        HashMap<String, Object> map = new HashMap<>();
        if (CollUtil.isNotEmpty(todoDTO.getData())) {
            map.putAll(todoDTO.getData());
        }
        map.put("code", repairTask1.getCode());
        String typeName = sysBaseApi.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repairTask1.getType()));
        map.put("repairTaskName", typeName + repairTask1.getCode());
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
        map.put("repairStation", stringBuilder.toString());
        if (repairTask1.getEndTime() != null) {
            map.put("repairTaskTime", DateUtil.format(repairTask1.getStartTime(), "yyyy-MM-dd HH:mm") + "-" + DateUtil.format(repairTask1.getEndTime(), "yyyy-MM-dd HH:mm"));
        } else {
            map.put("repairTaskTime", DateUtil.format(repairTask1.getStartTime(), "yyyy-MM-dd HH:mm"));
        }
        if (StrUtil.isNotEmpty(realNames)) {
            map.put("repairName", realNames);
        } else {
            map.put("repairName", realName);
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

        //todo 该接口没用起来，如果要用，需要增加条件，因为通信十一期的改动之后检修单不一定有设备，只有设备分类了
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
        if (CollUtil.isEmpty(userIds)) {
            return map;
        }
        //获取当前班组用户的检修任务编号
        List<RepairTaskUser> taskUsers = repairTaskUserMapper.selectList(new LambdaQueryWrapper<RepairTaskUser>().in(RepairTaskUser::getUserId, userIds).eq(RepairTaskUser::getDelFlag, 0));
        if (CollUtil.isNotEmpty(taskUsers)) {
            //根据任务编号，获取检修任务信息
            List<RepairTaskDeviceRel> taskDeviceRelList = new ArrayList<>();
            List<RepairTaskDeviceRel> oldTaskDeviceRelList = new ArrayList<>();
            List<String> pairTaskCodes = taskUsers.stream().map(RepairTaskUser::getRepairTaskCode).distinct().collect(Collectors.toList());
            List<RepairTask> taskList = repairTaskMapper.selectList(new LambdaQueryWrapper<RepairTask>().in(RepairTask::getCode, pairTaskCodes));
            //2023-3-27 需求确认，工作日志只看本班组下的相应时间的任务，不看工单，因此在sql合并
            List<String> repairTaskIds = taskList.stream().map(RepairTask::getId).distinct().collect(Collectors.toList());
            for (String repairTaskId : repairTaskIds) {
                //获取当前用户作为领取/指派人，当天，已提交的工单
                List<RepairTaskDeviceRel> deviceRelList = repairTaskDeviceRelMapper.getTodaySubmit(startTime, endTime, repairTaskId, null);
                if (ObjectUtil.isNotEmpty(deviceRelList)) {
                    taskDeviceRelList.addAll(deviceRelList);
                }

            }

            //2023-3-27 需求确认，同行人也是本班组的人，去掉同行人，
//            //获取当前用户作为同行人参与的单号
//            List<RepairTaskPeerRel> relList = repairTaskPeerRelMapper.selectList(new LambdaQueryWrapper<RepairTaskPeerRel>().eq(RepairTaskPeerRel::getUserId, sysUser.getId()));
//            //获取单号信息
//            if (CollUtil.isNotEmpty(relList)) {
//                for (RepairTaskPeerRel taskPeerRel : relList) {
//                    List<RepairTaskDeviceRel> deviceRelList = repairTaskDeviceRelMapper.getTodaySubmit(startTime, endTime, null, taskPeerRel.getRepairTaskDeviceCode());
//                    if (ObjectUtil.isNotEmpty(deviceRelList)) {
//                        oldTaskDeviceRelList.addAll(deviceRelList);
//                    }
//                }
//            }
//
//            taskDeviceRelList.addAll(oldTaskDeviceRelList);


            if (CollUtil.isNotEmpty(taskDeviceRelList)) {
                //去重
                Set<RepairTaskDeviceRel> list = new HashSet<>(taskDeviceRelList);
                StringBuilder content = new StringBuilder();
                StringBuilder code = new StringBuilder();
                String string = null;

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
                            if (StrUtil.isEmpty(deviceCode)) {
                                JSONObject deviceByCode = iSysBaseAPI.getDeviceByCode(deviceCode);
                                stationName = iSysBaseAPI.getPosition(deviceByCode.getString("lineCode"));
                                lineName = iSysBaseAPI.getPosition(deviceByCode.getString("stationCode"));
                            }
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

                        // 工作日志的检修内容是否需要拼接标准表，从实施配置获取
                        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.WORKLOG_REPAIR_CONCAT_STANDARD);
                        if ("1".equals(sysParamModel.getValue()) && StrUtil.isNotBlank(deviceRel.getRepairTaskId())) {
                            List<RepairTaskStandardRel> repairTaskStandardRelList = repairTaskStandardRelMapper.selectList(new LambdaQueryWrapper<RepairTaskStandardRel>().eq(RepairTaskStandardRel::getRepairTaskId, deviceRel.getRepairTaskId()));
                            if (CollUtil.isNotEmpty(repairTaskStandardRelList)) {
                                List<String> collect = repairTaskStandardRelList.stream().map(RepairTaskStandardRel::getTitle).collect(Collectors.toList());
                                string = CollUtil.join(collect, ",");
                            }
                        }
                        StringBuilder append1 = lineStation.append(lineName).append("-").append(stationName).append(StrUtil.isNotBlank(string) ? string : " ").append("第").append(deviceRel.getWeeks()).append("周检修任务").append(" ").append(" 检修人:");
                        StringBuilder append2 = staffName.append(userById.getRealname());
                        //同检修任务下的，不同工单中的，同线路站点的不同检修人要合并起来
                        Map<StringBuilder, StringBuilder> mapList = hashMap.get(deviceRel.getTaskCode());
                        if (CollUtil.isNotEmpty(mapList)) {
                            for (Map.Entry<StringBuilder, StringBuilder> n : mapList.entrySet()) {
                                StringBuilder stringBuilder = n.getValue();
                                if (ObjectUtil.isNotEmpty(stringBuilder) && !stringBuilder.toString().contains(staffName)) {
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
        map.put("code", repairTaskMessageDTO.getCode());
        String typeName = sysBaseApi.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repairTaskMessageDTO.getType()));
        map.put("repairTaskName", typeName + repairTaskMessageDTO.getCode());
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
            map.put("repairStation", stringBuilder.toString());
        }
        if (repairTaskMessageDTO.getEndTime() != null) {
            map.put("repairTaskTime", DateUtil.format(repairTaskMessageDTO.getStartTime(), "yyyy-MM-dd HH:mm") + "-" + DateUtil.format(repairTaskMessageDTO.getEndTime(), "yyyy-MM-dd HH:mm"));
        } else {
            map.put("repairTaskTime", DateUtil.format(repairTaskMessageDTO.getStartTime(), "yyyy-MM-dd HH:mm"));
        }
        if (StrUtil.isNotEmpty(realNames)) {
            map.put("repairName", realNames);
        } else {
            map.put("repairName", realName);
        }
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, repairTaskMessageDTO.getId());
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, repairTaskMessageDTO.getBusType());
        messageDTO.setData(map);
        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.REPAIR_MESSAGE);
        messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
        iSysBaseAPI.sendTemplateMessage(messageDTO);
    }

    @Override
    public List<SystemInformationDTO> getSystemInformation() {
        //查询所有线路
        List<SystemInformationDTO> systemInformation = repairTaskMapper.getSystemInformation();
        systemInformation.forEach(e -> {
            e.setSystemTyp("通信");
            String lineCode = e.getLineCode();
            if (StrUtil.isNotBlank(lineCode)) {
                //根据线路Code查询站点Code
                List<String> stationCodeByLineCode = sysBaseApi.getStationCodeByLineCode(lineCode);
                if (CollectionUtil.isNotEmpty(stationCodeByLineCode)) {
                    //检修总数
                    Long maintenanceQuantity = repairTaskMapper.getMaintenanceQuantity(stationCodeByLineCode, null);
                    //巡检总数
                    Long inspection = repairTaskMapper.getInspection(stationCodeByLineCode, null);
                    e.setIplanSum(maintenanceQuantity + inspection);

                    //检修已完成总数
                    Long maintenanceQuantity1 = repairTaskMapper.getMaintenanceQuantity(stationCodeByLineCode, CommonConstant.REPAIR_POOL_ACCOMPLISH);
                    //巡检已完成总数
                    Long inspection1 = repairTaskMapper.getInspection(stationCodeByLineCode, CommonConstant.PATROL_TASK);
                    e.setIplanComplete(maintenanceQuantity1 + inspection1);


                    List<String> faultCodeList = repairTaskMapper.getFaultCodeList(stationCodeByLineCode, null);
                    if (CollectionUtil.isNotEmpty(faultCodeList)) {
                        //故障总数
                        e.setFaultSum((long) faultCodeList.size());
                    }
                    //故障完成总数
                    List<String> faultCodeList1 = repairTaskMapper.getFaultCodeList(stationCodeByLineCode, CommonConstant.FAULT_STATUS);
                    if (CollectionUtil.isNotEmpty(faultCodeList1)) {
                        e.setFaultComplete((long) faultCodeList1.size());
                    }
                } else {
                    e.setIplanSum(CommonConstant.ASSIGNMENT);
                    e.setIplanComplete(CommonConstant.ASSIGNMENT);
                    e.setFaultSum(CommonConstant.ASSIGNMENT);
                    e.setFaultComplete(CommonConstant.ASSIGNMENT);

                }
            }
        });
        return systemInformation;
    }

    @Override
    public void archRepairTask(RepairTask repairTask, String token, String archiveUserId, String refileFolderId, String realname, String sectId) {
        try {
            dealInfo(repairTask);
            SXSSFWorkbook archiveRepairTask = createArchiveRepairTask(repairTask, "/templates/repairTaskTemplate.xlsx");
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Date date = new Date();
            Date startTime = repairTask.getStartTime();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = repairTask.getSiteName() + "检修记录表" + sdf.format(startTime);
            String path = exportPath + fileName + ".xlsx";
            FileOutputStream fos = new FileOutputStream(path);
            archiveRepairTask.write(os);
            fos.write(os.toByteArray());
            os.close();
            fos.close();
            PdfUtil.excel2pdf(path);
            //传入档案系统
            //创建文件夹
            String foldername = fileName + "_" + date.getTime();
            String refileFolderIdNew = archiveUtils.createFolder(token, refileFolderId, foldername);
            //上传文件
            String fileType = "pdf";
            File file = new File(exportPath + fileName + "." + fileType);
            Long size = file.length();
            InputStream in = Files.newInputStream(file.toPath());
            JSONObject res = archiveUtils.upload(token, refileFolderIdNew, fileName + "." + fileType, size, fileType, in);
            String fileId = res.getString("fileId");
            Map<String, String> fileInfo = new HashMap<>();
            fileInfo.put("fileId", fileId);
            fileInfo.put("operateType", "upload");
            ArrayList<Object> fileList = new ArrayList<>();
            fileList.add(fileInfo);
            Map<String, Object> values = new HashMap<>();
            values.put("archiver", archiveUserId);
            values.put("username", realname);
            values.put("duration", repairTask.getSecertDuration());
            values.put("secert", repairTask.getSecert());
            values.put("secertDuration", repairTask.getSecertDuration());
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealInfo(RepairTask repairTask) {
        List<RepairTaskStationDTO> repairTaskStationDTOS = this.repairTaskStationList(repairTask.getId());
        //检查结果状态字典
        List<DictModel> resultDicts = sysBaseApi.queryDictItemsByCode(DictConstant.OVERHAUL_RESULT);
        Map<Integer, String> dictMap = CollUtil.isNotEmpty(resultDicts)
                ? resultDicts.stream().collect(Collectors.toMap(d -> Integer.valueOf(d.getValue()), DictModel::getText))
                : new HashMap<>(0);
        //所有检查项
        List<RepairTaskResult> allResultList = new ArrayList<>();
        //备件更换信息
        List<SpareResult> spareChange = new ArrayList<>();
        //处理结果
        StringBuilder stringBuilder = new StringBuilder();
        //附件
        List<String> enclosureUrl = new ArrayList<>();
        //所有状态
        Set<Integer> statuss = new HashSet<>();
        int i = 0;
        for (RepairTaskStationDTO stationDTO : repairTaskStationDTOS) {
            //获取站点下所有工单
            List<RepairTaskDeviceRel> repairTaskDeviceRels = repairTaskDeviceRelMapper.selectList(new LambdaQueryWrapper<RepairTaskDeviceRel>()
                    .eq(RepairTaskDeviceRel::getRepairTaskId, repairTask.getId())
                    .eq(RepairTaskDeviceRel::getDelFlag, CommonConstant.DEL_FLAG_0));
            if (CollUtil.isNotEmpty(repairTaskDeviceRels)) {
                repairTaskDeviceRels = repairTaskDeviceRels.stream().filter(d -> {
                    //无设备
                    if (StrUtil.equals(stationDTO.getStationCode(), d.getStationCode())) {
                        return true;
                    }
                    //有设备
                    String deviceCode = d.getDeviceCode();
                    if(StrUtil.isNotBlank(deviceCode)){
                        JSONObject deviceByCode = sysBaseApi.getDeviceByCode(deviceCode);
                        if (ObjectUtil.isNotEmpty(deviceByCode)) {
                            String station_code = deviceByCode.getString("stationCode");
                            return stationDTO.getStationCode().equals(station_code);
                        }
                    }
                    return false;
                }).collect(Collectors.toList());
            }
            //遍历每个工单
            for (RepairTaskDeviceRel d : repairTaskDeviceRels) {
                //获取备件更换信息
                String faultCode = d.getFaultCode();
                if (StrUtil.isNotBlank(faultCode)) {
                    //获取备件更换信息
                    List<SpareResult> change = sysBaseApi.getSpareChange(faultCode);
                    spareChange.addAll(change);

                    //处理结果
                    String faultRepairReuslt = sysBaseApi.getFaultRepairReuslt(faultCode);
                    if (StrUtil.isNotBlank(faultRepairReuslt)) {
                        stringBuilder.append(Convert.toStr(++i)).append(".").append("故障编号：").append(faultCode).append(",").append(faultRepairReuslt).append(",");
                    } else {
                        stringBuilder.append(Convert.toStr(++i)).append(".").append(faultCode).append(":该故障没有完成维修").append(",");
                    }
                }

                //获取检查项
                List<RepairTaskResult> repairTaskResultList = repairTaskMapper.selectSingle(d.getId(), null);
                repairTaskResultList.forEach(r -> {
                    if ("0".equals(r.getPid())) {
                        r.setName(r.getName() != null ? r.getName() : "");
                    }
                    r.setStatusName(dictMap.get(r.getStatus()));
                    if (ObjectUtil.isNotEmpty(r.getStatus())) {
                        statuss.add(r.getStatus());
                    }
                });
                List<RepairTaskResult> treeList = treeFirst(repairTaskResultList);
                allResultList.addAll(treeList);

                //获取工单全部附件
                if (CollUtil.isNotEmpty(repairTaskResultList)) {
                    List<String> rtrIds = repairTaskResultList.stream().map(RepairTaskResult::getId).collect(Collectors.toList());
                    if (CollUtil.isNotEmpty(rtrIds)) {
                        List<RepairTaskEnclosure> repairTaskEnclosures = repairTaskEnclosureMapper.selectList(new LambdaQueryWrapper<RepairTaskEnclosure>()
                                .eq(RepairTaskEnclosure::getDelFlag, CommonConstant.DEL_FLAG_0)
                                .in(RepairTaskEnclosure::getRepairTaskResultId, rtrIds));
                        if (CollUtil.isNotEmpty(repairTaskEnclosures)) {
                            List<String> urls = repairTaskEnclosures.stream().map(RepairTaskEnclosure::getUrl).collect(Collectors.toList());
                            enclosureUrl.addAll(urls);
                        }
                    }
                }
            };
        }
        //设置检查项
        repairTask.setRepairTaskResultList(allResultList);
        //设置备件更换信息
        repairTask.setSpareChange(spareChange);
        //设置附件
        repairTask.setEnclosureUrl(enclosureUrl);
        //设置检查记录
        if (statuss.contains(2)) {
            repairTask.setRepairRecord(dictMap.get(2));
        } else if (statuss.contains(1)) {
            repairTask.setRepairRecord(dictMap.get(1));
        }
        //设置处理结果
        if (stringBuilder.length() > 0) {
            // 截取字符
            StringBuilder sb = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            repairTask.setRepairResult(sb.toString());
        }
    }

    private SXSSFWorkbook createArchiveRepairTask(RepairTask repairTask, String path) {
        InputStream in;
        XSSFWorkbook xssfWb = null;
        try {
            org.springframework.core.io.Resource resource = new ClassPathResource(path);
            in = resource.getInputStream();
            xssfWb = new XSSFWorkbook(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        SXSSFWorkbook workbook = new SXSSFWorkbook(xssfWb);
        Sheet sheet = workbook.getXSSFWorkbook().getSheetAt(0);
        PrintSetup printSetup = sheet.getPrintSetup();
        //横向展示
        //printSetup.setLandscape(true);
        //A4
        printSetup.setPaperSize((short) 9);
        //画图的顶级管理器，一个sheet只能获取一个（一定要注意这点）
        Drawing drawingPatriarch = sheet.createDrawingPatriarch();
        CreationHelper helper = sheet.getWorkbook().getCreationHelper();
        Row row = sheet.getRow(0);
        Cell cell = row.getCell(0);
        String head = repairTask.getLineName()+repairTask.getSiteName() + "检修记录表";
        cell.setCellValue(head);
        Row rowOne = sheet.getRow(1);
        Cell c11 = rowOne.getCell(1);
        c11.setCellValue(repairTask.getOrganizational());
        //检修日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String start ="";
        String end ="";
        if (repairTask.getStartTime()!=null){
            start = sdf.format(repairTask.getStartTime());
        }
        if (repairTask.getEndTime()!=null){
            end = sdf.format(repairTask.getEndTime());
        }
        Cell c13 = rowOne.getCell(3);
        c13.setCellValue(StrUtil.trim(start+" "+end));
        Row rowTwo = sheet.getRow(2);
        Cell c21 = rowTwo.getCell(1);
        c21.setCellValue(repairTask.getLineName()+repairTask.getSiteName());
        Cell c23 = rowTwo.getCell(3);
        c23.setCellValue(sdf.format(repairTask.getStartTime()));
        Row rowThree = sheet.getRow(3);
        Cell c31 = rowThree.getCell(1);
        c31.setCellValue(repairTask.getOverhaulName());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Cell c33 = rowThree.getCell(3);
        if (ObjectUtil.isNotEmpty(repairTask.getBeginTime())) {
            c33.setCellValue(format.format(repairTask.getBeginTime()));
        }
        Row rowFour = sheet.getRow(4);
        //内容
        StringBuilder content = new StringBuilder();
        List<RepairTaskResult> list = repairTask.getRepairTaskResultList();
        for (RepairTaskResult repairTaskResult : list) {
            content.append(repairTaskResult.getName());
            List<RepairTaskResult> children = repairTaskResult.getChildren();
            if (CollUtil.isNotEmpty(children)) {
                content.append("：");
                for (int j = 0, index = 0; j < children.size(); j++) {
                    ++index;
                    content.append(index + "." + children.get(j).getName());
                }
            }
            content.append("\r\n");
        }
        if (ObjectUtil.isNotEmpty(content)) {
            float height = ArchExecelUtil.getExcelCellAutoHeight(content.toString(),16f);
            Cell c41 = rowFour.getCell(1);
            CellStyle c41Style = c41.getCellStyle();
            c41Style.setWrapText(true);
            c41.setCellValue(content.toString());
            rowFour.setHeightInPoints(height/2);
        }
        //检修记录
        Row fowFive = sheet.getRow(5);
        Cell c51 = fowFive.getCell(1);
        c51.setCellValue(repairTask.getRepairRecord());
        //处理结果
        Row rowSix = sheet.getRow(6);
        Cell c61 = rowSix.getCell(1);
        //c61.setCellValue(repairTask.getProcessContent());
        c61.setCellValue(repairTask.getRepairRecord());
        //更换备件
        Row rowSeven = sheet.getRow(7);
        Cell c71 = rowSeven.getCell(1);
        CellStyle c71Style = c71.getCellStyle();
        c71Style.setWrapText(true);
        List<SpareResult> spareResults = repairTask.getSpareChange();
        if (spareResults != null){
            int index2 = 0;
            StringBuilder spare = new StringBuilder();
            for (SpareResult spareResult : spareResults) {
                index2++;
                spare.append(index2).append(".").append("组件名称(旧)：").append(spareResult.getOldSparePartName()).append(" ")
                        .append("数量：").append(spareResult.getOldSparePartNum()).append("  ")
                        .append("组件名称(新)：").append(spareResult.getNewSparePartName())
                        .append("数量：").append(spareResult.getNewSparePartNum());
            }
            float height2 = ArchExecelUtil.getExcelCellAutoHeight(spare.toString(),16f);
            c71.setCellValue(spare.toString());
            rowSeven.setHeightInPoints(height2);
        }
        //附件信息，只展示图片
        Row rowEight = sheet.getRow(8);
        List<String> enclosureUrlList = repairTask.getEnclosureUrl();
        if (CollUtil.isNotEmpty(enclosureUrlList)) {
            List<BufferedImage> bufferedImageList = new ArrayList<>();
            //遍历附件，获取其中的图片
            for (String url : enclosureUrlList) {
                BufferedImage bufferedImage = null;
                try (InputStream inputStreamByUrl = this.getInputStreamByUrl(url)) {
                    //读取图片，非图片bufferedImage为null
                    if (ObjectUtil.isNotNull(inputStreamByUrl)) {
                        bufferedImage = ImageIO.read(inputStreamByUrl);
                    }
                    if (ObjectUtil.isNotNull(bufferedImage)) {
                        bufferedImageList.add(bufferedImage);
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
            //插入图片到单元格
            if (CollUtil.isNotEmpty(bufferedImageList)) {
                //设置边距
                int widthCol1 = Units.columnWidthToEMU(sheet.getColumnWidth(1));
                int heightRow8 = Units.toEMU(rowEight.getHeightInPoints());
                int wMar = 3 * Units.EMU_PER_POINT;
                int hMar = 2 * Units.EMU_PER_POINT;
                int size = bufferedImageList.size();
                //每个图片宽度（大致平均值）
                int ave = (widthCol1 - (size + 1) * wMar) / size;
                for (int i = 0; i < size; i++) {
                    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
                        ImageIO.write(bufferedImageList.get(i), "jpg", byteArrayOutputStream);
                        byte[] bytes = byteArrayOutputStream.toByteArray();
                        int pictureIdx = sheet.getWorkbook().addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
                        ClientAnchor anchor = helper.createClientAnchor();
                        anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                        anchor.setCol1(1);
                        anchor.setCol2(2);
                        anchor.setRow1(8);
                        anchor.setRow2(8);
                        anchor.setDx1((i + 1) * wMar + i * ave);
                        anchor.setDy1(hMar);
                        anchor.setDx2((i + 1) * (wMar + ave));
                        anchor.setDy2(heightRow8 - hMar);
                        drawingPatriarch.createPicture(anchor, pictureIdx);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
        }

        //
        Row rowNine = sheet.getRow(9);
        Cell c91 = rowNine.getCell(1);
        c91.setCellValue(repairTask.getWorkTypeName());
        Cell c93 = rowNine.getCell(3);
        c93.setCellValue(repairTask.getPlanOrderCode());

        //计划令 TODO
        Row rowTen = sheet.getRow(10);
        Cell c101 = rowTen.getCell(1);
        String planOrderCodeUrl = repairTask.getPlanOrderCodeUrl();
        if (StrUtil.isNotBlank(planOrderCodeUrl)) {
            try (InputStream inputStreamByUrl = this.getInputStreamByUrl(planOrderCodeUrl)) {
                if (ObjectUtil.isNotEmpty(inputStreamByUrl)) {
                    byte[] bytes = IoUtils.toByteArray(inputStreamByUrl);
                    int pictureIdx = sheet.getWorkbook().addPicture(bytes, Workbook.PICTURE_TYPE_JPEG);
                    ClientAnchor anchor = helper.createClientAnchor();
                    anchor.setAnchorType(ClientAnchor.AnchorType.MOVE_AND_RESIZE);
                    anchor.setCol1(1);
                    anchor.setCol2(4);
                    anchor.setRow1(10);
                    anchor.setRow2(11);
                    anchor.setDx1(Units.EMU_PER_POINT);
                    anchor.setDy1(Units.EMU_PER_POINT);
                    drawingPatriarch.createPicture(anchor, pictureIdx);
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        //
        Row rowELe = sheet.getRow(11);
        Cell c111= rowELe.getCell(1);
        c111.setCellValue(repairTask.getSumitUserName());
        Cell c113 = rowELe.getCell(3);
        c113.setCellValue(DateUtil.format(repairTask.getSubmitTime(), "yyyy-MM-dd HH:mm:ss"));

        //
        Row rowTwe = sheet.getRow(12);
        Cell c121= rowTwe.getCell(1);
        c121.setCellValue(repairTask.getConfirmUserName());
        Cell c123 = rowTwe.getCell(3);
        if (repairTask.getConfirmTime()!=null) {
            c123.setCellValue(format.format(repairTask.getConfirmTime()));
        }
        //
        Row rowThr = sheet.getRow(13);
        Cell c131 = rowThr.getCell(1);
        c131.setCellValue(repairTask.getReceiptUserName());
        Cell c133 = rowThr.getCell(3);
        if (repairTask.getReceiptTime()!=null) {
            c133.setCellValue(format.format(repairTask.getReceiptTime()));
        }


        return workbook;
    }

    /**
     * 根据图片url获取InputSream
     * @param url
     * @return
     */
    private InputStream getInputStreamByUrl(String url) {
        InputStream inputStream = null;
        SysAttachment sysAttachment = null;
        try {
            if (url.contains("?")) {
                int index = url.indexOf("?");
                String attachId = url.substring(0, index);
                sysAttachment = iSysBaseAPI.getFilePath(attachId);

            }
            if (ObjectUtil.isNotEmpty(sysAttachment)) {
                if (StrUtil.equalsIgnoreCase("minio",sysAttachment.getType())) {
                    inputStream = MinioUtil.getMinioFile(bucketName, sysAttachment.getFilePath());
                } else {
                    String filePath = uploadpath + File.separator + sysAttachment.getFilePath();
                    File file = new File(filePath);
                    if (file.exists()) {
                        inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(filePath)));
                    }
                }
            } else {
                String filePath = uploadpath + File.separator + url;
                File file = new File(filePath);
                if (file.exists()) {
                    inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(filePath)));
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return inputStream;
    }

    @Override
    public void exportPdf(HttpServletRequest request, RepairTask repairTask, HttpServletResponse response) throws IOException {
        String path = "templates/repairTaskTemplate.xlsx";
        TemplateExportParams params = new TemplateExportParams(path, true);
        Map<String, Object> map = new HashMap<String, Object>();

//        检修任务单号
        map.put("code", repairTask.getCode());
//        任务来源
        map.put("sourceName", repairTask.getSourceName());
//        适用专业
        map.put("majorName", repairTask.getMajorName());
//        适用系统
        map.put("systemName", repairTask.getSystemName());
//        适用站点
        map.put("siteName", repairTask.getSiteName());
//        组织机构
        map.put("organizational", repairTask.getOrganizational());
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
//        抽检人
        map.put("samplingName", repairTask.getSamplingName());
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
        map.put("sumitUserName", repairTask.getSumitUserName());

        String fileName = repairTask.getSiteName() + "检修记录表";
        String exportRepairTaskPath = exportPath + "/" + fileName + ".xlsx";
        Workbook workbook = ExcelExportUtil.exportExcel(params, map);
        FileOutputStream fos = new FileOutputStream(exportRepairTaskPath);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        workbook.write(bos);
        bos.close();
        fos.close();
        workbook.close();
        PdfUtil.excel2pdf(exportRepairTaskPath);
    }

    @Override
    public List<SignUserDTO> appGetSignUserList(String taskId) {
        // 1、查看任务是否存在
        RepairTask repairTask = repairTaskMapper.selectById(taskId);
        if (ObjectUtil.isEmpty(repairTask)) {
            throw new AiurtBootException("任务不存在");
        }
        // 2、获取检修人员列表
        List<RepairTaskUser> repairTaskUserList = repairTaskUserMapper.selectList(
                new LambdaQueryWrapper<RepairTaskUser>()
                        .select(RepairTaskUser::getUserId, RepairTaskUser::getName)
                        .eq(RepairTaskUser::getRepairTaskCode, repairTask.getCode())
                        .eq(RepairTaskUser::getDelFlag, CommonConstant.DEL_FLAG_0)
        );
        if (CollUtil.isEmpty(repairTaskUserList)) {
            throw new AiurtBootException("该任务没有对应的检修人");
        }
        // 3、将检修人转化成SignUserDTO对象
        List<SignUserDTO> taskSignUserDTOList = repairTaskUserList.stream()
                .map(taskUser -> new SignUserDTO(taskUser.getUserId(), taskUser.getName(), 0, null))
                .collect(Collectors.toList());

        // 4、查询同行人
        // 4.1 查询检修任务对应的所有检修工单，获取工单code
        Set<String> repairTaskDeviceRelCodeList = repairTaskDeviceRelMapper.selectList(
                new LambdaQueryWrapper<RepairTaskDeviceRel>()
                        .select(RepairTaskDeviceRel::getCode)
                        .eq(RepairTaskDeviceRel::getRepairTaskId, taskId)
                        .eq(RepairTaskDeviceRel::getDelFlag, CommonConstant.DEL_FLAG_0)
        ).stream().map(RepairTaskDeviceRel::getCode).collect(Collectors.toSet());

        // 4.2 根据检修工单code，获取同行人，并转化为SignUserDTO对象
        List<SignUserDTO> peerSignUserDTOList = repairTaskPeerRelMapper.selectList(
                new LambdaQueryWrapper<RepairTaskPeerRel>()
                        .select(RepairTaskPeerRel::getUserId, RepairTaskPeerRel::getRealName)
                        .in(RepairTaskPeerRel::getRepairTaskDeviceCode, repairTaskDeviceRelCodeList)
                        .eq(RepairTaskPeerRel::getDelFlag, CommonConstant.DEL_FLAG_0)
                        .orderByAsc(RepairTaskPeerRel::getCreateTime)
        ).stream().map(peerUser -> new SignUserDTO(peerUser.getUserId(), peerUser.getRealName(), 1, null))
                .collect(Collectors.toList());

        // 同行人过滤，当不同的工单是可以选择同一个同行人的。检修人不用过滤，因为同行人选不到检修人，检修人多选时也不能重复
        List<SignUserDTO> filterPeerSignUserDTOList = new ArrayList<>(
                peerSignUserDTOList.stream()
                        .collect(Collectors.toMap(SignUserDTO::getUserId, signUser -> signUser, (oldValue, newValue) -> oldValue))
                        .values()
        );

        // 返回检修人和同行人的列表
        taskSignUserDTOList.addAll(filterPeerSignUserDTOList);
        return taskSignUserDTOList;
    }

    @Override
    public String printTask(String id, String code, String deviceId) {
        Page<RepairTask> pageList = new Page<>(1, 10);
        RepairTask patrolTask = new RepairTask();
        patrolTask.setSelections(Collections.singletonList(id));
        RepairTask repairTask = selectables(pageList,patrolTask).getRecords().get(0);
        InspectionCode inspectionCode = inspectionCodeMapper.selectOne(new LambdaQueryWrapper<InspectionCode>()
                .eq(InspectionCode::getCode,code).eq(InspectionCode::getDelFlag,CommonConstant.DEL_FLAG_0));
        String excelName = null;
        DictModel excelDictModel = new DictModel();
        if (StrUtil.isNotEmpty(inspectionCode.getPrintTemplate())){
            excelDictModel = sysBaseApi.dictById(inspectionCode.getPrintTemplate());
            excelName = excelDictModel.getValue();
        }else {
            excelName = "equipment.xlsx";
        }
        // 模板文件路径
        String templateFileName = "patrol" +"/" + "template" + "/" + excelName;
        log.info("templateFileName:"+templateFileName);

        // 填充数据后的文件路径
        String fileName = inspectionCode.getTitle() + System.currentTimeMillis() + ".xlsx";
        fileName = fileName.replaceAll("[/*?:\"<>|]", "-");
        String relatiePath = "/" + "patrol" + "/" + "print" + "/" + fileName;
        String filePath = path +"/" +  fileName;
        //填充头部Map
        Map<String, Object> headerMap = getHeaderData(repairTask);
        //获取显示图片位置
        List<String> imageList = null;
        if (ObjectUtil.isNotEmpty(excelDictModel.getDescription())&&excelDictModel.getDescription().contains(",")){
            imageList = Arrays.asList(excelDictModel.getDescription().split(","));
        }
        //文件打印签名
        Map<String, Object> imageMap = getSignImageMap(repairTask,imageList);

        InputStream minioFile2 = MinioUtil.getMinioFile("platform", templateFileName);
        ExcelWriter excelWriter = null;
        try {
            excelWriter = EasyExcel.write(filePath).withTemplate(minioFile2).build();
            int[] mergeColumnIndex = {0,1,2};
            CustomCellMergeHandler customCellMergeStrategy = new CustomCellMergeHandler(3,mergeColumnIndex);
            WriteSheet writeSheet = EasyExcel.writerSheet().registerWriteHandler(customCellMergeStrategy).build();
            //FillConfig fillConfig = FillConfig.builder().forceNewRow(Boolean.FALSE).build();
            FillConfig fillConfig = FillConfig.builder().direction(WriteDirectionEnum.HORIZONTAL).build();
            //填充列表数据
            excelWriter = fillData(id, excelName, excelWriter, writeSheet,headerMap,filePath,deviceId,fillConfig);
            //填充表头
            excelWriter.fill(headerMap, writeSheet);
            //填充图片
            excelWriter.fill(imageMap, writeSheet);
            excelWriter.finish();
            //对已填充数据的文件进行后处理
            processFilledFile(filePath);

            MinioUtil.upload(new FileInputStream(filePath),relatiePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        SysAttachment sysAttachment = new SysAttachment();
        sysAttachment.setFileName(fileName);
        sysAttachment.setFilePath(relatiePath);
        sysAttachment.setType("minio");
        sysBaseApi.saveSysAttachment(sysAttachment);

        return sysAttachment.getId()+"?fileName="+sysAttachment.getFileName();
    }

    private ExcelWriter fillData(String id, String excelName, ExcelWriter excelWriter, WriteSheet writeSheet, Map<String, Object> headerMap, String filePath, String deviceId, FillConfig fillConfig) {
        List<PrintDataDTO> patrolData = new ArrayList<>();
        if ("equipment.xlsx".equals(excelName)||"platformDoors.xlsx".equals(excelName)){
            patrolData = getEquipment(headerMap,deviceId);
            //填充列表数据
            excelWriter.fill(new FillWrapper("list",patrolData),fillConfig,writeSheet);
        }else if ("threeViolations.xlsx".equals(excelName)){
            patrolData = getThreeViolations(deviceId);
            excelWriter.fill(new FillWrapper("list",patrolData),writeSheet);
        }
        return excelWriter;
    }

    private List<PrintDataDTO> getThreeViolations(String deviceId) {
        List<PrintDataDTO> getThreeViolations = new ArrayList<>();
        List<RepairTaskResult> resultList = repairTaskMapper.selectSingle(deviceId, null);
        //过滤为部署检查项的
        List<RepairTaskResult> checks = resultList.stream().filter(c -> c.getType()==0).collect(Collectors.toList());
        for (int i = 0; i < checks.size(); i++) {
            RepairTaskResult patrolCheckResultDTO = checks.get(i);
            List<RepairTaskResult> list = resultList.stream().filter(c-> c.getPid().equals(patrolCheckResultDTO.getId())).collect(Collectors.toList());
            list.forEach(l->{
                PrintDataDTO printDTO = new PrintDataDTO();
                printDTO.setRemark(l.getUnNote());
                if (l.getStatus()==1){
                    printDTO.setResult("无");
                }else {
                    printDTO.setResult("异常");
                }
                getThreeViolations.add(printDTO);
            });
            if (i != checks.size() - 1) {
                // 不是最后一个元素，执行特殊操作
                PrintDataDTO printDTO = new PrintDataDTO();
                getThreeViolations.add(printDTO);
            }
        }
            return getThreeViolations;
    }

    private List<PrintDataDTO> getEquipment(Map<String, Object> headerMap, String deviceId) {
        List<PrintDataDTO> equipmentList= new ArrayList<>();
        List<RepairTaskResult> resultList = repairTaskMapper.selectSingle(deviceId, null);
        //过滤为部署检查项的
        List<RepairTaskResult> checks = resultList.stream().filter(c -> c.getPid().equals("0")).collect(Collectors.toList());
        AtomicInteger i = new AtomicInteger(1);
        StringBuilder text  = new StringBuilder();
        checks.forEach(r->{
            if (r.getType()==1){
                PrintDataDTO printDataDTO = new PrintDataDTO();
                if (1==r.getStatus()){
                    printDataDTO.setData("√");
                    equipmentList.add(printDataDTO);
                }else {
                    printDataDTO.setData("✕");
                    equipmentList.add(printDataDTO);
                    if (StrUtil.isNotEmpty(r.getUnNote())){
                    text.append(i).append(".").append(r.getName()).append(":").append(r.getUnNote()).append("\n");
                    i.getAndIncrement();
                    }
                }
            }else {
            List<RepairTaskResult> checkPid = resultList.stream().filter(c -> c.getPid().equals(r.getId())).collect(Collectors.toList());
            checkPid.forEach(check->{
                PrintDataDTO printDataDTO = new PrintDataDTO();
                if (1==check.getStatus()){
                    printDataDTO.setData("√");
                    equipmentList.add(printDataDTO);
                }else {
                    printDataDTO.setData("✕");
                    equipmentList.add(printDataDTO);
                    if (StrUtil.isNotEmpty(check.getUnNote())){
                    text.append(i).append(".").append(r.getName()).append("-").append(check.getName()).append(":").append(check.getUnNote()).append("\n");
                    i.getAndIncrement();
                    }
                }
            });}
        });
        headerMap.put("unNote",text.toString());
        return equipmentList;
    }

    /**
     * 定制模板的文件后处理
     * @param filePath
     * @throws IOException
     */
    private static void processFilledFile(String filePath) throws IOException {
        try (InputStream inputStream = new FileInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet  = workbook.getSheetAt(0);
            //打印设置
            FilePrintUtils.printSet(sheet);

            // 保存修改后的Excel文件
            OutputStream outputStream = null;
            try{
                outputStream = new FileOutputStream(filePath);
                workbook.write(outputStream);
            }finally {
                if (null!=inputStream){
                    inputStream.close();
                }
                if (null!=outputStream){
                    outputStream.close();
                }
                if (null!=workbook){
                    workbook.close();
                }
            }
        }
    }
    /**
     * 获取头部数据
     * @param repairTask
     * @return
     */
    @NotNull
    private Map<String, Object> getHeaderData(RepairTask repairTask) {
        Map<String, Object> map = MapUtils.newHashMap();
        map.put("siteName", repairTask.getSiteName());
        map.put("startTime", DateUtil.format(repairTask.getStartTime(),"yyyy-MM-dd HH:mm"));
        map.put("startOverhaulTime", DateUtil.format(repairTask.getStartOverhaulTime(),"yyyy-MM-dd HH:mm"));
        map.put("peerName", repairTask.getPeerName());
        map.put("overhaulName", repairTask.getOverhaulName());
        map.put("orgName",sysBaseApi.getUserById(repairTask.getOverhaulId()).getOrgName());
        return map;
    }
    /**
     * 获取签字图片Map
     *
     * @param repairTask
     * @param columnRangeList
     * @return
     */
    @NotNull
    private Map<String, Object> getSignImageMap(RepairTask repairTask, List<String> columnRangeList) {
        Map<String, Object> imageMap = MapUtils.newHashMap();
        if(StrUtil.isNotEmpty(repairTask.getConfirmUrl())&& repairTask.getConfirmUrl().indexOf("?")!=-1){
            int index =  repairTask.getConfirmUrl().indexOf("?");
            SysAttachment sysAttachment = sysBaseApi.getFilePath(repairTask.getConfirmUrl().substring(0, index));
            InputStream inputStream = MinioUtil.getMinioFile("platform",sysAttachment.getFilePath());
            if(ObjectUtil.isEmpty(inputStream)){
                imageMap.put("signImage",null);
            } else {
                try {
                    byte[] convert = FilePrintUtils.convert(inputStream);
                    WriteCellData writeImageData = FilePrintUtils.writeCellImageData(convert,columnRangeList);
                    imageMap.put("signImage",writeImageData);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }else{
            imageMap.put("signImage",null);
        }
        return imageMap;
    }
    @Override
    public List<PrintRepairTaskDTO> printRepairTaskById(String ids,String overhaulCode) {
        List<String> idList = StrUtil.splitTrim(ids, ",");
        //创建结果集
        List<PrintRepairTaskDTO> list = new ArrayList<>();

        for (String id : idList) {
            PrintRepairTaskDTO printRepairTaskDTO = new PrintRepairTaskDTO();
            RepairTask repairTask = new RepairTask();
            repairTask.setId(id);
            Page<RepairTask> pageList = new Page<>(1, 1);
            Page<RepairTask> repairTaskPage = this.selectables(pageList, repairTask);
            List<RepairTask> records = repairTaskPage.getRecords();
            if (CollUtil.isEmpty(records)) {
                //获取计划检查项
                getRepairPoolResult(id, printRepairTaskDTO,list);
                break;
            }else {
                //获取任务检查项
                RepairTask one = records.get(0);
                getRepairTaskResult(id, printRepairTaskDTO, one,list,overhaulCode);
            }
        }
        return list;

    }

    /**
     * 从查询条件中移除空格。
     *
     * @param condition 查询条件对象
     */
    private void removeSpacesFromQueryCondition(RepairTask condition) {
        if (condition.getCode() != null) {
            condition.setCode(condition.getCode().replaceAll(" ", ""));
        }
    }

    /**
     * 获取检修任务状态映射表。
     *
     * @return 检修任务状态映射表（Key: 状态值，Value: 状态文本）
     */
    private Map<String, String> getTaskStateMap() {
        return Optional.ofNullable(sysBaseApi.queryEnableDictItemsByCode(DictConstant.INSPECTION_TASK_STATE))
                .orElse(Collections.emptyList())
                .stream()
                .filter(dictModel -> dictModel.getValue() != null && dictModel.getText() != null)
                .collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (v1, v2) -> v1));
    }

    /**
     * 获取任务类型映射表。
     *
     * @return 任务类型映射表（Key: 类型值，Value: 类型文本）
     */
    private Map<String, String> getTaskTypeMap() {
        List<DictModel> dictItems = sysBaseApi.queryEnableDictItemsByCode(DictConstant.INSPECTION_CYCLE_TYPE);
        if (dictItems == null || dictItems.isEmpty()) {
            return Collections.emptyMap();
        }

        return dictItems.stream()
                .filter(dictModel -> dictModel.getValue() != null && dictModel.getText() != null)
                .collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (v1, v2) -> v1));
    }

    /**
     * 获取是否需要审核映射表。
     *
     * @return 是否需要审核映射表（Key: 审核值，Value: 审核文本）
     */
    private Map<String, String> getIsConfirmMap() {
        List<DictModel> dictItems = sysBaseApi.queryEnableDictItemsByCode(DictConstant.INSPECTION_IS_CONFIRM);
        if (dictItems == null || dictItems.isEmpty()) {
            return Collections.emptyMap();
        }

        return dictItems.stream()
                .filter(dictModel -> dictModel.getValue() != null && dictModel.getText() != null)
                .collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (v1, v2) -> v1));
    }

    /**
     * 获取任务来源映射表。
     *
     * @return 任务来源映射表（Key: 来源值，Value: 来源文本）
     */
    private Map<String, String> getSourceMap() {
        List<DictModel> dictItems = sysBaseApi.queryEnableDictItemsByCode(DictConstant.PATROL_TASK_ACCESS);
        if (dictItems == null || dictItems.isEmpty()) {
            return Collections.emptyMap();
        }

        return dictItems.stream()
                .filter(dictModel -> dictModel.getValue() != null && dictModel.getText() != null)
                .collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (v1, v2) -> v1));
    }


    /**
     * 获取作业类型映射表。
     *
     * @return 作业类型映射表（Key: 类型值，Value: 类型文本）
     */
    private Map<String, String> getWorkTypeMap() {
        List<DictModel> dictItems = sysBaseApi.queryEnableDictItemsByCode(DictConstant.WORK_TYPE);
        if (dictItems == null || dictItems.isEmpty()) {
            return Collections.emptyMap();
        }

        return dictItems.stream()
                .filter(dictModel -> dictModel.getValue() != null && dictModel.getText() != null)
                .collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (v1, v2) -> v1));
    }

    /**
     * 获取检修归档状态映射表。
     *
     * @return 检修归档状态映射表（Key: 状态值，Value: 状态文本）
     */
    private Map<String, String> getEcmStatusMap() {
        List<DictModel> dictItems = sysBaseApi.queryEnableDictItemsByCode(DictConstant.ECM_STATUS);
        if (dictItems == null || dictItems.isEmpty()) {
            return Collections.emptyMap();
        }

        return dictItems.stream()
                .filter(dictModel -> dictModel.getValue() != null && dictModel.getText() != null)
                .collect(Collectors.toMap(DictModel::getValue, DictModel::getText, (v1, v2) -> v1));
    }

    /**
     * 获取检修任务ID与同行人名称映射。
     *
     * @param repairTaskIds 检修任务ID列表
     * @return 检修任务ID与同行人名称映射
     */
    private Map<String, String> getPeerNameMap(List<String> repairTaskIds) {
        if (CollUtil.isEmpty(repairTaskIds)) {
            return CollUtil.newHashMap();
        }
        return Optional.ofNullable(repairTaskPeerRelMapper.selectTaskIdWithPeerNames(repairTaskIds))
                .orElse(Collections.emptyList())
                .stream()
                .filter(peerNameDto -> peerNameDto.getId()!=null && peerNameDto.getPeerNames() != null)
                .collect(Collectors.toMap(RepairTaskPeerNameDTO::getId, RepairTaskPeerNameDTO::getPeerNames, (v1, v2) -> v1));
    }

    /**
     * 获取检修任务ID与检修人名称映射。
     *
     * @param repairTaskIds 检修任务ID列表
     * @return 检修任务ID与检修人名称映射
     */
    private Map<String, RepairTaskUserNameDTO> getOverhaulNameMap(List<String> repairTaskIds) {
        if (CollUtil.isEmpty(repairTaskIds)) {
            return CollUtil.newHashMap();
        }

        return Optional.ofNullable(repairTaskUserMapper.selectTaskIdWithUserNames(repairTaskIds))
                .orElse(Collections.emptyList())
                .stream()
                .filter(userNameDTO -> userNameDTO.getId() != null && userNameDTO != null)
                .collect(Collectors.toMap(RepairTaskUserNameDTO::getId, userNameDTO -> userNameDTO, (userNameDTO1, userNameDTO2) -> userNameDTO1));
    }

    /**
     * 获取检修任务ID与抽检人名称映射。
     *
     * @param repairTaskIds 检修任务ID列表
     * @return 检修任务ID与抽检人名称映射
     */
    private Map<String, String> getSampNameMap(List<String> repairTaskIds) {
        if (CollUtil.isEmpty(repairTaskIds)) {
            return CollUtil.newHashMap();
        }
        return Optional.ofNullable(repairTaskSamplingMapper.selectTaskIdWithSampNames(repairTaskIds))
                .orElse(Collections.emptyList())
                .stream()
                .filter(sampNameDTO -> sampNameDTO.getId() != null && sampNameDTO.getSampNames() != null)
                .collect(Collectors.toMap(RepairTaskSampNameDTO::getId, RepairTaskSampNameDTO::getSampNames, (v1, v2) -> v1));

    }

    /**获取计划检查项*/
    private void getRepairPoolResult(String id , PrintRepairTaskDTO printRepairTaskDTO,List<PrintRepairTaskDTO> list) {
        //当计划还没安排任务的时候，去找计划的检查项
        RepairPool repairPool = repairPoolMapper.selectById(id);
        if (ObjectUtil.isEmpty(repairPool)) {
            throw new AiurtNoDataException("未找到对应任务计划");
        }

        // 站点
        List<StationDTO> repairPoolStationRels = repairPoolStationRelMapper.selectStationList(repairPool.getCode());
        printRepairTaskDTO.setSiteName(manager.translateStation(repairPoolStationRels));
        //组织机构
        List<RepairPoolOrgRel> repairPoolOrgRels = orgRelMapper.selectList(new LambdaQueryWrapper<RepairPoolOrgRel>()
                .eq(RepairPoolOrgRel::getRepairPoolCode, repairPool.getCode())
                .eq(RepairPoolOrgRel::getDelFlag, CommonConstant.DEL_FLAG_0));
        List<String> orgList = new ArrayList<>();
        if (CollUtil.isNotEmpty(repairPoolOrgRels)) {
            orgList = repairPoolOrgRels.stream().map(r -> r.getOrgCode()).collect(Collectors.toList());
            printRepairTaskDTO.setOrgName(manager.translateOrg(orgList));
        }
        // 所属周（相对年）
        int year = DateUtil.year(ObjectUtil.isNotEmpty(repairPool.getStartTime()) ? repairPool.getStartTime() : new Date());
        if (ObjectUtil.isNotEmpty(year) && repairPool.getWeeks() != null) {
            Date[] dateByWeek = DateUtils.getDateByWeek(year, repairPool.getWeeks());
            if (dateByWeek.length != 0) {
                String weekName = String.format("第%d周(%s~%s)", repairPool.getWeeks(), DateUtil.format(dateByWeek[0], "yyyy/MM/dd"), DateUtil.format(dateByWeek[1], "yyyy/MM/dd"));
                printRepairTaskDTO.setWeekName(weekName);
            }
        }
        //周期类型和作业类型
        printRepairTaskDTO.setType(sysBaseApi.translateDict(DictConstant.INSPECTION_CYCLE_TYPE, String.valueOf(repairPool.getType())));
        printRepairTaskDTO.setWorkType(sysBaseApi.translateDict(DictConstant.WORK_TYPE, String.valueOf(repairPool.getWorkType())));
        printRepairTaskDTO.setStartTime(repairPool.getStartTime());
        printRepairTaskDTO.setEndTime(repairPool.getEndTime());

        LambdaQueryWrapper<RepairPoolRel> lambdaQueryWrapper = new LambdaQueryWrapper<RepairPoolRel>();
        lambdaQueryWrapper.eq(RepairPoolRel::getRepairPoolCode, repairPool.getCode());
        lambdaQueryWrapper.eq(RepairPoolRel::getDelFlag, CommonConstant.DEL_FLAG_0);
        //根据计划code找出检修计划标准关联表数据
        List<RepairPoolRel> repairPoolRels = relMapper.selectList(lambdaQueryWrapper);
        List<RepairTaskResult> repairTaskResults = new ArrayList<>();
        if (CollUtil.isNotEmpty(repairPoolRels)) {
            List<String> codeList = repairPoolRels.stream().map(RepairPoolRel::getRepairPoolStaId).collect(Collectors.toList());
            if (CollUtil.isNotEmpty(codeList)) {

                LambdaQueryWrapper<RepairPoolCode> poolCodeLambdaQueryWrapper = new LambdaQueryWrapper<RepairPoolCode>();
                poolCodeLambdaQueryWrapper.in(RepairPoolCode::getId, codeList);
                poolCodeLambdaQueryWrapper.eq(RepairPoolCode::getDelFlag, CommonConstant.DEL_FLAG_0);

                //检修计划标准内容
                List<RepairPoolCode> repairPoolCodes = repairPoolCodeMapper.selectList(poolCodeLambdaQueryWrapper);

                if (CollUtil.isNotEmpty(repairPoolCodes)) {
                    for (RepairPoolCode repairPoolCode : repairPoolCodes) {
                        List<RepairTaskResult> repairTaskResultList = repairPoolCodeContentMapper.getRepairTaskResultList(repairPoolCode.getId());
                        if (CollUtil.isNotEmpty(repairTaskResultList)) {
                            repairTaskResultList.forEach(r -> {
                                r.setTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_PROJECT, String.valueOf(r.getType())));
                                r.setInspeciontValueName(sysBaseApi.translateDict(DictConstant.INSPECTION_STATUS_ITEM, String.valueOf(r.getStatusItem())));
                                r.setInspectionTypeName(sysBaseApi.translateDict(DictConstant.INSPECTION_VALUE, String.valueOf(r.getInspectionType())));
                            });
                        }
                        List<RepairTaskResult> repairTaskResults1 = RepairTaskServiceImpl.treeFirst(repairTaskResultList);
                        repairTaskResults.addAll(repairTaskResults1);
                    }
                }
            }
        }
        printRepairTaskDTO.setTitle(printRepairTaskDTO.getSiteName()+"检修记录表");
        printRepairTaskDTO.setRepairTaskResultList(repairTaskResults);
        list.add(printRepairTaskDTO);
    }

    /**获取任务检查项*/
    private void getRepairTaskResult(String id , PrintRepairTaskDTO printRepairTaskDTO, RepairTask one,List<PrintRepairTaskDTO> list,String overhaulCode){
        printRepairTaskDTO.setOrgName(one.getOrganizational());

        // 所属周（相对年）
        if (one.getYear() != null && one.getWeeks() != null) {
            Date[] dateByWeek = DateUtils.getDateByWeek(one.getYear(), one.getWeeks());
            if (dateByWeek.length != 0) {
                String weekName = String.format("第%d周(%s~%s)", one.getWeeks(), DateUtil.format(dateByWeek[0], "yyyy/MM/dd"), DateUtil.format(dateByWeek[1], "yyyy/MM/dd"));
                one.setWeekName(weekName);
            }
        }
        BeanUtil.copyProperties(one, printRepairTaskDTO,"repairRecord");
        printRepairTaskDTO.setOverhaulName(one.getSumitUserName());
        printRepairTaskDTO.setStartRepairTime(DateUtil.format(one.getBeginTime(), "yyyy-MM-dd HH:mm"));
        printRepairTaskDTO.setType(one.getTypeName());
        printRepairTaskDTO.setSubmitTime(DateUtil.format(one.getSubmitTime(), "yyyy-MM-dd HH:mm"));
        printRepairTaskDTO.setConfirmTime(DateUtil.format(one.getConfirmTime(), "yyyy-MM-dd HH:mm:ss"));
        printRepairTaskDTO.setReceiptTime(DateUtil.format(one.getReceiptTime(), "yyyy-MM-dd HH:mm:ss"));
        LambdaQueryWrapper<RepairTaskSignUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RepairTaskSignUser::getRepairTaskId, id);
        queryWrapper.eq(RepairTaskSignUser::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<RepairTaskSignUser> taskSignUser = repairTaskSignUserService.list(queryWrapper);
        if (CollUtil.isNotEmpty(taskSignUser)){
            List<String> url = taskSignUser.stream().filter(t-> StrUtil.isNotEmpty(t.getSignUrl())).map(RepairTaskSignUser::getSignUrl).collect(Collectors.toList());
            printRepairTaskDTO.setSignUrl(url.stream().collect(Collectors.joining(",")));
        }
        List<RepairTaskResult> repairTaskResults = new ArrayList<>();
        //获取检修站点
        List<RepairTaskStationDTO> repairTaskStationDTOS = this.repairTaskStationList(id);
        List<SpareResult> spareChange = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();
        List<String> enclosureUrl = new ArrayList<>();
        for (RepairTaskStationDTO repairTaskStationDTO : repairTaskStationDTOS) {
            //无设备
            List<RepairTaskDTO> tasks = repairTaskMapper.selectTaskList(id, repairTaskStationDTO.getStationCode());
            //有设备
            List<RepairTaskDTO> repairDeviceTask = repairTaskMapper.selectDeviceTaskList(id);
            for (RepairTaskDTO repairTaskDTO : repairDeviceTask) {
                String equipmentCode = repairTaskDTO.getEquipmentCode();
                if(StrUtil.isNotBlank(equipmentCode)){
                    JSONObject deviceByCode = sysBaseApi.getDeviceByCode(equipmentCode);
                    if (ObjectUtil.isNotEmpty(deviceByCode)) {
                        String lineCode = deviceByCode.getString("lineCode");
                        String station_code = deviceByCode.getString("stationCode");
                        String positionCode = deviceByCode.getString("positionCode") == null ? "" : deviceByCode.getString("positionCode");
                        String lineCodeName = sysBaseApi.translateDictFromTable("cs_line", "line_name", "line_code", lineCode);
                        String stationCodeName = sysBaseApi.translateDictFromTable("cs_station", "station_name", "station_code", station_code);
                        String positionCodeName = sysBaseApi.translateDictFromTable("cs_station_position", "position_name", "position_code", positionCode);
                        repairTaskDTO.setEquipmentName(deviceByCode.getString("name"));
                        String positionCodeCcName = lineCodeName;
                        if (stationCodeName != null && !"".equals(stationCodeName)) {
                            positionCodeCcName += "-" + stationCodeName;
                        }
                        if (!"".equals(positionCodeName) && positionCodeName != null) {
                            positionCodeCcName += "-"+ positionCodeName;
                        }
                        repairTaskDTO.setEquipmentLocation(positionCodeCcName);
                        if((repairTaskStationDTO.getStationCode()).equals(station_code)){
                            tasks.add(repairTaskDTO);
                        }
                    }
                }
            }
            if (StrUtil.isNotEmpty(overhaulCode)){
                tasks = tasks.stream().filter(t-> t.getOverhaulCode().equals(overhaulCode)).collect(Collectors.toList());
            }
            //获取单号
            //taskDeviceCode.addAll(tasks.stream().map(RepairTaskDTO::getOverhaulCode).collect(Collectors.toList()));
            //详细信息获取
            getDetail(tasks, id, repairTaskStationDTO, printRepairTaskDTO, spareChange, stringBuilder, enclosureUrl,repairTaskResults);
        }

        if (StrUtil.isEmpty(printRepairTaskDTO.getRepairRecord())) {
            printRepairTaskDTO.setRepairRecord("无");
        }
        printRepairTaskDTO.setTitle(one.getSiteName()+"检修记录表");
        printRepairTaskDTO.setRepairTaskResultList(repairTaskResults);
        printRepairTaskDTO.setSpareChange(spareChange);
        if (stringBuilder.length() > 0) {
            // 截取字符
            stringBuilder = stringBuilder.deleteCharAt(stringBuilder.length() - 1);
            printRepairTaskDTO.setRepairResult(stringBuilder.toString());
        }
        printRepairTaskDTO.setEnclosureUrl(enclosureUrl);
        list.add(printRepairTaskDTO);
    }


    public void getDetail( List<RepairTaskDTO> tasks,String id,RepairTaskStationDTO repairTaskStationDTO,PrintRepairTaskDTO printRepairTaskDTO, List<SpareResult> spareChange,StringBuilder stringBuilder, List<String> enclosureUrl,List<RepairTaskResult> repairTaskResults) {
        int i = 1;
        for (RepairTaskDTO repairTaskDTO : tasks) {
            repairTaskDTO.setSystemName(manager.translateMajor(Arrays.asList(repairTaskDTO.getSystemCode()), InspectionConstant.SUBSYSTEM));

            String deviceId = repairTaskDTO.getDeviceId();

            CheckListDTO checkListDTO = repairTaskMapper.selectRepairTaskInfo(id, repairTaskStationDTO.getStationCode(), deviceId);
            InspectionCode inspectionCode = inspectionCodeMapper.selectOne(new LambdaQueryWrapper<InspectionCode>()
                    .eq(InspectionCode::getCode,checkListDTO.getStandardCode()).eq(InspectionCode::getDelFlag,0));
            List<DictModel> taskType = sysBaseApi.getDictItems("task_type");
            String typeName = taskType.stream().filter(d -> d.getValue().equals(inspectionCode.getType().toString()))
                    .map(DictModel::getText).collect(Collectors.joining());
            printRepairTaskDTO.setTypeName(typeName);
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

            List<RepairTaskSampling> repairTaskSamplings = repairTaskSamplingMapper.selectList(new LambdaQueryWrapper<RepairTaskSampling>()
                    .eq(RepairTaskSampling::getRepairTaskDeviceCode,repairTaskDTO.getOverhaulCode()).eq(RepairTaskSampling::getDelFlag,0));
            //获取检查项
            List<RepairTaskResult> resultList = repairTaskMapper.selectSingle(deviceId, null);
            resultList.forEach(r -> {
                if (CollUtil.isNotEmpty(repairTaskSamplings)){
                    //获取抽检人名称
                    r.setSamplingName(repairTaskSamplings.stream().map(RepairTaskSampling::getRealName).collect(Collectors.toList())
                            .stream()
                            .collect(Collectors.joining(",")));
                    //抽检时间
                    r.setSamplingDate(repairTaskSamplings.get(0).getCreateTime());
                    //抽检人签名
                    RepairTaskDeviceRel repairTaskDeviceRel = repairTaskDeviceRelMapper.selectOne(new LambdaQueryWrapper<RepairTaskDeviceRel>()
                            .eq(RepairTaskDeviceRel::getCode,repairTaskDTO.getOverhaulCode()).eq(RepairTaskDeviceRel::getDelFlag,0));
                    r.setSamplingUrl(repairTaskDeviceRel.getSamplingSignUrl());
                }
                r.setDeviceName(repairTaskDTO.getEquipmentName());
                r.setEquipmentLocation(repairTaskDTO.getEquipmentLocation());
                List<RepairTaskEnclosure> repairTaskDevice = repairTaskEnclosureMapper.selectList(
                        new LambdaQueryWrapper<RepairTaskEnclosure>()
                                .eq(RepairTaskEnclosure::getRepairTaskResultId, r.getId()));
                if (CollectionUtils.isNotEmpty(repairTaskDevice)) {
                    //获取检修单的检修结果的附件
                    List<String> urllist = repairTaskDevice.stream().map(RepairTaskEnclosure::getUrl).collect(Collectors.toList());
                    enclosureUrl.addAll(urllist);
                }

                if ("0".equals(r.getPid())) {
                    r.setName(r.getName() != null ? r.getName() : "");
                }

                //检修结果
                r.setStatusName(sysBaseApi.translateDict(DictConstant.OVERHAUL_RESULT, String.valueOf(r.getStatus())));

                //当第一次检修结果为空时，且有检修结果是正常
                if (ObjectUtil.isEmpty(printRepairTaskDTO.getRepairRecord())&& r.getStatus() != null && r.getStatus() == 1) {
                    printRepairTaskDTO.setRepairRecord(r.getStatusName());
                }
                //当检修结果异常时覆盖
                if (r.getStatus() != null && r.getStatus() == 2) {
                    printRepairTaskDTO.setRepairRecord(r.getStatusName());
                }
                if (CollUtil.isEmpty(r.getChildren())){
                    r.setChildren(new ArrayList<>());
                }
            });
            List<RepairTaskResult> repairTaskResults1 = RepairTaskServiceImpl.treeFirst(resultList);
            repairTaskResults.addAll(repairTaskResults1);
        }
    }

    /**
     * 维保任务执行中延时提醒
     * @param repairTask 检修单
     */
    private void processRepairInExecutionToRemind(RepairTask repairTask) {
        //提醒配置是否开启
        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.REPAIR_IN_EXECUTION_REMIND);
        boolean b = ObjectUtil.isNotNull(sysParamModel) && "1".equals(sysParamModel.getValue());
        if (b) {
            //获取配置参数
            SysParamModel delayParam = iSysParamAPI.selectByCode(SysParamCodeConstant.RIE_DELAY);
            SysParamModel periodParam = iSysParamAPI.selectByCode(SysParamCodeConstant.RIE_PERIOD);
            if (ObjectUtil.isNull(delayParam) || ObjectUtil.isNull(periodParam)) {
                throw new AiurtBootException("请检查是否配置初始延时时长和间隔时长");
            }
            long delay = Long.parseLong(delayParam.getValue());
            int period = Integer.parseInt(periodParam.getValue());
            // 计算初始执行时间
            LocalDateTime localDateTime;
            if (ObjectUtil.isNotEmpty(repairTask.getUpdateTime())) {
                localDateTime = repairTask.getUpdateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            } else {
                localDateTime = repairTask.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            }
            LocalDateTime newDateTime = localDateTime.plus(delay, ChronoUnit.SECONDS);
            Date startTime = Date.from(newDateTime.atZone(ZoneId.systemDefault()).toInstant());
            log.info("首次执行时间:" + DateUtil.formatDateTime(startTime) + ",RepairInExecutionRemindJob,检修单号:" + repairTask.getCode());
            // 自定义触发器
            SimpleTrigger trigger = TriggerBuilder.newTrigger()
                    .startAt(startTime)
                    .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInSeconds(period).repeatForever())
                    .build();
            String updateTime = ObjectUtil.isNotEmpty(repairTask.getUpdateTime()) ? DateUtil.format(repairTask.getUpdateTime(), "yyyy-MM-dd HH:mm:ss") : "";
            // 创建定时任务
            QuartzJobDTO quartzJobDTO = new QuartzJobDTO();
            quartzJobDTO.setTrigger(trigger);
            quartzJobDTO.setParameter(repairTask.getCode() + StrUtil.COMMA + repairTask.getStatus() + StrUtil.COMMA + updateTime);
            quartzJobDTO.setJobClassName("com.aiurt.boot.task.job.RepairInExecutionRemindJob");
            quartzJobDTO.setDescription("维保任务执行中延时提醒任务");
            quartzJobDTO.setStatus(0);
            sysBaseApi.saveAndScheduleJob(quartzJobDTO);
        }
    }
}
