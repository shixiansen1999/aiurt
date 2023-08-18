package com.aiurt.modules.user.getuser.impl;

import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.getuser.GetTaskUser;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fgw
 */
@Service
public class CustomVariableGetTaskUser implements GetTaskUser {

    /**
     * 获取用户下
     *
     * @param customUser 定义id
     * @return
     */
    @Override
    public List<String> getUser(ActCustomUser customUser) {
        return null;
    }
}
