package com.aiurt.modules.editor.language.json.converter;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.*;
import org.flowable.editor.language.json.converter.util.JsonConverterUtil;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author fgw
 */
public class CustomSequenceFlowJsonConverter extends SequenceFlowJsonConverter {

    private static final String CUSTOM_CONDITION = "customCondition";
    private static final String PROPERTY = "property";
    private static final String SERVICE = "service";

    /**
     * 注入自定义CustomUserTaskJsonConverter
     */
    public static void fillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap, Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {

        fillJsonTypes(convertersToBpmnMap);
        fillBpmnTypes(convertersToJsonMap);
    }

    public static void fillJsonTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
        convertersToBpmnMap.put(STENCIL_SEQUENCE_FLOW, CustomSequenceFlowJsonConverter.class);
    }

    public static void fillBpmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(SequenceFlow.class, CustomSequenceFlowJsonConverter.class);
    }

    /**
     *  Element和Json转换方法
     * @param converterContext
     * @param baseElement
     * @param processor
     * @param model
     * @param container
     * @param shapesArrayNode
     * @param subProcessX
     * @param subProcessY
     */
    @Override
    public void convertToJson(BpmnJsonConverterContext converterContext, BaseElement baseElement, ActivityProcessor processor, BpmnModel model,
                                             FlowElementsContainer container, ArrayNode shapesArrayNode, double subProcessX, double subProcessY)  {
        SequenceFlow sequenceFlow = (SequenceFlow) baseElement;
        ObjectNode flowNode = BpmnJsonConverterUtil.createChildShape(sequenceFlow.getId(), STENCIL_SEQUENCE_FLOW, 172, 212, 128, 212);
        ArrayNode dockersArrayNode = objectMapper.createArrayNode();
        ObjectNode dockNode = objectMapper.createObjectNode();
        dockNode.put(EDITOR_BOUNDS_X, model.getGraphicInfo(sequenceFlow.getSourceRef()).getWidth() / 2.0);
        dockNode.put(EDITOR_BOUNDS_Y, model.getGraphicInfo(sequenceFlow.getSourceRef()).getHeight() / 2.0);
        dockersArrayNode.add(dockNode);

        if (model.getFlowLocationGraphicInfo(sequenceFlow.getId()).size() > 2) {
            for (int i = 1; i < model.getFlowLocationGraphicInfo(sequenceFlow.getId()).size() - 1; i++) {
                GraphicInfo graphicInfo = model.getFlowLocationGraphicInfo(sequenceFlow.getId()).get(i);
                dockNode = objectMapper.createObjectNode();
                dockNode.put(EDITOR_BOUNDS_X, graphicInfo.getX());
                dockNode.put(EDITOR_BOUNDS_Y, graphicInfo.getY());
                dockersArrayNode.add(dockNode);
            }
        }

        dockNode = objectMapper.createObjectNode();
        dockNode.put(EDITOR_BOUNDS_X, model.getGraphicInfo(sequenceFlow.getTargetRef()).getWidth() / 2.0);
        dockNode.put(EDITOR_BOUNDS_Y, model.getGraphicInfo(sequenceFlow.getTargetRef()).getHeight() / 2.0);
        dockersArrayNode.add(dockNode);
        flowNode.set("dockers", dockersArrayNode);
        ArrayNode outgoingArrayNode = objectMapper.createArrayNode();
        outgoingArrayNode.add(BpmnJsonConverterUtil.createResourceNode(sequenceFlow.getTargetRef()));
        flowNode.set("outgoing", outgoingArrayNode);
        flowNode.set("target", BpmnJsonConverterUtil.createResourceNode(sequenceFlow.getTargetRef()));

        ObjectNode propertiesNode = objectMapper.createObjectNode();
        propertiesNode.put(PROPERTY_OVERRIDE_ID, sequenceFlow.getId());
        if (StringUtils.isNotEmpty(sequenceFlow.getName())) {
            propertiesNode.put(PROPERTY_NAME, sequenceFlow.getName());
        }

        if (StringUtils.isNotEmpty(sequenceFlow.getDocumentation())) {
            propertiesNode.put(PROPERTY_DOCUMENTATION, sequenceFlow.getDocumentation());
        }

        if (StringUtils.isNotEmpty(sequenceFlow.getConditionExpression())) {
            propertiesNode.put(PROPERTY_SEQUENCEFLOW_CONDITION, sequenceFlow.getConditionExpression());
        }

        if (StringUtils.isNotEmpty(sequenceFlow.getSourceRef())) {

            FlowElement sourceFlowElement = container.getFlowElement(sequenceFlow.getSourceRef());
            if (sourceFlowElement != null) {
                String defaultFlowId = null;
                if (sourceFlowElement instanceof ExclusiveGateway) {
                    ExclusiveGateway parentExclusiveGateway = (ExclusiveGateway) sourceFlowElement;
                    defaultFlowId = parentExclusiveGateway.getDefaultFlow();
                } else if (sourceFlowElement instanceof InclusiveGateway) {
                    InclusiveGateway parentInclusiveGateway = (InclusiveGateway) sourceFlowElement;
                    defaultFlowId = parentInclusiveGateway.getDefaultFlow();
                } else if (sourceFlowElement instanceof Activity) {
                    Activity parentActivity = (Activity) sourceFlowElement;
                    defaultFlowId = parentActivity.getDefaultFlow();
                }

                if (defaultFlowId != null && defaultFlowId.equals(sequenceFlow.getId())) {
                    propertiesNode.put(PROPERTY_SEQUENCEFLOW_DEFAULT, true);
                }

            }
        }

        setPropertyValue(PROPERTY_SKIP_EXPRESSION, sequenceFlow.getSkipExpression(), propertiesNode);

        if (sequenceFlow.getExecutionListeners().size() > 0) {
            BpmnJsonConverterUtil.convertListenersToJson(sequenceFlow.getExecutionListeners(), true, propertiesNode);
        }

        Map<String, List<ExtensionElement>> extensionElements = baseElement.getExtensionElements();
        List<ExtensionElement> customConditionElements = extensionElements.get(CUSTOM_CONDITION);
        if (CollUtil.isNotEmpty(customConditionElements)) {

            ObjectNode objectNode = super.objectMapper.createObjectNode();
            ExtensionElement e = customConditionElements.get(0);
            objectNode.put("type", e.getAttributeValue(null, "type"));
            objectNode.put("operationType", e.getAttributeValue(null, "operationType"));

            flowNode.set(CUSTOM_CONDITION, objectNode);
        }

        List<ExtensionElement> propertyElements = extensionElements.get(PROPERTY);

        if (CollUtil.isNotEmpty(propertyElements)) {
            ObjectNode objectNode = super.objectMapper.createObjectNode();
            ExtensionElement e = propertyElements.get(0);
            objectNode.put("value", e.getAttributeValue(null, "value"));
            objectNode.put("service", e.getAttributeValue(null, "service"));
            objectNode.put("name", e.getAttributeValue(null, "name"));
            flowNode.set(PROPERTY, objectNode);
        }

        List<ExtensionElement> serviceElementList = extensionElements.get(SERVICE);
        if (CollUtil.isNotEmpty(serviceElementList)) {
            ObjectNode objectNode = super.objectMapper.createObjectNode();
            ExtensionElement e = serviceElementList.get(0);
            objectNode.put("name", e.getAttributeValue(null, "name"));
            flowNode.set(SERVICE, objectNode);
        }

        flowNode.set(EDITOR_SHAPE_PROPERTIES, propertiesNode);
        shapesArrayNode.add(flowNode);
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
            if (flowElement instanceof SequenceFlow) {
                SequenceFlow sequenceFlow = (SequenceFlow) flowElement;
                addExtansionPropertiesElemt(elementNode, sequenceFlow, PROPERTY);
                addExtansionPropertiesElemt(elementNode, sequenceFlow, CUSTOM_CONDITION);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return flowElement;
    }

    private void addExtansionPropertiesElemt(JsonNode elementNode, SequenceFlow sequenceFlow, String name) throws JsonProcessingException {

        JsonNode expansionNode = elementNode.get(name);
        if (Objects.nonNull(expansionNode)) {
            String json = objectMapper.writeValueAsString(expansionNode);
            JSONObject jsonObject = JSONObject.parseObject(json);
            ExtensionElement ee = new ExtensionElement();
            ee.setName(name);
            ee.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
            ee.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
            Set<String> keySet = jsonObject.keySet();
            keySet.stream().forEach(key-> {
                ExtensionAttribute attribute = new ExtensionAttribute();
                attribute.setName(key);
                attribute.setValue(jsonObject.getString(key));
                ee.addAttribute(attribute);
            });
            sequenceFlow.addExtensionElement(ee);
        }
    }
}
