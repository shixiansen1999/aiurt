package com.aiurt.modules.system.mapper;

import com.aiurt.modules.system.dto.SysUserUsageRespDTO;
import com.aiurt.modules.system.entity.SysUserUsage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 系统用户被选用频率表
 * @Author: aiurt
 * @Date:   2023-07-24
 * @Version: V1.0
 */
public interface SysUserUsageMapper extends BaseMapper<SysUserUsage> {

    /**
     * 查询常用的用户信息
     * @param id
     * @param search
     * @return
     */
    List<SysUserUsageRespDTO> queryList(@Param("id") String id, @Param("search") String search);

    /**
     * 全局搜索
     * @param name
     * @return
     */
    List<SysUserUsageRespDTO> globalSearch(@Param("name") String name);
;}
