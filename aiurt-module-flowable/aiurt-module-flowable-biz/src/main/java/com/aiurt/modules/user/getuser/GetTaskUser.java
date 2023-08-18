package com.aiurt.modules.user.getuser;


import com.aiurt.modules.user.entity.ActCustomUser;

import java.util.List;

/**
 * @author fgw
 * @date 2023-08-17
 */
public interface GetTaskUser {

    /**
     * 获取用户下
     * @param customUser
     * @return
     */
    List<String> getUser(ActCustomUser customUser);
}
