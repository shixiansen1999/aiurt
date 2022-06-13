package com.aiurt.modules.AppUser.service;


import com.aiurt.common.enums.StatusEnum;
import com.aiurt.common.util.DateUtils;
import com.aiurt.modules.AppUser.entity.UserParam;
import com.aiurt.modules.AppUser.entity.UserStatusVo;
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

@Service
public class AppUserService {
    @Autowired
    private ISysUserService userService;
  /*  @Autowired
    private IScheduleRecordService recordService;
    @Autowired
    private IScheduleItemService itemService;*/
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
           /* ScheduleRecord record = recordService.getOne(wrapper);
            UserStatusVo vo = new UserStatusVo();
            if (record != null && record.getItemId() != null) {
                ScheduleItem item = itemService.getById(record.getItemId());
                if (item != null) {
                    // todo 有bug
                    String startTime = DateUtils.formatDate(item.getStartTime(), "HH:mm:ss");
                    String endTime = DateUtils.formatDate(item.getEndTime(), "HH:mm:ss");
                    String today = DateUtils.formatDate(new Date(), "yyyy-MM-dd");
                    Calendar c1 = Calendar.getInstance();
                    Calendar c2 = Calendar.getInstance();
                    c1.setTime(DateUtils.parseDate(today + " " + startTime, "yyyy-MM-dd HH:mm:ss"));
                    c2.setTime(DateUtils.parseDate(today + " " + endTime, "yyyy-MM-dd HH:mm:ss"));
                    if (startTime.equals(endTime) || item.getEndTime().before(item.getStartTime())) {
                        c2.add(Calendar.DAY_OF_YEAR, 1);
                    }
                    Date date = new Date();
                    if (c1.getTime().before(date) && c2.getTime().after(date)) {
                        vo.setStatusName(item.getName());
                        vo.setStatusColor(item.getColor());
                    }
                }
            }*/
            result.setResult(vo);
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
