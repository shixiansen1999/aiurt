package com.aiurt.modules.editor.language.json.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.aiurt.modules.utils.ExtensionPropertiesUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverterContext;
import org.flowable.editor.language.json.converter.UserTaskJsonConverter;
import org.flowable.editor.language.json.converter.util.JsonConverterUtil;
import org.jetbrains.annotations.NotNull;

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
            Map<String, List<ExtensionElement>> extensionElements = baseElement.getExtensionElements();
            //  自定义属性:操作按钮
            List<ExtensionElement> formOperationElements =
                    ExtensionPropertiesUtil.getMyExtensionElementList(extensionElements, OPERATION_LIST, FORM_OPERATION);
            if (CollUtil.isNotEmpty(formOperationElements)) {
                ObjectNode node = super.objectMapper.createObjectNode();
                ArrayNode arrayNode = super.objectMapper.createArrayNode();
                for (ExtensionElement e : formOperationElements) {
                    ObjectNode objectNode = super.objectMapper.createObjectNode();
                    objectNode.put("id", e.getAttributeValue(null, "id"));
                    objectNode.put("label", e.getAttributeValue(null, "label"));
                    objectNode.put("type", e.getAttributeValue(null, "type"));
                    objectNode.put("showOrder", e.getAttributeValue(null, "showOrder"));
                    String multiSignAssignee = e.getAttributeValue(null, "multiSignAssignee");
                    arrayNode.add(objectNode);
                }
                node.set(FORM_OPERATION, arrayNode);
                propertiesNode.set(OPERATION_LIST, node);
            }

            // 流程变量
            List<ExtensionElement> variableElements = ExtensionPropertiesUtil.getMyExtensionElementList(extensionElements, "variableList", "formVariable");
            if (CollUtil.isNotEmpty(variableElements)) {
                ObjectNode node = super.objectMapper.createObjectNode();
                ArrayNode arrayNode = super.objectMapper.createArrayNode();
                for (ExtensionElement e : variableElements) {
                    ObjectNode objectNode = super.objectMapper.createObjectNode();
                    objectNode.put("id", e.getAttributeValue(null, "id"));
                    arrayNode.add(objectNode);
                }
                node.set(FORM_VARIABLE, arrayNode);
                propertiesNode.set("variableList", node);
            }

            // 流程选人, 上级部门以及
            List<ExtensionElement> deptPostElements =
                    ExtensionPropertiesUtil.getMyExtensionElementList(extensionElements, "deptPostList", "deptPost");
            if (CollUtil.isNotEmpty(deptPostElements)) {
                ObjectNode node = super.objectMapper.createObjectNode();
                ArrayNode arrayNode = super.objectMapper.createArrayNode();
                for (ExtensionElement e : deptPostElements) {
                    ObjectNode objectNode = super.objectMapper.createObjectNode();
                    objectNode.put("id", e.getAttributeValue(null, "id"));
                    objectNode.put("type", e.getAttributeValue(null, "type"));
                    objectNode.put("postId", e.getAttributeValue(null, "postId"));
                    objectNode.put("deptPostId", e.getAttributeValue(null, "deptPostId"));
                    arrayNode.add(objectNode);
                }
                node.set("deptPost", arrayNode);
                propertiesNode.set("deptPostList", node);
            }


            List<ExtensionElement> elementCandidateGroupsList = extensionElements.get("userCandidateGroups");
            if (CollUtil.isNotEmpty(elementCandidateGroupsList)) {
                ExtensionElement ee = elementCandidateGroupsList.get(0);
                ObjectNode node = super.objectMapper.createObjectNode();
                node.put("type", ee.getAttributeValue(null, "type"));
                node.put("value", ee.getAttributeValue(null, "value"));
                propertiesNode.set("userCandidateGroups", node);
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
        if (flowElement instanceof UserTask){
            UserTask userTask = (UserTask) flowElement;
            JsonNode expansionNode = JsonConverterUtil.getProperty(OPERATION_LIST, elementNode);

            if (Objects.nonNull(expansionNode)) {
                String json = objectMapper.writeValueAsString(expansionNode);
                log.info("json->{}",json);
                ExtensionElement ee = buildElement(expansionNode, json, OPERATION_LIST, FORM_OPERATION);
                userTask.addExtensionElement(ee);
            }

            JsonNode variableList = JsonConverterUtil.getProperty("variableList", elementNode);

            if (Objects.nonNull(variableList)) {
                String json = objectMapper.writeValueAsString(variableList);
                log.info("json->{}",json);
                ExtensionElement ee = buildElement(variableList, json, "variableList", FORM_VARIABLE);
                userTask.addExtensionElement(ee);
            }

            JsonNode deptPostList = JsonConverterUtil.getProperty("deptPostList", elementNode);

            if (Objects.nonNull(deptPostList)) {
                String json = objectMapper.writeValueAsString(deptPostList);
                log.info("json->{}",json);
                ExtensionElement ee = buildElement(deptPostList, json, "deptPostList", "deptPost");
                userTask.addExtensionElement(ee);
            }

            // 候选用户
            JsonNode userCandidateGroupNode = JsonConverterUtil.getProperty("userCandidateGroups", elementNode);
            if (Objects.nonNull(userCandidateGroupNode)) {
                String json = objectMapper.writeValueAsString(userCandidateGroupNode);

                JSONObject jsonObject = JSONObject.parseObject(json);

                ExtensionElement ee = new ExtensionElement();
                ee.setName("userCandidateGroups");
                ee.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
                ee.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
                Map<String, List<ExtensionAttribute>> attributes = new LinkedHashMap<>();
                Set<String> keySet = jsonObject.keySet();
                keySet.stream().forEach(key-> {
                    ExtensionAttribute attribute = new ExtensionAttribute();
                    attribute.setName(key);
                    attribute.setValue(jsonObject.getString(key));
                    ee.addAttribute(attribute);
                });
                userTask.addExtensionElement(ee);
            }
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



}
