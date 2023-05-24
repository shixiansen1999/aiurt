package com.aiurt.boot.wificonnect.service.impl;


import com.aiurt.boot.wificonnect.entity.WifiConnect;
import com.aiurt.boot.wificonnect.mapper.WifiConnectMapper;
import com.aiurt.boot.wificonnect.service.IWifiConnectService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;

/**
 * @Description: wifi_connect
 * @Author: jeecg-boot
 * @Date:   2023-05-24
 * @Version: V1.0
 */
@Service
public class WifiConnectServiceImpl extends ServiceImpl<WifiConnectMapper, WifiConnect> implements IWifiConnectService {

    @Override
    public void saveOne(WifiConnect wifiConnect) {
        wifiConnect.setConnectTime(new Date());
        this.save(wifiConnect);
    }
}
