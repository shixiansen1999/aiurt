package com.aiurt.modules.syntheticalpanel.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.syntheticalpanel.mapper.PositionPanelMapper;
import com.aiurt.modules.syntheticalpanel.model.PositionPanel;
import com.aiurt.modules.syntheticalpanel.service.PositionPanelService;
import com.aiurt.modules.system.entity.SysUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
        List<String> ids = positionPanel.getIds();
        List<PositionPanel> positionPanels = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(ids)) {
            for (String id : ids) {
                List<PositionPanel> list = positionPanelMapper.queryById(id);
                if (CollectionUtil.isNotEmpty(list)) {
                    for (PositionPanel panel : list) {
                        List<SysUser> userById = positionPanelMapper.getUserById(panel.getId());
                        panel.setUserList(userById);
                    }
                    positionPanels.addAll(list);
                }
                return positionPanels;
            }
        }
        return null;
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
