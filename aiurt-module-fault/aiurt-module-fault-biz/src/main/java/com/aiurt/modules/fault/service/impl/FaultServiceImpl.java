package com.aiurt.modules.fault.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.api.InspectionApi;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.manager.dto.FaultCallbackDTO;
import com.aiurt.common.api.dto.message.MessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.basic.entity.CsWork;
import com.aiurt.modules.common.api.IBaseApi;
import com.aiurt.modules.fault.constants.FaultConstant;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.*;
import com.aiurt.modules.fault.enums.FaultStatusEnum;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.mapper.FaultRepairRecordMapper;
import com.aiurt.modules.fault.service.*;
import com.aiurt.modules.faultexternal.entity.FaultExternal;
import com.aiurt.modules.faultexternal.mapper.FaultExternalMapper;
import com.aiurt.modules.faultexternal.service.IFaultExternalService;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
import com.aiurt.modules.faultknowledgebase.service.IFaultKnowledgeBaseService;
import com.aiurt.modules.faultknowledgebasetype.entity.FaultKnowledgeBaseType;
import com.aiurt.modules.faultknowledgebasetype.service.IFaultKnowledgeBaseTypeService;
import com.aiurt.modules.faultlevel.entity.FaultLevel;
import com.aiurt.modules.faultlevel.service.IFaultLevelService;
import com.aiurt.modules.schedule.dto.SysUserTeamDTO;
import com.aiurt.modules.sparepart.dto.DeviceChangeSparePartDTO;
import com.aiurt.modules.todo.dto.TodoDTO;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.api.ISTodoBaseAPI;
import org.jeecg.common.system.api.ISparePartBaseApi;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: fault
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@Slf4j
@Service
public class FaultServiceImpl extends ServiceImpl<FaultMapper, Fault> implements IFaultService {

    private static final String SELF_FAULT_MODE_CODE = "0";


    @Autowired
    private IFaultDeviceService faultDeviceService;

    @Autowired
    private IOperationProcessService operationProcessService;

    @Autowired
    private IFaultRepairRecordService repairRecordService;

    @Autowired
    private IFaultRepairParticipantsService repairParticipantsService;

    @Autowired
    private ISysBaseAPI sysBaseAPI;
    @Autowired
    private IBaseApi baseApi;

    @Autowired
    private IDeviceChangeSparePartService sparePartService;

    @Autowired
    private InspectionApi inspectionApi;

    @Autowired
    private IFaultLevelService faultLevelService;

    @Autowired
    private ISparePartBaseApi sparePartBaseApi;

    @Autowired
    private IFaultKnowledgeBaseTypeService faultKnowledgeBaseTypeService;

    @Autowired
    private IFaultKnowledgeBaseService faultKnowledgeBaseService;

    @Autowired
    private ISTodoBaseAPI todoBaseApi;
    @Autowired
    private ISysParamAPI iSysParamAPI;

    @Autowired
    private FaultRepairRecordMapper recordMapper;

    @Autowired
    private FaultMapper faultMapper;

    @Autowired
    private FaultExternalMapper faultExternalMapper;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private IFaultExternalService faultExternalService;


    /**
     * 故障上报
     *
     * @param fault 故障对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(Fault fault) {

        //根据配置实现是否需要自动抄送
        getRemindUserName(fault);

        LoginUser user = checkLogin();
        log.info("故障上报：操作人员：[{}], 请求参数：{}", user.getRealname(), JSON.toJSONString(fault));
        // 故障编号处理
        String majorCode = fault.getMajorCode();
        StringBuilder builder = new StringBuilder("WX");
        builder.append(majorCode).append(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        fault.setCode(builder.toString());

        // 接报人
        //fault.setReceiveTime();
        fault.setReceiveUserName(user.getUsername());

        String faultModeCode = fault.getFaultModeCode();

        LambdaQueryWrapper<FaultKnowledgeBaseType> queryWrapper = new LambdaQueryWrapper<>();
        FaultKnowledgeBaseType one = faultKnowledgeBaseTypeService.getOne(queryWrapper.eq(FaultKnowledgeBaseType::getCode, fault.getFaultPhenomenon()).eq(FaultKnowledgeBaseType::getDelFlag, 0));
        // 自报自修跳过
        boolean b = StrUtil.equalsIgnoreCase(faultModeCode, SELF_FAULT_MODE_CODE);
        // 根据配置决定是否需要审核
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_PROCESS);
        boolean value = "1".equals(paramModel.getValue());
        if (b) {
            // 根据配置决定故障上报是否开启控制中心班组自检故障指派功能及权限
            SysParamModel faultCenterAddParam = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_CENTER_ADD);
            boolean faultCenterAdd = FaultConstant.ENABLE.equals(faultCenterAddParam.getValue());
            // 根据配置获取控制中心班组code,并判断当前登陆人所在班组是否是控制中心班组
            SysParamModel faultCenterAddOrg = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_CENTER_ADD_ORG);
            boolean contains1 = StrUtil.splitTrim(faultCenterAddOrg.getValue(),',').contains(user.getOrgCode());
            // 根据配置获取控制中心站点code，并判断故障站点是否时控制中心站点
            SysParamModel faultCenterAddStation = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_CENTER_ADD_STATION);
            boolean contains2 = StrUtil.splitTrim(faultCenterAddStation.getValue(), ',').contains(fault.getStationCode());
            if (faultCenterAdd && contains1 && !contains2 ) {
                // 跳过到待指派
                fault.setStatus(FaultStatusEnum.APPROVAL_PASS.getStatus());
                fault.setApprovalPassTime(new Date());
            } else {
                fault.setAppointUserName(user.getUsername());
                fault.setStatus(FaultStatusEnum.REPAIR.getStatus());
                // 方便统计
                fault.setApprovalPassTime(fault.getReceiveTime());
                // 创建维修记录
                FaultRepairRecord record = FaultRepairRecord.builder()
                        // 做类型
                        .faultCode(fault.getCode())
                        // 故障现象
                        .faultPhenomenon(one.getName())
                        .startTime(new Date())
                        .delFlag(CommonConstant.DEL_FLAG_0)
                        // 负责人
                        .appointUserName(user.getUsername())
                        .build();

                repairRecordService.save(record);
            }
        } else {
            if (value) {
                //如果配置需要审核，但是是从调度过来的数据不需要审核
                if(ObjectUtil.isNotEmpty(fault.getIsFaultExternal())&&fault.getIsFaultExternal()){
                    fault.setStatus(FaultStatusEnum.APPROVAL_PASS.getStatus());
                    fault.setApprovalPassTime(new Date());
                }else {
                    fault.setStatus(FaultStatusEnum.NEW_FAULT.getStatus());
                }
            }else {
                fault.setStatus(FaultStatusEnum.APPROVAL_PASS.getStatus());
                fault.setApprovalPassTime(new Date());
            }
        }
        //故障的所属部门为站点通过工区关联的部门
        List<String> departs = sysBaseAPI.getWorkAreaByCode(fault.getStationCode())
                .stream()
                .flatMap(csWorkAreaModel -> csWorkAreaModel.getOrgCodeList().stream())
                .collect(Collectors.toList());
        if (CollUtil.isNotEmpty(departs)) {
            fault.setSysOrgCode(departs.get(0));
        }
        // 保存故障
        save(fault);


        // 设置故障设备
        dealDevice(fault, fault.getFaultDeviceList());


        // 记录日志
        saveLog(user, "故障上报", fault.getCode(), 1, null);



        try {
            FaultMessageDTO faultMessageDTO = new FaultMessageDTO();
            BeanUtil.copyProperties(fault,faultMessageDTO);

            // 待办任务
            TodoDTO todoDTO = new TodoDTO();
            todoDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
            if (b) {
                todoDTO.setTitle("故障维修任务");
                todoDTO.setMsgAbstract("有新的故障信息");
                todoDTO.setPublishingContent("有新的维修任务");
                todoDTO.setIsRingBell(true);
                // 自检
                sendTodo(fault.getCode(), null, user.getUsername(), "故障维修任务", TodoBusinessTypeEnum.FAULT_DEAL.getType(),todoDTO,faultMessageDTO);
            } else {
                if (value) {
                    if (ObjectUtil.isEmpty(fault.getIsFaultExternal())) {
                        todoDTO.setTitle("故障上报审核");
                        todoDTO.setMsgAbstract("有新的故障信息");
                        todoDTO.setPublishingContent("有新的故障信息，请审核");
                        sendTodo(fault.getCode(), RoleConstant.PRODUCTION, null, "故障上报审核", TodoBusinessTypeEnum.FAULT_APPROVAL.getType(), todoDTO, faultMessageDTO);
                    }
                } else {
                    //此班组当前时间段当班维修人员收到通知
                    List<LoginUser> users = getUserByWorkArea(fault.getStationCode());
                    if (CollectionUtil.isNotEmpty(users)) {
                        List<String> list = users.stream().map(LoginUser::getUsername).collect(Collectors.toList());
                        //发送通知
                        MessageDTO messageDTO = new MessageDTO(user.getUsername(),CollUtil.join(list,","), "有新的故障上报" + DateUtil.today(), null);

                        //业务类型，消息类型，消息模板编码，摘要，发布内容
                        faultMessageDTO.setBusType(SysAnnmentTypeEnum.FAULT.getType());
                        messageDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
                        messageDTO.setMsgAbstract("有新的故障信息");
                        messageDTO.setPublishingContent("有新的故障信息，请查看");
                        messageDTO.setIsRingBell(true);
                        sendMessage(messageDTO,faultMessageDTO);
                    }
                }

            }

            if (value) {

                // 抄送
                String remindUserName = fault.getRemindUserName();
                if (StrUtil.isNotBlank(remindUserName)) {
                    //发送通知
                    MessageDTO messageDTO = new MessageDTO(user.getUsername(), remindUserName, "故障上报审核" + DateUtil.today(), null);

                    //业务类型，消息类型，消息模板编码，摘要，发布内容
                    faultMessageDTO.setBusType(SysAnnmentTypeEnum.FAULT.getType());
                    messageDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
                    messageDTO.setMsgAbstract("有新的故障信息");
                    messageDTO.setPublishingContent("有新的故障信息，请审核");

                    sendMessage(messageDTO, faultMessageDTO);
                }
            } else {
                // 抄送
                String remindUserName = fault.getRemindUserName();
                if (StrUtil.isNotBlank(remindUserName)) {
                    //发送通知
                    MessageDTO messageDTO = new MessageDTO(user.getUsername(), remindUserName, "有新的故障信息" + DateUtil.today(), null);

                    //业务类型，消息类型，消息模板编码，摘要，发布内容
                    faultMessageDTO.setBusType(SysAnnmentTypeEnum.FAULT.getType());
                    messageDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
                    messageDTO.setMsgAbstract("有新的故障信息");
                    messageDTO.setPublishingContent("您有一条新的故障信息");

                    sendMessage(messageDTO, faultMessageDTO);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 回调
        if (StrUtil.isNotBlank(fault.getRepairCode())) {
            FaultCallbackDTO faultCallbackDTO = new FaultCallbackDTO();
            faultCallbackDTO.setFaultCode(fault.getCode());
            faultCallbackDTO.setSingleCode(fault.getRepairCode());
            inspectionApi.editFaultCallback(faultCallbackDTO);
        }

        //单个设备一个月内重复出现两次故障，系统自动发布一条特情（专用）
        // 根据配置决定是否需要发送
        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_SITUATION);
        boolean equals = "1".equals(sysParamModel.getValue());
        if (equals) {
            sendInfo(fault,fault.getFaultDeviceList());
        }

        return builder.toString();
    }
    /**
     * 根据配置实现是否需要自动抄送
     *
     * @param fault 故障对象
     */
    private void getRemindUserName(Fault fault) {
        //获取配置信息
        SysParamModel sysParam = iSysParamAPI.selectByCode(SysParamCodeConstant.AUTO_CC);
        boolean autoCc = "1".equals(sysParam.getValue());
        if (autoCc) {
            if (StrUtil.isEmpty(fault.getFaultLevel())) {
                throw new AiurtBootException("故障级别不能为空");
            }
            //获取对应故障级别应当抄送给哪些角色
            String faultLevelCode = fault.getFaultLevel();
            FaultLevel one = faultLevelService.getOne(new LambdaQueryWrapper<FaultLevel>()
                    .eq(FaultLevel::getCode, faultLevelCode)
                    .eq(FaultLevel::getDelFlag, 0));

            List<String> roles;
            int i = 5;
            if (Integer.parseInt(one.getWeight()) > i) {
                SysParamModel high = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_LEVEL_HIGH);
                roles = StrUtil.splitTrim(high.getValue(), ",");
            } else {
                SysParamModel little = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_LEVEL_LITTLE);
                roles = StrUtil.splitTrim(little.getValue(), ",");
            }

            //通过站点从工区获取部门
            List<String> userNames = new ArrayList<>();

            List<String> departs = sysBaseAPI.getWorkAreaByCode(fault.getStationCode())
                    .stream()
                    .flatMap(csWorkAreaModel -> csWorkAreaModel.getOrgCodeList().stream())
                    .collect(Collectors.toList());

            for (String role : roles) {
                if (StrUtil.equalsAnyIgnoreCase(role, RoleConstant.FOREMAN) && CollUtil.isNotEmpty(departs)) {
                    for (String orgCode : departs) {
                        String userName = this.getUserNameByOrgCodeAndRoleCode(StrUtil.split(role, ','), null, null, null, orgCode);
                        userNames.add(userName);
                    }
                } else {
                    String roleId = sysBaseAPI.getRoleIdByCode(role);
                    List<SysUserRoleModel> userList = sysBaseAPI.getUserByRoleId(roleId);
                    userNames.addAll(userList.stream().map(SysUserRoleModel::getUserName).collect(Collectors.toList()));
                }
            }
            if (CollUtil.isNotEmpty(userNames)) {
                String join = StrUtil.join(",", userNames);
                fault.setRemindUserName(StrUtil.isNotEmpty(fault.getRemindUserName())?fault.getRemindUserName()+","+join:join);
            }
        }
    }

    /**
     * 任务池
     * @param businessKey 业务标识
     * @param roleCode 角色编码
     * @param currentUserName 用户名
     * @param taskName 任务标题
     */
    private void sendTodo(String businessKey, String roleCode, String currentUserName, String taskName,String businessType,TodoDTO todoDTO,FaultMessageDTO faultMessageDTO) {
        if (StrUtil.isNotBlank(roleCode)) {
            String userName = null;
            if (StrUtil.equalsAnyIgnoreCase(roleCode, RoleConstant.FOREMAN)) {
                // 专业，子系统，站点
                Fault fault = isExist(businessKey);
                String majorCode = fault.getMajorCode();
                String subSystemCode = fault.getSubSystemCode();
                String stationCode = fault.getStationCode();
                String sysOrgCode = fault.getSysOrgCode();
                userName = this.getUserNameByOrgCodeAndRoleCode(StrUtil.split(roleCode, ','), majorCode, subSystemCode, stationCode,sysOrgCode);
            }else {
                userName = this.getUserNameByOrgCodeAndRoleCode(StrUtil.split(roleCode, ','), null, null, null,null);
            }
            todoDTO.setCurrentUserName(userName);
        }else {
            todoDTO.setCurrentUserName(currentUserName);
        }
        //构建消息模板
        HashMap<String, Object> map = new HashMap<>();
        if (CollUtil.isNotEmpty(todoDTO.getData())) {
            map.putAll(todoDTO.getData());
        }
        map.put("code",faultMessageDTO.getCode());
        String faultLevel = sysBaseAPI.translateDictFromTable("fault_level", "name","code",faultMessageDTO.getFaultLevel());
        map.put("faultLevel",faultLevel);
        String faultUrgency = sysBaseAPI.translateDict("fault_urgency", Convert.toStr(faultMessageDTO.getUrgency()));
        map.put("urgency",faultUrgency);
        String faultType = sysBaseAPI.translateDictFromTable("fault_type","name","code", faultMessageDTO.getFaultTypeCode());
        map.put("faultTypeCode",faultType);
        String faultModeCode = sysBaseAPI.translateDict("fault_mode_code", faultMessageDTO.getFaultModeCode());
        map.put("faultModeCode",faultModeCode);

        String line = sysBaseAPI.getPosition(faultMessageDTO.getLineCode());
        String station = sysBaseAPI.getPosition(faultMessageDTO.getStationCode());
        String position = sysBaseAPI.getPosition(faultMessageDTO.getStationPositionCode());
        String faultStationPosition = line + station;
        if (StrUtil.isNotBlank(position)) {
            faultStationPosition = faultStationPosition + position;
        }
        map.put("faultStationPosition",faultStationPosition);
        todoDTO.setData(map);
        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_MESSAGE_PROCESS);
        todoDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
        // 根据角色获取人员
        todoDTO.setTaskName(taskName);
        todoDTO.setBusinessKey(businessKey);
        todoDTO.setBusinessType(businessType);
        todoDTO.setTaskType(TodoTaskTypeEnum.FAULT.getType());
        todoDTO.setTodoType("0");
        todoDTO.setProcessDefinitionName("故障管理");
        todoDTO.setUrl(null);
        todoDTO.setAppUrl(null);
        todoBaseApi.createTodoTask(todoDTO);
    }

    /**
     * 故障审批
     *
     * @param approvalDTO 审批对象
     */
    @Override
    public void approval(ApprovalDTO approvalDTO) {

        LoginUser user = checkLogin();
        String faultCode = approvalDTO.getFaultCode();
        Fault fault = isExist(faultCode);

        // 通过的状态 = 1
        Integer status = 1;
        Integer approvalStatus = approvalDTO.getApprovalStatus();
        OperationProcess operationProcess = OperationProcess.builder()
                .processTime(new Date())
                .faultCode(fault.getCode())
                .processPerson(user.getUsername())
                .build();
        fault.setApprovalTime(new Date());
        fault.setApprovalUserName(user.getUsername());

        // 判断是否为审批通过
        boolean b = Objects.isNull(approvalStatus) || status.equals(approvalStatus);
        if (b) {
            // 审批通过
            fault.setApprovalPassTime(new Date());
            fault.setStatus(FaultStatusEnum.APPROVAL_PASS.getStatus());
            operationProcess.setProcessLink(FaultStatusEnum.APPROVAL_PASS.getMessage())
                    .setProcessCode(FaultStatusEnum.APPROVAL_PASS.getStatus()).setRemark(approvalDTO.getApprovalRejection());
        } else {
            // 驳回
            fault.setStatus(FaultStatusEnum.APPROVAL_REJECT.getStatus());
            fault.setApprovalRejection(approvalDTO.getApprovalRejection());
            operationProcess.setProcessLink(FaultStatusEnum.APPROVAL_REJECT.getMessage())
                    .setProcessCode(FaultStatusEnum.APPROVAL_REJECT.getStatus()).setRemark(approvalDTO.getApprovalRejection());
        }

        updateById(fault);

        operationProcessService.save(operationProcess);

        // 更新上报的待办
        todoBaseApi.updateTodoTaskState(TodoBusinessTypeEnum.FAULT_APPROVAL.getType(), faultCode, user.getUsername(), "1");
        try {
            FaultMessageDTO faultMessageDTO = new FaultMessageDTO();
            BeanUtil.copyProperties(fault,faultMessageDTO);
            if (b) {
                TodoDTO todoDTO = new TodoDTO();
                todoDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
                todoDTO.setTitle("故障上报审核");
                todoDTO.setMsgAbstract("有新的故障信息");
                todoDTO.setPublishingContent("有新的故障信息，请尽快安排维修");
                // 审批通过 新增任务， 该线路或者是工班长，指派任务
                sendTodo(faultCode, RoleConstant.FOREMAN, null, "故障指派", TodoBusinessTypeEnum.FAULT_ASSIGN.getType(),todoDTO,faultMessageDTO);

                //此班组当前时间段当班维修人员收到通知
                List<LoginUser> users = getUserByWorkArea(fault.getStationCode());
                if (CollectionUtil.isNotEmpty(users)) {
                    List<String> list = users.stream().map(LoginUser::getUsername).collect(Collectors.toList());
                    //发送通知
                    MessageDTO messageDTO = new MessageDTO(user.getUsername(),CollUtil.join(list,","), "有新的故障上报" + DateUtil.today(), null);

                    //业务类型，消息类型，消息模板编码，摘要，发布内容
                    faultMessageDTO.setBusType(SysAnnmentTypeEnum.FAULT.getType());
                    messageDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
                    messageDTO.setMsgAbstract("有新的故障信息");
                    messageDTO.setPublishingContent("有新的故障信息，请查看");
                    messageDTO.setIsRingBell(true);
                    sendMessage(messageDTO,faultMessageDTO);
                }
            } else {
                //被驳回发送通知
                MessageDTO messageDTO = new MessageDTO(user.getUsername(),fault.getReceiveUserName(), "故障上报审核驳回" + DateUtil.today(), null);
                //构建消息模板
                HashMap<String, Object> map = new HashMap<>();
                map.put("approvalRejection",fault.getApprovalRejection());
                messageDTO.setData(map);
                //业务类型，消息类型，消息模板编码，摘要，发布内容
                faultMessageDTO.setBusType(SysAnnmentTypeEnum.FAULT.getType());
                messageDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE_REJECT);
                messageDTO.setMsgAbstract("故障上报审核驳回");
                messageDTO.setPublishingContent("上报的故障被驳回，请处理");

                sendMessage(messageDTO,faultMessageDTO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 编辑
     *
     * @param fault 故障对象 @see com.aiurt.modules.fault.entity.Fault
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Fault fault) {
        log.info("修改故障工单：[{}]", JSON.toJSONString(fault));

        isExist(fault.getCode());

        LoginUser loginUser = checkLogin();

        dealDevice(fault, fault.getFaultDeviceList());

        // 根据配置决定是否需要审核
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_PROCESS);
        boolean value = "1".equals(paramModel.getValue());

        // update status
        if (value) {
            fault.setStatus(FaultStatusEnum.NEW_FAULT.getStatus());

            updateById(fault);

            // 记录日志
            saveLog(loginUser, "修改故障工单", fault.getCode(), FaultStatusEnum.NEW_FAULT.getStatus(), null);
        } else {
            fault.setStatus(FaultStatusEnum.APPROVAL_PASS.getStatus());

            updateById(fault);

            // 记录日志
            saveLog(loginUser, "修改故障工单", fault.getCode(), FaultStatusEnum.APPROVAL_PASS.getStatus(), null);
        }


        // 待办任务池，指派
        //sendTodo(fault.getCode(), RoleConstant.PRODUCTION, null, "故障上报审核", );
    }


    /**
     * 作废
     *
     * @param cancelDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(CancelDTO cancelDTO) {
        log.info("故障工单作废,请求参数：[{}]", JSON.toJSONString(cancelDTO));

        LoginUser user = checkLogin();

        // 故障单
        Fault fault = isExist(cancelDTO.getFaultCode());

        // 作废
        fault.setStatus(FaultStatusEnum.CANCEL.getStatus());

        //更新状态
        fault.setCancelTime(new Date());
        fault.setCancelUserName(user.getUsername());
        fault.setCancelRemark(cancelDTO.getCancelRemark());
        updateById(fault);

        // 记录日志
        saveLog(user, FaultStatusEnum.CANCEL.getMessage(), fault.getCode(), FaultStatusEnum.CANCEL.getStatus(), cancelDTO.getCancelRemark());

        String receiveUserName = fault.getReceiveUserName();

        if (!StrUtil.equalsAnyIgnoreCase(receiveUserName, user.getUsername())) {
            //发送通知
            try {
                MessageDTO messageDTO = new MessageDTO(user.getUsername(),receiveUserName, "故障已被作废" + DateUtil.today(), null);
                FaultMessageDTO faultMessageDTO = new FaultMessageDTO();
                BeanUtil.copyProperties(fault,faultMessageDTO);
                //业务类型，消息类型，消息模板编码，摘要，发布内容
                faultMessageDTO.setBusType(SysAnnmentTypeEnum.FAULT.getType());
                messageDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
                messageDTO.setMsgAbstract("故障已被作废");
                messageDTO.setPublishingContent("故障已被作废");

                sendMessage(messageDTO,faultMessageDTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 根据编码查询详情
     *
     * @param code
     * @return
     */
    @Override
    public Fault queryByCode(String code) {
        LoginUser user = checkLogin();

        Fault fault = isExist(code);
        if(ObjectUtil.isNotEmpty(fault.getAppointUserName())){
            if(fault.getAppointUserName().equals(user.getUsername())){
                fault.setIsFault(true);
            }else {
                fault.setIsFault(false);
            }
        }else {
            fault.setIsFault(false);
        }
        // 设备
        List<FaultDevice> faultDeviceList = faultDeviceService.queryByFaultCode(code);
        fault.setFaultDeviceList(faultDeviceList);

        // 故障等级,权重登记
        if (StrUtil.isNotBlank(fault.getFaultLevel())) {
            LambdaQueryWrapper<FaultLevel> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(FaultLevel::getCode, fault.getFaultLevel()).last("limit 1");
            FaultLevel faultLevel = faultLevelService.getBaseMapper().selectOne(wrapper);
            if (Objects.isNull(faultLevel)) {
                fault.setWeight(0);
            } else {
                String weight = faultLevel.getWeight();
                if (StrUtil.isNotBlank(weight)) {
                    try {
                        fault.setWeight(Integer.valueOf(weight));
                    } catch (NumberFormatException e) {
                        fault.setWeight(0);
                    }
                } else {
                    fault.setWeight(0);
                }
            }

        } else {
            fault.setWeight(0);
        }
        // 根据配置决定控制中心成员能否领取正线站点故障，开启时表示不能领取
        SysParamModel faultCenterReceiveParam = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_CENTER_RECEIVE);
        boolean faultCenterReceive = FaultConstant.ENABLE.equals(faultCenterReceiveParam.getValue());
        // 根据配置获取控制中心班组code,并判断当前登陆人所在班组是否是控制中心班组
        SysParamModel faultCenterAddOrg = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_CENTER_ADD_ORG);
        boolean contains1 = StrUtil.splitTrim(faultCenterAddOrg.getValue(),',').contains(user.getOrgCode());
        // 通过站点获取工区部门
        List<String> departs = sysBaseAPI.getWorkAreaByCode(fault.getStationCode())
                .stream()
                .flatMap(csWorkAreaModel -> csWorkAreaModel.getOrgCodeList().stream())
                .collect(Collectors.toList());
        boolean contains2 = !(ObjectUtil.isNotEmpty(departs) && departs.contains(user.getOrgCode()));
        // 设置是否能领取
        if (faultCenterReceive && contains1 && contains2) {
            fault.setCanReceive(false);
        } else {
            fault.setCanReceive(true);
        }
        // 按钮权限
        return fault;
    }

    /**
     * 指派
     *
     * @param assignDTO
     */
    @Override
    public void assign(AssignDTO assignDTO) {

        LoginUser user = checkLogin();
        String faultCode = assignDTO.getFaultCode();
        Fault fault = isExist(faultCode);

        LoginUser loginUser = sysBaseAPI.getUserByName(assignDTO.getOperatorUserName());
        if (Objects.isNull(loginUser)) {
            throw new AiurtBootException("指派失败，作业人员不存在!");
        }

        // 删除其他记录
        LambdaUpdateWrapper<FaultRepairRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FaultRepairRecord::getDelFlag, CommonConstant.DEL_FLAG_1).eq(FaultRepairRecord::getFaultCode, fault.getCode());
        repairRecordService.update(updateWrapper);

        // 更新状态
        fault.setStatus(FaultStatusEnum.ASSIGN.getStatus());
        fault.setAssignTime(new Date());
        fault.setAssignUserName(user.getUsername());
        fault.setAppointUserName(assignDTO.getOperatorUserName());

        // 维修记录
        FaultRepairRecord record = FaultRepairRecord.builder()
                // 做类型
                .workType(assignDTO.getCaWorkCode())
                .planOrderCode(assignDTO.getPlanCode())
                .faultCode(fault.getCode())
                .planOrderImg(assignDTO.getFilepath())
                .assignTime(new Date())
                .delFlag(CommonConstant.DEL_FLAG_0)
                // 故障想象
                .faultPhenomenon(fault.getFaultPhenomenon())
                // 负责人
                .appointUserName(assignDTO.getOperatorUserName())
                // 附件
                .assignFilePath(assignDTO.getFilepath())
                .build();

        // 修改状态
        //更新响应时间,区分是否是调度过来的
        Date faultExternalTime = getFaultExternalTime(faultCode);
        if (ObjectUtil.isNotNull(faultExternalTime)) {
            long responseDuration = DateUtil.between(fault.getAssignTime(), faultExternalTime, DateUnit.SECOND);
            fault.setResponseDuration((int) responseDuration);
        } else {
            long responseDuration = DateUtil.between(fault.getAssignTime(), fault.getApprovalPassTime(), DateUnit.SECOND);
            fault.setResponseDuration((int) responseDuration);
        }
        updateById(fault);

        // 保存维修记录
        repairRecordService.save(record);


        // 日志记录
        saveLog(user, "指派 " + loginUser.getRealname(), faultCode, FaultStatusEnum.ASSIGN.getStatus(), null);

        // 更新待办任务, 指派的
        todoBaseApi.updateTodoTaskState(TodoBusinessTypeEnum.FAULT_ASSIGN.getType(), faultCode, user.getUsername(), "1");

        // 重新写任务，指派人
        // sendTodo(faultCode, null, assignDTO.getOperatorUserName(), "故障维修任务", TodoBusinessTypeEnum.FAULT_DEAL.getType());
        //发送通知
        try {
            MessageDTO messageDTO = new MessageDTO(user.getUsername(),loginUser.getUsername(), "故障指派" + DateUtil.today(), null);
            FaultMessageDTO faultMessageDTO = new FaultMessageDTO();
            BeanUtil.copyProperties(fault,faultMessageDTO);
            //业务类型，消息类型，消息模板编码，摘要，发布内容
            faultMessageDTO.setBusType(SysAnnmentTypeEnum.FAULT.getType());
            messageDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
            messageDTO.setMsgAbstract("有一个新的故障维修任务");
            messageDTO.setPublishingContent("有一个新的故障维修任务，请尽快确认");
            messageDTO.setIsRingBell(true);
            sendMessage(messageDTO,faultMessageDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 领取工单
     *
     * @param assignDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receive(AssignDTO assignDTO) {
        LoginUser user = checkLogin();
        String faultCode = assignDTO.getFaultCode();
        Fault fault = isExist(faultCode);

        LambdaUpdateWrapper<FaultRepairRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FaultRepairRecord::getDelFlag, CommonConstant.DEL_FLAG_1).eq(FaultRepairRecord::getFaultCode, fault.getCode());
        repairRecordService.update(updateWrapper);

        // 更新状态
        fault.setStatus(FaultStatusEnum.RECEIVE.getStatus());
        fault.setAppointUserName(user.getUsername());

        // 维修记录
        FaultRepairRecord record = FaultRepairRecord.builder()
                // 做类型
                .workType(assignDTO.getCaWorkCode())
                .planOrderCode(assignDTO.getPlanCode())
                .faultCode(fault.getCode())
                .planOrderImg(assignDTO.getFilepath())
                // 负责人
                .appointUserName(assignDTO.getOperatorUserName())
                // 故障想象
                .faultPhenomenon(fault.getFaultPhenomenon())
                .delFlag(CommonConstant.DEL_FLAG_0)
                // 领取时间
                .receviceTime(new Date())
                // 附件
                .assignFilePath(assignDTO.getFilepath())
                .build();
        fault.setReceiveTime(record.getReceviceTime());
        //更新响应时间,区分是否是调度过来的
        Date faultExternalTime = getFaultExternalTime(faultCode);
        if (ObjectUtil.isNotNull(faultExternalTime)) {
            long responseDuration = DateUtil.between(fault.getReceiveTime(), faultExternalTime, DateUnit.SECOND);
            fault.setResponseDuration((int) responseDuration);
        } else {
            long responseDuration = DateUtil.between(fault.getReceiveTime(), fault.getApprovalPassTime(), DateUnit.SECOND);
            fault.setResponseDuration((int) responseDuration);
        }
        updateById(fault);

        repairRecordService.save(record);

        // 日志记录
        saveLog(user, FaultStatusEnum.RECEIVE.getMessage(), faultCode, FaultStatusEnum.RECEIVE.getStatus(), null);

        // 更新工班长指派的任务
        todoBaseApi.updateTodoTaskState(TodoBusinessTypeEnum.FAULT_ASSIGN.getType(), faultCode, user.getUsername(), "1");
        // 发送消息，告诉工班长已指派, // 工班长
       // sendMessage(user, faultCode, fault.getAssignUserName(), String.format("故障【%s】已被【%s】领取!", faultCode, user.getRealname()));
        String receiveUserName = getUserNameByOrgCodeAndRoleCode(Collections.singletonList(RoleConstant.FOREMAN), fault.getMajorCode(), fault.getSubSystemCode(), fault.getStationCode(),user.getOrgCode());

        try {
            FaultMessageDTO faultMessageDTO = new FaultMessageDTO();
            BeanUtil.copyProperties(fault,faultMessageDTO);

            //发送通知
            MessageDTO messageDTO = new MessageDTO(user.getUsername(),receiveUserName, "故障领取" + DateUtil.today(), null);

            //业务类型，消息类型，消息模板编码，摘要，发布内容
            faultMessageDTO.setBusType(SysAnnmentTypeEnum.FAULT.getType());
            messageDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
            messageDTO.setMsgAbstract("故障被主动领取");
            messageDTO.setPublishingContent("故障被"+user.getRealname()+"主动领取");
            sendMessage(messageDTO,faultMessageDTO);

            // 维修待办
            TodoDTO todoDTO = new TodoDTO();
            todoDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
            todoDTO.setTitle("故障维修");
            todoDTO.setMsgAbstract("故障被主动领取");
            todoDTO.setPublishingContent("故障被主动领取，维修人请尽快维修，并维修后填写维修记录");
            sendTodo(faultCode, null, assignDTO.getOperatorUserName(), "故障维修任务", TodoBusinessTypeEnum.FAULT_DEAL.getType(),todoDTO,faultMessageDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 接收指派
     *
     * @param code
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void receiveAssignment(String code) {
        LoginUser loginUser = checkLogin();

        Fault fault = isExist(code);

        // 状态-已接收
        fault.setStatus(FaultStatusEnum.RECEIVE_ASSIGN.getStatus());

        // 修改维修记录的领取时间
        FaultRepairRecord repairRecord = getFaultRepairRecord(code, loginUser);

        if (Objects.isNull(repairRecord)) {
            throw new AiurtBootException("您没有权限接收改工单！");
        }

        repairRecord.setReceviceTime(new Date());
        fault.setReceiveTime(repairRecord.getReceviceTime());
        updateById(fault);

        repairRecordService.updateById(repairRecord);

        saveLog(loginUser, "接收指派", code, FaultStatusEnum.RECEIVE_ASSIGN.getStatus(), null);

        //发送通知
        try {
            MessageDTO messageDTO = new MessageDTO(loginUser.getUsername(), fault.getAssignUserName(), "故障维修" + DateUtil.today(), null);
            FaultMessageDTO faultMessageDTO = new FaultMessageDTO();
            BeanUtil.copyProperties(fault,faultMessageDTO);
            //业务类型，消息类型，消息模板编码，摘要，发布内容
            faultMessageDTO.setBusType(SysAnnmentTypeEnum.FAULT.getType());
            /*messageDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
            messageDTO.setMsgAbstract("接收到新的故障维修任务");
            messageDTO.setPublishingContent("接收到新的故障维修任务，请尽快维修，并维修后填写维修记录");

            sendMessage(messageDTO,faultMessageDTO);*/
            // 待办任务
            TodoDTO todoDTO = new TodoDTO();
            todoDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
            todoDTO.setTitle("故障接收");
            todoDTO.setMsgAbstract("接收到新的故障维修任务");
            todoDTO.setPublishingContent("接收到新的故障维修任务，请尽快维修，并维修后填写维修记录");
            sendTodo(code, null, loginUser.getUsername(), "故障维修任务", TodoBusinessTypeEnum.FAULT_DEAL.getType(),todoDTO,faultMessageDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 拒绝接收指派
     *
     * @param refuseAssignmentDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void refuseAssignment(RefuseAssignmentDTO refuseAssignmentDTO) {
        LoginUser loginUser = checkLogin();
        String faultCode = refuseAssignmentDTO.getFaultCode();
        Fault fault = isExist(faultCode);

        FaultRepairRecord repairRecord = getFaultRepairRecord(refuseAssignmentDTO.getFaultCode(), loginUser);

        if (Objects.nonNull(repairRecord)) {
            repairRecord.setRefuseAssignTime(new Date());
            repairRecord.setRefuseAssignRemark(refuseAssignmentDTO.getRefuseRemark());
        }



        // 状态-已审批待指派
        fault.setStatus(FaultStatusEnum.APPROVAL_PASS.getStatus());
        fault.setAppointUserName(null);

        updateById(fault);

        // repairRecordService.updateById(repairRecord);

        // 设置状态
        saveLog(loginUser, "拒绝接收指派", faultCode, FaultStatusEnum.APPROVAL_PASS.getStatus(), refuseAssignmentDTO.getRefuseRemark());


        // 更新待处理的人任务
        // todoBaseApi.updateTodoTaskState(TodoBusinessTypeEnum.FAULT_DEAL.getType(), faultCode, loginUser.getUsername(), "1");
        try {
            FaultMessageDTO faultMessageDTO = new FaultMessageDTO();
            BeanUtil.copyProperties(fault,faultMessageDTO);
            HashMap<String, Object> map = new HashMap<>();
            map.put("refuseRemark", refuseAssignmentDTO.getRefuseRemark());

            // 仅需要发送消息，不需要更新待办
            TodoDTO todoDTO = new TodoDTO();
            todoDTO.setData(map);
            todoDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE_RETURN);
            todoDTO.setTitle("故障退回");
            todoDTO.setMsgAbstract("指派故障被退回");
            todoDTO.setPublishingContent("指派的维修任务被退回，请尽快重新指派");
            sendTodo(refuseAssignmentDTO.getFaultCode(), RoleConstant.FOREMAN, null, "故障重新指派", TodoBusinessTypeEnum.FAULT_ASSIGN.getType(),todoDTO,faultMessageDTO);
            /*// 消息通知，发送给指派人
            MessageDTO messageDTO = new MessageDTO(loginUser.getUsername(), fault.getAssignUserName(), "故障退回" + DateUtil.today(), null);

            messageDTO.setData(map);
            //业务类型，消息类型，消息模板编码，摘要，发布内容
            faultMessageDTO.setBusType(SysAnnmentTypeEnum.FAULT.getType());
            messageDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE_RETURN);
            messageDTO.setMsgAbstract("指派故障被退回");
            messageDTO.setPublishingContent("指派的维修任务被退回，请尽快重新指派");

            sendMessage(messageDTO,faultMessageDTO);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 开始维修
     *
     * @param code
     */
    @Override
    public void startRepair(String code) {
        LoginUser user = checkLogin();

        Fault fault = isExist(code);
        // 开始维修时间
        fault.setStatus(FaultStatusEnum.REPAIR.getStatus());

        FaultRepairRecord repairRecord = getFaultRepairRecord(code, user);

        // 维修记录
        repairRecord.setStartTime(new Date());

        // 故障单状态
        updateById(fault);

        // 更新时间
        repairRecordService.updateById(repairRecord);

        // 记录日志
        saveLog(user, "开始维修", code, FaultStatusEnum.REPAIR.getStatus(), null);

        // 发送给指派人
        String receiveUserName = getUserNameByOrgCodeAndRoleCode(Collections.singletonList(RoleConstant.FOREMAN), fault.getMajorCode(), fault.getSubSystemCode(), fault.getStationCode(),fault.getSysOrgCode());


        // 消息通知，发送给指派人
        try {
            MessageDTO messageDTO = new MessageDTO(user.getUsername(), receiveUserName, "开始维修" + DateUtil.today(), null);
            FaultMessageDTO faultMessageDTO = new FaultMessageDTO();
            BeanUtil.copyProperties(fault,faultMessageDTO);
            //业务类型，消息类型，消息模板编码，摘要，发布内容
            faultMessageDTO.setBusType(SysAnnmentTypeEnum.FAULT.getType());
            messageDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
            messageDTO.setMsgAbstract("开始维修");
            messageDTO.setPublishingContent("开始维修");

            sendMessage(messageDTO,faultMessageDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 挂起申请
     *
     * @param hangUpDTO
     */
    @Override
    public void hangUp(HangUpDTO hangUpDTO) {

        LoginUser user = checkLogin();

        Fault fault = isExist(hangUpDTO.getFaultCode());

        fault.setStatus(FaultStatusEnum.HANGUP_REQUEST.getStatus());
        // 挂起原因

        fault.setHangUpReason(hangUpDTO.getHangUpReason());

        FaultRepairRecord repairRecord = getFaultRepairRecord(hangUpDTO.getFaultCode(), user);

        repairRecord.setHangupReason(hangUpDTO.getHangUpReason());
        repairRecord.setReqHangupTime(new Date());

        updateById(fault);

        repairRecordService.updateById(repairRecord);

        saveLog(user, "申请挂起", hangUpDTO.getFaultCode(), FaultStatusEnum.HANGUP_REQUEST.getStatus(), hangUpDTO.getHangUpReason());

        // 更新工班长指派的任务
        todoBaseApi.updateTodoTaskState(TodoBusinessTypeEnum.FAULT_DEAL.getType(), hangUpDTO.getFaultCode(), user.getUsername(), "1");

        // 生产调度挂起审核
        try {
            FaultMessageDTO faultMessageDTO = new FaultMessageDTO();
            BeanUtil.copyProperties(fault,faultMessageDTO);
            HashMap<String, Object> map = new HashMap<>();
            map.put("hangUpReason", fault.getHangUpReason());

            TodoDTO todoDTO = new TodoDTO();
            todoDTO.setData(map);
            todoDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE_HANGUP);
            todoDTO.setTitle("故障挂起审核");
            todoDTO.setMsgAbstract("故障挂起申请");
            todoDTO.setPublishingContent("故障挂起申请，请确认");

            sendTodo(fault.getCode(), RoleConstant.PRODUCTION, null, "故障挂起审核", TodoBusinessTypeEnum.FAULT_HANG_UP.getType(),todoDTO,faultMessageDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 审批挂起
     *
     * @param approvalHangUpDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvalHangUp(ApprovalHangUpDTO approvalHangUpDTO) {
        LoginUser user = checkLogin();
        String faultCode = approvalHangUpDTO.getFaultCode();
        Fault fault = isExist(faultCode);

        FaultRepairRecord faultRepairRecord = getFaultRepairRecord(faultCode, user);

        // 通过的状态 = 1
        Integer status = 1;
        Integer approvalStatus = approvalHangUpDTO.getApprovalStatus();
        boolean flag = Objects.isNull(approvalStatus) || status.equals(approvalStatus);
        if (flag) {
            // 审批通过-挂起
            fault.setStatus(FaultStatusEnum.HANGUP.getStatus());
            saveLog(user, "挂起审批通过", faultCode, FaultStatusEnum.HANGUP.getStatus(), null);
        } else {
            // 驳回-维修中
            fault.setStatus(FaultStatusEnum.REPAIR.getStatus());
            fault.setApprovalRejection(approvalHangUpDTO.getApprovalRejection());
            saveLog(user, "挂起审批驳回", faultCode, FaultStatusEnum.REPAIR.getStatus(), approvalHangUpDTO.getApprovalRejection());
        }

        faultRepairRecord.setApprovalHangUpRemark(approvalHangUpDTO.getApprovalRejection());
        faultRepairRecord.setApprovalHangUpResult(approvalHangUpDTO.getApprovalStatus());
        faultRepairRecord.setApprovalHangUpTime(new Date());
        faultRepairRecord.setApprovalHangUpUser(user.getUsername());

        // 更新数据库
        updateById(fault);

        repairRecordService.updateById(faultRepairRecord);

        // 更新工班长指派的任务
        todoBaseApi.updateTodoTaskState(TodoBusinessTypeEnum.FAULT_HANG_UP.getType(), faultCode, user.getUsername(), "1");

        try {
            if (flag) {
                // 消息通知，发送给指派人
                MessageDTO messageDTO = new MessageDTO(user.getUsername(), faultRepairRecord.getAppointUserName(), "故障挂起审核通过" + DateUtil.today(), null);
                FaultMessageDTO faultMessageDTO = new FaultMessageDTO();
                BeanUtil.copyProperties(fault,faultMessageDTO);
                //业务类型，消息类型，消息模板编码，摘要，发布内容
                faultMessageDTO.setBusType(SysAnnmentTypeEnum.FAULT.getType());
                messageDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
                messageDTO.setMsgAbstract("挂起申请");
                messageDTO.setPublishingContent("故障挂起申请已通过");
                sendMessage(messageDTO,faultMessageDTO);

                // 消息通知，发送给报修人
                messageDTO.setToUser(fault.getFaultApplicant());
                messageDTO.setIsRingBell(true);
                sendMessage(messageDTO,faultMessageDTO);

            }else {
                FaultMessageDTO faultMessageDTO = new FaultMessageDTO();
                BeanUtil.copyProperties(fault,faultMessageDTO);
                HashMap<String, Object> map = new HashMap<>();
                map.put("approvalRejection",approvalHangUpDTO.getApprovalRejection());

                // 维修待办
                TodoDTO todoDTO = new TodoDTO();
                todoDTO.setData(map);
                todoDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE_REJECT);
                todoDTO.setTitle("故障挂起审核驳回");
                todoDTO.setMsgAbstract("挂起申请被驳回");
                todoDTO.setPublishingContent("您申请的故障挂起申请被驳回，关联故障编号："+faultCode);
                sendTodo(faultCode, null, faultRepairRecord.getAppointUserName(), "故障维修任务", TodoBusinessTypeEnum.FAULT_DEAL.getType(),todoDTO,faultMessageDTO);

                // 消息通知，发送给指派人
               /* MessageDTO messageDTO = new MessageDTO(user.getUsername(), faultRepairRecord.getAppointUserName(), "故障挂起审核驳回" + DateUtil.today(), null);

                messageDTO.setData(map);
                //业务类型，消息类型，消息模板编码，摘要，发布内容
                faultMessageDTO.setBusType(SysAnnmentTypeEnum.FAULT.getType());
                messageDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE_REJECT);
                messageDTO.setMsgAbstract("挂起申请被驳回");
                messageDTO.setPublishingContent("您申请的故障挂起申请被驳回，关联故障编号："+faultCode);

                sendMessage(messageDTO,faultMessageDTO);*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消挂起
     *
     * @param code 故障编码
     */
    @Override
    public void cancelHangup(String code) {

        LoginUser loginUser = checkLogin();

        Fault fault = isExist(code);

        // 更新状态-维修中
        fault.setStatus(FaultStatusEnum.REPAIR.getStatus());
        // 挂起时间

        updateById(fault);

        //
        FaultRepairRecord faultRepairRecord = getFaultRepairRecord(code, loginUser);

        Date reqHangupTime = faultRepairRecord.getReqHangupTime();

        long between = DateUtil.between(reqHangupTime, new Date(), DateUnit.SECOND);

        saveLog(loginUser, "取消挂起", code, FaultStatusEnum.REPAIR.getStatus(), null, between);

        todoBaseApi.updateTodoTaskState(TodoBusinessTypeEnum.FAULT_HANG_UP.getType(), code, null, "1");

        // 维修待办
        try {
            FaultMessageDTO faultMessageDTO = new FaultMessageDTO();
            BeanUtil.copyProperties(fault,faultMessageDTO);

            TodoDTO todoDTO = new TodoDTO();
            todoDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
            todoDTO.setTitle("取消挂起");
            todoDTO.setMsgAbstract("挂起申请取消");
            todoDTO.setPublishingContent("挂起申请取消");
            sendTodo(code, null, fault.getAppointUserName(), "故障维修任务", TodoBusinessTypeEnum.FAULT_DEAL.getType(),todoDTO,faultMessageDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void saveLog(LoginUser loginUser, String context, String faultCode, Integer status, String remark, long between) {
        OperationProcess operationProcess = OperationProcess.builder()
                .processLink(context)
                .processTime(new Date())
                .faultCode(faultCode)
                .processPerson(loginUser.getUsername())
                .processCode(status)
                .remark(remark)
                .hangUpTime(between == 0 ? 1 : between)
                .build();
        operationProcessService.save(operationProcess);
    }

    /**
     * 查询故障记录详情
     *
     * @param faultCode 故障编码
     * @return
     */
    @Override
    public RepairRecordDTO queryRepairRecord(String faultCode) {
        LoginUser loginUser = checkLogin();

        Fault fault = isExist(faultCode);

        LambdaQueryWrapper<FaultRepairRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultRepairRecord::getFaultCode, faultCode)
                //.eq(FaultRepairRecord::getAppointUserName, loginUser.getUsername())
                .eq(FaultRepairRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
                .orderByDesc(FaultRepairRecord::getCreateTime).last("limit 1");
        FaultRepairRecord repairRecord = repairRecordService.getBaseMapper().selectOne(wrapper);

        if (Objects.isNull(repairRecord)) {
            throw new AiurtBootException(20001, "没有该维修记录");
        }

        RepairRecordDTO repairRecordDTO = new RepairRecordDTO();
        BeanUtils.copyProperties(repairRecord, repairRecordDTO);

        repairRecordDTO.setStationCode(fault.getStationCode());
        repairRecordDTO.setStationPositionCode(fault.getStationPositionCode());
        // 查询参与人
        List<FaultRepairParticipants> participantsList = repairParticipantsService.queryParticipantsByRecordId(repairRecord.getId());
        repairRecordDTO.setParticipantsList(participantsList);
        List<String> list = participantsList.stream().map(FaultRepairParticipants::getUserName).collect(Collectors.toList());
        List<String> userNameList = participantsList.stream().map(FaultRepairParticipants::getRealName).collect(Collectors.toList());
        repairRecordDTO.setUsers(StrUtil.join(",", list));
        repairRecordDTO.setUserNames(StrUtil.join(",", userNameList));
        List<DeviceChangeSparePart> deviceChangeSparePartList = sparePartService.queryDeviceChangeByFaultCode(faultCode, repairRecord.getId());
        // 易耗品 1是易耗
        List<SparePartStockDTO> consumableList = deviceChangeSparePartList.stream().filter(sparepart -> StrUtil.equalsIgnoreCase("1", sparepart.getConsumables()))
                .map(sparepart -> {
                    SparePartStockDTO build = SparePartStockDTO.builder()
                            .deviceCode(sparepart.getDeviceCode())
                            .materialCode(sparepart.getNewSparePartCode())
                            .newSparePartCode(sparepart.getNewSparePartCode())
                            .newSparePartName(sparepart.getNewSparePartName())
                            .name(sparepart.getNewSparePartName())
                            .id(sparepart.getId())
                            .repairRecordId(sparepart.getRepairRecordId())
                            .specifications(sparepart.getSpecifications())
                            .newSparePartNum(sparepart.getNewSparePartNum())
                            .newSparePartSplitCode(sparepart.getNewSparePartSplitCode())
                            .lendOutOrderId(sparepart.getLendOutOrderId())
                            .warehouseCode(sparepart.getWarehouseCode())
                            .build();
                    return build;
                }).collect(Collectors.toList());
        repairRecordDTO.setConsumableList(consumableList);

        List<SparePartStockDTO> deviceChangeList = deviceChangeSparePartList.stream().filter(sparepart -> StrUtil.equalsIgnoreCase("0", sparepart.getConsumables()))
                .map(sparepart -> {
                    SparePartStockDTO build = SparePartStockDTO.builder()
                            .deviceCode(sparepart.getDeviceCode())
                            .newSparePartCode(sparepart.getNewSparePartCode())
                            .name(sparepart.getNewSparePartName())
                            .materialCode(sparepart.getNewSparePartCode())
                            .newSparePartName(sparepart.getNewSparePartName())
                            .oldSparePartCode(sparepart.getOldSparePartCode())
                            .oldSparePartName(sparepart.getOldSparePartName())
                            .deviceCode(sparepart.getDeviceCode())
                            .deviceName(sparepart.getDeviceName())
                            .specifications(sparepart.getSpecifications())
                            .newSparePartNum(sparepart.getNewSparePartNum())
                            .id(sparepart.getId())
                            .repairRecordId(sparepart.getRepairRecordId())
                            .newSparePartSplitCode(sparepart.getNewSparePartSplitCode())
                            .lendOutOrderId(sparepart.getLendOutOrderId())
                            .warehouseCode(sparepart.getWarehouseCode())
                            .build();
                    return build;
                }).collect(Collectors.toList());

        repairRecordDTO.setDeviceChangeList(deviceChangeList);
        // 维修设备
        List<FaultDevice> faultDeviceList = faultDeviceService.queryByFaultCode(faultCode);
        repairRecordDTO.setDeviceList(faultDeviceList);

        // 指派时间
        if (Objects.isNull(repairRecordDTO.getAssignTime())) {
            repairRecordDTO.setAssignTime(repairRecord.getReceviceTime());
        }

        // 解决方案
        repairRecordDTO.setKnowledgeId(fault.getKnowledgeId());
        String knowledgeId = fault.getKnowledgeId();
        String knowledgeBaseIds = fault.getKnowledgeBaseIds();
        List<String> split = StrUtil.split(knowledgeBaseIds, ',');
        if (CollectionUtil.isEmpty(split)) {
            repairRecordDTO.setTotal(0L);
        } else {
            repairRecordDTO.setTotal((long) split.size());
        }

        if (StrUtil.isNotBlank(knowledgeId)){
            FaultKnowledgeBase base = faultKnowledgeBaseService.getById(repairRecordDTO.getKnowledgeId());

            repairRecordDTO.setFaultAnalysis(base.getFaultReason());
            repairRecordDTO.setMaintenanceMeasures(base.getSolution());
        }

//        one.setFaultAnalysis(repairRecordDTO.getFaultAnalysis());
//        one.setMaintenanceMeasures(repairRecordDTO.getMaintenanceMeasures());
        return repairRecordDTO;
    }

    /**
     * 填写维修记录
     *
     * @param repairRecordDTO 填写维修记录
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void fillRepairRecord(RepairRecordDTO repairRecordDTO) {

        LoginUser loginUser = checkLogin();

        String faultCode = repairRecordDTO.getFaultCode();

        Fault fault = isExist(faultCode);

        FaultRepairRecord one = repairRecordService.getById(repairRecordDTO.getId());

        String userIds = repairRecordDTO.getUsers();

        // 删除参与人员
        repairParticipantsService.removeByRecordId(one.getId());

        if (StrUtil.isNotBlank(userIds)) {
            List<LoginUser> userList = sysBaseAPI.queryUserByNames(StrUtil.split(userIds, ","));
            List<FaultRepairParticipants> participantsList = userList.stream().map(user -> {
                FaultRepairParticipants participants = new FaultRepairParticipants();
                participants.setFaultRepairRecordId(one.getId());
                participants.setUserName(user.getUsername());
                participants.setFaultCode(faultCode);
                return participants;
            }).collect(Collectors.toList());
            repairParticipantsService.saveBatch(participantsList);
        }
        // 故障现象
        fault.setSymptoms(repairRecordDTO.getSymptoms());
        // 设备
        fault.setDeviceCodes(repairRecordDTO.getDeviceCodes());
        dealDevice(fault, repairRecordDTO.getDeviceList());
        // 故障报修单站点和位置信息
        fault.setLineCode(repairRecordDTO.getLineCode());
        fault.setStationCode(repairRecordDTO.getStationCode());
        fault.setStationPositionCode(repairRecordDTO.getStationPositionCode());
        //判断是否要删除
        repairRecordDTO.getDeviceChangeList();
        //非|是易耗品
       //sysBaseAPI.addSparePartOutOrder(repairRecordDTO.getNonConsumablesList());
      //  Map<String, Integer> updateMap = buildSparePartNumMap(repairRecordDTO, faultCode);

        // 更新备件出库未使用的数量
        List<SparePartStockDTO> list = repairRecordDTO.getDeviceChangeList();
        list.addAll(repairRecordDTO.getConsumableList());
        sparePartBaseApi.addSparePartOutOrder(list,faultCode);
        //sparePartBaseApi.updateSparePartOutOrder(updateMap);

        one.setProcessing(repairRecordDTO.getProcessing());
        one.setArriveTime(repairRecordDTO.getArriveTime());
        one.setWorkTicketCode(repairRecordDTO.getWorkTicketCode());
        one.setWorkTickPath(repairRecordDTO.getWorkTickPath());
        // 工作票图片
        one.setSolveStatus(repairRecordDTO.getSolveStatus());
        one.setUnSloveRemark(repairRecordDTO.getUnSloveRemark());
        one.setFilePath(repairRecordDTO.getFilePath());
        one.setFaultAnalysis(repairRecordDTO.getFaultAnalysis());
        one.setMaintenanceMeasures(repairRecordDTO.getMaintenanceMeasures());
        one.setSymptoms(repairRecordDTO.getSymptoms());

        // 如果是提交未解决, 0
        Integer assignFlag = repairRecordDTO.getAssignFlag();
        // 解决状态，1已解决， 0为解决
        Integer solveStatus = repairRecordDTO.getSolveStatus();
        Integer flag = 1;

        FaultMessageDTO faultMessageDTO = new FaultMessageDTO();
        BeanUtil.copyProperties(fault,faultMessageDTO);

        // 未解决，需要重新指派
        if (!flag.equals(solveStatus) && flag.equals(assignFlag)) {
            // 重新指派
            fault.setStatus(FaultStatusEnum.APPROVAL_PASS.getStatus());
            one.setEndTime(new Date());


            // 重新指派
            // 仅需要发送消息，不需要更新待办
            try {
                TodoDTO todoDTO = new TodoDTO();
                todoDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
                todoDTO.setTitle("故障指派");
                todoDTO.setMsgAbstract("有一个新的故障维修任务");
                todoDTO.setPublishingContent("有一个新的故障维修任务，请尽快确认");
                sendTodo(faultCode, RoleConstant.FOREMAN, null, "故障重新指派", TodoBusinessTypeEnum.FAULT_ASSIGN.getType(),todoDTO,faultMessageDTO);
                //String name = getUserNameByOrgCodeAndRoleCode(Collections.singletonList(RoleConstant.FOREMAN), null, null, null);

                /*//发送通知
                MessageDTO messageDTO = new MessageDTO(loginUser.getUsername(),name, "故障指派" + DateUtil.today(), null);

                //业务类型，消息类型，消息模板编码，摘要，发布内容
                faultMessageDTO.setBusType(SysAnnmentTypeEnum.FAULT.getType());
                messageDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
                messageDTO.setMsgAbstract("有一个新的故障维修任务");
                messageDTO.setPublishingContent("有一个新的故障维修任务，请尽快确认");

                sendMessage(messageDTO,faultMessageDTO);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 已解决
        SysParamModel submitParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_AUDIT);
        boolean submitValue = "1".equals(submitParamModel.getValue());
        if (flag.equals(solveStatus)) {
            Date date = new Date();
            fault.setEndTime(date);
            fault.setDuration(DateUtil.between(fault.getReceiveTime(), fault.getEndTime(), DateUnit.MINUTE));
            one.setEndTime(date);
            if(submitValue){
                fault.setStatus(FaultStatusEnum.RESULT_CONFIRM.getStatus());
                // 审核
                try {
                    TodoDTO todoDTO = new TodoDTO();
                    todoDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
                    todoDTO.setTitle("维修待审核");
                    todoDTO.setMsgAbstract("维修待审核");
                    todoDTO.setPublishingContent("故障维修完成待审核");
                    sendTodo(faultCode, RoleConstant.FOREMAN, null, "故障维修结果待审核", TodoBusinessTypeEnum.FAULT_RESULT.getType(),todoDTO,faultMessageDTO);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                fault.setStatus(FaultStatusEnum.Close.getStatus());
                noAudit(faultCode);
            }
            //推送数据到调度系统
            faultExternalService.complete(repairRecordDTO,one.getEndTime(),loginUser);
        }

        // 使用的解决方案
        fault.setKnowledgeId(repairRecordDTO.getKnowledgeId());

        one.setKnowledgeId(repairRecordDTO.getKnowledgeId());
        //是否需要自动提交签名（通信需要、站台门不需要）
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_SUBMIT_SIGNATURE);
        boolean value = "1".equals(paramModel.getValue());
        if(value){
            LoginUser user = sysBaseAPI.getUserById(loginUser.getId());
            one.setSignPath(user.getSignatureUrl());
        }else {
            one.setSignPath(repairRecordDTO.getSignPath());
        }

        //更新维修时长,区分是否是调度过来的
        int repairDuration1 = fault.getRepairDuration() != null ? fault.getRepairDuration() : 0;
        long repairDuration = DateUtil.between(one.getEndTime(), one.getReceviceTime(), DateUnit.SECOND);
        fault.setRepairDuration((int) repairDuration + repairDuration1);
        one.setRepairDuration((int) repairDuration);

        repairRecordService.updateById(one);

        //更新故障时长,更新解决时长,区分是否是调度过来的
        Date faultExternalTime = getFaultExternalTime(faultCode);
        if (ObjectUtil.isNotNull(faultExternalTime)) {
            long faultDuration = DateUtil.between(fault.getEndTime(), faultExternalTime, DateUnit.SECOND);
            fault.setFaultDuration((int) faultDuration);
            fault.setResolutionDuration((int) faultDuration);
        } else {
            long faultDuration = DateUtil.between(fault.getEndTime(), fault.getApprovalPassTime(), DateUnit.SECOND);
            fault.setFaultDuration((int) faultDuration);
            fault.setResolutionDuration((int) faultDuration);
        }
        updateById(fault);

        // 备件更换记录
     /*   sparePartBaseApi.updateSparePartReplace(list);

        sparePartBaseApi.updateSparePartMalfunction(malfunctionList);*/

        if(submitValue){
            saveLog(loginUser, "填写维修记录", faultCode, FaultStatusEnum.REPAIR.getStatus(), null);
        }

        todoBaseApi.updateTodoTaskState(TodoBusinessTypeEnum.FAULT_DEAL.getType(), faultCode, loginUser.getUsername(), "1");

    }


    /**
     *  统计实际的出库量以及更新故障组件更换记录device_change_spare_part
     * @param repairRecordDTO
     * @param faultCode
     * @return
     */
//    private Map<String, Integer> buildSparePartNumMap(RepairRecordDTO repairRecordDTO, String faultCode) {
//        LambdaQueryWrapper<DeviceChangeSparePart> dataWrapper = new LambdaQueryWrapper<>();
//        dataWrapper.eq(DeviceChangeSparePart::getCode, faultCode);
//        List<DeviceChangeSparePart> oneSourceList = sparePartService.list(dataWrapper);
//
//        // 不能简单删除， 对比，修改出库的实际使用数量
//        List<DeviceChangeDTO> deviceChangeList = repairRecordDTO.getDeviceChangeList();
//        Map<String, Integer> updateMap = new HashMap<>(16);
//        if (CollectionUtil.isNotEmpty(deviceChangeList)) {
//
//            // key-> 主键id_出库单id_物资编码， value： 使用的数量
//            Map<String, Integer> map = oneSourceList.stream().collect(Collectors.toMap(sparepart -> {
//                return String.format("%s_%s_%s", sparepart.getId(), sparepart.getOutOrderId(), sparepart.getNewSparePartCode());
//            }, DeviceChangeSparePart::getNewSparePartNum, (t1, t2) -> t1));
//
//            Map<String, DeviceChangeSparePart> sparePartMap = oneSourceList.stream().collect(Collectors.toMap(DeviceChangeSparePart::getId, t -> t, (t1, t2) -> t1));
//
//            Set<String> recordIdSet = deviceChangeList.stream().filter(s -> StrUtil.isNotBlank(s.getId())).map(DeviceChangeDTO::getId).collect(Collectors.toSet());
//            List<DeviceChangeSparePart> sparePartList = deviceChangeList.stream().map(deviceChangeDTO -> {
//                // 原纪录id,
//                String dtoId = deviceChangeDTO.getId();
//                // 出库单
//                String outOrderId = deviceChangeDTO.getOutOrderId();
//                // 物资编码
//                String newSparePartCode = deviceChangeDTO.getNewSparePartCode();
//                // 数量
//                Integer newSparePartNum = deviceChangeDTO.getNewSparePartNum();
//
//                // 新增数据
//                if (StrUtil.isNotBlank(dtoId)) {
//                    Integer mapNum = updateMap.getOrDefault(dtoId, 0);
//                    updateMap.put(outOrderId, mapNum + newSparePartNum);
//                } else {
//                    // 修改数据
//                    String key = String.format("%s_%s_%s", dtoId, outOrderId, newSparePartCode);
//                    Integer orignNum = map.getOrDefault(key, 0);
//                    Integer mapNum = updateMap.getOrDefault(outOrderId, 0);
//                    updateMap.put(outOrderId, (newSparePartNum - orignNum) + mapNum);
//                }
//
//                DeviceChangeSparePart build = DeviceChangeSparePart.builder()
//                        .code(faultCode)
//                        .consumables("0")
//                        .deviceCode(deviceChangeDTO.getDeviceCode())
//                        .newSparePartCode(newSparePartCode)
//                        .newSparePartNum(newSparePartNum)
//                        .repairRecordId(dtoId)
//                        .id(dtoId)
//                        .oldSparePartNum(deviceChangeDTO.getOldSparePartNum())
//                        .oldSparePartCode(deviceChangeDTO.getOldSparePartCode())
//                        .delFlag(CommonConstant.DEL_FLAG_0)
//                        .outOrderId(outOrderId)
//                        .build();
//                return build;
//            }).collect(Collectors.toList());
//
//            // 删除的数据
//
//            Set<String> set = sparePartMap.keySet();
//            set.removeAll(recordIdSet);
//            if (CollectionUtil.isNotEmpty(set)) {
//                set.stream().forEach(id -> {
//                    DeviceChangeSparePart deviceChangeSparePart = sparePartMap.get(id);
//                    if (Objects.nonNull(deviceChangeSparePart)) {
//                        String outOrderId = deviceChangeSparePart.getOutOrderId();
//                        Integer num = Optional.ofNullable(deviceChangeSparePart.getNewSparePartNum()).orElse(0);
//                        Integer mapNum = updateMap.getOrDefault(outOrderId, 0);
//                        updateMap.put(outOrderId, mapNum + (0 - num));
//                    }
//                });
//            }
//
//            // 删除
//            if (CollectionUtil.isNotEmpty(set)) {
//                sparePartService.removeBatchByIds(set);
//            }
//            // 更新备件更换记录
//            sparePartService.saveOrUpdateBatch(sparePartList);
//            //
//        } else {
//            oneSourceList.stream().forEach(deviceChangeSparePart -> {
//                String id = deviceChangeSparePart.getOutOrderId();
//                Integer newSparePartNum = deviceChangeSparePart.getNewSparePartNum();
//                Integer mapNum = updateMap.getOrDefault(id, 0);
//                if (Objects.nonNull(newSparePartNum)) {
//                    updateMap.put(id, mapNum + (0 - newSparePartNum));
//                }
//            });
//            // s
//            sparePartService.remove(dataWrapper);
//        }
//        return updateMap;
//    }

    /**
     * 审核结果
     *
     * @param resultDTO 审核结果对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvalResult(ApprovalResultDTO resultDTO) {

        LoginUser loginUser = checkLogin();

        String faultCode = resultDTO.getFaultCode();

        Fault fault = isExist(faultCode);

        Integer approvalStatus = resultDTO.getApprovalStatus();

        Integer flag = 1;

        if (flag.equals(approvalStatus)) {
            fault.setStatus(FaultStatusEnum.Close.getStatus());
            // 修改备件, 更改状态
            LambdaQueryWrapper<DeviceChangeSparePart> dataWrapper = new LambdaQueryWrapper<>();
            dataWrapper.eq(DeviceChangeSparePart::getCode, faultCode).eq(DeviceChangeSparePart::getConsumables,0);
            List<DeviceChangeSparePart> oneSourceList = sparePartService.list(dataWrapper);
            // 处理备件
            if (CollectionUtil.isNotEmpty(oneSourceList)) {
                List<DeviceChangeSparePartDTO> dataList = new ArrayList<>();
                oneSourceList.stream().forEach(deviceChangeSparePart -> {
                    DeviceChangeSparePartDTO dto = new DeviceChangeSparePartDTO();
                    BeanUtils.copyProperties(deviceChangeSparePart, dto);
                    dataList.add(dto);
                });
                try {
                    sparePartBaseApi.dealChangeSparePart(dataList);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }

            saveLog(loginUser, "维修结果审核通过", faultCode, FaultStatusEnum.Close.getStatus(), resultDTO.getApprovalRejection());
            Set<String> userNameSet = new HashSet<>();
            userNameSet.add(fault.getAppointUserName());
            userNameSet.add(fault.getReceiveUserName());
            if (StrUtil.isNotBlank(fault.getApprovalUserName())) {
                userNameSet.add(fault.getApprovalUserName());
            }
            String remindUserName = fault.getRemindUserName();
            String faultApplicant = fault.getFaultApplicant();
            if (StrUtil.isNotBlank(remindUserName)) {
                List<String> list = StrUtil.split(remindUserName, ',');
                userNameSet.addAll(list);
            }
            if (StrUtil.isNotBlank(faultApplicant)) {
                List<String> list = StrUtil.split(faultApplicant, ',');
                userNameSet.addAll(list);
            }
            String name = getUserNameByOrgCodeAndRoleCode(Collections.singletonList(RoleConstant.FOREMAN), null, null, null,null);
            if (StrUtil.isNotBlank(name)) {
                List<String> list = StrUtil.splitTrim(",", name);
                userNameSet.addAll(list);
            }
            //  发送消息
            try {
                MessageDTO messageDTO = new MessageDTO(loginUser.getUsername(), CollUtil.join(userNameSet,",") , "维修完成" + DateUtil.today(), null);
                FaultMessageDTO faultMessageDTO = new FaultMessageDTO();
                BeanUtil.copyProperties(fault,faultMessageDTO);
                //业务类型，消息类型，消息模板编码，摘要，发布内容
                faultMessageDTO.setBusType(SysAnnmentTypeEnum.FAULT.getType());
                messageDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
                messageDTO.setMsgAbstract("维修完成");
                messageDTO.setPublishingContent("故障维修确认无误");

                sendMessage(messageDTO,faultMessageDTO);

                // 消息通知，发送给报修人
                messageDTO.setToUser(fault.getFaultApplicant());
                messageDTO.setIsRingBell(true);
                sendMessage(messageDTO,faultMessageDTO);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                FaultMessageDTO faultMessageDTO = new FaultMessageDTO();
                BeanUtil.copyProperties(fault,faultMessageDTO);
                HashMap<String, Object> map = new HashMap<>();
                map.put("approvalRejection",resultDTO.getApprovalRejection());

                TodoDTO todoDTO = new TodoDTO();
                todoDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE_REJECT);
                todoDTO.setTitle("维修确认驳回");
                todoDTO.setMsgAbstract("维修确认被驳回");
                todoDTO.setPublishingContent("故障维修确认被退回，请重新处理");
                todoDTO.setData(map);

                // FaultRepairRecord faultRepairRecord = getFaultRepairRecord(faultCode, null);
                fault.setStatus(FaultStatusEnum.REPAIR.getStatus());
                saveLog(loginUser, "维修结果驳回", faultCode, FaultStatusEnum.REPAIR.getStatus(), resultDTO.getApprovalRejection());
                // 审核
                sendTodo(faultCode, null, fault.getAppointUserName(), "故障维修处理", TodoBusinessTypeEnum.FAULT_DEAL.getType(),todoDTO,faultMessageDTO);

                /*MessageDTO messageDTO = new MessageDTO(loginUser.getUsername(), fault.getAppointUserName(), "维修确认驳回" + DateUtil.today(), null);

                messageDTO.setData(map);
                //业务类型，消息类型，消息模板编码，摘要，发布内容
                faultMessageDTO.setBusType(SysAnnmentTypeEnum.FAULT.getType());
                messageDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE_REJECT);
                messageDTO.setMsgAbstract("维修确认被驳回");
                messageDTO.setPublishingContent("故障维修确认被退回，请重新处理");

                sendMessage(messageDTO,faultMessageDTO);*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        updateById(fault);

        todoBaseApi.updateTodoTaskState(TodoBusinessTypeEnum.FAULT_RESULT.getType(), faultCode, loginUser.getUsername(), "1");
    }
    /**
     * 不需要工班长审核
     *
     * @param faultCode 故障编号
     */
    @Transactional(rollbackFor = Exception.class)
    public void noAudit(String faultCode) {
        LoginUser loginUser = checkLogin();
        // 修改备件, 更改状态
        LambdaQueryWrapper<DeviceChangeSparePart> dataWrapper = new LambdaQueryWrapper<>();
        dataWrapper.eq(DeviceChangeSparePart::getCode, faultCode).eq(DeviceChangeSparePart::getConsumables,0);
        List<DeviceChangeSparePart> oneSourceList = sparePartService.list(dataWrapper);
        // 处理备件
        if (CollectionUtil.isNotEmpty(oneSourceList)) {
            List<DeviceChangeSparePartDTO> dataList = new ArrayList<>();
            oneSourceList.stream().forEach(deviceChangeSparePart -> {
                DeviceChangeSparePartDTO dto = new DeviceChangeSparePartDTO();
                BeanUtils.copyProperties(deviceChangeSparePart, dto);
                dataList.add(dto);
            });
            try {
                sparePartBaseApi.dealChangeSparePart(dataList);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        saveLog(loginUser, "维修完成", faultCode, FaultStatusEnum.Close.getStatus(), null);
        todoBaseApi.updateTodoTaskState(TodoBusinessTypeEnum.FAULT_RESULT.getType(), faultCode, loginUser.getUsername(), "1");
    }
    /**
     * 查询工作类型
     *
     * @param faultCode
     * @return
     */
    @Override
    public List<CsWork> queryCsWork(String faultCode) {
        Fault fault = isExist(faultCode);
        String majorCode = fault.getMajorCode();
        List<CsWork> csWorkList = baseMapper.queryCsWorkByMajorCode(null);
        if (CollectionUtil.isEmpty(csWorkList)) {
            return Collections.emptyList();
        }
        return csWorkList;
    }

    /**
     * 查询人员
     *
     * @param fault
     * @return
     */
    @Override
    public List<LoginUser> queryUser(Fault fault) {
        LoginUser loginUser = checkLogin();
        List<String> orgCodeList = new ArrayList<>();
        if (!loginUser.getRoleCodes().contains("admin")) {
            //根据当前登录人所拥有的部门权限查人员
            List<CsUserDepartModel> departByUserId = sysBaseAPI.getDepartByUserId(loginUser.getId());

            if (CollectionUtil.isEmpty(departByUserId)) {
                return Collections.emptyList();
            }
            orgCodeList = departByUserId.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(orgCodeList)) {
                return Collections.emptyList();
            }
        }else {
            List<SysDepartModel> allSysDepart = sysBaseAPI.getAllSysDepart();
            if (CollectionUtil.isEmpty(allSysDepart)) {
                return Collections.emptyList();
            }
            orgCodeList = allSysDepart.stream().map(SysDepartModel::getOrgCode).collect(Collectors.toList());
            if (CollectionUtil.isEmpty(orgCodeList)) {
                return Collections.emptyList();
            }

        }
        // 当前登录人的部门权限和任务的站点通过工区关联的部门的交集
        List<String> departs = sysBaseAPI.getWorkAreaByCode(fault.getStationCode())
                .stream()
                .flatMap(csWorkAreaModel -> csWorkAreaModel.getOrgCodeList().stream())
                .collect(Collectors.toList());

        List<String> intersectOrg = new ArrayList<>(CollectionUtil.intersection(orgCodeList, departs));
        if (CollectionUtil.isEmpty(intersectOrg)) {
            return Collections.emptyList();
        }
        List<LoginUser> loginUserList = sysBaseAPI.getUserByDepIds(orgCodeList);
        // 根据配置决定是否关联排班
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_SCHEDULING);
        boolean value = "1".equals(paramModel.getValue());
        if (value) {
            // 获取今日当班人员信息
            List<SysUserTeamDTO> todayOndutyDetail = baseApi.getTodayOndutyDetailNoPage(intersectOrg, new Date());
            if (CollectionUtil.isEmpty(todayOndutyDetail)) {
                return Collections.emptyList();
            }
            List<String> userIds = todayOndutyDetail.stream().map(SysUserTeamDTO::getUserId).collect(Collectors.toList());
            // 过滤仅在今日当班的待指派人员
            loginUserList = loginUserList.stream().filter(l -> userIds.contains(l.getId())).collect(Collectors.toList());
        }
        return loginUserList;
    }

    /**
     * @param faultKnowledgeBase
     * @return
     */
    @Override
    public KnowledgeDTO queryKnowledge(FaultKnowledgeBase faultKnowledgeBase) {
        String deviceCode = faultKnowledgeBase.getDeviceCode();
        /*if (StrUtil.isBlank(deviceCode)) {
            log.info("设备编码有空, 不推荐使用故障知识库");
            return new KnowledgeDTO();
        }*/
        String faultPhenomenon = faultKnowledgeBase.getFaultPhenomenon();
        log.info("分词解析前数据：{}", faultPhenomenon);

        if (StrUtil.isBlank(faultPhenomenon)) {
            return new KnowledgeDTO();
        }
        List<String> deviceCodeList = StrUtil.splitTrim(deviceCode, ',');
        faultKnowledgeBase.setDeviceCodeList(deviceCodeList);
        // 分词
        Result parse = ToAnalysis.parse(faultPhenomenon);
        List<Term> termList = parse.getTerms();
        Set<String> set = termList.stream().map(Term::getName).filter(name -> name.length() > 1).collect(Collectors.toSet());
        if (CollectionUtil.isNotEmpty(set)) {
            String matchName = StrUtil.join(" ", set);
            log.info("分词解析后的数据：{}", matchName);

            faultKnowledgeBase.setMatchName(matchName);
            faultKnowledgeBase.setFaultPhenomenon(null);
        }

        List<String> list = baseMapper.queryKnowledge(faultKnowledgeBase);
        KnowledgeDTO knowledgeDTO = new KnowledgeDTO();
        knowledgeDTO.setKnowledgeIds(StrUtil.join(",", list));
        knowledgeDTO.setTotal((long) list.size());

        return knowledgeDTO;
    }


    @Override
    public IPage<FaultKnowledgeBase> pageList(Page<FaultKnowledgeBase> page, FaultKnowledgeBase knowledgeBase) {
        String faultPhenomenon = knowledgeBase.getFaultPhenomenon();
        log.info("分词解析前数据：{}", faultPhenomenon);
        if (StrUtil.isNotBlank(faultPhenomenon)) {
            // 分词
            Result parse = ToAnalysis.parse(faultPhenomenon);
            List<Term> termList = parse.getTerms();
            Set<String> set = termList.stream().map(Term::getName).filter(name -> name.length() > 1).collect(Collectors.toSet());

            if (CollectionUtil.isNotEmpty(set)) {
                String matchName = StrUtil.join(" ", set);
                log.info("分词解析后的数据：{}", matchName);
                knowledgeBase.setMatchName(matchName);
                knowledgeBase.setFaultPhenomenon(null);
            }
        }
        String id = knowledgeBase.getId();
        String deviceCode = knowledgeBase.getDeviceCode();

        if (StrUtil.isNotBlank(id)) {
            knowledgeBase.setIdList(StrUtil.split(id, ','));
        } else if (StrUtil.isNotBlank(deviceCode)) {
            List<String> deviceCodeList = StrUtil.splitTrim(deviceCode, ',');
            knowledgeBase.setDeviceCodeList(deviceCodeList);
        }


        List<FaultKnowledgeBase> baseList = baseMapper.pageList(page, knowledgeBase);
        page.setRecords(baseList);
        return page;
    }

    @Override
    public void confirmDevice(ConfirmDeviceDTO confirmDeviceDTO) {
        Fault fault = isExist(confirmDeviceDTO.getFaultCode());
        dealDevice(fault, confirmDeviceDTO.getFaultDeviceList());
    }

    /**
     * @param faultCode
     * @param knowledgeId
     */
    @Override
    public void useKnowledgeBase(String faultCode, String knowledgeId) {
        Fault fault = isExist(faultCode);
        // 使用的解决方案
        LambdaUpdateWrapper<Fault> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Fault::getKnowledgeId, knowledgeId).eq(Fault::getCode, faultCode);
        update(updateWrapper);
    }

    /**
     * 提审
     * @param faultCode 编码
     */
    @Override
    public void submitResult(String faultCode) {
        // update status
        LambdaUpdateWrapper<Fault> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Fault::getStatus, FaultStatusEnum.NEW_FAULT.getStatus()).eq(Fault::getCode, faultCode);
        update(updateWrapper);
        Fault fault = isExist(faultCode);
        try {
            FaultMessageDTO faultMessageDTO = new FaultMessageDTO();
            BeanUtil.copyProperties(fault,faultMessageDTO);

            TodoDTO todoDTO = new TodoDTO();
            todoDTO.setTemplateCode(CommonConstant.FAULT_SERVICE_NOTICE);
            todoDTO.setTitle("故障上报审核");
            todoDTO.setMsgAbstract("有新的故障信息");
            todoDTO.setPublishingContent("有新的故障信息，请尽快安排维修");

            // 待办任务
            sendTodo(faultCode, RoleConstant.PRODUCTION, null, "故障上报审核", TodoBusinessTypeEnum.FAULT_APPROVAL.getType(),todoDTO,faultMessageDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveResult(Fault fault) {
        log.info("修改故障工单：[{}]", JSON.toJSONString(fault));

        isExist(fault.getCode());

        LoginUser loginUser = checkLogin();

        dealDevice(fault, fault.getFaultDeviceList());

        updateById(fault);

        // 记录日志
        saveLog(loginUser, "修改故障工单", fault.getCode(), FaultStatusEnum.APPROVAL_REJECT.getStatus(), null);


    }

    /**
     * 获取当前登录用户
     */
    private LoginUser checkLogin() {

        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        if (Objects.isNull(user)) {
            throw new AiurtBootException("请重新登录");
        }

        return user;
    }

    /**
     * 根据编码判断故障单是否存在
     *
     * @param code 故障编码
     * @return
     */
    private Fault isExist(String code) {

        Fault fault = baseMapper.selectByCode(code);

        if (Objects.isNull(fault)) {
            throw new AiurtBootException("故障工单不存在");
        }

        return fault;
    }

    /**
     * 保存日志
     *
     * @param user      用户
     * @param context   日志内容
     * @param faultCode 故障编码
     * @param status    状态
     */
    private void saveLog(LoginUser user, String context, String faultCode, int status, String remark) {
        OperationProcess operationProcess = OperationProcess.builder()
                .processLink(context)
                .processTime(new Date())
                .faultCode(faultCode)
                .processPerson(user.getUsername())
                .processCode(status)
                .remark(remark)
                .build();
        operationProcessService.save(operationProcess);
    }

    /**
     * 设备处理
     *
     * @param fault
     * @param faultDeviceList 故障设备列表
     */
    private void dealDevice(Fault fault, List<FaultDevice> faultDeviceList) {
        if (StrUtil.isNotBlank(fault.getDeviceCodes())) {
            List<FaultDevice> deviceList = StrUtil.split(fault.getDeviceCodes(), ',').stream().map(deviceCode -> {
                FaultDevice faultDevice = new FaultDevice();
                faultDevice.setDeviceCode(deviceCode);
                return faultDevice;
            }).collect(Collectors.toList());
            faultDeviceList = deviceList;
        }

        // 删除旧设备
        LambdaQueryWrapper<FaultDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultDevice::getFaultCode, fault.getCode());
        faultDeviceService.remove(wrapper);


        if (CollectionUtil.isNotEmpty(faultDeviceList)) {

            faultDeviceList.stream().forEach(faultDevice -> {
                faultDevice.setDeviceId(faultDevice.getId());
                faultDevice.setId(null);
                faultDevice.setDelFlag(0);
                faultDevice.setFaultCode(fault.getCode());
                faultDevice.setCreateTime(new Date());
            });

            // 保存设备信息
            faultDeviceService.saveBatch(faultDeviceList);
        }
    }

    /**
     * 查询当前人员的维修记录
     *
     * @param code
     * @param user
     * @return
     */
    private FaultRepairRecord getFaultRepairRecord(String code, LoginUser user) {
        LambdaQueryWrapper<FaultRepairRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultRepairRecord::getFaultCode, code)
                //.eq(FaultRepairRecord::getAppointUserName, user.getUsername())
                .eq(FaultRepairRecord::getDelFlag, CommonConstant.DEL_FLAG_0)
                .orderByDesc(FaultRepairRecord::getCreateTime).last("limit 1");
        return repairRecordService.getBaseMapper().selectOne(wrapper);
    }

    /**
     * 根据部门，角色编码查询人员账号
     *
     * @param roleCode 角色编码
     * @return 人员账号用逗号隔开
     */
    private String getUserNameByOrgCodeAndRoleCode(List<String> roleCode,String majorCode, String subSystemCode, String stationCode,String sysOrgCode) {
        if (CollUtil.isEmpty(roleCode)) {
            return "";
        }
        List<String> result = baseMapper.selectUserNameByComplex(roleCode, majorCode, subSystemCode, stationCode,sysOrgCode);
        /*if (CollUtil.isEmpty(result)) {
            result = baseMapper.selectUserNameByComplex(roleCode, null, null, null,null);
        }*/
        return CollUtil.isNotEmpty(result) ? StrUtil.join(",", result) : "";
    }

    /**
     * 发送消息
     * @param messageDTO
     * @param faultMessageDTO
     */
    private void sendMessage(MessageDTO messageDTO,FaultMessageDTO faultMessageDTO) {
        //发送通知
        //构建消息模板
        HashMap<String, Object> map = new HashMap<>();
        if (CollUtil.isNotEmpty(messageDTO.getData())) {
            map.putAll(messageDTO.getData());
        }
        map.put("code",faultMessageDTO.getCode());
        String faultLevel = sysBaseAPI.translateDictFromTable("fault_level", "name","code",faultMessageDTO.getFaultLevel());
        map.put("faultLevel",faultLevel);
        String faultUrgency = sysBaseAPI.translateDict("fault_urgency", Convert.toStr(faultMessageDTO.getUrgency()));
        map.put("urgency",faultUrgency);
        String faultType = sysBaseAPI.translateDictFromTable("fault_type","name","code", faultMessageDTO.getFaultTypeCode());
        map.put("faultTypeCode",faultType);
        String faultModeCode = sysBaseAPI.translateDict("fault_mode_code", faultMessageDTO.getFaultModeCode());
        map.put("faultModeCode",faultModeCode);

        String line = sysBaseAPI.getPosition(faultMessageDTO.getLineCode());
        String station = sysBaseAPI.getPosition(faultMessageDTO.getStationCode());
        String position = sysBaseAPI.getPosition(faultMessageDTO.getStationPositionCode());
        String faultStationPosition = line + station;
        if (StrUtil.isNotBlank(position)) {
            faultStationPosition = faultStationPosition + position;
        }
        map.put("faultStationPosition",faultStationPosition);

        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, faultMessageDTO.getCode());
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, faultMessageDTO.getBusType());
        messageDTO.setData(map);
        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_MESSAGE);
        messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
        messageDTO.setPriority("L");
        messageDTO.setStartTime(new Date());
        messageDTO.setCategory(CommonConstant.MSG_CATEGORY_6);
        sysBaseAPI.sendTemplateMessage(messageDTO);
    }

    /**获取设备所在站点对应关联的班组的当前时间有排班的人*/
    private List<LoginUser> getUserByWorkArea(String stationCode) {

        // 根据配置决定是否关联排班
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_SCHEDULING);
        boolean value = "1".equals(paramModel.getValue());

        List<LoginUser> users = new ArrayList<>();
        List<CsWorkAreaModel> workAreaByCode = sysBaseAPI.getWorkAreaByCode(stationCode);
        if (CollUtil.isNotEmpty(workAreaByCode)) {
            for (CsWorkAreaModel csWorkAreaModel : workAreaByCode) {
                List<String> orgCodeList = csWorkAreaModel.getOrgCodeList();
                List<LoginUser> userList = sysBaseAPI.getUserByDepIds(orgCodeList);
                if (value) {
                    // 获取今日当班人员信息
                    List<SysUserTeamDTO> todayOndutyDetail = baseApi.getTodayOndutyDetailNoPage(orgCodeList, new Date());
                    if (CollectionUtil.isEmpty(todayOndutyDetail)) {
                        return Collections.emptyList();
                    }
                    List<String> userIds = todayOndutyDetail.stream().map(SysUserTeamDTO::getUserId).collect(Collectors.toList());
                    // 过滤仅在今日当班的人员
                    if (CollUtil.isNotEmpty(userList)) {
                        List<LoginUser> list = userList.stream().filter(l -> userIds.contains(l.getId())).collect(Collectors.toList());
                        users.addAll(list);
                    }

                } else {
                    users.addAll(userList);
                }
            }
        }
        return users;
    }


    /**获取设备所在站点对应关联的班组的工班长*/
    private List<String> getForemanByWorkArea(String stationCode) {
        List<String> users = new ArrayList<>();
        List<CsWorkAreaModel> workAreaByCode = sysBaseAPI.getWorkAreaByCode(stationCode);
        if (CollUtil.isNotEmpty(workAreaByCode)) {
            for (CsWorkAreaModel csWorkAreaModel : workAreaByCode) {
                List<String> orgCodeList = csWorkAreaModel.getOrgCodeList();
                for (String s : orgCodeList) {
                    List<String> result = baseMapper.selectUserNameByComplex(StrUtil.split(RoleConstant.FOREMAN, ','), null, null, null, s);
                    users.addAll(result);
                }
            }
        }
        return users;
    }

    private void sendInfo(Fault fault,List<FaultDevice> faultDeviceList) {
        LoginUser user = checkLogin();
        if (StrUtil.isNotBlank(fault.getDeviceCodes())) {
            List<FaultDevice> deviceList = StrUtil.split(fault.getDeviceCodes(), ',').stream().map(deviceCode -> {
                FaultDevice faultDevice = new FaultDevice();
                faultDevice.setDeviceCode(deviceCode);
                return faultDevice;
            }).collect(Collectors.toList());
            faultDeviceList = deviceList;
        }

        List<String> faults = new ArrayList<>();
        if (CollUtil.isNotEmpty(faultDeviceList)) {
            for (FaultDevice faultDevice : faultDeviceList) {
                LambdaQueryWrapper<FaultDevice> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(FaultDevice::getDeviceCode, faultDevice.getDeviceCode());
                List<FaultDevice> devices = faultDeviceService.list(wrapper);
                if (CollUtil.isNotEmpty(devices)) {
                    List<String> list = devices.stream().map(FaultDevice::getFaultCode).collect(Collectors.toList());
                    faults.addAll(list);
                }
            }

            if (CollUtil.isNotEmpty(faults)) {

                LambdaQueryWrapper<Fault> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Fault::getFaultPhenomenon, fault.getFaultPhenomenon());
                Date happenTime = fault.getHappenTime();
                DateTime newDate3 = DateUtil.offsetHour(happenTime, -29);
                wrapper.between(Fault::getHappenTime, newDate3, happenTime);
                wrapper.orderByDesc(Fault::getCreateTime);
                wrapper.in(Fault::getCode, faults);
                List<Fault> list = this.list(wrapper);


                if (list.size() >= 2) {
                    // 发消息给设备所负责工区的班组的班组长
                    MessageDTO messageDTO = new MessageDTO();
                    List<String> users = getForemanByWorkArea(fault.getStationCode());
                    //构建消息模板
                    if (CollUtil.isNotEmpty(users)) {
                        List<String> collect = users.stream().distinct().collect(Collectors.toList());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.SITUATION.getType());
                        List<FaultDevice> deviceList = faultDeviceService.queryByFaultCode(fault.getCode());
                        if (CollUtil.isNotEmpty(deviceList)) {
                            String deviceNames = deviceList.stream().map(FaultDevice::getDeviceName).collect(Collectors.joining(","));
                            map.put("deviceNames",deviceNames);
                        }
                        Date thisHappenTime = fault.getHappenTime();
                        Fault lastFault = list.get(1);
                        Date lastHappenTime = lastFault.getHappenTime();
                        String line = sysBaseAPI.getPosition(fault.getLineCode());
                        String station = sysBaseAPI.getPosition(lastFault.getStationCode());

                        map.put("line",line);
                        map.put("station",station);
                        map.put("thisHappenTime",DateUtil.format(thisHappenTime,"yyyy-MM-dd HH:mm"));
                        map.put("lastHappenTime",DateUtil.format(lastHappenTime,"yyyy-MM-dd HH:mm"));
                        messageDTO.setData(map);

                        messageDTO.setTitle("设备出现重复故障");
                        messageDTO.setFromUser(user.getUsername());
                        messageDTO.setToUser(CollUtil.join(collect, ","));
                        messageDTO.setToAll(false);
                        messageDTO.setTemplateCode(CommonConstant.FAULT_SPECIAL_INFO_NOTICE);
                        SysParamModel sysParamModel = iSysParamAPI.selectByCode(SysParamCodeConstant.SPECIAL_INFO_MESSAGE);
                        messageDTO.setType(ObjectUtil.isNotEmpty(sysParamModel) ? sysParamModel.getValue() : "");
                        messageDTO.setMsgAbstract("你有一条特情消息");
                        messageDTO.setPublishingContent("你有一条特情消息");
                        messageDTO.setCategory(CommonConstant.MSG_CATEGORY_3);
                        messageDTO.setStartTime(new Date());
                        messageDTO.setLevel("0");
                        sysBaseAPI.sendTemplateMessage(messageDTO);
                    }

                }
            }

        }
    }

    //获取调度系统过来的故障
    private Date getFaultExternalTime(String code) {
        LambdaQueryWrapper<FaultExternal> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultExternal::getFaultcode, code);
        FaultExternal faultExternal = faultExternalMapper.selectOne(wrapper);
        if (ObjectUtil.isNotNull(faultExternal)) {
            return faultExternal.getCreateTime();
        }
        return null;
    }
}
