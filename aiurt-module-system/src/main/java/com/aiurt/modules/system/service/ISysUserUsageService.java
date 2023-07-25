package com.aiurt.modules.system.service;

import com.aiurt.modules.system.dto.SysUserUsageRespDTO;
import com.aiurt.modules.system.entity.SysUserUsage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: 系统用户被选用频率表
 * @Author: aiurt
 * @Date:   2023-07-24
 * @Version: V1.0
 */
public interface ISysUserUsageService extends IService<SysUserUsage> {

    /**
     *  查询常用的用户信息
     *
     * @return
     */
    List<SysUserUsageRespDTO> queryList(String search);
}
