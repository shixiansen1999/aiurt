package com.aiurt.modules.system.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.system.entity.SysUserPosition;
import com.aiurt.modules.system.service.ISysUserPositionService;
import com.aiurt.modules.system.util.CoordinateTransformUtil;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

/**
 * GPS、wifi定位数据上报
 * @author hlq
 */
@Slf4j
@Api(tags = "GPS、wifi定位数据上报")
@RestController
@RequestMapping("/sys/user/position")
public class SysUserPositionController {

    @Autowired
    private ISysUserPositionService sysUserPositionService;



    /**
     * 添加
     *
     * @param sysUserPosition
     * @return
     */
    @ApiOperation(value = "GPS、wifi定位数据上报", notes = "GPS、wifi定位数据上报")
    @PostMapping(value = "/add")
    public Result<SysUserPosition> add(@RequestBody SysUserPosition sysUserPosition) {
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
            sysUserPositionService.save(sysUserPosition);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

}
