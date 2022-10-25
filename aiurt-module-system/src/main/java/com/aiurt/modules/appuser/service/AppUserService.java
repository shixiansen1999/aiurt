package com.aiurt.modules.appuser.service;


import com.aiurt.common.enums.StatusEnum;
import com.aiurt.common.util.DateUtils;
import com.aiurt.modules.appuser.entity.UserParam;
import com.aiurt.modules.appuser.entity.UserStatusVo;
import com.aiurt.modules.system.entity.SysAbout;
import com.aiurt.modules.system.entity.SysHelp;
import com.aiurt.modules.system.entity.SysUser;
import com.aiurt.modules.system.service.ISysAboutService;
import com.aiurt.modules.system.service.ISysHelpService;
import com.aiurt.modules.system.service.ISysUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author fgw
 */

@Service
public class AppUserService {
    @Autowired
    private ISysUserService userService;
    @Autowired
    private ISysAboutService aboutService;
    @Autowired
    private ISysHelpService helpService;

    public Result<SysUser> editUser(UserParam userParam, Result<SysUser> result) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        SysUser sysUser = userService.getById(loginUser.getId());
        if (userParam != null && StringUtils.isNotEmpty(userParam.getRealName())) {
            sysUser.setRealname(userParam.getRealName());
        }
        if (userParam != null && userParam.getSex() != null) {
            sysUser.setSex(userParam.getSex());
        }
        if (userParam != null && StringUtils.isNotEmpty(userParam.getPhone())) {
            sysUser.setPhone(userParam.getPhone());
        }
        if (userParam != null && StringUtils.isNotEmpty(userParam.getImgurl())) {
            sysUser.setAvatar(userParam.getImgurl());
        }
        userService.updateById(sysUser);
        result.setResult(userService.getUserByName(sysUser.getUsername()));
        result.success("修改成功");
        return result;
    }

    public Result<UserStatusVo> getUserStatus(Result<UserStatusVo> result) {
        try {
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            QueryWrapper wrapper = new QueryWrapper();
            wrapper.eq("date_format(date,'%Y-%m-%d')", DateUtils.formatDate(new Date(), "yyyy-MM-dd"));
            wrapper.eq("user_id", loginUser.getId());
            wrapper.eq("del_flag", StatusEnum.ZERO.getCode());
            result.setResult(null);
            result.success("状态获取成功");
        } catch (Exception e) {
            result.error500("状态获取失败");
        }
        return result;
    }

    public Result<SysAbout> getSysAbout(Result<SysAbout> result) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("status", StatusEnum.ONE.getCode());
        wrapper.eq("del_flag", StatusEnum.ZERO.getCode());
        SysAbout about = new SysAbout();
        List<SysAbout> list = aboutService.list(wrapper);
        if (list != null && list.size() > 0) {
            about = list.get(0);

        }
        result.setResult(about);
        result.success("获取成功");
        return result;
    }

    public Result<List<SysHelp>> getHelpList(Result<List<SysHelp>> result) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("status", StatusEnum.ONE.getCode());
        wrapper.eq("del_flag", StatusEnum.ZERO.getCode());
        wrapper.select("id", "title", "create_time");
        result.setResult(helpService.list(wrapper));
        result.success("帮助列表获取成功");
        return result;
    }

    public Result<SysHelp> getHelp(Result<SysHelp> result, String id) {
        SysHelp help = helpService.getById(id);
        if (help != null) {
            result.setResult(help);
            result.success("帮助详情获取成功");
        } else {
            result.error500("帮助详情获取失败");
        }
        return result;
    }
}
