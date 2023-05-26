package com.aiurt.modules.system.service.impl;

import com.aiurt.modules.system.entity.SysUserPosition;
import com.aiurt.modules.system.entity.SysUserPositionCurrent;
import com.aiurt.modules.system.mapper.SysUserPositionMapper;
import com.aiurt.modules.system.service.ISysUserPositionCurrentService;
import com.aiurt.modules.system.service.ISysUserPositionService;
import com.aiurt.modules.system.util.CoordinateTransformUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * GPS、wifi定位数据上报
 * @author hlq
 */
@Service
@Slf4j
public class SysUserPositionServiceImpl extends ServiceImpl<SysUserPositionMapper, SysUserPosition> implements ISysUserPositionService {

    @Autowired
    private ISysBaseAPI sysBaseApi;
    @Autowired
    private ISysUserPositionCurrentService sysUserPositionCurrentService;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result<SysUserPosition> saveOne(SysUserPosition sysUserPosition) {
        Result<SysUserPosition> result = new Result<SysUserPosition>();
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            sysUserPosition.setUploadTime(new Date());
            sysUserPosition.setSysOrgCode(sysUser.getOrgCode());
            log.info("转换前的参数：{}", JSON.toJSONString(sysUserPosition));
            // 坐标转换
            // 纬度
            BigDecimal latitude = sysUserPosition.getLatitude();
            // 经度
            BigDecimal longitude = sysUserPosition.getLongitude();
            if (Objects.nonNull(latitude) && Objects.nonNull(longitude)) {
                double[] bd09 = CoordinateTransformUtil.wgs84tobd09(longitude.doubleValue(), latitude.doubleValue());
                if (Objects.nonNull(bd09)) {
                    sysUserPosition.setLongitude(BigDecimal.valueOf(bd09[0]));
                    sysUserPosition.setLatitude(BigDecimal.valueOf(bd09[1]));
                }
            }
            log.info("转换前的参数：{}", JSON.toJSONString(sysUserPosition));
            // 通过mac地址获取stationCode
            sysUserPosition.setStationCode(sysBaseApi.getStationCodeByMac(sysUserPosition.getBssid()));
            this.save(sysUserPosition);
            // 添加或者更新sys_user_position_current表
            SysUserPositionCurrent sysUserPositionCurrent = new SysUserPositionCurrent();
            BeanUtils.copyProperties(sysUserPosition, sysUserPositionCurrent);
            sysUserPositionCurrentService.saveOrUpdateOne(sysUserPositionCurrent);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }
}
