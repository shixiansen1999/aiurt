package com.aiurt.modules.system.service;

import com.aiurt.modules.system.entity.SysUserPosition;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

/**
 * GPS、wifi定位数据上报
 * @author hlq
 */
public interface ISysUserPositionService extends IService<SysUserPosition> {

    /**
     * 保存
     * @param sysUserPosition
     * @return
     */
    Result<SysUserPosition> saveOne(SysUserPosition sysUserPosition);
}
