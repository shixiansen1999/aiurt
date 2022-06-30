package com.aiurt.modules.subsystem.service;

import com.aiurt.modules.subsystem.entity.CsSubsystem;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: cs_subsystem
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface ICsSubsystemService extends IService<CsSubsystem> {
    /**
     * 添加
     *
     * @param csSubsystem
     * @return
     */
    Result<?> add(CsSubsystem csSubsystem);
    /**
     * 编辑
     *
     * @param csSubsystem
     * @return
     */
    Result<?> update(CsSubsystem csSubsystem);
}
