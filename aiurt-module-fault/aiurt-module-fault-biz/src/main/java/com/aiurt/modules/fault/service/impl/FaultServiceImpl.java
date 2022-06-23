package com.aiurt.modules.fault.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.fault.entity.Fault;
import com.aiurt.modules.fault.entity.FaultDevice;
import com.aiurt.modules.fault.entity.OperationProcess;
import com.aiurt.modules.fault.mapper.FaultMapper;
import com.aiurt.modules.fault.service.IFaultDeviceService;
import com.aiurt.modules.fault.service.IFaultService;
import com.aiurt.modules.fault.service.IOperationProcessService;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @Description: fault
 * @Author: aiurt
 * @Date:   2022-06-22
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
     * @param fault 故障对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String add(Fault fault) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        if (Objects.isNull(user)) {
            throw  new AiurtBootException("请重新登录");
        }

        // 故障编号处理
        String majorCode = fault.getMajorCode();
        StringBuilder builder = new StringBuilder("WX");
        builder.append(majorCode).append(DateUtil.format(new Date(), "yyyyMMddHHmm"));
        fault.setCode(builder.toString());


        // 接报人
        fault.setReceiveTime(new Date());
        fault.setReceiveUserName(user.getUsername());

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


}
