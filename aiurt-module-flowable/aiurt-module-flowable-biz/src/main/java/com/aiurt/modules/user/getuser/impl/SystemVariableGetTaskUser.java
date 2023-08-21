package com.aiurt.modules.user.getuser.impl;

import com.aiurt.modules.modeler.dto.FlowUserRelationAttributeModel;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.getuser.GetTaskUser;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fgw
 */
@Service
public class SystemVariableGetTaskUser implements GetTaskUser {

    /**
     * 获取用户下
     *
     * @param customUser
     * @return
     */
    @Override
    public List<String> getUser(ActCustomUser customUser) {

        List<FlowUserRelationAttributeModel> relation = customUser.getRelation();

        //relation.stream().filter(model->)

        return null;
    }
}
