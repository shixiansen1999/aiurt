package com.aiurt.modules.situation.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.situation.entity.SysAnnouncement;
import com.aiurt.modules.situation.entity.SysAnnouncementSend;
import com.aiurt.modules.situation.mapper.SysInfoSendMapper;
import com.aiurt.modules.situation.service.SysInfoListService;
import com.aiurt.modules.situation.service.SysInfoSendService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    @Autowired
    private SysInfoListService bdInfoListService;
    @Autowired
    private ISysBaseAPI sysBaseApi;

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

    @Override
    public void editById(SysAnnouncement sysAnnouncement) {
        List<SysAnnouncementSend> list = sysInfoSendMapper.selectList(new LambdaQueryWrapper<SysAnnouncementSend>().eq(SysAnnouncementSend::getAnntId, sysAnnouncement.getId()));
        if (ObjectUtil.isNotEmpty(sysAnnouncement.getUserIds())) {
            List<LoginUser> loginUsers = sysBaseApi.queryUserByNames(sysAnnouncement.getUserIds().split(","));
            List<String> userIds = loginUsers.stream().map(LoginUser::getId).collect(Collectors.toList());
            List<SysAnnouncementSend> sendList = new ArrayList<>();
            if (CollUtil.isEmpty(list)) {
                for (LoginUser user : loginUsers) {
                    SysAnnouncementSend send = new SysAnnouncementSend();
                    send.setAnntId(sysAnnouncement.getId());
                    send.setUserId(user.getId());
                    send.setReadFlag(CommonConstant.NO_READ_FLAG);
                    sendList.add(send);
                }
            } else {
                List<String> sendUserIds = list.stream().map(SysAnnouncementSend::getUserId).collect(Collectors.toList());
                //添加
                List<String> newUser = userIds.stream().filter(u -> !sendUserIds.contains(u)).collect(Collectors.toList());
                for (String user : newUser) {
                    SysAnnouncementSend send = new SysAnnouncementSend();
                    send.setAnntId(sysAnnouncement.getId());
                    send.setUserId(user);
                    send.setReadFlag(CommonConstant.NO_READ_FLAG);
                    sendList.add(send);
                }
                //删除
                List<SysAnnouncementSend> delUserList = list.stream().filter(l -> !userIds.contains(l.getUserId())).collect(Collectors.toList());
                this.removeBatchByIds(delUserList);

            }
            if(CollUtil.isNotEmpty(sendList)){
                this.saveBatch(sendList);
            }
        } else {
            if (ObjectUtil.isNotEmpty(list)) {
                this.removeBatchByIds(list);
            }
        }
        bdInfoListService.updateById(sysAnnouncement);
    }
}
