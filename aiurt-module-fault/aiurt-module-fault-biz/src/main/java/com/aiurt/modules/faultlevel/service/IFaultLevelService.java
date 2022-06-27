package com.aiurt.modules.faultlevel.service;


import com.aiurt.modules.faultlevel.entity.FaultLevel;
import com.aiurt.modules.faulttype.entity.FaultType;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: 故障等级
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
public interface IFaultLevelService extends IService<FaultLevel> {
    /**
     * 添加
     *
     * @param faultLevel
     * @return
     */
    Result<?> add(FaultLevel faultLevel);
    /**
     * 编辑
     *
     * @param faultLevel
     * @return
     */
    Result<?> update(FaultLevel faultLevel);
}
