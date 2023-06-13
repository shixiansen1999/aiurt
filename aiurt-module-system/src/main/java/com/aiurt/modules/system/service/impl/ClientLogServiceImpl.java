package com.aiurt.modules.system.service.impl;


import com.aiurt.modules.system.entity.ClientLog;
import com.aiurt.modules.system.mapper.ClientLogMapper;
import com.aiurt.modules.system.service.IClientLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @Description: client_log
 * @Author: aiurt
 * @Date:   2023-06-12
 * @Version: V1.0
 */
@Service
public class ClientLogServiceImpl extends ServiceImpl<ClientLogMapper, ClientLog> implements IClientLogService {

}
