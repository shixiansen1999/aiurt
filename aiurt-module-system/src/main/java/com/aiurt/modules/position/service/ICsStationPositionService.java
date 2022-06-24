package com.aiurt.modules.position.service;

import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;

import java.util.List;

/**
 * @Description: cs_station_position
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
public interface ICsStationPositionService extends IService<CsStationPosition> {
    /**
     * 查询列表
     * @param page
     * @return
     */
    List<CsStationPosition> readAll(Page<CsStationPosition> page,CsStationPosition csStationPosition);
    /**
     * 添加
     *
     * @param csStationPosition
     * @return
     */
    Result<?> add(CsStationPosition csStationPosition);
    /**
     * 编辑
     *
     * @param csStationPosition
     * @return
     */
    Result<?> update(CsStationPosition csStationPosition);
}
