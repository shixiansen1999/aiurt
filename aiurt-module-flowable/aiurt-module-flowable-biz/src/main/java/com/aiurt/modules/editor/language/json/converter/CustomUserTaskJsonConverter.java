package com.aiurt.modules.editor.language.json.converter;

import com.aiurt.modules.utils.ExtensionPropertiesUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverterContext;
import org.flowable.editor.language.json.converter.UserTaskJsonConverter;

import java.util.List;
import java.util.Map;

/**
 * 扩展任务节点属性解析器
 * @author fgw
 */
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

                ObjectNode node = objectMapper.createObjectNode();
                //  自定义属性:操作按钮
                if (OPERATION_LIST.equalsIgnoreCase(extensionElement.getName())) {
                    ArrayNode arrayNode = super.objectMapper.createArrayNode();
                    Map<String, List<ExtensionElement>> childElements = extensionElement.getChildElements();
                    childElements.forEach((key,e)->{

                        ExtensionElement extensionElement1 = e.get(0);
                        Map<String, List<ExtensionAttribute>> attributes = extensionElement1.getAttributes();

                        String id = extensionElement1.getAttributeValue(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE, "id");
                    });
                    node.set("formOperation",arrayNode);

                    //node.set(OPERATION_LIST, arrayNode);
                    propertiesNode.set(OPERATION_LIST, node);
                }

            }));
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
     *  //重写Json和UserTask Element转换方法
     * @param elementNode
     * @param modelNode
     * @param shapeMap
     * @param converterContex
     * @return
     */
    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap, BpmnJsonConverterContext converterContex) {
        FlowElement flowElement = super.convertJsonToElement(elementNode, modelNode, shapeMap, converterContex);
        this.addExtansionPropertiesElement(flowElement, elementNode);
        return flowElement;
    }

    private void addExtansionPropertiesElement(FlowElement flowElement, JsonNode elementNode) {
        if (flowElement instanceof UserTask){
            UserTask userTask = (UserTask) flowElement;
            ExtensionPropertiesUtil.addExtensionPropertiesElement(elementNode, userTask, ASSIGNEE_TYPE);
            ExtensionPropertiesUtil.addExtensionPropertiesElement(elementNode, userTask, IDM_ASSIGNEE);
            ExtensionPropertiesUtil.addExtensionPropertiesElement(elementNode, userTask, IDM_CANDIDATE_GROUPS);
            ExtensionPropertiesUtil.addExtensionPropertiesElement(elementNode, userTask, IDM_CANDIDATE_USERS);
            ExtensionPropertiesUtil.addExtensionPropertiesElement(elementNode, userTask, IS_EDITDATA);
            ExtensionPropertiesUtil.addExtensionPropertiesElement(elementNode, userTask, NODE_TYPE);
            ExtensionPropertiesUtil.addExtensionPropertiesElement(elementNode, userTask, NEXT_SEQUENCE_FLOW_LABEL);
            ExtensionPropertiesUtil.addExtensionPropertiesElement(elementNode, userTask, NEXT_USER_LABEL);
        }
    }
}
