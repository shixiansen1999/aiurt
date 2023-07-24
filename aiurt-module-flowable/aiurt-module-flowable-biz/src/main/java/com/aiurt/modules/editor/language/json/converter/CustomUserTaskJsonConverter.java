package com.aiurt.modules.editor.language.json.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.constant.FlowModelAttConstant;
import com.aiurt.modules.modeler.entity.ActOperationEntity;
import com.aiurt.modules.modeler.entity.ActUserTypeEntity;
import com.aiurt.modules.utils.ExtensionPropertiesUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverterContext;
import org.flowable.editor.language.json.converter.UserTaskJsonConverter;
import org.flowable.editor.language.json.converter.util.JsonConverterUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 扩展任务节点属性解析器
 * @author fgw
 */
@Slf4j
public class CustomUserTaskJsonConverter  extends UserTaskJsonConverter {


    /**
     * 操作按钮, [{"formOperation":{"id":"","label":"","type":"","showOrder":""}}]
     */
    public static final String OPERATION_LIST = "operationList";

    /**
     * 表单操作按钮
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
                ArrayNode arrayNode = convertExtensionElementsToJson(Collections.singletonList(extensionElement), ActUserTypeEntity.class);
                propertiesNode.set(USER_TYPE, arrayNode);
            }
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
            // 候选用户
            addExtensionElementToUserTask(userTask, USER_CANDIDATE_GROUPS, JsonConverterUtil.getProperty(USER_CANDIDATE_GROUPS, elementNode));

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

            // 部门岗位
            JsonNode deptPostList = JsonConverterUtil.getProperty(DEPT_POST_LIST, elementNode);
            addExtensionElementWithJson(userTask, DEPT_POST_LIST, deptPostList);
        }
    }

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
     *  构建xml
     * @param expansionNode nodejson
     * @param json jsonString
     * @param listName 一级名称
     * @param childListName 二级
     * @return
     */
    @NotNull
    private ExtensionElement buildElement(JsonNode expansionNode, String json,String listName, String childListName) {
        ExtensionElement ee = new ExtensionElement();
        ee.setName(listName);
        ee.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
        ee.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
        if (expansionNode instanceof ObjectNode) {
            JSONObject jsonObject = JSONObject.parseObject(json);
            JSONArray operation = jsonObject.getJSONArray(childListName);

            Map<String, List<ExtensionElement>> map = new LinkedHashMap<>();
            List<ExtensionElement> extensionElementList = new ArrayList<>();
            for (int i = 0; i < operation.size(); i++) {
                ExtensionElement child = new ExtensionElement();
                child.setName(childListName);
                child.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
                child.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
                JSONObject object = operation.getJSONObject(i);
                Set<String> keySet = object.keySet();
                keySet.stream().forEach(key->{
                    ExtensionAttribute attribute = new ExtensionAttribute();
                    attribute.setName(key);
                    attribute.setValue(object.getString(key));
                    child.addAttribute(attribute);
                });
                extensionElementList.add(child);
            }
            map.put(childListName, extensionElementList);
            ee.setChildElements(map);
            // 如果是文本
        }else if (expansionNode instanceof TextNode) {
            ee.setElementText(expansionNode.asText());
        }
        return ee;
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

            JSONArray extensionArray = JSONObject.parseArray(json);
            for (int i = 0; i < extensionArray.size(); i++) {
                JSONObject extensionObject = extensionArray.getJSONObject(i);
                ExtensionElement extensionElement = new ExtensionElement();
                extensionElement.setName(extensionName);
                extensionElement.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
                extensionElement.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);

                Set<String> keySet = extensionObject.keySet();
                keySet.forEach(key -> {
                    ExtensionAttribute attribute = new ExtensionAttribute();
                    attribute.setName(key);
                    attribute.setValue(extensionObject.getString(key));
                    extensionElement.addAttribute(attribute);
                });

                userTask.addExtensionElement(extensionElement);
            }
        }
    }

    /**
     * 向用户任务（UserTask）添加带有 JSON 数据的扩展元素（ExtensionElement）。
     *
     * @param userTask     用户任务对象，用于添加扩展元素
     * @param propertyName 扩展元素的属性名称
     * @param jsonNode     扩展元素的 JSON 数据
     * @throws JsonProcessingException 如果 JSON 转换过程中出现异常
     */
    private void addExtensionElementWithJson(UserTask userTask, String propertyName, JsonNode jsonNode) throws JsonProcessingException {
        if (Objects.nonNull(jsonNode)) {
            String json = objectMapper.writeValueAsString(jsonNode);
            log.info("json->{}", json);
            // 这里将 "deptPostList" 用作属性名称和元素名称
            ExtensionElement ee = buildElement(jsonNode, json, propertyName, "deptPost");
            userTask.addExtensionElement(ee);
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
            Arrays.stream(fields).filter(field -> !StrUtil.equals("serialVersionUID", field.getName())).forEach(field -> {
                objectNode.put(field.getName(), e.getAttributeValue(null, field.getName()));
            });
            arrayNode.add(objectNode);
        }
        return arrayNode;
    }

}
