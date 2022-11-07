package com.aiurt.modules.syntheticalpanel.service;

import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.syntheticalpanel.model.PositionPanelModel;

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
    List<CsStation> readAll(PositionPanelModel positionPanel);

    /**
     * 通过名称查询
     *
     * @param stationName
     * @return
     */
    List<PositionPanelModel> queryById(String stationName);

    /**
     *  编辑
     *
     * @param positionPanel
     * @return
     */
    void edit(PositionPanelModel positionPanel);
}
