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
import org.jeecg.common.system.api.ISysBaseAPI;
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
        return sysUserPositionService.saveOne(sysUserPosition);
    }

}
