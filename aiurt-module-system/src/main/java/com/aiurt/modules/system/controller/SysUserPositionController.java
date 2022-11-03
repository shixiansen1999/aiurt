package com.aiurt.modules.system.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.system.entity.SysUserPosition;
import com.aiurt.modules.system.service.ISysUserPositionService;
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

import java.util.Date;

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
    @AutoLog(value = "GPS、wifi定位数据上报")
    @ApiOperation(value = "GPS、wifi定位数据上报", notes = "GPS、wifi定位数据上报")
    @PostMapping(value = "/add")
    public Result<SysUserPosition> add(@RequestBody SysUserPosition sysUserPosition) {
        Result<SysUserPosition> result = new Result<SysUserPosition>();
        try {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            sysUserPosition.setUploadTime(new Date());
            sysUserPosition.setSysOrgCode(sysUser.getOrgCode());
            sysUserPositionService.save(sysUserPosition);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

}
