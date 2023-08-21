package com.aiurt.modules.user.getuser.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.modeler.dto.FlowUserRelationAttributeModel;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.getuser.GetTaskUser;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
        List<FlowUserRelationAttributeModel> relation = customUser.getRelation();

        // 系统变量
        List<FlowUserRelationAttributeModel> systemVarList = relation.stream().filter(model -> StrUtil.isNotBlank(model.getVariable()))
                .collect(Collectors.toList());

        if (CollUtil.isEmpty(systemVarList)) {
            return Collections.emptyList();
        }

        systemVarList.stream().forEach(model->{

        });
        return null;
    }
}
