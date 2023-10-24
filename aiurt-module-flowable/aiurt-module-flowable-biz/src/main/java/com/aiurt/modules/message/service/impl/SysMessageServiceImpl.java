package com.aiurt.modules.message.service.impl;

import cn.hutool.core.date.DateUtil;
import com.aiurt.common.util.SysAnnmentTypeEnum;
import com.aiurt.modules.message.dto.MessageContext;
import com.aiurt.modules.message.service.ISysMessageService;
import org.flowable.engine.runtime.ProcessInstance;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

@Service
public class SysMessageServiceImpl implements ISysMessageService {

    @Autowired
    private ISysBaseAPI sysBaseApi;


    @Override
    public void sendMessage(MessageContext messageContext) {

        ProcessInstance processInstance = messageContext.getProcessInstance();

        String startUserId = processInstance.getStartUserId();

        LoginUser loginUser = sysBaseApi.queryUser(startUserId);
        if (Objects.isNull(loginUser)) {
            return;
        }


        Date startTime = processInstance.getStartTime();
        String createTime = DateUtil.format(startTime, "yyyy-MM-dd HH:mm");


        HashMap<String, Object> map = new HashMap<>(16);

        map.put("creatTime", createTime);
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_ID, processInstance.getBusinessKey());
        map.put(org.jeecg.common.constant.CommonConstant.NOTICE_MSG_BUS_TYPE, SysAnnmentTypeEnum.BPM.getType());
    }
}
