package com.aiurt.modules.editor.language.json.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.constant.FlowModelAttConstant;
import com.aiurt.modules.common.enums.FlowConditionEnum;
import com.aiurt.modules.common.enums.FlowConditionTypeEnum;
import com.aiurt.modules.constants.FlowConstant;
import com.aiurt.modules.modeler.dto.FlowConditionDTO;
import com.aiurt.modules.modeler.entity.ActOperationEntity;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import liquibase.pro.packaged.V;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.*;
import org.flowable.editor.language.json.converter.util.JsonConverterUtil;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author fgw
 */
@Slf4j
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
            // 获取表达式
            String conditionExpression = sequenceFlow.getConditionExpression();

            propertiesNode.put(PROPERTY_SEQUENCEFLOW_CONDITION, conditionExpression);
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

        // 按钮流转条件
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
            objectNode.put("name", e.getAttributeValue(null, "name"));
            flowNode.set(PROPERTY, objectNode);
        }

        List<ExtensionElement> serviceElementList = extensionElements.get(SERVICE);
        if (CollUtil.isNotEmpty(serviceElementList)) {
            ObjectNode objectNode = super.objectMapper.createObjectNode();
            ExtensionElement e = serviceElementList.get(0);
            objectNode.put(FlowModelAttConstant.NAME, e.getAttributeValue(null, FlowModelAttConstant.NAME));
            flowNode.set(SERVICE, objectNode);
        }

        // 优化的条件表达式修改, "flowCondition":[{},{}]
        List<ExtensionElement> flowConditionElementList = extensionElements.getOrDefault(FlowModelAttConstant.FLOW_CONDITION, new ArrayList<>());
        if (CollUtil.isNotEmpty(flowConditionElementList)) {
            ArrayNode arrayNode = super.objectMapper.createArrayNode();
            flowConditionElementList.stream().forEach(extensionElement -> {

                ObjectNode objectNode = super.objectMapper.createObjectNode();
                Class clazz = FlowConditionDTO.class;
                Field[] fields = clazz.getDeclaredFields();
                Arrays.stream(fields).filter(field -> !StrUtil.equals("serialVersionUID", field.getName())).forEach(field -> {
                    objectNode.put(field.getName(), extensionElement.getAttributeValue(null, field.getName()));
                });
                arrayNode.add(objectNode);
            });
            // 设计 <![CDATA[${var:equals(name,"李四") && var:lt(money, 100)}]]
            try {
                String condition = super.objectMapper.writeValueAsString(arrayNode);
                List<FlowConditionDTO> dtoList = JSONObject.parseArray(condition, FlowConditionDTO.class);
                StringBuilder exp = new StringBuilder();
                for (int i = 0; i < dtoList.size(); i++) {
                    FlowConditionDTO flowConditionDTO = dtoList.get(i);
                    // 为空，不为空, 还要考虑类型 ${var:value(
                    exp.append("${var:").append(flowConditionDTO.getCondition()).append("(");
                    if (StrUtil.equalsAnyIgnoreCase(flowConditionDTO.getCondition(), FlowConditionEnum.IS_NOT_EMPTY.getCode(), FlowConditionEnum.EMPTY.getCode())) {
                        exp.append(flowConditionDTO.getCode()).append(")");
                        // 等于，不等于
                    }else if (StrUtil.equalsAnyIgnoreCase(flowConditionDTO.getCondition(), FlowConditionEnum.EQ.getCode(), FlowConditionEnum.NOT_EQUALS.getCode())) {
                        exp.append(flowConditionDTO.getCode()).append(",");
                        // 字符串
                        if (StrUtil.equalsAnyIgnoreCase(flowConditionDTO.getType(), FlowConditionTypeEnum.STRING.getCode())) {
                           exp.append("\"").append(flowConditionDTO.getValue()).append("\")");
                        } else {
                           exp.append(flowConditionDTO.getValue()).append(")");
                        }

                    } else if (StrUtil.equalsAnyIgnoreCase(flowConditionDTO.getCondition(), FlowConditionEnum.CONTAINS_ANY.getCode())) {
                        exp.append(flowConditionDTO.getCode()).append(",");
                        // 字符串
                        if (StrUtil.equalsAnyIgnoreCase(flowConditionDTO.getType(), FlowConditionTypeEnum.STRING.getCode())) {
                            exp.append("\"").append(flowConditionDTO.getValue()).append("\")");
                        } else {
                            String value = flowConditionDTO.getValue();
                            List<String> list = StrUtil.split(value, ';');
                            String join = StrUtil.join("\",\"", list);
                            exp.append("\"").append(join).append("\")");
                        }
                        // 大于，大于等于 ，小于，小于等于
                    } else {
                        exp.append(flowConditionDTO.getCode()).append(",").append(flowConditionDTO.getValue()).append(")");
                    }
                    if (dtoList.size()-1 != i) {
                        exp.append(" ").append(flowConditionDTO.getCondition()).append(" ");
                    }
                }
                propertiesNode.put(PROPERTY_SEQUENCEFLOW_CONDITION, String.format("<![CDATA[%s]]", exp.toString()));
            } catch (JsonProcessingException e) {
                log.error(e.getMessage(), e);
            }
            flowNode.set(FlowModelAttConstant.FLOW_CONDITION, arrayNode);
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
                addExtansionPropertiesElemt(elementNode, sequenceFlow, SERVICE);

                // 流转条件
                JsonNode expansionNode = elementNode.get(FlowModelAttConstant.FLOW_CONDITION);
               // JsonNode expansionNode = JsonConverterUtil.getProperty(FlowModelAttConstant.FLOW_CONDITION, elementNode);
                if (Objects.nonNull(expansionNode)) {
                    String json = super.objectMapper.writeValueAsString(expansionNode);
                    log.info("json->{}",json);
                    JSONArray jsonArray = JSONObject.parseArray(json);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        ExtensionElement ee = new ExtensionElement();
                        ee.setName(FlowModelAttConstant.FLOW_CONDITION);
                        ee.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
                        ee.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
                        Set<String> keySet = jsonObject.keySet();
                        keySet.stream().forEach(key-> {
                            ExtensionAttribute attribute = new ExtensionAttribute();
                            attribute.setName(key);
                            attribute.setValue(jsonObject.getString(key));
                            ee.addAttribute(attribute);
                        });
                        flowElement.addExtensionElement(ee);
                    }
                }
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
