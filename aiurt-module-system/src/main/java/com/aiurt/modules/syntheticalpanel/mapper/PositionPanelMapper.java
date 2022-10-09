package com.aiurt.modules.syntheticalpanel.mapper;

import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.syntheticalpanel.model.PositionPanelModel;
import com.aiurt.modules.system.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author lkj
 */
@Component
public interface PositionPanelMapper {

    /**
     * 查询工区
     *
     * @param positionPanel
     * @return
     */
    List<PositionPanelModel> getAllWorkArea(@Param("positionPanel") PositionPanelModel positionPanel);

    /**
     * 查询工区关联的站点
     *
     * @param code
     * @return
     */
    List<CsStation> getStations(@Param("code") String code);

    /**
     * 通过站点名称查询班组信息
     *
     * @param stationName
     * @return
     */
    List<PositionPanelModel>  queryById(@Param("stationName") String stationName);

    /**
     * 修改
     *
     * @param positionPanel
     * @return
     */
    void edit(@Param("positionPanel") PositionPanelModel positionPanel);

    /**
     * 通过名字查询站点信息
     *
     * @param positionPanel
     * @return
     */
    List<CsStation> getStation(@Param("positionPanel") PositionPanelModel positionPanel);

    /**
     * 通过id查询班组人员信息
     *
     * @param orgCode
     * @return
     */
    List<SysUser> getUserById(@Param("orgCode")String orgCode);
}
