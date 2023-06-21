package com.aiurt.modules.situation.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.aiurt.modules.situation.entity.SysAnnouncementSend;
import com.aiurt.modules.situation.mapper.SysInfoSendMapper;
import com.aiurt.modules.situation.service.SysInfoSendService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Description: 用户通告阅读标记表
 * @Author: jeecg-boot
 * @Date: 2019-02-21
 * @Version: V1.0
 */
@Service
public class SysInfoSendServiceImpl extends ServiceImpl<SysInfoSendMapper, SysAnnouncementSend> implements SysInfoSendService {
@Autowired
private SysInfoSendMapper sysInfoSendMapper;

    @Override
    public void updateReadFlag(SysAnnouncementSend send) {
        SysAnnouncementSend sysAnnouncementSend = sysInfoSendMapper.selectById(send.getId());
        if(ObjectUtil.isNotEmpty(sysAnnouncementSend)){
            //未读，则更新状态和阅读时间
            String readFlag = "1";
            if(!readFlag.equals(sysAnnouncementSend.getReadFlag())){
                sysAnnouncementSend.setReadFlag(readFlag);
                sysAnnouncementSend.setReadTime(new Date());
                sysInfoSendMapper.updateById(sysAnnouncementSend);
            }
        }
    }
}
