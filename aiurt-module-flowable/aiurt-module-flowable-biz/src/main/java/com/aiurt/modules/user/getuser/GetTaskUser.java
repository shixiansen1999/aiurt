package com.aiurt.modules.user.getuser;


import java.util.List;

/**
 * @author fgw
 * @date 2023-08-17
 */
public interface GetTaskUser {

    /**
     * 获取用户下
     * @param processDefinitionId 定义id
     * @param nodeId 节点id
     * @param type 类型抄送，还是其他
     * @return
     */
    List<String> getUser(String processDefinitionId, String nodeId, String type);
}
