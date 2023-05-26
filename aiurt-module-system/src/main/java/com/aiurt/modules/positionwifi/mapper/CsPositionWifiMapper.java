package com.aiurt.modules.positionwifi.mapper;

import com.aiurt.modules.positionwifi.entity.CsPositionWifi;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: wiif位置管理
 * @Author: aiurt
 * @Date:   2022-11-15
 * @Version: V1.0
 */
public interface CsPositionWifiMapper extends BaseMapper<CsPositionWifi> {
    /**
     * 获取mac地址
     * @param stationCodes
     * @return
     */
    List<String> getMac(@Param("stationCodes") List<String> stationCodes);

    /**
     * 根据mac地址获取station_code
     * @param mac
     * @return
     */
    @Select("select station_code from cs_position_wifi where mac=#{mac} and del_flag=0")
    String getStationCodeByMac(String mac);
}
