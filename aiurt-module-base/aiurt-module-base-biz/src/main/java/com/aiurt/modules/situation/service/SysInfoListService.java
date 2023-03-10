package com.aiurt.modules.situation.service;

import com.aiurt.modules.situation.entity.SysAnnouncement;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

public interface SysInfoListService extends IService<SysAnnouncement> {
    /**
     * 导出
     * @param request
     * @return
     */
    ModelAndView reportExport(HttpServletRequest request,SysAnnouncement sysAnnouncement, Class<SysAnnouncement> clazz, String title);

    /**
     * 获取指定范围
     * @param announcement
     */
    void getUserNames(SysAnnouncement announcement);
}