package com.aiurt.modules.user.getuser;

import com.aiurt.modules.user.entity.ActCustomUser;
import org.flowable.engine.runtime.ProcessInstance;

import java.util.List;
import java.util.Map;

/**
 * @author fgw
 */
public interface DefaultSelectUserService {

    /**
     * 全部人员
     * @param actCustomUser
     * @param variableData
     * @param processInstance
     * @return
     */
    List<String> getAllUserList(ActCustomUser actCustomUser, Map<String, Object> variableData, ProcessInstance processInstance);

    /**
     * 获取关系类型的人员
     * @param actCustomUser
     * @param variableData
     * @param processInstance
     * @return
     */
    List<String> getUserList(ActCustomUser actCustomUser, Map<String, Object> variableData, ProcessInstance processInstance);
}
