package com.aiurt.modules.fault.service.impl;
import java.util.Date;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.api.InspectionApi;
import com.aiurt.boot.constant.RoleConstant;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.boot.manager.dto.FaultCallbackDTO;
import com.aiurt.common.api.dto.message.BusMessageDTO;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.common.constant.enums.TodoBusinessTypeEnum;
import com.aiurt.common.constant.enums.TodoTaskTypeEnum;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.basic.entity.CsWork;
import com.aiurt.modules.common.api.IBaseApi;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.*;
import com.aiurt.modules.fault.enums.FaultStatusEnum;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.service.*;
import com.aiurt.modules.faultknowledgebase.entity.FaultKnowledgeBase;
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
import org.jeecg.common.system.vo.CsUserDepartModel;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ISTodoBaseAPI todoBaseApi;
    @Autowired
    private ISysParamAPI iSysParamAPI;
    /**
     * 故障上报
     *
     * @param fault 故障对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(Fault fault) {

        LoginUser user = checkLogin();
        log.info("故障上报：操作人员：[{}], 请求参数：{}", user.getRealname(), JSON.toJSONString(fault));
        // 故障编号处理
        String majorCode = fault.getMajorCode();
        StringBuilder builder = new StringBuilder("WX");
        builder.append(majorCode).append(DateUtil.format(new Date(), "yyyyMMddHHmmss"));
        fault.setCode(builder.toString());

        // 接报人
        fault.setReceiveTime(new Date());
        fault.setReceiveUserName(user.getUsername());

        String faultModeCode = fault.getFaultModeCode();

        LambdaQueryWrapper<FaultKnowledgeBaseType> queryWrapper = new LambdaQueryWrapper<>();
        FaultKnowledgeBaseType one = faultKnowledgeBaseTypeService.getOne(queryWrapper.eq(FaultKnowledgeBaseType::getCode, fault.getFaultPhenomenon()).eq(FaultKnowledgeBaseType::getDelFlag, 0));
        // 自报自修跳过
        boolean b = StrUtil.equalsIgnoreCase(faultModeCode, SELF_FAULT_MODE_CODE);
        if (b) {
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
        } else {
            fault.setStatus(FaultStatusEnum.NEW_FAULT.getStatus());
        }

        // 保存故障
        save(fault);


        // 设置故障设备
        dealDevice(fault, fault.getFaultDeviceList());


        // 记录日志
        saveLog(user, "故障上报", fault.getCode(), 1, null);

        // 待办任务
        if (b) {
            // 自检
            sendTodo(fault.getCode(), null, user.getUsername(), "故障维修任务", TodoBusinessTypeEnum.FAULT_DEAL.getType());
        } else {
            sendTodo(fault.getCode(), RoleConstant.PRODUCTION, null, "故障上报审核", TodoBusinessTypeEnum.FAULT_APPROVAL.getType());
        }

        // 回调
        if (StrUtil.isNotBlank(fault.getRepairCode())) {
            FaultCallbackDTO faultCallbackDTO = new FaultCallbackDTO();
            faultCallbackDTO.setFaultCode(fault.getCode());
            faultCallbackDTO.setSingleCode(fault.getRepairCode());
            inspectionApi.editFaultCallback(faultCallbackDTO);
        }
        return builder.toString();
    }

    /**
     * 任务池
     * @param businessKey 业务标识
     * @param roleCode 角色编码
     * @param currentUserName 用户名
     * @param taskName 任务标题
     */
    private void sendTodo(String businessKey, String roleCode, String currentUserName, String taskName,String businessType) {
        TodoDTO todoDTO = new TodoDTO();
        if (StrUtil.isNotBlank(roleCode)) {
            String userName = this.getUserNameByOrgCodeAndRoleCode(StrUtil.split(roleCode, ','));
            todoDTO.setCurrentUserName(userName);
        }else {
            todoDTO.setCurrentUserName(currentUserName);
        }

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

        if (b) {
            // 审批通过 新增任务， 该线路或者是工班长，指派任务
            sendTodo(faultCode, RoleConstant.FOREMAN, null, "故障指派", TodoBusinessTypeEnum.FAULT_ASSIGN.getType());
        } else {
            // 被驳回则发送消息
            String message = String.format("您有一条故障【%s】上报被驳回。驳回原因：%s", faultCode, approvalDTO.getApprovalRejection());
            sendMessage(user, faultCode, fault.getReceiveUserName(), message);
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

        // update status
        fault.setStatus(FaultStatusEnum.NEW_FAULT.getStatus());

        updateById(fault);

        // 记录日志
        saveLog(loginUser, "修改故障工单", fault.getCode(), FaultStatusEnum.NEW_FAULT.getStatus(), null);

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

    }

    /**
     * 根据编码查询详情
     *
     * @param code
     * @return
     */
    @Override
    public Fault queryByCode(String code) {

        Fault fault = isExist(code);
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
                .faultPhenomenon(fault.getPhenomenonTypeName())
                // 负责人
                .appointUserName(assignDTO.getOperatorUserName())
                // 附件
                .assignFilePath(assignDTO.getFilepath())
                .build();

        // 修改状态
        updateById(fault);

        // 保存维修记录
        repairRecordService.save(record);


        // 日志记录
        saveLog(user, "指派 " + loginUser.getRealname(), faultCode, FaultStatusEnum.ASSIGN.getStatus(), null);

        // 更新待办任务, 指派的
        todoBaseApi.updateTodoTaskState(TodoBusinessTypeEnum.FAULT_ASSIGN.getType(), faultCode, user.getUsername(), "1");

        // 重新写任务，指派人
        // sendTodo(faultCode, null, assignDTO.getOperatorUserName(), "故障维修任务", TodoBusinessTypeEnum.FAULT_DEAL.getType());
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
                .faultPhenomenon(fault.getPhenomenonTypeName())
                .delFlag(CommonConstant.DEL_FLAG_0)
                // 领取时间
                .receviceTime(new Date())
                // 附件
                .assignFilePath(assignDTO.getFilepath())
                .build();

        updateById(fault);

        repairRecordService.save(record);

        // 日志记录
        saveLog(user, FaultStatusEnum.RECEIVE.getMessage(), faultCode, FaultStatusEnum.RECEIVE.getStatus(), null);

        // 更新工班长指派的任务
        todoBaseApi.updateTodoTaskState(TodoBusinessTypeEnum.FAULT_ASSIGN.getType(), faultCode, user.getUsername(), "1");
        // 发送消息，告诉工班长已指派, // 工班长
        sendMessage(user, faultCode, fault.getAssignUserName(), String.format("故障【%s】已被【%s】领取!", faultCode, user.getRealname()));

        // 维修待办
        sendTodo(faultCode, null, assignDTO.getOperatorUserName(), "故障维修任务", TodoBusinessTypeEnum.FAULT_DEAL.getType());

    }

    private void sendMessage(LoginUser user, String faultCode, String receiveUserName, String s) {
        BusMessageDTO message = new BusMessageDTO();
        message.setBusType(SysAnnmentTypeEnum.FAULT.getType());
        message.setBusId(faultCode);
        message.setFromUser(user.getUsername());

        message.setToUser(receiveUserName);
        message.setToAll(false);
        message.setTitle("故障管理");
        message.setContent(s);
        message.setCategory("1");
        message.setLevel(null);
        message.setPriority("L");
        message.setStartTime(new Date());
        sysBaseAPI.sendBusAnnouncement(message);
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

        updateById(fault);

        repairRecordService.updateById(repairRecord);

        saveLog(loginUser, "接收指派", code, FaultStatusEnum.RECEIVE_ASSIGN.getStatus(), null);

        // 发送消息通知指派人
        BusMessageDTO message = new BusMessageDTO();
        message.setBusType(SysAnnmentTypeEnum.FAULT.getType());
        message.setBusId(code);
        message.setFromUser(loginUser.getUsername());
        // 工班长
        message.setToUser(fault.getAssignUserName());
        message.setToAll(false);
        message.setTitle("故障管理");
        message.setContent(String.format("故障(%s)已经被 %s 领取!", code, loginUser.getUsername()));
        message.setCategory("1");
        message.setLevel(null);
        message.setPriority("L");
        message.setStartTime(new Date());
        sysBaseAPI.sendBusAnnouncement(message);

        // 待办任务
        sendTodo(code, null, loginUser.getUsername(), "故障维修任务", TodoBusinessTypeEnum.FAULT_DEAL.getType());
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

        /*FaultRepairRecord repairRecord = getFaultRepairRecord(refuseAssignmentDTO.getFaultCode(), loginUser);

        if (Objects.isNull(repairRecord)) {
            throw new AiurtBootException("该工单您的，您没有权限拒绝接收指派工单！");
        }

        repairRecord.setRefuseAssignTime(new Date());
        repairRecord.setRefuseAssignRemark(refuseAssignmentDTO.getRefuseRemark());*/

        // 状态-已审批待指派
        fault.setStatus(FaultStatusEnum.APPROVAL_PASS.getStatus());

        updateById(fault);

        // repairRecordService.updateById(repairRecord);

        // 设置状态
        saveLog(loginUser, "拒绝接收指派", faultCode, FaultStatusEnum.APPROVAL_PASS.getStatus(), refuseAssignmentDTO.getRefuseRemark());


        // 更新待处理的人任务
        // todoBaseApi.updateTodoTaskState(TodoBusinessTypeEnum.FAULT_DEAL.getType(), faultCode, loginUser.getUsername(), "1");

        // 仅需要发送消息，不需要更新待办
        sendTodo(refuseAssignmentDTO.getFaultCode(), RoleConstant.FOREMAN, null, "故障重新指派", TodoBusinessTypeEnum.FAULT_ASSIGN.getType());

        BusMessageDTO message = new BusMessageDTO();
        message.setBusType(SysAnnmentTypeEnum.FAULT.getType());
        message.setBusId(faultCode);
        message.setFromUser(loginUser.getUsername());
        // 工班长
        message.setToUser(fault.getAssignUserName());
        message.setToAll(false);
        message.setTitle("故障管理");
        message.setContent(String.format("【%s】拒绝接收指派，请重新指派故障【%s】!",  loginUser.getUsername(), faultCode));
        message.setCategory("1");
        message.setLevel(null);
        message.setPriority("L");
        message.setStartTime(new Date());
        sysBaseAPI.sendBusAnnouncement(message);
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
        BusMessageDTO message = new BusMessageDTO();
        message.setBusType(SysAnnmentTypeEnum.FAULT.getType());
        message.setBusId(code);
        message.setFromUser(user.getUsername());
        // 工班长
        message.setToUser(fault.getAssignUserName());
        message.setToAll(false);
        message.setTitle("故障管理");
        message.setContent(String.format("【%s】开始处理故障【%s】!",  user.getUsername(), code));
        message.setCategory("1");
        message.setLevel(null);
        message.setPriority("L");
        message.setStartTime(new Date());
        sysBaseAPI.sendBusAnnouncement(message);
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
        sendTodo(fault.getCode(), RoleConstant.PRODUCTION, null, "故障挂起审核", TodoBusinessTypeEnum.FAULT_HANG_UP.getType());
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

        if (flag) {
            // 消息通知，发送给指派人
            sendMessage(user, faultCode, faultRepairRecord.getAppointUserName(), String.format("故障(%s)挂起审核已通过!", faultCode));
        }else {
            // 维修待办
            sendTodo(faultCode, null, faultRepairRecord.getAppointUserName(), "故障维修任务", TodoBusinessTypeEnum.FAULT_DEAL.getType());

            // 消息通知，发送给指派人
            sendMessage(user, faultCode, faultRepairRecord.getAppointUserName(), String.format("故障(%s)挂起审核被驳回，驳回原因：%s!", faultCode, approvalHangUpDTO.getApprovalRejection()));
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
        sendTodo(code, null, loginUser.getUsername(), "故障维修任务", TodoBusinessTypeEnum.FAULT_DEAL.getType());

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

        // 查询参与人
        List<FaultRepairParticipants> participantsList = repairParticipantsService.queryParticipantsByRecordId(repairRecord.getId());
        repairRecordDTO.setParticipantsList(participantsList);
        List<String> list = participantsList.stream().map(FaultRepairParticipants::getUserName).collect(Collectors.toList());
        List<String> userNameList = participantsList.stream().map(FaultRepairParticipants::getRealName).collect(Collectors.toList());
        repairRecordDTO.setUsers(StrUtil.join(",", list));
        repairRecordDTO.setUserNames(StrUtil.join(",", userNameList));

        List<DeviceChangeSparePart> deviceChangeSparePartList = sparePartService.queryDeviceChangeByFaultCode(faultCode, repairRecord.getId());
        // 易耗品 1是易耗
        List<DeviceChangeDTO> consumableList = deviceChangeSparePartList.stream().filter(sparepart -> StrUtil.equalsIgnoreCase("1", sparepart.getConsumables()))
                .map(sparepart -> {
                    DeviceChangeDTO build = DeviceChangeDTO.builder()
                            .deviceCode(sparepart.getDeviceCode())
                            .newSparePartCode(sparepart.getNewSparePartCode())
                            .newSparePartName(sparepart.getNewSparePartName())
                            .id(sparepart.getId())
                            .repairRecordId(sparepart.getRepairRecordId())
                            .build();
                    return build;
                }).collect(Collectors.toList());

        repairRecordDTO.setConsumableList(consumableList);

        List<DeviceChangeDTO> deviceChangeList = deviceChangeSparePartList.stream().filter(sparepart -> StrUtil.equalsIgnoreCase("0", sparepart.getConsumables()))
                .map(sparepart -> {
                    DeviceChangeDTO build = DeviceChangeDTO.builder()
                            .deviceCode(sparepart.getDeviceCode())
                            .newSparePartCode(sparepart.getNewSparePartCode())
                            .newSparePartName(sparepart.getNewSparePartName())
                            .oldSparePartCode(sparepart.getOldSparePartCode())
                            .oldSparePartName(sparepart.getOldSparePartName())
                            .deviceCode(sparepart.getDeviceCode())
                            .deviceName(sparepart.getDeviceName())
                            .specifications(sparepart.getSpecifications())
                            .newSparePartNum(sparepart.getNewSparePartNum())
                            .id(sparepart.getId())
                            .repairRecordId(sparepart.getRepairRecordId())
                            .outOrderId(sparepart.getOutOrderId())
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
        String knowledgeBaseIds = fault.getKnowledgeBaseIds();
        List<String> split = StrUtil.split(knowledgeBaseIds, ',');
        if (CollectionUtil.isEmpty(split)) {
            repairRecordDTO.setTotal(0L);
        } else {
            repairRecordDTO.setTotal((long) split.size());
        }
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

        // 设备
        fault.setDeviceCodes(repairRecordDTO.getDeviceCodes());
        dealDevice(fault, repairRecordDTO.getDeviceList());

        Map<String, Integer> updateMap = buildSparePartNumMap(repairRecordDTO, faultCode);

        // 更新备件出库未使用的数量
        sparePartBaseApi.updateSparePartOutOrder(updateMap);

        one.setArriveTime(repairRecordDTO.getArriveTime());
        one.setWorkTicketCode(repairRecordDTO.getWorkTicketCode());
        one.setWorkTickPath(repairRecordDTO.getWorkTickPath());
        // 工作票图片
        one.setSolveStatus(repairRecordDTO.getSolveStatus());
        one.setUnSloveRemark(repairRecordDTO.getUnSloveRemark());
        one.setFilePath(repairRecordDTO.getFilePath());
        one.setFaultAnalysis(repairRecordDTO.getFaultAnalysis());
        one.setMaintenanceMeasures(repairRecordDTO.getMaintenanceMeasures());

        // 如果是提交未解决, 0
        Integer assignFlag = repairRecordDTO.getAssignFlag();
        // 解决状态，1已解决， 0为解决
        Integer solveStatus = repairRecordDTO.getSolveStatus();
        Integer flag = 1;
        // 未解决，需要重新指派
        if (!flag.equals(solveStatus) && flag.equals(assignFlag)) {
            // 重新指派
            fault.setStatus(FaultStatusEnum.APPROVAL_PASS.getStatus());
            one.setEndTime(new Date());

            // 重新指派
            // 仅需要发送消息，不需要更新待办
            sendTodo(faultCode, RoleConstant.FOREMAN, null, "故障重新指派", TodoBusinessTypeEnum.FAULT_ASSIGN.getType());
        }
        // 已解决
        if (flag.equals(solveStatus)) {
            fault.setStatus(FaultStatusEnum.RESULT_CONFIRM.getStatus());
            fault.setEndTime(new Date());
            fault.setDuration(DateUtil.between(fault.getReceiveTime(), fault.getEndTime(), DateUnit.MINUTE));
            one.setEndTime(new Date());

            // 审核
            sendTodo(faultCode, null, fault.getAssignUserName(), "故障维修结果审核", TodoBusinessTypeEnum.FAULT_RESULT.getType());
        }

        // 使用的解决方案
        fault.setKnowledgeId(repairRecordDTO.getKnowledgeId());

        one.setKnowledgeId(repairRecordDTO.getKnowledgeId());
        one.setSignPath(repairRecordDTO.getSignPath());


        repairRecordService.updateById(one);

        updateById(fault);


        // 备件更换记录
     /*   sparePartBaseApi.updateSparePartReplace(list);

        sparePartBaseApi.updateSparePartMalfunction(malfunctionList);*/


        saveLog(loginUser, "填写维修记录", faultCode, FaultStatusEnum.REPAIR.getStatus(), null);

        todoBaseApi.updateTodoTaskState(TodoBusinessTypeEnum.FAULT_DEAL.getType(), faultCode, loginUser.getUsername(), "1");

    }

    /**
     *  统计实际的出库量以及更新故障组件更换记录device_change_spare_part
     * @param repairRecordDTO
     * @param faultCode
     * @return
     */
    private Map<String, Integer> buildSparePartNumMap(RepairRecordDTO repairRecordDTO, String faultCode) {
        LambdaQueryWrapper<DeviceChangeSparePart> dataWrapper = new LambdaQueryWrapper<>();
        dataWrapper.eq(DeviceChangeSparePart::getCode, faultCode);
        List<DeviceChangeSparePart> oneSourceList = sparePartService.list(dataWrapper);

        // 不能简单删除， 对比，修改出库的实际使用数量
        List<DeviceChangeDTO> deviceChangeList = repairRecordDTO.getDeviceChangeList();
        Map<String, Integer> updateMap = new HashMap<>(16);
        if (CollectionUtil.isNotEmpty(deviceChangeList)) {

            // key-> 主键id_出库单id_物资编码， value： 使用的数量
            Map<String, Integer> map = oneSourceList.stream().collect(Collectors.toMap(sparepart -> {
                return String.format("%s_%s_%s", sparepart.getId(), sparepart.getOutOrderId(), sparepart.getNewSparePartCode());
            }, DeviceChangeSparePart::getNewSparePartNum, (t1, t2) -> t1));

            Map<String, DeviceChangeSparePart> sparePartMap = oneSourceList.stream().collect(Collectors.toMap(DeviceChangeSparePart::getId, t -> t, (t1, t2) -> t1));

            Set<String> recordIdSet = deviceChangeList.stream().filter(s -> StrUtil.isNotBlank(s.getId())).map(DeviceChangeDTO::getId).collect(Collectors.toSet());
            List<DeviceChangeSparePart> sparePartList = deviceChangeList.stream().map(deviceChangeDTO -> {
                // 原纪录id,
                String dtoId = deviceChangeDTO.getId();
                // 出库单
                String outOrderId = deviceChangeDTO.getOutOrderId();
                // 物资编码
                String newSparePartCode = deviceChangeDTO.getNewSparePartCode();
                // 数量
                Integer newSparePartNum = deviceChangeDTO.getNewSparePartNum();

                // 新增数据
                if (StrUtil.isNotBlank(dtoId)) {
                    Integer mapNum = updateMap.getOrDefault(dtoId, 0);
                    updateMap.put(outOrderId, mapNum + newSparePartNum);
                } else {
                    // 修改数据
                    String key = String.format("%s_%s_%s", dtoId, outOrderId, newSparePartCode);
                    Integer orignNum = map.getOrDefault(key, 0);
                    Integer mapNum = updateMap.getOrDefault(outOrderId, 0);
                    updateMap.put(outOrderId, (newSparePartNum - orignNum) + mapNum);
                }

                DeviceChangeSparePart build = DeviceChangeSparePart.builder()
                        .code(faultCode)
                        .consumables("0")
                        .deviceCode(deviceChangeDTO.getDeviceCode())
                        .newSparePartCode(newSparePartCode)
                        .newSparePartNum(newSparePartNum)
                        .repairRecordId(dtoId)
                        .id(dtoId)
                        .oldSparePartNum(deviceChangeDTO.getOldSparePartNum())
                        .oldSparePartCode(deviceChangeDTO.getOldSparePartCode())
                        .delFlag(CommonConstant.DEL_FLAG_0)
                        .outOrderId(outOrderId)
                        .build();
                return build;
            }).collect(Collectors.toList());

            // 删除的数据

            Set<String> set = sparePartMap.keySet();
            set.removeAll(recordIdSet);
            if (CollectionUtil.isNotEmpty(set)) {
                set.stream().forEach(id -> {
                    DeviceChangeSparePart deviceChangeSparePart = sparePartMap.get(id);
                    if (Objects.nonNull(deviceChangeSparePart)) {
                        String outOrderId = deviceChangeSparePart.getOutOrderId();
                        Integer num = Optional.ofNullable(deviceChangeSparePart.getNewSparePartNum()).orElse(0);
                        Integer mapNum = updateMap.getOrDefault(outOrderId, 0);
                        updateMap.put(outOrderId, mapNum + (0 - num));
                    }
                });
            }

            // 删除
            if (CollectionUtil.isNotEmpty(set)) {
                sparePartService.removeBatchByIds(set);
            }
            // 更新备件更换记录
            sparePartService.saveOrUpdateBatch(sparePartList);
            //
        } else {
            oneSourceList.stream().forEach(deviceChangeSparePart -> {
                String id = deviceChangeSparePart.getOutOrderId();
                Integer newSparePartNum = deviceChangeSparePart.getNewSparePartNum();
                Integer mapNum = updateMap.getOrDefault(id, 0);
                if (Objects.nonNull(newSparePartNum)) {
                    updateMap.put(id, mapNum + (0 - newSparePartNum));
                }
            });
            // s
            sparePartService.remove(dataWrapper);
        }
        return updateMap;
    }

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
            dataWrapper.eq(DeviceChangeSparePart::getCode, faultCode);
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

            saveLog(loginUser, "维修结果审核通过", faultCode, FaultStatusEnum.Close.getStatus(), null);
        } else {
            fault.setStatus(FaultStatusEnum.REPAIR.getStatus());
            saveLog(loginUser, "维修结果驳回", faultCode, FaultStatusEnum.REPAIR.getStatus(), resultDTO.getApprovalRejection());
            // 审核
            sendTodo(faultCode, null, fault.getAppointUserName(), "故障维修处理", TodoBusinessTypeEnum.FAULT_RESULT.getType());
        }

        updateById(fault);

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
     * @param faultCode
     * @return
     */
    @Override
    public List<LoginUser> queryUser(String faultCode) {
        LoginUser loginUser = checkLogin();
        //根据当前登录人所拥有的部门权限查人员
        List<CsUserDepartModel> departByUserId = sysBaseAPI.getDepartByUserId(loginUser.getId());

        if (CollectionUtil.isEmpty(departByUserId)) {
            return Collections.emptyList();
        }

        List<String> orgCodeList = departByUserId.stream().map(CsUserDepartModel::getOrgCode).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(orgCodeList)) {
            return Collections.emptyList();
        }

        // 根据故障编号获取故障所属组织机构
        Fault fault = this.lambdaQuery().eq(Fault::getCode, faultCode).last("limit 1").one();
        if (ObjectUtil.isEmpty(fault)) {
            return Collections.emptyList();
        }
        // 当前登录人的部门权限和任务的组织机构交集
        List<String> intersectOrg = CollectionUtil.intersection(orgCodeList, Arrays.asList(fault.getSysOrgCode()))
                .stream().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(intersectOrg)) {
            return Collections.emptyList();
        }
        List<LoginUser> loginUserList = sysBaseAPI.getUserByDepIds(orgCodeList);
        // 根据配置决定是否关联排班
        SysParamModel paramModel = iSysParamAPI.selectByCode(SysParamCodeConstant.FAULT_SCHEDULING);
        boolean value = "1".equals(paramModel.getValue()) ? true : false;
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

        // 待办任务
        sendTodo(faultCode, RoleConstant.PRODUCTION, null, "故障上报审核", TodoBusinessTypeEnum.FAULT_APPROVAL.getType());
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
    private String getUserNameByOrgCodeAndRoleCode(List<String> roleCode) {
        if (CollUtil.isEmpty(roleCode)) {
            return "";
        }
        List<String> result = baseMapper.selectUserNameByComplex(roleCode);
        return CollUtil.isNotEmpty(result) ? StrUtil.join(",", result) : "";
    }
}
