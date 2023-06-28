package com.aiurt.modules.positionwifi.mapper;

import com.aiurt.modules.positionwifi.entity.CsPositionWifi;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.common.system.vo.StationAndMacModel;

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
    List<String> getMac(@Param("stationCodes") List<String> stationCodes,@Param("changeCode")List<String> changeCode);

    /**
     * 根据站点code获取mac地址和站点名称
     * @param stationCodes
     * @return
     */
    List<StationAndMacModel> getStationAndMac(@Param("stationCodes") List<String> stationCodes,@Param("changeCode")List<String> changeCode);

    /**
     * 根据mac地址获取station_code，如果该站点是换乘车站，那获取的就是换乘编码
     * @param mac
     * @return
     */
    String getStationCodeByMac(@Param("mac") String mac);

    /**
     * 获取换乘站站点
     * @param stationCodes
     * @return
     */
    List<String> getChangeCode(@Param("stationCodes")List<String> stationCodes);

}
