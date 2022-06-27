package com.aiurt.modules.fault.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.fault.dto.*;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.entity.OperationProcess;
import com.aiurt.modules.fault.enums.FaultStatusEnum;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.service.IFaultDeviceService;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.fault.service.IOperationProcessService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Description: fault
 * @Author: aiurt
 * @Date: 2022-06-22
 * @Version: V1.0
 */
@Service
public class FaultServiceImpl extends ServiceImpl<FaultMapper, Fault> implements IFaultService {


    @Autowired
    private IFaultDeviceService faultDeviceService;

    @Autowired
    private IOperationProcessService operationProcessService;

    /**
     * 故障上报
     *
     * @param fault 故障对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(Fault fault) {

        LoginUser user = checkLogin();

        // 故障编号处理
        String majorCode = fault.getMajorCode();
        StringBuilder builder = new StringBuilder("WX");
        builder.append(majorCode).append(DateUtil.format(new Date(), "yyyyMMddHHmm"));
        fault.setCode(builder.toString());

        // 接报人
        fault.setReceiveTime(new Date());
        fault.setReceiveUserName(user.getUsername());

        //todo 自检自修
        String faultModeCode = fault.getFaultModeCode();

        if (StrUtil.equalsIgnoreCase(faultModeCode, "1")) {
            fault.setStatus(FaultStatusEnum.REPAIR.getStatus());

            //todo 需要给班组长发送消息
        }else {
            fault.setStatus(FaultStatusEnum.NEW_FAULT.getStatus());
        }

        // 保存故障
        save(fault);

        // 设置故障编码
        List<FaultDevice> faultDeviceList = fault.getFaultDeviceList();
        faultDeviceList.stream().forEach(faultDevice -> {
            faultDevice.setDelFlag(0);
            faultDevice.setFaultCode(fault.getCode());
        });

        // 保存故障设备
        if (CollectionUtil.isNotEmpty(faultDeviceList)) {
            faultDeviceService.saveBatch(faultDeviceList);
        }

        // 记录日志
        saveLog(user, "故障上报", fault.getCode(), 1);

        // todo 消息通知

        return builder.toString();
    }

    /**
     * 故障审批
     *
     * @param approvalDTO 审批对象
     */
    @Override
    public void approval(ApprovalDTO approvalDTO) {

        LoginUser user = checkLogin();

        Fault fault = isExist(approvalDTO.getFaultCode());

        // 通过的状态 = 1
        Integer status = 1;
        Integer approvalStatus = approvalDTO.getApprovalStatus();
        OperationProcess operationProcess = OperationProcess.builder()
                .processTime(new Date())
                .faultCode(fault.getCode())
                .processPerson(user.getUsername())
                .build();
        if (Objects.isNull(approvalStatus) || status.equals(approvalStatus)) {
            // 审批通过
            fault.setStatus(FaultStatusEnum.APPROVAL_PASS.getStatus());
            operationProcess.setProcessLink(FaultStatusEnum.APPROVAL_PASS.getMessage())
                    .setProcessCode(FaultStatusEnum.APPROVAL_PASS.getStatus());
        } else {
            // 驳回
            fault.setStatus(FaultStatusEnum.APPROVAL_REJECT.getStatus());
            fault.setApprovalRejection(approvalDTO.getApprovalRejection());
            operationProcess.setProcessLink(FaultStatusEnum.APPROVAL_REJECT.getMessage())
                    .setProcessCode(FaultStatusEnum.APPROVAL_REJECT.getStatus());
        }

        updateById(fault);

        //todo 消息发送
    }

    /**
     * 编辑
     *
     * @param fault 故障对象 @see com.aiurt.modules.fault.entity.Fault
     */
    @Override
    public void edit(Fault fault) {

        LoginUser loginUser = checkLogin();

        //todo 设备处理

        // update status
        fault.setStatus(FaultStatusEnum.NEW_FAULT.getStatus());
        updateById(fault);

        // 记录日志
        saveLog(loginUser, "修改故障工单", fault.getCode(), FaultStatusEnum.NEW_FAULT.getStatus());
    }

    /**
     * 作废
     *
     * @param cancelDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(CancelDTO cancelDTO) {
        LoginUser user = checkLogin();

        // 故障单
        Fault fault = isExist(cancelDTO.getFaultCode());

        // 作废
        fault.setStatus(FaultStatusEnum.CANCEL.getStatus());

        //更新状态
        fault.setCancelTime(new Date());
        fault.setCancelUserName(user.getUsername());
        updateById(fault);

        // 记录日志
        saveLog(user, FaultStatusEnum.CANCEL.getMessage(), fault.getCode(), FaultStatusEnum.CANCEL.getStatus());

        // todo 发送消息提醒
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
        LambdaQueryWrapper<FaultDevice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FaultDevice::getFaultCode, code);
        List<FaultDevice> faultDeviceList = faultDeviceService.getBaseMapper().selectList(wrapper);
        fault.setFaultDeviceList(faultDeviceList);

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

        Fault fault = isExist(assignDTO.getFaultCode());

        // 更新状态
        fault.setStatus(FaultStatusEnum.ASSIGN.getStatus());
        fault.setAssignTime(new Date());
        fault.setAssignUserName(user.getUsername());
        fault.setAppointUserName(assignDTO.getOperatorUserName());

        updateById(fault);

        // todo 发送消息

        // 日志记录
        saveLog(user, FaultStatusEnum.ASSIGN.getMessage(), assignDTO.getFaultCode(), FaultStatusEnum.ASSIGN.getStatus());
    }




    /**
     * 领取
     *
     * @param assignDTO
     */
    @Override
    public void receive(AssignDTO assignDTO) {
        LoginUser user = checkLogin();

        Fault fault = isExist(assignDTO.getFaultCode());

        // 更新状态
        fault.setStatus(FaultStatusEnum.RECEIVE.getStatus());
        fault.setAppointUserName(user.getUsername());
        // 领取时间

        // 日志记录
        saveLog(user, FaultStatusEnum.RECEIVE.getMessage(), assignDTO.getFaultCode(), FaultStatusEnum.RECEIVE.getStatus());

        // todo 发送消息
    }

    /**
     * 接收指派
     *
     * @param code
     */
    @Override
    public void receiveAssignment(String code) {
        LoginUser loginUser = checkLogin();

        Fault fault = isExist(code);

        fault.setStatus(FaultStatusEnum.RECEIVE_ASSIGN.getStatus());

        updateById(fault);

        saveLog(loginUser, "接收指派", code, FaultStatusEnum.RECEIVE_ASSIGN.getStatus());
        //todo 创建维修记录
    }


    /**
     * 拒绝接收指派
     * @param refuseAssignmentDTO
     */
    @Override
    public void refuseAssignment(RefuseAssignmentDTO refuseAssignmentDTO) {
        LoginUser loginUser = checkLogin();

        Fault fault = isExist(refuseAssignmentDTO.getFaultCode());

        fault.setStatus(FaultStatusEnum.APPROVAL_PASS.getStatus());

        updateById(fault);

        // 设置状态
        saveLog(loginUser, "拒绝接收指派", refuseAssignmentDTO.getFaultCode(), FaultStatusEnum.APPROVAL_PASS.getStatus());
    }

    /**
     * 开始维修
     * @param code
     */
    @Override
    public void startRepair(String code) {
        LoginUser user = checkLogin();

        Fault fault = isExist(code);
        // 开始维修时间
        fault.setStatus(FaultStatusEnum.REPAIR.getStatus());
        //
        saveLog(user, "开始维修", code, FaultStatusEnum.REPAIR.getStatus());
    }

    /**
     * 挂起申请
     * @param hangUpDTO
     */
    @Override
    public void hangUp(HangUpDTO hangUpDTO) {

        LoginUser user = checkLogin();

        Fault fault = isExist(hangUpDTO.getFaultCode());

        fault.setStatus(FaultStatusEnum.HANGUP_REQUEST.getStatus());
        // 挂起原因
        fault.setHangUpReason(hangUpDTO.getHangUpReason());

        updateById(fault);

        saveLog(user, "申请挂起", hangUpDTO.getFaultCode(), FaultStatusEnum.HANGUP_REQUEST.getStatus());

        // todo 发送消息提醒

    }

    /**
     * 审批挂起
     * @param approvalHangUpDTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvalHangUp(ApprovalHangUpDTO approvalHangUpDTO) {
        LoginUser user = checkLogin();

        Fault fault = isExist(approvalHangUpDTO.getFaultCode());

        // 通过的状态 = 1
        Integer status = 1;
        Integer approvalStatus = approvalHangUpDTO.getApprovalStatus();
        if (Objects.isNull(approvalStatus) || status.equals(approvalStatus)) {
            // 审批通过-挂起
            fault.setStatus(FaultStatusEnum.HANGUP.getStatus());
            saveLog(user, "挂起审批通过", approvalHangUpDTO.getFaultCode(), FaultStatusEnum.HANGUP.getStatus());
        } else {
            // 驳回-维修中
            fault.setStatus(FaultStatusEnum.REPAIR.getStatus());
            //todo
            fault.setApprovalRejection(approvalHangUpDTO.getApprovalRejection());
            saveLog(user, "挂起审批驳回", approvalHangUpDTO.getFaultCode(), FaultStatusEnum.REPAIR.getStatus());
        }

        updateById(fault);
        // todo 发送消息


    }

    /**
     * 取消挂起
     * @param code
     */
    @Override
    public void cancelHangup(String code) {

        LoginUser loginUser = checkLogin();

        Fault fault = isExist(code);

        // 更新状态-维修中
        fault.setStatus(FaultStatusEnum.REPAIR.getStatus());
        // 挂起时间

        updateById(fault);

        saveLog(loginUser, "取消挂起", code, FaultStatusEnum.REPAIR.getStatus());

        // todo 发送消息
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
     * @param code
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
     * @param user
     * @param context
     * @param faultCode
     * @param status
     */
    private void saveLog(LoginUser user, String context, String faultCode, int status) {
        OperationProcess operationProcess = OperationProcess.builder()
                .processLink(context)
                .processTime(new Date())
                .faultCode(faultCode)
                .processPerson(user.getUsername())
                .processCode(status)
                .build();
        operationProcessService.save(operationProcess);
    }
}
