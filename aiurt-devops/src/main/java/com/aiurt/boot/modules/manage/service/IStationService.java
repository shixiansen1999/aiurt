package com.aiurt.boot.modules.manage.service;

import com.aiurt.boot.modules.manage.entity.Station;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Description: cs_station
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
public interface IStationService extends IService<Station> {
    /**
     * 根据班组id更新站点班组为空
     * @param deaprtId
     */
    void updateStationDeaprt(String deaprtId);

    List<Integer> getIdsByLineCode(String lineCode);

    List<Station> getStationsInOrdered();
}
