package com.aiurt.modules.position.mapper;

import com.aiurt.modules.position.entity.CsStation;
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
     * 查询线路名
     * @param code
     * @return
     */
    @Select("select line_name from cs_line where del_flag = 0 and  line_code=#{code}")
    String getLineName(@Param("code")String code);
    /**
     * 查询站点名
     * @param code
     * @return
     */
    @Select("select station_name from cs_station where del_flag = 0 and  station_code=#{code}")
    String getStationName(@Param("code")String code);
    /**
     * 查询位置名
     * @param code
     * @return
     */
    @Select("select position_name from cs_station_position  where del_flag = 0 and  position_code=#{code}")
    String getPositionName(@Param("code")String code);
}
