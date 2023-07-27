package com.aiurt.modules.editor.language.json.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.constant.FlowModelAttConstant;
import com.aiurt.modules.common.constant.FlowModelExtElementConstant;
import com.aiurt.modules.modeler.entity.ActOperationEntity;
import com.aiurt.modules.modeler.entity.ActUserTypeEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
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
    public static final String USER_TYPE = "userType";

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

            // usetask属性修改
            Map<String, List<ExtensionAttribute>> attributes = baseElement.getAttributes();
            log.info("处理自定义属性:{}",JSON.toJSONString(attributes));
            attributes.forEach((key,list)->{
                ExtensionAttribute extensionAttribute = list.get(0);
                ObjectNode objectNode = super.objectMapper.createObjectNode();
                objectNode.put(extensionAttribute.getName(), extensionAttribute.getValue());
                propertiesNode.put(extensionAttribute.getName(),  extensionAttribute.getValue());
            });

            Map<String, List<ExtensionElement>> extensionElements = baseElement.getExtensionElements();

            // 自定义属性:操作按钮
            List<ExtensionElement> formOperationElements = extensionElements.get(FORM_OPERATION);
            if (CollUtil.isNotEmpty(formOperationElements)) {
                ArrayNode arrayNode = convertExtensionElementsToJson(formOperationElements, ActOperationEntity.class);
                propertiesNode.set(FORM_OPERATION, arrayNode);
            }

            // 多人审批规则
            List<ExtensionElement> userTypeElements = extensionElements.get(USER_TYPE);
            if (CollUtil.isNotEmpty(userTypeElements)) {
                ExtensionElement extensionElement = userTypeElements.get(0);

                ObjectNode objectNode = super.objectMapper.createObjectNode();
                Field[] fields = ActUserTypeEntity.class.getDeclaredFields();
                Arrays.stream(fields).filter(field -> !StrUtil.equalsAnyIgnoreCase(SERIAL_VERSION_UID, field.getName())).forEach(field -> {
                    objectNode.put(field.getName(), extensionElement.getAttributeValue(null, field.getName()));
                });

                propertiesNode.set(USER_TYPE, objectNode);
            }

            // 选人将 flowable:userassignee 属性转换为 JSON 格式
            List<ExtensionElement> userAssigneeElements = extensionElements.get(FlowModelExtElementConstant.EXT_USER_ASSIGNEE);
            buildJsonElement(propertiesNode, userAssigneeElements, FlowModelExtElementConstant.EXT_USER_ASSIGNEE);

            // 抄送人
            List<ExtensionElement> carbonCopyElements = extensionElements.get(FlowModelExtElementConstant.EXT_CARBON_COPY);
            buildJsonElement(propertiesNode, carbonCopyElements, FlowModelExtElementConstant.EXT_CARBON_COPY);
        }
    }

    private void buildJsonElement(ObjectNode propertiesNode, List<ExtensionElement> extensionElementList, String elementName) {
        if (CollUtil.isNotEmpty(extensionElementList)) {
            ExtensionElement extensionElement = extensionElementList.get(0);
            String name = extensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_USER_NAME);
            String value = extensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_USER_VALUE);
            String alias = extensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_USER_ALIAS);
            JsonNode jsonNode = parseUserAssigneeValue(value);
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put(FlowModelExtElementConstant.EXT_USER_NAME, StrUtil.isNotBlank(name) ? name : "");
            objectNode.set(FlowModelExtElementConstant.EXT_USER_VALUE, jsonNode);
            objectNode.put(FlowModelExtElementConstant.EXT_USER_ALIAS, alias);
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
            addExtensionElementToUserTask(userTask, USER_TYPE, JsonConverterUtil.getProperty(USER_TYPE, elementNode));
            // 选人
            addExtensionElementToUserTask(userTask, FlowModelExtElementConstant.EXT_USER_ASSIGNEE,
                    JsonConverterUtil.getProperty(FlowModelExtElementConstant.EXT_USER_ASSIGNEE, elementNode));

            // 抄送人
            addExtensionElementToUserTask(userTask, FlowModelExtElementConstant.EXT_CARBON_COPY,
                    JsonConverterUtil.getProperty(FlowModelExtElementConstant.EXT_CARBON_COPY, elementNode));

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


            addCustomAttributeForPrefix(elementNode, userTask,"flowable", "formtaskVariables");


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
     * 构造属性
     * @param value 属性值
     * @param userTask 任务节点
     * @param prefix 前缀
     * @param attr xml 节点属性
     */
    private void addCustomAttributeForPrefix(String value, UserTask userTask, String prefix, String attr) {
        if (StrUtil.isNotBlank(value)) {
            ExtensionAttribute attribute = new ExtensionAttribute();
            attribute.setName(attr);
            attribute.setValue(value);
            attribute.setNamespacePrefix(prefix);
            attribute.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
            userTask.addAttribute(attribute);
        }
    }

    private void addCustomAttribute(JsonNode elementNode, UserTask userTask, String s) {
        String formType = JsonConverterUtil.getPropertyValueAsString(s, elementNode);
        if (StrUtil.isNotBlank(formType)) {
            ExtensionAttribute attribute = new ExtensionAttribute();
            attribute.setName(s);
            attribute.setValue(formType);
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
