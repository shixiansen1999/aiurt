package com.aiurt.modules.fault.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.fault.dto.ApprovalDTO;
import com.aiurt.modules.fault.dto.AssignDTO;
import com.aiurt.modules.fault.dto.CancelDTO;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.entity.OperationProcess;
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
        fault.setStatus(1);

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
        OperationProcess operationProcess = OperationProcess.builder()
                .processLink("故障上报")
                .processTime(new Date())
                .faultCode(fault.getCode())
                .processPerson(user.getUsername())
                .processCode(1)
                .build();
        operationProcessService.save(operationProcess);

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
            fault.setStatus(3);
            operationProcess.setProcessLink("审批通过").setProcessCode(3);
        } else {
            // 驳回
            fault.setStatus(2);
            fault.setApprovalRejection(approvalDTO.getApprovalRejection());
            operationProcess.setProcessLink("审批已驳回").setProcessCode(2);
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

        // 设备处理

        fault.setStatus(1);
        updateById(fault);
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
        fault.setStatus(0);
        //
        fault.setCancelTime(new Date());
        fault.setCancelUserName(user.getUsername());
        updateById(fault);

        // 记录日志
        OperationProcess operationProcess = OperationProcess.builder()
                .processLink("作废")
                .processTime(new Date())
                .faultCode(fault.getCode())
                .processPerson(user.getUsername())
                .processCode(0)
                .build();
        operationProcessService.save(operationProcess);
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


    }


    /**
     * 领取
     *
     * @param assignDTO
     */
    @Override
    public void receive(AssignDTO assignDTO) {

    }

    /**
     * 接收指派
     *
     * @param code
     */
    @Override
    public void receiveAssignment(String code) {

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
}
