package com.aiurt.modules.online.workflowapi.service;

import com.aiurt.modules.online.workflowapi.entity.ActCustomInterface;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: act_custom_interface
 * @Author: wgp
 * @Date:   2023-07-25
 * @Version: V1.0
 */
public interface IActCustomInterfaceService extends IService<ActCustomInterface> {

    /**
     * 查询接口名称在数据库是否存在
     * @param name
     * @param id
     * @return
     */
    boolean isNameExists(String name,String id);
}
