package com.aiurt.boot.wificonnect.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.aiurt.boot.wificonnect.entity.WifiConnect;
import com.aiurt.boot.wificonnect.mapper.WifiConnectMapper;
import com.aiurt.boot.wificonnect.service.IWifiConnectService;
import com.aiurt.common.constant.CommonConstant;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Date;
import java.util.List;

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

    @Override
    public WifiConnect getRecentConnect(String stationCode, Date connectTimeBegin) {
        LambdaQueryWrapper<WifiConnect> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(WifiConnect::getSysOrgCode, stationCode);
        queryWrapper.ge(WifiConnect::getConnectTime, connectTimeBegin);
        queryWrapper.eq(WifiConnect::getDelFlag, CommonConstant.DEL_FLAG_0);
        queryWrapper.orderByDesc(WifiConnect::getConnectTime);
        queryWrapper.last("limit 1");
        List<WifiConnect> list = this.list(queryWrapper);
        return CollUtil.isEmpty(list) ? null : list.get(0);
    }
}
