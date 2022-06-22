package com.aiurt.modules.fault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.modules.fault.entity.ErpHelloEntity;

/**
 * 测试接口
 */
public interface IErpHelloService extends IService<ErpHelloEntity> {

    String hello();

}
