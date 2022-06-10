package com.aiurt.boot.modules.worklog.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.aiurt.boot.common.api.vo.Result;
import com.aiurt.boot.modules.worklog.dto.WorkLogRemindDTO;
import com.aiurt.boot.modules.worklog.entity.WorkLogRemind;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author WangHongTao
 * @Date 2021/11/30
 */
public interface IWorkLogRemindService extends IService<WorkLogRemind> {

    /**
     * 添加工作日志提醒时间设置
     * @param dto
     * @param req
     * @return
     */
    Result add (WorkLogRemindDTO dto, HttpServletRequest req);

    /**
     * 获取当登陆人所在班组日志提醒信息
     * @param req
     * @return
     */
    WorkLogRemind getWorkLogRemind(HttpServletRequest req);
}
