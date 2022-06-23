package com.aiurt.modules.fault.service.impl;


import com.aiurt.modules.fault.entity.OperationProcess;
import com.aiurt.modules.fault.mapper.OperationProcessMapper;
import com.aiurt.modules.fault.service.IOperationProcessService;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

/**
 * @Description: 故障操作日志
 * @Author: aiurt
 * @Date:   2022-06-23
 * @Version: V1.0
 */
@Service
public class OperationProcessServiceImpl extends ServiceImpl<OperationProcessMapper, OperationProcess> implements IOperationProcessService {

}
