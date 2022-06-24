package com.aiurt.modules.faulttype.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.modules.faulttype.entity.FaultType;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: fault_type
 * @Author: aiurt
 * @Date:   2022-06-24
 * @Version: V1.0
 */
public interface IFaultTypeService extends IService<FaultType> {
    /**
     * 添加
     *
     * @param faultType
     * @return
     */
    Result<?> add(FaultType faultType);
    /**
     * 编辑
     *
     * @param faultType
     * @return
     */
    Result<?> update(FaultType faultType);
}
