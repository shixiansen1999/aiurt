package com.aiurt.modules.online.workflowapi.service;

import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.online.workflowapi.entity.ActCustomInterface;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: act_custom_interface
 * @Author: wgp
 * @Date: 2023-07-25
 * @Version: V1.0
 */
public interface IActCustomInterfaceService extends IService<ActCustomInterface> {

    /**
     * 查询接口名称在数据库是否存在
     *
     * @param name
     * @param id
     * @return
     */
    boolean isNameExists(String name, String id);

    /**
     * 根据接口的唯一标识符删除接口数据。
     *
     * @param id 接口的唯一标识符
     * @return 删除成功返回 true，删除失败返回 false
     */
    boolean removeInterfaceById(String id);

    /**
     * 删除模块前检查是否存在关联接口，如果存在则抛出异常。
     *
     * @param moduleIds 模块的唯一标识符集合
     * @throws AiurtBootException 如果模块下存在关联接口，则抛出异常
     */
    void checkAndThrowIfModuleHasAssociatedInterfaces(List<String> moduleIds) throws AiurtBootException;

}
