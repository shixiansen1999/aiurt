package com.aiurt.modules.editor.language.json.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.constant.FlowModelAttConstant;
import com.aiurt.modules.common.constant.FlowModelExtElementConstant;
import com.aiurt.modules.common.constant.FlowVariableConstant;
import com.aiurt.modules.common.enums.MultiApprovalRuleEnum;
import com.aiurt.modules.modeler.entity.ActOperationEntity;
import com.aiurt.modules.modeler.entity.ActUserTypeEntity;
import com.aiurt.modules.modeler.entity.AutoSelectEntity;
import com.aiurt.modules.modeler.entity.NodeActionDTO;
import com.aiurt.modules.online.page.dto.FormFiledJsonDTO;
import com.aiurt.modules.utils.FlowRelationUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverterContext;
import org.flowable.editor.language.json.converter.UserTaskJsonConverter;
import org.flowable.editor.language.json.converter.util.JsonConverterUtil;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 扩展任务节点属性解析器
 * @author fgw
 */
@Slf4j
public class CustomUserTaskJsonConverter  extends UserTaskJsonConverter {

    /**
     * <flowable:formOperation></flowable:formOperation>
     * 表单操作按钮 {"formOperation":{"id":"","label":"","type":"","showOrder":""}
     */
    public static final String FORM_OPERATION = "formOperation";
    /**
     * 部门岗位集合
     */
    public static final String DEPT_POST_LIST = "deptPostList";

    /**
     * 多人审批规则
     */
    public static final String MULTI_APPROVAL_RULE = "multiApprovalRule";

    /**
     * 候选用户
     */
    public static final String USER_CANDIDATE_GROUPS = "userCandidateGroups";

    /**
     * 变量
     */
    private static final String FORM_VARIABLE = "formVariable";

    /**
     * 表格
     */
    private static final String FORM = "form";


    private static final String SERIAL_VERSION_UID = "serialVersionUID";

    /**
     * 注入自定义CustomUserTaskJsonConverter
     */
    static void customFillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap, Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        fillJsonTypes(convertersToBpmnMap);
        fillBpmnTypes(convertersToJsonMap);
    }

    public static void fillJsonTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
        convertersToBpmnMap.put(STENCIL_TASK_USER, CustomUserTaskJsonConverter.class);
    }

    public static void fillBpmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(UserTask.class, CustomUserTaskJsonConverter.class);
    }

    /**
     * //重写UserTask Element和Json转换方法
     * @param propertiesNode
     * @param baseElement
     * @param converterContext
     */
    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement, BpmnJsonConverterContext converterContext) {
        super.convertElementToJson(propertiesNode, baseElement, converterContext);
        if (baseElement instanceof UserTask){
            UserTask userTask = (UserTask) baseElement;
            // usetask属性修改
            Map<String, List<ExtensionAttribute>> attributes = baseElement.getAttributes();
            if (log.isDebugEnabled()) {
                log.debug("处理自定义属性:{}", attributes);
            }
            attributes.forEach((key,list)->{
                ExtensionAttribute extensionAttribute = list.get(0);
                if (StrUtil.isNotBlank(extensionAttribute.getValue())) {
                    propertiesNode.put(extensionAttribute.getName(),  extensionAttribute.getValue());
                }
            });

            Map<String, List<ExtensionElement>> extensionElements = baseElement.getExtensionElements();

            // 自定义属性:操作按钮
            List<ExtensionElement> formOperationElements = extensionElements.get(FORM_OPERATION);
            if (CollUtil.isNotEmpty(formOperationElements)) {
                ArrayNode arrayNode = convertExtensionElementsToJson(formOperationElements, ActOperationEntity.class);
                propertiesNode.set(FORM_OPERATION, arrayNode);
            }

            // 多人审批规则
            List<ExtensionElement> userTypeElements = extensionElements.get(MULTI_APPROVAL_RULE);
            if (CollUtil.isNotEmpty(userTypeElements)) {

                // 判断是否第一个节点
                ExtensionElement extensionElement = userTypeElements.get(0);

                ObjectNode objectNode = super.objectMapper.createObjectNode();
                Field[] fields = ActUserTypeEntity.class.getDeclaredFields();
                Arrays.stream(fields).filter(field -> !StrUtil.equalsAnyIgnoreCase(SERIAL_VERSION_UID, field.getName())).forEach(field -> {
                    objectNode.put(field.getName(), extensionElement.getAttributeValue(null, field.getName()));
                });

                propertiesNode.set(MULTI_APPROVAL_RULE, objectNode);

                // 根据选人构造多实例配置；
                String multiApprovalRule = extensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_USER_VALUE);

                // 第一个节点不构造多实例配置；
                boolean isFirstUserTask = isFirstUserTask(userTask);
                MultiApprovalRuleEnum approvalRuleEnum = MultiApprovalRuleEnum.getByCode(multiApprovalRule);
                if (!isFirstUserTask && Objects.nonNull(approvalRuleEnum)) {
                    propertiesNode.put(PROPERTY_MULTIINSTANCE_COLLECTION, FlowVariableConstant.ASSIGNEE_LIST + baseElement.getId());
                    propertiesNode.put(PROPERTY_MULTIINSTANCE_VARIABLE, "assignee");
                    propertiesNode.putNull(PROPERTY_MULTIINSTANCE_VARIABLE_AGGREGATIONS);
                    MultiInstanceLoopCharacteristics loopCharacteristics = userTask.getLoopCharacteristics();
                    switch (approvalRuleEnum) {
                        // 任意会签
                        case TASK_MULTI_INSTANCE_TYPE_1:
                            propertiesNode.put(PROPERTY_MULTIINSTANCE_TYPE, "Parallel");
                            propertiesNode.put(PROPERTY_MULTIINSTANCE_CONDITION, "${multiInstance.accessCondition(execution)}");
                            if (Objects.nonNull(loopCharacteristics)) {
                                loopCharacteristics.setSequential(false);
                                loopCharacteristics.setCompletionCondition("${multiInstance.accessCondition(execution)}");
                            }
                            break;
                        // 并行
                        case TASK_MULTI_INSTANCE_TYPE_2:
                            if (Objects.nonNull(loopCharacteristics)) {
                                loopCharacteristics.setSequential(false);
                                // 修提交条件，否则二次编辑提交条件无法修改，原因先执行customUserTaskJsonConverter ，再执行BaseBpmnJsonConverter
                                loopCharacteristics.setCompletionCondition("${nrOfCompletedInstances == nrOfInstances}");
                            }
                            propertiesNode.put(PROPERTY_MULTIINSTANCE_TYPE, "Parallel");
                            propertiesNode.put(PROPERTY_MULTIINSTANCE_CONDITION, "${nrOfCompletedInstances == nrOfInstances}");
                            break;
                        default:
                            if (Objects.nonNull(loopCharacteristics)) {
                                loopCharacteristics.setSequential(true);
                                // 修提交条件，否则二次编辑提交条件无法修改，原因先执行customUserTaskJsonConverter ，再执行BaseBpmnJsonConverter
                                loopCharacteristics.setCompletionCondition(null);
                            }
                            propertiesNode.put(PROPERTY_MULTIINSTANCE_TYPE, "Sequential");
                    }
                }

                // 流程选人的
                ObjectNode assignmentNode = objectMapper.createObjectNode();
                ObjectNode assignmentValuesNode = objectMapper.createObjectNode();
                assignmentValuesNode.put("type", "static");
                // $INITIATOR 表示流程发起人
                assignmentValuesNode.put(PROPERTY_USERTASK_ASSIGNEE,isFirstUserTask ? "$INITIATOR": "${assignee}");
                assignmentValuesNode.put("initiatorCanCompleteTask", true);
                assignmentNode.set("assignment", assignmentValuesNode);
                propertiesNode.set(PROPERTY_USERTASK_ASSIGNMENT, assignmentNode);
            }

            // 选人将 flowable:userassignee 属性转换为 JSON 格式
            List<ExtensionElement> userAssigneeElements = extensionElements.get(FlowModelExtElementConstant.EXT_USER_ASSIGNEE);
            buildJsonElement(propertiesNode, userAssigneeElements, FlowModelExtElementConstant.EXT_USER_ASSIGNEE);

            // 抄送人
            List<ExtensionElement> carbonCopyElements = extensionElements.get(FlowModelExtElementConstant.EXT_CARBON_COPY);
            buildJsonElement(propertiesNode, carbonCopyElements, FlowModelExtElementConstant.EXT_CARBON_COPY);

            // 自动选人
            List<ExtensionElement> autoSelectElements = extensionElements.get(FlowModelExtElementConstant.EXT_AUTO_SELECT);
            if (CollUtil.isNotEmpty(autoSelectElements)) {
                ExtensionElement extensionElement = autoSelectElements.get(0);

                ObjectNode objectNode = super.objectMapper.createObjectNode();
                Field[] fields = AutoSelectEntity.class.getDeclaredFields();
                Arrays.stream(fields).filter(field -> !StrUtil.equalsAnyIgnoreCase(SERIAL_VERSION_UID, field.getName())).forEach(field -> {
                    objectNode.put(field.getName(), extensionElement.getAttributeValue(null, field.getName()));
                });

                propertiesNode.set(FlowModelExtElementConstant.EXT_AUTO_SELECT, objectNode);
            }

            // 节点前后附加操作
            List<ExtensionElement> preNodeActionElements = extensionElements.get(FlowModelExtElementConstant.EXT_PRE_NODE_ACTION);
            if (CollUtil.isNotEmpty(preNodeActionElements)) {
                ObjectNode preNodeActionObjectNode = FlowRelationUtil.createObjectNodeFromFields(NodeActionDTO.class, preNodeActionElements.get(0));
                propertiesNode.set(FlowModelExtElementConstant.EXT_PRE_NODE_ACTION, preNodeActionObjectNode);
            }
            List<ExtensionElement> postNodeActionElements = extensionElements.get(FlowModelExtElementConstant.EXT_POST_NODE_ACTION);
            if (CollUtil.isNotEmpty(postNodeActionElements)) {
                ObjectNode postNodeActionObjectNode = FlowRelationUtil.createObjectNodeFromFields(NodeActionDTO.class, postNodeActionElements.get(0));
                propertiesNode.set(FlowModelExtElementConstant.EXT_POST_NODE_ACTION, postNodeActionObjectNode);
            }

            // 表单字段在节点上的配置
            List<ExtensionElement> formFieldConfigElements = extensionElements.get(FlowModelExtElementConstant.FORM_FIELD_CONFIG);
            if(CollUtil.isNotEmpty(formFieldConfigElements)){
                ArrayNode arrayNode = convertExtensionElementsToJson(formFieldConfigElements, FormFiledJsonDTO.class);
                propertiesNode.set(FlowModelExtElementConstant.FORM_FIELD_CONFIG, arrayNode);
            }

            // 表单类型
            List<ExtensionElement> formType = extensionElements.get(FlowModelExtElementConstant.EXT_FORM_TYPE);
            buildJsonElement(propertiesNode, formType, FlowModelExtElementConstant.EXT_FORM_TYPE);

            // 关联表单
            List<ExtensionElement> associatedForm = extensionElements.get(FlowModelExtElementConstant.EXT_ASSOCIATED_FORM);
            buildJsonElement(propertiesNode, associatedForm, FlowModelExtElementConstant.EXT_ASSOCIATED_FORM);

            // 字段权限配置
            List<ExtensionElement> formPermissionConfig = extensionElements.get(FlowModelExtElementConstant.EXT_FIELD_LIST);
            buildJsonElement(propertiesNode, formPermissionConfig, FlowModelExtElementConstant.EXT_FIELD_LIST);

            // 加减签
            List<ExtensionElement> multiElementList = extensionElements.get(FlowModelExtElementConstant.EXT_ADD_MULTI);
            if (CollUtil.isNotEmpty(multiElementList)) {
                ExtensionElement extensionElement = multiElementList.get(0);
                String value = extensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_VALUE);
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.put(FlowModelExtElementConstant.EXT_VALUE, value);
                propertiesNode.set(FlowModelExtElementConstant.EXT_ADD_MULTI, objectNode);
            }

            // 审批人为空
            List<ExtensionElement> emptyElementList = extensionElements.get(FlowModelExtElementConstant.EX_EMPTY_APPROVE);
            if (CollUtil.isNotEmpty(emptyElementList)) {
                ExtensionElement extensionElement = emptyElementList.get(0);
                String value = extensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_VALUE);
                String username = extensionElement.getAttributeValue(null, "username");
                ObjectNode objectNode = objectMapper.createObjectNode();
                objectNode.put(FlowModelExtElementConstant.EXT_VALUE, value);
                if (StrUtil.isNotBlank(username)) {
                    JsonNode jsonNode = parseUserAssigneeValue(username);
                    objectNode.set("username", jsonNode);
                }
                propertiesNode.set(FlowModelExtElementConstant.EX_EMPTY_APPROVE, objectNode);
            }
        }
    }

    /**
     *
     * @param userTask
     * @return
     */
    private boolean isFirstUserTask(UserTask userTask) {
        List<SequenceFlow> incomingFlows = userTask.getIncomingFlows();
        for (int i = 0; i < incomingFlows.size(); i++) {
            SequenceFlow sequenceFlow = incomingFlows.get(i);

            String sourceRef = sequenceFlow.getSourceRef();
            if (StrUtil.containsIgnoreCase(sourceRef, "Event")) {
                return true;
            }
        }
        return false;
    }

    private void buildJsonElement(ObjectNode propertiesNode, List<ExtensionElement> extensionElementList, String elementName) {
        if (CollUtil.isNotEmpty(extensionElementList)) {
            ExtensionElement extensionElement = extensionElementList.get(0);
            String name = extensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_USER_NAME);
            String value = extensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_USER_VALUE);
            String alias = extensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_USER_ALIAS);
            JsonNode jsonNode = parseUserAssigneeValue(value);
            ObjectNode objectNode = objectMapper.createObjectNode();

            if (StrUtil.isNotBlank(name)) {
                objectNode.put(FlowModelExtElementConstant.EXT_USER_NAME, StrUtil.isNotBlank(name) ? name : "");
            }
            if (StrUtil.isNotBlank(alias)) {
                objectNode.put(FlowModelExtElementConstant.EXT_USER_ALIAS, alias);
            }
            objectNode.set(FlowModelExtElementConstant.EXT_USER_VALUE, jsonNode);
            propertiesNode.set(elementName, objectNode);
        }
    }

    /**
     *  重写Json和UserTask Element转换方法
     * @param elementNode
     * @param modelNode
     * @param shapeMap
     * @param converterContex
     * @return
     */
    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap, BpmnJsonConverterContext converterContex) {
        FlowElement flowElement = super.convertJsonToElement(elementNode, modelNode, shapeMap, converterContex);
        try {
            this.addExtansionPropertiesElement(flowElement, elementNode);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return flowElement;
    }

    private void addExtansionPropertiesElement(FlowElement flowElement, JsonNode elementNode) throws JsonProcessingException {
        if (flowElement instanceof UserTask) {
            UserTask userTask = (UserTask) flowElement;

            // 自定义属性:操作按钮
            addExtensionElementToUserTask(userTask, FORM_OPERATION, JsonConverterUtil.getProperty(FORM_OPERATION, elementNode));
            // 多人审批规则
            addExtensionElementToUserTask(userTask, MULTI_APPROVAL_RULE, JsonConverterUtil.getProperty(MULTI_APPROVAL_RULE, elementNode));
            // 节点前后附件操作
            addExtensionElementToUserTask(userTask, FlowModelExtElementConstant.EXT_PRE_NODE_ACTION, JsonConverterUtil.getProperty(FlowModelExtElementConstant.EXT_PRE_NODE_ACTION, elementNode));
            addExtensionElementToUserTask(userTask, FlowModelExtElementConstant.EXT_POST_NODE_ACTION, JsonConverterUtil.getProperty(FlowModelExtElementConstant.EXT_POST_NODE_ACTION, elementNode));
            // 自动选人
            addExtensionElementToUserTask(userTask, FlowModelExtElementConstant.EXT_AUTO_SELECT,
                    JsonConverterUtil.getProperty(FlowModelExtElementConstant.EXT_AUTO_SELECT, elementNode));
            // 选人
            addExtensionElementToUserTask(userTask, FlowModelExtElementConstant.EXT_USER_ASSIGNEE,
                    JsonConverterUtil.getProperty(FlowModelExtElementConstant.EXT_USER_ASSIGNEE, elementNode));

            // 抄送人
            addExtensionElementToUserTask(userTask, FlowModelExtElementConstant.EXT_CARBON_COPY,
                    JsonConverterUtil.getProperty(FlowModelExtElementConstant.EXT_CARBON_COPY, elementNode));

            // 表单类型
            addExtensionElementToUserTask(userTask, FlowModelExtElementConstant.EXT_FORM_TYPE,
                    JsonConverterUtil.getProperty(FlowModelExtElementConstant.EXT_FORM_TYPE, elementNode));

            // 关联表单
            addExtensionElementToUserTask(userTask, FlowModelExtElementConstant.EXT_ASSOCIATED_FORM,
                    JsonConverterUtil.getProperty(FlowModelExtElementConstant.EXT_ASSOCIATED_FORM, elementNode));

            // 字段权限配置
            addExtensionElementToUserTask(userTask, FlowModelExtElementConstant.EXT_FIELD_LIST,
                    JsonConverterUtil.getProperty(FlowModelExtElementConstant.EXT_FIELD_LIST, elementNode));

            // 1.0选人
            // 选人类型， initiator是为：流程发起人, data
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.USER_TYPE);
            // 角色
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.ROLE);
            // 部门
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.DEPT);
            // 指定人员
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.USER);
            // 动态人员
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.DYNAMIC_PERSON);
            // 人员类型: fixed ,dynim
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.DATA_TYPE);
            // 表单页面 类型
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.FORM_TYPE);
            // 表单设计器
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.FORM_DYNAMIC_URL);
            // 表单url
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.FORM_URL);
            // 业务处理
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.SERVICE);
            // 流程变量
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.FORM_TASK_VARIABLES);
            // 表单字段在节点上的配置
            addExtensionElementToUserTask(userTask, FlowModelExtElementConstant.FORM_FIELD_CONFIG, JsonConverterUtil.getProperty(FlowModelExtElementConstant.FORM_FIELD_CONFIG, elementNode));
            // 加减签
            addExtensionElementToUserTask(userTask, FlowModelExtElementConstant.EXT_ADD_MULTI, JsonConverterUtil.getProperty(FlowModelExtElementConstant.EXT_ADD_MULTI, elementNode));
            // 审批人为空
            addExtensionElementToUserTask(userTask, FlowModelExtElementConstant.EX_EMPTY_APPROVE, JsonConverterUtil.getProperty(FlowModelExtElementConstant.EX_EMPTY_APPROVE, elementNode));
        }
    }




    /**
     * 属性
     * @param elementNode
     * @param userTask
     * @param prefix
     * @param attr
     */
    private void addCustomAttributeForPrefix(JsonNode elementNode, UserTask userTask, String prefix, String attr) {
        String formType = JsonConverterUtil.getPropertyValueAsString(attr, elementNode);
        if (StrUtil.isNotBlank(formType)) {
            ExtensionAttribute attribute = new ExtensionAttribute();
            attribute.setName(attr);
            attribute.setValue(formType);
            attribute.setNamespacePrefix(prefix);
            attribute.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
            userTask.addAttribute(attribute);
        }
    }





    /**
     * json 字符转为 JsonNode,json对象非json数组
     * @param value
     * @return
     */
    private JsonNode parseUserAssigneeValue(String value) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(value);
            return jsonNode;
        } catch (IOException e) {
            // ignore exception
        }
        return null;
    }



    /**
     * 构建自定义xml标签<flowable:userTye name="userType" value="taskMultiInstanceType-2">
     * @param extensionName 扩展元素的名称
     * @param jsonObject  扩展元素的数据
     * @return
     */
    @NotNull
    private ExtensionElement getExtensionElement(String extensionName, JSONObject jsonObject) {
        ExtensionElement extensionElement = new ExtensionElement();
        extensionElement.setName(extensionName);
        extensionElement.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
        extensionElement.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
        Set<String> keySet = jsonObject.keySet();
        keySet.stream().forEach(key -> {
            ExtensionAttribute attribute = new ExtensionAttribute();
            attribute.setName(key);
            attribute.setValue(jsonObject.getString(key));
            extensionElement.addAttribute(attribute);
        });
        return extensionElement;
    }

    /**
     * 向用户任务（UserTask）添加扩展元素（ExtensionElement）。
     *
     * @param userTask      用户任务对象，用于添加扩展元素
     * @param extensionName 扩展元素的名称
     * @param extensionData 扩展元素的数据，以 JSON 格式表示
     * @throws JsonProcessingException 如果 JSON 转换过程中出现异常
     */
    private void addExtensionElementToUserTask(UserTask userTask, String extensionName, JsonNode extensionData) throws JsonProcessingException {
        if (Objects.nonNull(extensionData)) {
            String json = objectMapper.writeValueAsString(extensionData);
            log.info("json->{}", json);
            if (extensionData.isArray()) {
                JSONArray jsonArray = JSONObject.parseArray(json);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ExtensionElement ee = getExtensionElement(extensionName, jsonObject);
                    userTask.addExtensionElement(ee);
                }
            } else if (extensionData.isObject()) {
                JSONObject jsonObject = JSONObject.parseObject(json);
                ExtensionElement ee = getExtensionElement(extensionName, jsonObject);
                userTask.addExtensionElement(ee);
            }
        }
    }



    /**
     * 将扩展元素列表转换为 JSON 数组，并根据指定类（clazz）的字段将属性名和属性值添加到 JSON 对象中。
     *
     * @param extensionElements 扩展元素列表，用于生成 JSON 数组
     * @param clazz             指定的类，用于获取字段信息
     * @return 生成的 JSON 数组（ArrayNode），包含扩展元素的属性名和属性值
     */
    private ArrayNode convertExtensionElementsToJson(List<ExtensionElement> extensionElements, Class<?> clazz) {
        ArrayNode arrayNode = super.objectMapper.createArrayNode();
        for (ExtensionElement e : extensionElements) {
            ObjectNode objectNode = super.objectMapper.createObjectNode();
            Field[] fields = clazz.getDeclaredFields();
            Arrays.stream(fields).filter(field -> !StrUtil.equalsAnyIgnoreCase(SERIAL_VERSION_UID, field.getName())).forEach(field -> {
                objectNode.put(field.getName(), e.getAttributeValue(null, field.getName()));
            });
            arrayNode.add(objectNode);
        }
        return arrayNode;
    }
}
