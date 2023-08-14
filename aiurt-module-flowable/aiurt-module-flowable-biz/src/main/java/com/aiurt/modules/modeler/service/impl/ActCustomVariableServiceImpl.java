package com.aiurt.modules.modeler.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.aiurt.common.constant.CommonConstant;
import com.aiurt.modules.common.constant.FlowVariableConstant;
import com.aiurt.modules.modeler.dto.ConnectionConditionConfigDTO;
import com.aiurt.modules.modeler.entity.ActCustomVariable;
import com.aiurt.modules.modeler.mapper.ActCustomVariableMapper;
import com.aiurt.modules.modeler.service.IActCustomVariableService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.system.api.ISysBaseAPI;
import org.jeecg.common.system.vo.SysUserRoleModel;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 流程变量
 * @Author: aiurt
 * @Date: 2022-07-27
 * @Version: V1.0
 */
@Service
public class ActCustomVariableServiceImpl extends ServiceImpl<ActCustomVariableMapper, ActCustomVariable> implements IActCustomVariableService {
    private static final String SYSTEM_VARIABLE_CONDITION = "系统变量";
    private static final String CUSTOM_VARIABLE_CONDITION = "自定义变量";

    /**
     * 系统变量字典code
     */
    private static final String FLOWABLE_FILTER_FIELD_NAME = "flowable_filter_field_name";

    /**
     * 岗位字典code
     */
    private static final String SYS_POST = "sys_post";

    /**
     * 系统变量:发起人角色
     */
    private static final String ROLE_INITIATOR = "ROLE_INITIATOR";
    /**
     * 系统变量:发起人岗位
     */
    private static final String POSITION_INITIATOR = "POSITION_INITIATOR";
    /**
     * 系统变量:发起人所属机构
     */
    private static final String ORG_INITIATOR = "ORG_INITIATOR";

    @Resource
    private ISysBaseAPI sysBaseApi;

    @Override
    public List<ConnectionConditionConfigDTO> getFilterFieldNamesDropdown(String modelId) {
        List<ConnectionConditionConfigDTO> result = CollUtil.newArrayList();

        // 系统变量
        List<ConnectionConditionConfigDTO> systemVariableList = getSystemVariableList();
        ConnectionConditionConfigDTO systemVariableNode = new ConnectionConditionConfigDTO();
        systemVariableNode.setLabel(SYSTEM_VARIABLE_CONDITION);
        systemVariableNode.setSelectable(Boolean.FALSE);
        systemVariableNode.setChildren(systemVariableList);
        result.add(systemVariableNode);

        // 自定义变量
        List<ConnectionConditionConfigDTO> customVariableList = getCustomVariableList(modelId);
        if (CollUtil.isNotEmpty(customVariableList)) {
            ConnectionConditionConfigDTO customVariableNode = new ConnectionConditionConfigDTO();
            customVariableNode.setLabel(CUSTOM_VARIABLE_CONDITION);
            customVariableNode.setSelectable(Boolean.FALSE);
            customVariableNode.setChildren(customVariableList);
            result.add(customVariableNode);
        }

        return result;
    }

    /**
     * 获取系统变量
     *
     * @return
     */
    private List<ConnectionConditionConfigDTO> getSystemVariableList() {
        List<ConnectionConditionConfigDTO> systemVariableList = Optional.ofNullable(sysBaseApi.getDictItems(FLOWABLE_FILTER_FIELD_NAME)).orElse(CollUtil.newArrayList()).stream()
                .map(dictItem -> {
                    ConnectionConditionConfigDTO ConnectionConditionConfigDTO = new ConnectionConditionConfigDTO();
                    ConnectionConditionConfigDTO.setLabel(dictItem.getText());
                    ConnectionConditionConfigDTO.setKey(dictItem.getValue());
                    ConnectionConditionConfigDTO.setValue(dictItem.getValue());

                    switch (dictItem.getValue()) {
                        case ROLE_INITIATOR:
                            ConnectionConditionConfigDTO.setOptions(getRoleInitiatorList());
                            break;
                        case POSITION_INITIATOR:
                            ConnectionConditionConfigDTO.setOptions(getPositionInitiatorList());
                            break;
                        case ORG_INITIATOR:
                            List<ConnectionConditionConfigDTO> deptList = getOrgInitiatorList();
                            ConnectionConditionConfigDTO.setOptions(constructTree(deptList));
                            break;
                        default:
                            break;
                    }
                    return ConnectionConditionConfigDTO;
                }).collect(Collectors.toList());
        return systemVariableList;
    }

    /**
     * 根据模板id查询流程变量
     *
     * @param modelId
     * @return
     */
    private List<ConnectionConditionConfigDTO> getCustomVariableList(String modelId) {
        LambdaQueryWrapper<ActCustomVariable> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActCustomVariable::getModelId, modelId)
                .eq(ActCustomVariable::getVariableType, FlowVariableConstant.VARIABLE_TYPE_1).eq(ActCustomVariable::getType, 0);
        List<ActCustomVariable> actCustomVariables = list(wrapper);
        if (CollUtil.isNotEmpty(actCustomVariables)) {
            List<ConnectionConditionConfigDTO> customVariableList = Optional.of(actCustomVariables).orElse(CollUtil.newArrayList()).stream().map(actCustomVariable -> {
                ConnectionConditionConfigDTO ConnectionConditionConfigDTO = new ConnectionConditionConfigDTO();
                ConnectionConditionConfigDTO.setLabel(actCustomVariable.getShowName());
                ConnectionConditionConfigDTO.setKey(actCustomVariable.getVariableName());
                ConnectionConditionConfigDTO.setValue(actCustomVariable.getVariableName());
                return ConnectionConditionConfigDTO;
            }).collect(Collectors.toList());
            return customVariableList;
        }
        return CollUtil.newArrayList();
    }

    /**
     * 获取发起人组织机构列表。
     *
     * @return 包含组织机构发起人信息的 ConnectionConditionConfigDTO 对象列表。
     */
    private List<ConnectionConditionConfigDTO> getOrgInitiatorList() {
        List<ConnectionConditionConfigDTO> deptList = Optional.ofNullable(sysBaseApi.getAllSysDepart()).orElse(CollUtil.newArrayList()).stream().map(depart -> {
            ConnectionConditionConfigDTO flowCondition = new ConnectionConditionConfigDTO();
            flowCondition.setLabel(depart.getDepartName());
            flowCondition.setKey(depart.getId());
            flowCondition.setValue(depart.getId());
            flowCondition.setPid(depart.getParentId());
            return flowCondition;
        }).collect(Collectors.toList());
        return deptList;
    }

    /**
     * 获取发起人岗位列表。
     *
     * @return 包含岗位发起人信息的 ConnectionConditionConfigDTO 对象列表。
     */
    private List<ConnectionConditionConfigDTO> getPositionInitiatorList() {
        List<ConnectionConditionConfigDTO> postList = Optional.ofNullable(sysBaseApi.getDictItems(SYS_POST)).orElse(CollUtil.newArrayList()).stream().map(post -> {
            ConnectionConditionConfigDTO flowCondition = new ConnectionConditionConfigDTO();
            flowCondition.setLabel(post.getText());
            flowCondition.setKey(post.getValue());
            flowCondition.setValue(post.getValue());
            return flowCondition;
        }).collect(Collectors.toList());
        return postList;
    }

    /**
     * 获取发起人角色列表。
     *
     * @return 包含角色发起人信息的 ConnectionConditionConfigDTO 对象列表。
     */
    private List<ConnectionConditionConfigDTO> getRoleInitiatorList() {
        List<SysUserRoleModel> roles = sysBaseApi.getRole(null);
        List<ConnectionConditionConfigDTO> roleInitiatorList = Optional.ofNullable(roles).orElse(CollUtil.newArrayList()).stream().map(role -> {
            ConnectionConditionConfigDTO flowCondition = new ConnectionConditionConfigDTO();
            flowCondition.setLabel(role.getRoleName());
            flowCondition.setKey(role.getRoleCode());
            flowCondition.setValue(role.getRoleCode());
            return flowCondition;
        }).collect(Collectors.toList());
        return roleInitiatorList;
    }

    /**
     * 将deptList构造成一个树形关系。
     *
     * @param deptList 包含组织机构信息的 ConnectionConditionConfigDTO 对象列表。
     * @return 构造后的树形结构 ConnectionConditionConfigDTO 对象列表。
     */
    private List<ConnectionConditionConfigDTO> constructTree(List<ConnectionConditionConfigDTO> deptList) {
        // 使用 key 作为唯一标识，构建一个节点 Map 以便快速查找。
        Map<String, ConnectionConditionConfigDTO> nodeMap = new HashMap<>(64);

        // 用于存放顶层节点（没有父节点）的列表。
        List<ConnectionConditionConfigDTO> rootList = new ArrayList<>();

        for (ConnectionConditionConfigDTO dept : deptList) {
            nodeMap.put(dept.getKey(), dept);

            // 如果节点没有父节点（pid 为 null 或空），说明它是顶层节点。
            if (CommonConstant.ROOT_NODE_ID.equals(dept.getPid()) || ObjectUtil.isEmpty(dept.getPid())) {
                rootList.add(dept);
            } else {
                // 根据 pid 查找父节点，并将当前节点作为其子节点添加到对应的孩子列表中。
                ConnectionConditionConfigDTO parent = nodeMap.get(dept.getPid());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(dept);
                }
            }
        }

        // 返回构造后的树形结构 ConnectionConditionConfigDTO 对象列表。
        return rootList;
    }

}
