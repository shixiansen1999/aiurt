package com.aiurt.modules.editor.language.json.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.constant.FlowModelAttConstant;
import com.aiurt.modules.common.enums.FlowConditionEnum;
import com.aiurt.modules.common.enums.FlowConditionTypeEnum;
import com.aiurt.modules.constants.FlowConstant;
import com.aiurt.modules.modeler.dto.FlowConditionDTO;
import com.aiurt.modules.modeler.dto.FlowRelationDTO;
import com.aiurt.modules.modeler.dto.RelationMaps;
import com.aiurt.modules.modeler.entity.ActOperationEntity;
import com.aiurt.modules.utils.FlowRelationUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.datical.liquibase.ext.util.ObjectSqlFileUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
     * 将SequenceFlow对象转换为JSON格式，并添加到shapesArrayNode中。
     *
     * @param converterContext    BpmnJsonConverterContext对象，用于转换过程中的上下文信息
     * @param baseElement         要转换的BaseElement对象
     * @param processor           ActivityProcessor对象，用于处理活动元素的特定属性
     * @param model               BpmnModel对象，表示整个BPMN模型
     * @param container           FlowElementsContainer对象，表示包含SequenceFlow的容器元素
     * @param shapesArrayNode     ArrayNode对象，用于存储转换后的JSON数据
     * @param subProcessX         子流程的X坐标
     * @param subProcessY         子流程的Y坐标
     */
    @Override
    public void convertToJson(BpmnJsonConverterContext converterContext, BaseElement baseElement, ActivityProcessor processor, BpmnModel model,
                              FlowElementsContainer container, ArrayNode shapesArrayNode, double subProcessX, double subProcessY) {
        SequenceFlow sequenceFlow = (SequenceFlow) baseElement;
        ObjectNode flowNode = BpmnJsonConverterUtil.createChildShape(sequenceFlow.getId(), STENCIL_SEQUENCE_FLOW, 172, 212, 128, 212);
        ArrayNode dockersArrayNode = objectMapper.createArrayNode();

        // 设置Source节点
        ObjectNode sourceDockNode  = objectMapper.createObjectNode();
        sourceDockNode.put(EDITOR_BOUNDS_X, model.getGraphicInfo(sequenceFlow.getSourceRef()).getWidth() / 2.0);
        sourceDockNode.put(EDITOR_BOUNDS_Y, model.getGraphicInfo(sequenceFlow.getSourceRef()).getHeight() / 2.0);
        dockersArrayNode.add(sourceDockNode);

        // 设置中间节点
        if (model.getFlowLocationGraphicInfo(sequenceFlow.getId()).size() > 2) {
            for (int i = 1; i < model.getFlowLocationGraphicInfo(sequenceFlow.getId()).size() - 1; i++) {
                GraphicInfo graphicInfo = model.getFlowLocationGraphicInfo(sequenceFlow.getId()).get(i);
                ObjectNode middleDockNode = objectMapper.createObjectNode();
                middleDockNode.put(EDITOR_BOUNDS_X, graphicInfo.getX());
                middleDockNode.put(EDITOR_BOUNDS_Y, graphicInfo.getY());
                dockersArrayNode.add(middleDockNode);
            }
        }

        // 设置Target节点
        ObjectNode targetDockNode = objectMapper.createObjectNode();
        targetDockNode.put(EDITOR_BOUNDS_X, model.getGraphicInfo(sequenceFlow.getTargetRef()).getWidth() / 2.0);
        targetDockNode.put(EDITOR_BOUNDS_Y, model.getGraphicInfo(sequenceFlow.getTargetRef()).getHeight() / 2.0);
        dockersArrayNode.add(targetDockNode);

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
            propertiesNode.put(PROPERTY_SEQUENCEFLOW_CONDITION, sequenceFlow.getConditionExpression());
        }

        // 检查给定的SequenceFlow是否为其源FlowElement的默认流向，并将结果设置到propertiesNode中。
        checkAndSetDefaultFlow(sequenceFlow, container, propertiesNode);

        setPropertyValue(PROPERTY_SKIP_EXPRESSION, sequenceFlow.getSkipExpression(), propertiesNode);

        // 将SequenceFlow的执行监听器转换为JSON格式，并将结果添加到propertiesNode中。
        convertExecutionListenersToJson(sequenceFlow, propertiesNode);

        // 按钮流转条件
        Map<String, List<ExtensionElement>> extensionElements = baseElement.getExtensionElements();
        convertPropertyElements(extensionElements, CUSTOM_CONDITION, flowNode, "type", "operationType");
        convertPropertyElements(extensionElements, PROPERTY, flowNode, "value", "name");
        convertPropertyElements(extensionElements, SERVICE, flowNode, FlowModelAttConstant.NAME);

        // 处理流程条件
        RelationMaps relationMaps = new RelationMaps();
        List<ExtensionElement> flowConditionElementList = extensionElements.getOrDefault(FlowModelAttConstant.FLOW_CONDITION, new ArrayList<>());
        if (CollUtil.isNotEmpty(flowConditionElementList)) {
            ArrayNode arrayNode = convertFlowConditionsToArrayNode(flowConditionElementList);
            relationMaps = generateNumberRelationMaps(FlowRelationUtil.parseJsonToList(arrayNode, FlowConditionDTO.class));
            flowNode.set(FlowModelAttConstant.FLOW_CONDITION, arrayNode);
        }

        // 条件关系
        String relationValue = null;
        ObjectNode flowRelationObjectNode = null;
        List<ExtensionElement> flowRelationElementList = extensionElements.getOrDefault(FlowModelAttConstant.FLOW_RELATION, new ArrayList<>());
        if (CollUtil.isNotEmpty(flowRelationElementList)) {
            ExtensionElement extensionElement = flowRelationElementList.get(0);
            flowRelationObjectNode = FlowRelationUtil.createObjectNodeFromFields(FlowRelationDTO.class, extensionElement);
            FlowRelationDTO flowRelationDto = FlowRelationUtil.parseJsonToObject(flowRelationObjectNode, FlowRelationDTO.class);
            relationValue = ObjectUtil.isNotEmpty(flowRelationDto) ? flowRelationDto.getValue() : null;
        }

        // 处理条件关系表达式,设计 <![CDATA[${var:equals(name,"李四")} && ${var:lt(money, 100)}]]
        String replacedExpressionWithStr = FlowRelationUtil.replacePlaceholders(relationValue, relationMaps.getNumberRelationMap());
        String replacedExpressionWithNameStr = FlowRelationUtil.replacePlaceholders(relationValue, relationMaps.getNumberRelationNameMap());
        String processedExpressionWithStr = FlowRelationUtil.replaceOperators(replacedExpressionWithStr, "||", "&&");
        String processedExpressionWithNameStr = FlowRelationUtil.replaceOperators(replacedExpressionWithNameStr, "或者", "并且");

        propertiesNode.put(PROPERTY_SEQUENCEFLOW_CONDITION, String.format("<![CDATA[%s]]", processedExpressionWithStr));
        flowNode.set(EDITOR_SHAPE_PROPERTIES, propertiesNode);
        flowRelationObjectNode.put("relationAlias", processedExpressionWithNameStr);
        flowNode.set(FlowModelAttConstant.FLOW_RELATION, flowRelationObjectNode);
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
                if (Objects.nonNull(expansionNode)) {
                    String json = super.objectMapper.writeValueAsString(expansionNode);
                    log.info("json->{}", json);
                    JSONArray jsonArray = JSONObject.parseArray(json);
                    for (int i = 0; i < jsonArray.size(); i++) {
                        addExtensionElementFromJson(flowElement, jsonArray.getJSONObject(i), FlowModelAttConstant.FLOW_CONDITION);
                    }
                }

                // 流转条件关系
                JsonNode relationNode = elementNode.get(FlowModelAttConstant.FLOW_RELATION);
                if (ObjectUtil.isNotEmpty(relationNode)) {
                    addExtansionPropertiesElemt(relationNode, sequenceFlow, FlowModelAttConstant.FLOW_RELATION);
                }

            }
        } catch (JsonProcessingException e) {
            log.error("将JSON数据转换为扩展元素时发生错误: {}", e.getMessage());
        }
        return flowElement;
    }

    /**
     * 从JsonNode中提取指定扩展属性的信息，并将其作为ExtensionElement添加到SequenceFlow对象的扩展元素集合中。
     *
     * @param elementNode  JsonNode对象，包含扩展属性的信息
     * @param sequenceFlow SequenceFlow对象，用于添加扩展元素
     * @param name         扩展属性的名称，用于标识不同的扩展元素
     * @throws JsonProcessingException 如果处理JSON数据时发生异常
     */
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

    /**
     * 生成 numberRelationMap 和 numberRelationNameMap。
     *
     * @param dtoList  FlowConditionDTO 对象列表，包含要生成的数据。
     * @return 封装了 numberRelationMap 和 numberRelationNameMap 的 RelationMaps 对象。
     */
    public RelationMaps generateNumberRelationMaps(List<FlowConditionDTO> dtoList) {
        RelationMaps relationMaps = new RelationMaps();

        for (FlowConditionDTO flowConditionDTO : dtoList) {
            String exp = generateExpression(flowConditionDTO);
            relationMaps.addNumberRelation(flowConditionDTO.getNumber(), String.format("${var:%s(%s)}", exp));

            FlowConditionEnum flowConditionEnum = FlowConditionEnum.getByCode(flowConditionDTO.getCode());
            String name = ObjectUtil.isNotEmpty(flowConditionEnum) ? flowConditionEnum.getName() : "";
            relationMaps.addNumberRelationName(flowConditionDTO.getNumber(), String.format("%s %s %s", flowConditionDTO.getName(), name, flowConditionDTO.getValue()));
        }

        return relationMaps;
    }

    /**
     * 根据 FlowConditionDTO 对象生成对应的表达式字符串 exp。
     *
     * @param flowConditionDTO  FlowConditionDTO 对象，包含条件数据。
     * @return 表达式字符串 exp。
     */
    private String generateExpression(FlowConditionDTO flowConditionDTO) {
        if (StrUtil.equalsAnyIgnoreCase(flowConditionDTO.getCondition(), FlowConditionEnum.IS_NOT_EMPTY.getCode(), FlowConditionEnum.EMPTY.getCode())) {
            return String.format("%s", flowConditionDTO.getCode());
        } else if (StrUtil.equalsAnyIgnoreCase(flowConditionDTO.getCondition(), FlowConditionEnum.EQ.getCode(), FlowConditionEnum.NOT_EQUALS.getCode())) {
            return String.format("%s,\"%s\"", flowConditionDTO.getCode(), flowConditionDTO.getValue());
        } else if (StrUtil.equalsAnyIgnoreCase(flowConditionDTO.getCondition(), FlowConditionEnum.CONTAINS_ANY.getCode())) {
            String value = flowConditionDTO.getValue();
            List<String> list = StrUtil.split(value, ';');
            String result = StrUtil.join("\",\"", list);
            return String.format("%s,\"%s\"", flowConditionDTO.getCode(), result);
        } else {
            return String.format("%s,%s", flowConditionDTO.getCode(), flowConditionDTO.getValue());
        }
    }

    /**
     * 将扩展元素列表中的属性元素转换为 ObjectNode 对象，并将其设置到流程节点中。
     *
     * @param extensionElements 扩展元素列表，包含要处理的属性元素。
     * @param flowNodeProperty  流程节点中属性的名称，用于标识转换后的 ObjectNode 对象。
     * @param flowNode          要设置属性的流程节点 ObjectNode 对象。
     * @param inputStrings      可变参数，包含多个输入字符串，用于进一步处理属性元素的值（可选）。
     */
    public void convertPropertyElements(Map<String, List<ExtensionElement>> extensionElements, String flowNodeProperty, ObjectNode flowNode,String... inputStrings) {
        List<ExtensionElement> propertyElements = extensionElements.get(flowNodeProperty);
        if (CollUtil.isNotEmpty(propertyElements)) {
            ObjectMapper objectMapper = new ObjectMapper();
            ObjectNode objectNode = objectMapper.createObjectNode();
            ExtensionElement e = propertyElements.get(0);
            for (String str : inputStrings) {
                objectNode.put(str, e.getAttributeValue(null, str));
            }
            flowNode.set(flowNodeProperty, objectNode);
        }
    }

    /**
     * 根据流程扩展元素中的条件配置，生成关系映射对象。
     *
     * @param extensionElements 流程扩展元素，包含条件配置信息。
     * @return 生成的关系映射对象 RelationMaps。
     */
    public RelationMaps generateRelationMapsFromExtension(Map<String, List<ExtensionElement>> extensionElements) {
        List<ExtensionElement> flowConditionElementList = extensionElements.getOrDefault(FlowModelAttConstant.FLOW_CONDITION, new ArrayList<>());
        RelationMaps relationMaps = new RelationMaps();

        if (CollUtil.isNotEmpty(flowConditionElementList)) {
            ArrayNode arrayNode = super.objectMapper.createArrayNode();
            flowConditionElementList.forEach(extensionElement -> {
                ObjectNode objectNode = FlowRelationUtil.createObjectNodeFromFields(FlowConditionDTO.class, extensionElement);
                arrayNode.add(objectNode);
            });

            List<FlowConditionDTO> dtoList = FlowRelationUtil.parseJsonToList(arrayNode, FlowConditionDTO.class);
            relationMaps = generateNumberRelationMaps(dtoList);
        }

        return relationMaps;
    }

    /**
     * 将流程扩展元素列表中的条件配置转换为 ArrayNode 对象。
     *
     * @param flowConditionElementList 流程扩展元素列表，包含条件配置信息。
     * @return 转换后的 ArrayNode 对象。
     */
    public ArrayNode convertFlowConditionsToArrayNode(List<ExtensionElement> flowConditionElementList) {
        ArrayNode arrayNode = super.objectMapper.createArrayNode();
        flowConditionElementList.forEach(extensionElement -> {
            ObjectNode objectNode = FlowRelationUtil.createObjectNodeFromFields(FlowConditionDTO.class, extensionElement);
            arrayNode.add(objectNode);
        });
        return arrayNode;
    }

    /**
     * 检查给定的SequenceFlow是否为其源FlowElement的默认流向，并将结果设置到propertiesNode中。
     *
     * @param sequenceFlow    要检查的SequenceFlow
     * @param container       包含SequenceFlow和FlowElement的容器
     * @param propertiesNode  包含属性信息的ObjectNode，用于存储检查结果
     */
    public void checkAndSetDefaultFlow(SequenceFlow sequenceFlow, FlowElementsContainer container, ObjectNode propertiesNode) {
        // 检查SequenceFlow的源FlowElement是否存在
        if (StringUtils.isNotEmpty(sequenceFlow.getSourceRef())) {
            FlowElement sourceFlowElement = container.getFlowElement(sequenceFlow.getSourceRef());
            if (sourceFlowElement != null) {
                String defaultFlowId = null;
                // 根据源FlowElement的类型获取其默认流向的ID
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

                // 检查SequenceFlow的ID是否与源FlowElement的默认流向ID相匹配，并设置结果到propertiesNode中
                if (defaultFlowId != null && defaultFlowId.equals(sequenceFlow.getId())) {
                    propertiesNode.put(PROPERTY_SEQUENCEFLOW_DEFAULT, true);
                }
            }
        }
    }

    /**
     * 将SequenceFlow的执行监听器转换为JSON格式，并将结果添加到propertiesNode中。
     *
     * @param sequenceFlow       要转换监听器的SequenceFlow对象
     * @param propertiesNode     包含属性信息的ObjectNode，用于存储转换后的JSON数据
     */
    public void convertExecutionListenersToJson(SequenceFlow sequenceFlow, ObjectNode propertiesNode) {
        // 检查SequenceFlow的执行监听器是否为空
        if (CollUtil.isNotEmpty(sequenceFlow.getExecutionListeners())) {
            // 将SequenceFlow的执行监听器转换为JSON格式，并将结果添加到propertiesNode中
            BpmnJsonConverterUtil.convertListenersToJson(sequenceFlow.getExecutionListeners(), true, propertiesNode);
        }
    }

    /**
     * 从JSON对象中提取信息，并添加为扩展元素到指定的FlowElement对象中。
     *
     * @param flowElement FlowElement对象，用于添加扩展元素
     * @param jsonObject  包含扩展元素信息的JSON对象
     * @param elementName 扩展元素的名称
     */
    private void addExtensionElementFromJson(FlowElement flowElement, JSONObject jsonObject, String elementName) {
        ExtensionElement ee = new ExtensionElement();
        ee.setName(elementName);
        ee.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
        ee.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
        Set<String> keySet = jsonObject.keySet();
        keySet.stream().forEach(key -> {
            ExtensionAttribute attribute = new ExtensionAttribute();
            attribute.setName(key);
            attribute.setValue(jsonObject.getString(key));
            ee.addAttribute(attribute);
        });
        flowElement.addExtensionElement(ee);
    }
}
