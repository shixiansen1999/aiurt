package com.aiurt.modules.user.getuser.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.modeler.dto.FlowUserRelationAttributeModel;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.enums.VariableUserTypeEnum;
import com.aiurt.modules.user.getuser.SelectUser;
import com.aiurt.modules.user.getuser.dto.SelectionParameters;
import com.aiurt.modules.user.service.IFlowUserService;
import org.flowable.engine.runtime.ProcessInstance;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Service
public class CustomVariableSelectUser implements SelectUser {

    @Autowired
    private IFlowUserService flowUserService;

    /**
     * 获取用户列表
     *
     * @param parameters
     * @return 用户列表
     */
    @Override
    public List<String> getUserList(SelectionParameters parameters) {
        return getUser(parameters.getCustomUser(), parameters.getVariable());
    }

    /**
     * 获取用户下
     *
     * @param customUser 定义id
     * @return
     */
    public List<String> getUser(ActCustomUser customUser, Map<String, Object> variables) {

        JSONArray jsonArray = customUser.getRelation();

        List<FlowUserRelationAttributeModel> relation = JSON.parseArray(JSON.toJSONString(jsonArray), FlowUserRelationAttributeModel.class);

        List<String> resultList = relation.stream()
                .filter(model -> StrUtil.isNotBlank(model.getVariable()))
                .map(model -> {
                    String variable = model.getVariable();
                    Object value = variables.getOrDefault(variable, null);
                    if (Objects.isNull(value)) {
                        return Collections.<String>emptyList();
                    }
                    String type = model.getType();
                    if (value instanceof String && StrUtil.isNotBlank(type)) {
                        List<String> list = StrUtil.split((String) value, ',');
                        VariableUserTypeEnum userTypeEnum = VariableUserTypeEnum.getByCode(type);
                        switch (userTypeEnum) {
                            case USER:
                                return flowUserService.getUserNameByUserIdOrUserName(list);
                            case ORG:
                                return flowUserService.getUserNameByOrgIdOrOrgCode(list);
                            case ROLE:
                                return flowUserService.getUserNameByRoleIdOrRoleCode(list);
                            case POST:
                                return flowUserService.getUserNameByPost(list);
                            default:
                                return Collections.<String>emptyList();
                        }
                    } else {
                        return Collections.<String>emptyList();
                    }
                })
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        return resultList;
    }
}
