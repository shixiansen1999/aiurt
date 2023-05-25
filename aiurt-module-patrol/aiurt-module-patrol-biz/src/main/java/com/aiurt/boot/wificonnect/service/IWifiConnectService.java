package com.aiurt.boot.wificonnect.service;

import com.aiurt.boot.wificonnect.entity.WifiConnect;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;

/**
 * @Description: wifi_connect
 * @Author: jeecg-boot
 * @Date:   2023-05-24
 * @Version: V1.0
 */
public interface IWifiConnectService extends IService<WifiConnect> {

    /**
     * wifi连接记录-添加
     * @param wifiConnect
     */
    void saveOne(WifiConnect wifiConnect);


    /**
     * 根据站点code和连接开始时间查询连接信息
     * @param stationCode
     * @param connectTimeBegin
     * @return
     */
    WifiConnect getRecentConnect(String stationCode, Date connectTimeBegin);
}
