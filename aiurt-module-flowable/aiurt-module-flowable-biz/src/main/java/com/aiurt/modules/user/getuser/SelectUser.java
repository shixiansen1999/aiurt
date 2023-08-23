package com.aiurt.modules.user.getuser;


import com.aiurt.modules.user.getuser.dto.SelectionParameters;

import java.util.List;

/**
 * @author fgw
 * @date 2023-08-17
 */
public interface SelectUser {

    /**
     * 获取用户列表
     * @param parameters 流程实例
     * @return 用户列表
     */
    List<String> getUserList(SelectionParameters parameters);



}
