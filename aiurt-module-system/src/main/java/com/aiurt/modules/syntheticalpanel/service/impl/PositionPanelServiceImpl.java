package com.aiurt.modules.syntheticalpanel.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.syntheticalpanel.mapper.PositionPanelMapper;
import com.aiurt.modules.syntheticalpanel.model.PositionPanelModel;
import com.aiurt.modules.syntheticalpanel.service.PositionPanelService;
import com.aiurt.modules.system.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lkj
 */
@Service
public class PositionPanelServiceImpl implements PositionPanelService {

    @Autowired
    private PositionPanelMapper positionPanelMapper;

    @Override
    public List<CsStation> readAll(PositionPanelModel positionPanel) {
        List<CsStation> stations = positionPanelMapper.getStations(positionPanel);
        return stations;
    }

    @Override
    public List<PositionPanelModel> queryById(String stationName) {
        List<PositionPanelModel> positionPanels = new ArrayList<>();
        if (StrUtil.isNotEmpty(stationName)) {
            List<PositionPanelModel> list = positionPanelMapper.queryById(stationName);
            if (CollUtil.isNotEmpty(list)) {
                //去掉没有班组的线路
                List<PositionPanelModel> collect = list.stream().filter(p -> p.getOrgCode()!=null).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(collect)) {
                    for (PositionPanelModel panel : collect) {
                        List<SysUser> userById = positionPanelMapper.getUserById(panel.getOrgCode());
                        panel.setUserList(userById);
                    }
                    positionPanels.addAll(collect);
                }
            }
        }
        return positionPanels;
    }

    @Override
    public void edit(PositionPanelModel positionPanel) {
        List<CsStation> stations = positionPanelMapper.getStation(positionPanel);
        if (CollUtil.isNotEmpty(stations)) {
            for (CsStation csStation : stations) {
                if (positionPanel.getWarningStatus() == null) {
                    positionPanel.setWarningStatus(csStation.getWarningStatus());
                }
                if (positionPanel.getOpenStatus() == null) {
                    positionPanel.setOpenStatus(csStation.getOpenStatus());
                }
                positionPanel.setId(csStation.getId());
                positionPanelMapper.edit(positionPanel);
            }
        }

    }
}
