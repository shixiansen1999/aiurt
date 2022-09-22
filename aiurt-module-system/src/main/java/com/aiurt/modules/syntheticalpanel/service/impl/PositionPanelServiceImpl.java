package com.aiurt.modules.syntheticalpanel.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.syntheticalpanel.mapper.PositionPanelMapper;
import com.aiurt.modules.syntheticalpanel.model.PositionPanel;
import com.aiurt.modules.syntheticalpanel.service.PositionPanelService;
import com.aiurt.modules.system.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lkj
 */
@Service
public class PositionPanelServiceImpl implements PositionPanelService {

    @Autowired
    private PositionPanelMapper positionPanelMapper;

    @Override
    public List<PositionPanel> readAll(PositionPanel positionPanel) {
        //查询工区
        List<PositionPanel> allWorkArea = positionPanelMapper.getAllWorkArea(positionPanel);
        //查询工区关联的站点
        if (CollectionUtil.isNotEmpty(allWorkArea)) {
            for (PositionPanel panel : allWorkArea) {
                List<CsStation> stations = positionPanelMapper.getStations(panel.getCode());
                panel.setCsStationList(stations);
            }
        }
        return allWorkArea;
    }

    @Override
    public List<PositionPanel> queryById(PositionPanel positionPanel) {
        String stationName = positionPanel.getStationName();
        List<PositionPanel> positionPanels = new ArrayList<>();
        if (StrUtil.isNotEmpty(stationName)) {
            List<PositionPanel> list = positionPanelMapper.queryById(stationName);
            if (CollUtil.isNotEmpty(list)) {
                //去掉没有班组的线路
                List<PositionPanel> collect = list.stream().filter(p -> !p.getOrgCode().isEmpty()).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(collect)) {
                    for (PositionPanel panel : collect) {
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
    public void edit(PositionPanel positionPanel) {
        CsStation station = positionPanelMapper.getStation(positionPanel);
        if (positionPanel.getWarningStatus() == null) {
            positionPanel.setWarningStatus(station.getWarningStatus());
        }
        if (positionPanel.getOpenStatus() == null) {
            positionPanel.setOpenStatus(station.getOpenStatus());
        }
        positionPanelMapper.edit(positionPanel);
    }
}
