package com.aiurt.modules.syntheticalpanel.mapper;

import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.syntheticalpanel.model.PositionPanel;
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
    List<PositionPanel> getAllWorkArea(@Param("positionPanel") PositionPanel positionPanel);

    /**
     * 查询工区关联的站点
     *
     * @param code
     * @return
     */
    List<CsStation> getStations(@Param("code") String code);

    /**
     * 通过id查询班组信息
     *
     * @param id
     * @return
     */
    List<PositionPanel>  queryById(@Param("id") String id);

    /**
     * 修改
     *
     * @param positionPanel
     * @return
     */
    void edit(@Param("positionPanel") PositionPanel positionPanel);

    /**
     * 通过id查询站点信息
     *
     * @param positionPanel
     * @return
     */
    CsStation getStation(@Param("positionPanel") PositionPanel positionPanel);

    /**
     * 通过id查询班组人员信息
     *
     * @param orgCode
     * @return
     */
    List<SysUser> getUserById(@Param("orgCode")String orgCode);
}
