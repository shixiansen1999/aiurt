package com.aiurt.modules.manage.service;

import com.aiurt.modules.manage.entity.ActCustomVersion;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 版本管理
 * @Author: aiurt
 * @Date:   2022-07-15
 * @Version: V1.0
 */
public interface IActCustomVersionService extends IService<ActCustomVersion> {

    /**
     * 挂起流程
     * @param actCustomVersion
     */
    void suspendFlowProcess(ActCustomVersion actCustomVersion);

    /**
     * 激活流程
     * @param actCustomVersion
     */
    void activeFlowProcess(ActCustomVersion actCustomVersion);

    /**
     * 设置主版本
     * @param actCustomVersion
     */
    void updateMainVersion(ActCustomVersion actCustomVersion);
}
