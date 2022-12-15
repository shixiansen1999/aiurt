package com.aiurt.modules.param.service;

import com.aiurt.modules.param.dto.SysParamDTO;
import com.aiurt.modules.param.entity.SysParam;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: sys_param
 * @Author: aiurt
 * @Date: 2022-12-15
 * @Version: V1.0
 */
public interface ISysParamService extends IService<SysParam> {
    /**
     * 系统参数配置-分页列表查询
     *
     * @param page
     * @param sysParamDTO
     * @return
     */
    IPage<SysParam> queryPageList(Page<SysParam> page, SysParamDTO sysParamDTO);

    /**
     * 系统参数配置-添加
     *
     * @param sysParam
     * @return
     */
    String add(SysParam sysParam);

    /**
     * 系统参数配置-编辑
     *
     * @param sysParam
     * @return
     */
    String edit(SysParam sysParam);
}
