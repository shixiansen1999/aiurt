package com.aiurt.modules.system.service;

import com.aiurt.modules.system.entity.SysUserPositionCurrent;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * 用户连接站点wifi的当前位置
 * @author
 */
public interface ISysUserPositionCurrentService extends IService<SysUserPositionCurrent> {

    /**
     * 用户连接站点wifi的当前位置表 添加或者更新
     * 根据create_by查询sys_user_position_current表，如果没有添加，否则更新
     * 更新时，如果是同一个站点的，不更新upload_time
     * @param sysUserPositionCurrent
     */
    void saveOrUpdateOne(SysUserPositionCurrent sysUserPositionCurrent);
}
