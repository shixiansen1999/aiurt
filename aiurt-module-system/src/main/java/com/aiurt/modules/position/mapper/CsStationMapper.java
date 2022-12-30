package com.aiurt.modules.position.mapper;

import com.aiurt.modules.position.entity.CsLine;
import com.aiurt.modules.position.entity.CsStation;
import com.aiurt.modules.position.entity.CsStationPosition;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * @Description: cs_station
 * @Author: aiurt
 * @Date:   2022-06-22
 * @Version: V1.0
 */
@Component
public interface CsStationMapper extends BaseMapper<CsStation> {

    /**
     * 查询线路
     * @param code
     * @return
     */
    @Select("select * from cs_line where del_flag = 0 and  line_code=#{code}")
    CsLine getLineName(@Param("code")String code);
    /**
     * 查询站点
     * @param code
     * @return
     */
    @Select("select * from cs_station where del_flag = 0 and  station_code=#{code}")
    CsStation getStationName(@Param("code")String code);
    /**
     * 查询位置
     * @param code
     * @return
     */
    @Select("select * from cs_station_position  where del_flag = 0 and  position_code=#{code}")
    CsStationPosition getPositionName(@Param("code")String code);
}
