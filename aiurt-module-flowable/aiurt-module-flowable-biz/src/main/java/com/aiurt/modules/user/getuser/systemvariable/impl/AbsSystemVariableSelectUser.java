package com.aiurt.modules.user.getuser.systemvariable.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.modeler.dto.FlowUserRelationAttributeModel;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.enums.FlowUserRelationEnum;
import com.aiurt.modules.user.getuser.systemvariable.ISystemVariableSelectUserService;
import com.aiurt.modules.user.getuser.systemvariable.impl.SystemVariableGetUserFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fgw
 */

public abstract class AbsSystemVariableSelectUser {

    @Autowired
    private SystemVariableGetUserFactory factory;

    /**
     * 获取用户下
     *
     * @param customUser
     * @return
     */
    public List<String> getUserList(ActCustomUser customUser, ProcessInstance processInstance) {
        JSONArray jsonArray = customUser.getRelation();

        List<FlowUserRelationAttributeModel> relation = JSON.parseArray(JSON.toJSONString(jsonArray), FlowUserRelationAttributeModel.class);
        if (CollUtil.isEmpty(relation)) {
            return Collections.emptyList();
        }

        // 系统变量
        List<FlowUserRelationAttributeModel> systemVarList = relation.stream().filter(model -> StrUtil.isBlank(model.getVariable())
                && StrUtil.isBlank(model.getClassName()) && StrUtil.isBlank(model.getType())).collect(Collectors.toList());

        if (CollUtil.isEmpty(systemVarList)) {
            return Collections.emptyList();
        }
        List<String> resultList = new ArrayList<>();
        systemVarList.stream().forEach(model -> {
            String variable = model.getValue();
            FlowUserRelationEnum relationEnum = FlowUserRelationEnum.getByCode(variable);
            if (Objects.isNull(relationEnum)) {
                return;
            }
            // 使用策略
            ISystemVariableSelectUserService bean = factory.getBean(relationEnum);
            if (Objects.isNull(bean)) {
                return;
            }
            List<String> user = bean.getUser(processInstance);
            resultList.addAll(user);
        });
        return resultList;
    }


}


