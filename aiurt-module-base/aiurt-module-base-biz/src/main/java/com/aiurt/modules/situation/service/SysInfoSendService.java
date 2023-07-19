package com.aiurt.modules.situation.service;


import com.aiurt.modules.situation.entity.SysAnnouncement;
import com.aiurt.modules.situation.entity.SysAnnouncementSend;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Description: 用户通告阅读标记表
 * @Author: jeecg-boot
 * @Date:  2019-02-21
 * @Version: V1.0
 */
public interface SysInfoSendService extends IService<SysAnnouncementSend> {

    /**
     * 用户告，修改阅读状态
     * @param sysAnnouncementSend 修改参数
     */
    void updateReadFlag(SysAnnouncementSend sysAnnouncementSend);

    /**
     * 特情-编辑
     * @param sysAnnouncement
     */
    void editById(SysAnnouncement sysAnnouncement);
}
