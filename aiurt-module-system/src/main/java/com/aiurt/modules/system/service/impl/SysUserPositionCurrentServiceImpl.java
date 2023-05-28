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

        SysParamModel filterParamModel = sysParamApi.selectByCode(SysParamCodeConstant.WIFI_UPDATE_INTERVAL);
        String wifiUpdateInterval = filterParamModel.getValue();

        // 如果one.getUpdateTime与new Date的时间间隔大于wifiUpdateInterval，就更新upload_time，不然就要进行判断是否同站点
        int interval = (int) DateUtil.between(new Date(),
                one.getUpdateTime() == null ? one.getCreateTime(): one.getUpdateTime(),  DateUnit.MINUTE);
        if (interval <= Integer.parseInt(wifiUpdateInterval) && StrUtil.equals(one.getStationCode(), sysUserPositionCurrent.getStationCode())){
            sysUserPositionCurrent.setUploadTime(null);
        }

        sysUserPositionCurrent.setId(one.getId());
        this.updateById(sysUserPositionCurrent);

    }
}
