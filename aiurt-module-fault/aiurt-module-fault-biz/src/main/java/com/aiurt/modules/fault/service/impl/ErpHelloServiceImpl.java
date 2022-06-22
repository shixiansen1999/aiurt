package com.aiurt.modules.fault.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.aiurt.modules.fault.entity.ErpHelloEntity;
import com.aiurt.modules.fault.mapper.ErpHelloMapper;
import com.aiurt.modules.fault.service.IErpHelloService;
import org.springframework.stereotype.Service;

/**
 * 测试Service
 */
@Service
public class ErpHelloServiceImpl extends ServiceImpl<ErpHelloMapper, ErpHelloEntity> implements IErpHelloService {

    @Override
    public String hello() {
        return "hello ，我是 erp 微服务节点!";
    }
}
