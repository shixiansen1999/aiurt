package com.aiurt.modules.position.mapper;

import com.aiurt.modules.position.entity.CsStationPosition;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: cs_station_position
 * @Author: jeecg-boot
 * @Date:   2022-06-21
 * @Version: V1.0
 */
@Mapper
@Component
public interface CsStationPositionMapper extends BaseMapper<CsStationPosition> {
    /**
     * 查询列表
     * @param page
     * @param csStationPosition
     * @return
     */
    List<CsStationPosition> queryCsStationPositionAll(@Param("page") Page<CsStationPosition> page,@Param("position") CsStationPosition csStationPosition);

    /**
     * 查询
     * @param id
     * @return
     */
    CsStationPosition getById(@Param("id")String id);

    /**
     * 根据位置编码获取线路站点和位置全名，格式如线路/站点/位置
     * @param positionCode
     * @return
     */
    String getFullNameByPositionCode(String positionCode);
}
