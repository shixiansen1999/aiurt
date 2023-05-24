package com.aiurt.boot.wificonnect.service;

import com.aiurt.boot.wificonnect.entity.WifiConnect;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
