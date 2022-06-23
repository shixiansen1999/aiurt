package com.aiurt.modules.fault.service;


import com.aiurt.modules.fault.entity.Fault;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: fault
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface IFaultService extends IService<Fault> {

    /**
     * 故障上报
     * @param fault 故障对象
     */
    String add(Fault fault);
}
