package com.aiurt.boot.modules.manage.service.impl;

import com.aiurt.boot.modules.manage.entity.Station;
import com.aiurt.boot.modules.manage.mapper.StationMapper;
import com.aiurt.boot.modules.manage.service.IStationService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: cs_station
 * @Author: swsc
 * @Date:   2021-09-15
 * @Version: V1.0
 */
@Service
public class StationServiceImpl extends ServiceImpl<StationMapper, Station> implements IStationService {
    @Override
    public void updateStationDeaprt(String departId) {
        this.baseMapper.updateStationDeaprt(departId);
    }

    @Override
    public List<Integer> getIdsByLineCode(String lineCode) {
        return this.baseMapper.getIdsByLineCode(lineCode);
    }

    @Override
    public List<Station> getStationsInOrdered() {
        return this.baseMapper.getStationsInOrdered();
    }
}
