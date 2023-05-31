package com.aiurt.modules.system.service.impl;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.boot.constant.SysParamCodeConstant;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.system.entity.SysUserPositionCurrent;
import com.aiurt.modules.system.mapper.SysUserPositionCurrentMapper;
import com.aiurt.modules.system.service.ISysUserPositionCurrentService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.api.ISysParamAPI;
import org.jeecg.common.system.vo.SysParamModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 用户连接站点wifi的当前位置
 * @author
 */
@Service
@Slf4j
public class SysUserPositionCurrentServiceImpl extends ServiceImpl<SysUserPositionCurrentMapper, SysUserPositionCurrent> implements ISysUserPositionCurrentService{

    @Autowired
    private ISysParamAPI sysParamApi;

    @Override
    public void saveOrUpdateOne(SysUserPositionCurrent sysUserPositionCurrent) {
        // 根据create_by查询是否已存在
        LambdaQueryWrapper<SysUserPositionCurrent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserPositionCurrent::getCreateBy, sysUserPositionCurrent.getCreateBy());
        SysUserPositionCurrent one;
        try{
            one = this.getOne(queryWrapper);
        }catch (Exception e){
            throw new AiurtBootException("用户连接wifi当前位置表中用户:" + sysUserPositionCurrent.getCreateBy() +" 不唯一");
        }
        if (sysUserPositionCurrent.getStationCode() == null) {
            // 站点为空时，设置为异常
            sysUserPositionCurrent.setIsPositionError("1");
        } else {
            // 不为空，正常
            sysUserPositionCurrent.setIsPositionError("0");
        }

        if (ObjectUtil.isNull(one)) {
            // 不存在，添加
            this.save(sysUserPositionCurrent);
            return;
        }

        // 已存在，更新
        // 不更新create_time
        sysUserPositionCurrent.setCreateTime(null);

        // 当前wifi的连接时间
        Date wifiConnectTime = sysUserPositionCurrent.getUploadTime();

        // 如果本次连接站点和one中获取的数据库的站点一致，不更新upload_time
        // 否则更新upload_time, station_code, last_upload_time, last_station_code
        if (StrUtil.equals(one.getStationCode(), sysUserPositionCurrent.getStationCode())) {
            sysUserPositionCurrent.setUploadTime(null);
            // 这里也要增加last_station_code，因为设置了null值也更新，不过设置的和原来的一样，相当于没更新
            sysUserPositionCurrent.setLastStationCode(one.getLastStationCode());
        }else {
            sysUserPositionCurrent.setLastUploadTime(one.getUploadTime());
            sysUserPositionCurrent.setLastStationCode(one.getStationCode());
        }

        // 如果one.getUpdateTime与当前wifi连接时间的时间间隔大于wifiUpdateInterval，就更新upload_time，无论是否同站点
        // 从实时配置管理中获取更新upload_time时间间隔
        SysParamModel filterParamModel = sysParamApi.selectByCode(SysParamCodeConstant.WIFI_UPDATE_INTERVAL);
        String wifiUpdateInterval = filterParamModel.getValue();
        int interval = (int) DateUtil.between(
                wifiConnectTime,
                one.getUpdateTime() == null ? one.getCreateTime(): one.getUpdateTime(),  DateUnit.MINUTE
        );
        if (interval > Integer.parseInt(wifiUpdateInterval)){
            sysUserPositionCurrent.setUploadTime(wifiConnectTime);
        }

        sysUserPositionCurrent.setId(one.getId());
        this.updateById(sysUserPositionCurrent);

    }
}
