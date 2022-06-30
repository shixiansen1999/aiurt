package com.aiurt.modules.position.service;

import com.aiurt.modules.position.entity.CsStation;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

/**
 * @Description: cs_station
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
public interface ICsStationService extends IService<CsStation> {
    /**
     * 添加
     *
     * @param csStation
     * @return
     */
    Result<?> add(CsStation csStation);
    /**
     * 编辑
     *
     * @param csStation
     * @return
     */
    Result<?> update(CsStation csStation);
}
