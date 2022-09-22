package com.aiurt.modules.syntheticalpanel.service;

import com.aiurt.modules.syntheticalpanel.model.PositionPanel;

import java.util.List;

/**
 * @author lkj
 */
public interface PositionPanelService {

    /**
     * 综合大屏线路工区查询
     * @param positionPanel
     * @return List<PositionPanel>
     */
    List<PositionPanel> readAll(PositionPanel positionPanel);

    /**
     * 通过名称查询
     *
     * @param stationName
     * @return
     */
    List<PositionPanel> queryById(String stationName);

    /**
     *  编辑
     *
     * @param positionPanel
     * @return
     */
    void edit(PositionPanel positionPanel);
}
