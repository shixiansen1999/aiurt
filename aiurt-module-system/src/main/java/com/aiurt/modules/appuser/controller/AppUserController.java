package com.aiurt.modules.appuser.controller;

import com.aiurt.common.aspect.annotation.AutoLog;
import com.aiurt.modules.appuser.entity.UserParam;
import com.aiurt.modules.appuser.entity.UserStatusVo;
import com.aiurt.modules.appuser.service.AppUserService;
import com.aiurt.modules.system.entity.SysAbout;
import com.aiurt.modules.system.entity.SysHelp;
import com.aiurt.modules.system.entity.SysUser;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author fgw
 */

@Slf4j
@RestController
@RequestMapping("/appUser")
@Api(tags = "app用户相关")
public class AppUserController {
    @Autowired
    private AppUserService appUserService;

    @AutoLog(value = "app用户相关-修改用户信息")
    @ApiOperation(value = "app用户相关-修改用户信息", notes = "app用户相关-修改用户信息")
    @PostMapping(value = "editUser")
    public Result<SysUser> editUser(HttpServletRequest req, @RequestBody @Validated UserParam param) {
        Result<SysUser> result = new Result<SysUser>();
        return appUserService.editUser(param, result);
    }

    @AutoLog(value = "app用户相关-获取用户状态")
    @ApiOperation(value = "app用户相关-获取用户状态", notes = "app用户相关-获取用户状态")
    @GetMapping(value = "getUserStatus")
    public Result<UserStatusVo> getUserStatus(HttpServletRequest req) {
        Result<UserStatusVo> result = new Result<UserStatusVo>();
        return appUserService.getUserStatus(result);
    }

    @AutoLog(value = "app用户相关-获取关于")
    @ApiOperation(value = "app用户相关-获取关于", notes = "app用户相关-获取关于")
    @GetMapping(value = "getAbout")
    public Result<SysAbout> getgetAbout(HttpServletRequest req) {
        Result<SysAbout> result = new Result<SysAbout>();
        return appUserService.getSysAbout(result);
    }

    @AutoLog(value = "app用户相关-获取帮助列表")
    @ApiOperation(value = "app用户相关-获取帮助列表", notes = "app用户相关-获取帮助列表")
    @GetMapping(value = "getHelpList")
    public Result<List<SysHelp>> getHelpList(HttpServletRequest req) {
        Result<List<SysHelp>> result = new Result<List<SysHelp>>();
        return appUserService.getHelpList(result);
    }

    @AutoLog(value = "app用户相关-获取帮助详情")
    @ApiOperation(value = "app用户相关-获取帮助详情", notes = "app用户相关-获取帮助详情")
    @GetMapping(value = "getHelp")
    public Result<SysHelp> getHelp(@RequestParam("id") @ApiParam(value = "帮助id", required = true) @NotNull String id){
        Result<SysHelp> result=new Result<>();
        return appUserService.getHelp(result,id);
    }


}
