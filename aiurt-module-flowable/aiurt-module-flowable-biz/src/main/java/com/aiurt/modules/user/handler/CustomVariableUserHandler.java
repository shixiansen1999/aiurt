package com.aiurt.modules.user.handler;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.pipeline.AbstractFlowHandler;
import com.aiurt.modules.modeler.dto.FlowUserRelationAttributeModel;
import com.aiurt.modules.user.dto.SelectUserContext;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.enums.VariableUserTypeEnum;
import com.aiurt.modules.user.service.IFlowUserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fgw
 */
@Service
public class CustomVariableUserHandler extends AbstractFlowHandler<SelectUserContext> {

    @Autowired
    private IFlowUserService flowUserService;

    /**
     * @param context
     */
    @Override
    public void handle(SelectUserContext context) {
        ActCustomUser customUser = context.getCustomUser();
        Map<String, Object> variables = context.getVariable();

        JSONArray jsonArray = customUser.getRelation();

        List<FlowUserRelationAttributeModel> relation = JSON.parseArray(JSON.toJSONString(jsonArray), FlowUserRelationAttributeModel.class);
        if (CollUtil.isEmpty(relation)) {
            return;
        }

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
        context.addUserList(resultList);
    }
}
