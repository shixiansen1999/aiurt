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

import java.util.*;

/**
 * 扩展任务节点属性解析器
 * @author fgw
 */
@Slf4j
public class CustomUserTaskJsonConverter  extends UserTaskJsonConverter {

    public static final String ASSIGNEE_TYPE = "assigneeType";
    public static final String IDM_ASSIGNEE = "idmAssignee";
    public static final String IDM_CANDIDATE_GROUPS = "idmCandidateGroups";
    public static final String IDM_CANDIDATE_USERS = "idmCandidateUsers";
    public static final String IS_EDITDATA = "isEditdata";
    public static final String NODE_TYPE = "nodeType";
    public static final String NEXT_SEQUENCE_FLOW_LABEL = "nextSequenceFlow";
    public static final String NEXT_USER_LABEL = "nextUser";

    /**
     * 操作按钮, [{"formOperation":{"id":"","label":"","type":"","showOrder":""}}]
     */
    public static final String OPERATION_LIST = "operationList";





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
            final String[] text = new String[8];
            baseElement.getExtensionElements().forEach((s, elements) -> elements.forEach(extensionElement -> {
                if (ASSIGNEE_TYPE.equals(extensionElement.getName())){
                    text[0] = extensionElement.getElementText();
                }
                if (IDM_ASSIGNEE.equals(extensionElement.getName())){
                    text[1] = extensionElement.getElementText();
                }
                if (IDM_CANDIDATE_GROUPS.equals(extensionElement.getName())){
                    text[2] = extensionElement.getElementText();
                }
                if (IDM_CANDIDATE_USERS.equals(extensionElement.getName())){
                    text[3] = extensionElement.getElementText();
                }
                if (IS_EDITDATA.equals(extensionElement.getName())){
                    text[4] = extensionElement.getElementText();
                }
                if (NODE_TYPE.equals(extensionElement.getName())){
                    text[5] = extensionElement.getElementText();
                }
                if (NEXT_SEQUENCE_FLOW_LABEL.equals(extensionElement.getName())) {
                    text[6] = extensionElement.getElementText();
                }
                if (NEXT_USER_LABEL.equals(extensionElement.getName())) {
                    text[7] = extensionElement.getElementText();
                }
            }));
            //  自定义属性:操作按钮
            List<ExtensionElement> formOperationElements =
                    this.getMyExtensionElementList(baseElement.getExtensionElements(), "operationList", "formOperation");
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
                node.set("formOperation", arrayNode);
                propertiesNode.set(OPERATION_LIST, node);
            }

            if (StringUtils.isNotBlank(text[0])){
                propertiesNode.put(ASSIGNEE_TYPE, text[0]);
            }
            if (StringUtils.isNotBlank(text[1])){
                propertiesNode.put(IDM_ASSIGNEE, text[1]);
            }
            if (StringUtils.isNotBlank(text[2])){
                propertiesNode.put(IDM_CANDIDATE_GROUPS, text[2]);
            }
            if (StringUtils.isNotBlank(text[3])){
                propertiesNode.put(IDM_CANDIDATE_USERS, text[3]);
            }
            if (StringUtils.isNotBlank(text[4])){
                propertiesNode.put(IS_EDITDATA, text[4]);
            }
            if (StringUtils.isNotBlank(text[5])){
                propertiesNode.put(NODE_TYPE, text[5]);
            }
            if (StringUtils.isNotBlank(text[6])) {
                propertiesNode.put(NEXT_SEQUENCE_FLOW_LABEL, text[6]);
            }

            if (StringUtils.isNotBlank(text[7])) {
                propertiesNode.put(NEXT_USER_LABEL, text[7]);
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
            String json = objectMapper.writeValueAsString(expansionNode);
            log.info("json->{}",json);
            if (Objects.nonNull(expansionNode)) {
                ExtensionElement ee = new ExtensionElement();
                ee.setName(OPERATION_LIST);
                ee.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
                ee.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
                if (expansionNode instanceof ObjectNode) {
                    JSONObject jsonObject = JSONObject.parseObject(json);
                    JSONArray operation = jsonObject.getJSONArray("formOperation");
                    ExtensionElement child = new ExtensionElement();
                    child.setName("formOperation");
                    child.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
                    child.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
                    Map<String, List<ExtensionElement>> map = new LinkedHashMap<>();
                    List<ExtensionElement> extensionElementList = new ArrayList<>();
                    for (int i = 0; i < operation.size(); i++) {
                        JSONObject object = operation.getJSONObject(i);
                        Set<String> keySet = object.keySet();
                        keySet.stream().forEach(key->{
                            ExtensionAttribute attribute = new ExtensionAttribute();
                            attribute.setName(key);
                            attribute.setValue(object.getString(key));
                            child.addAttribute(attribute);
                        });
                    }
                    extensionElementList.add(child);
                    map.put("formOperation", extensionElementList);
                    ee.setChildElements(map);
                    // 如果是文本
                }else if (expansionNode instanceof TextNode) {
                    ee.setElementText(expansionNode.asText());
                }
                userTask.addExtensionElement(ee);
            }
        }
    }


    private List<ExtensionElement> getMyExtensionElementList(
            Map<String, List<ExtensionElement>> extensionMap, String rootName, String childName) {
        List<ExtensionElement> elementList = extensionMap.get(rootName);
        if (CollUtil.isEmpty(elementList)) {
            return null;
        }
        ExtensionElement ee = elementList.get(0);
        Map<String, List<ExtensionElement>> childExtensionMap = ee.getChildElements();
        if (MapUtil.isEmpty(childExtensionMap)) {
            return null;
        }
        List<ExtensionElement> childrenElements = childExtensionMap.get(childName);
        if (CollUtil.isEmpty(childrenElements)) {
            return null;
        }
        return childrenElements;
    }
}
