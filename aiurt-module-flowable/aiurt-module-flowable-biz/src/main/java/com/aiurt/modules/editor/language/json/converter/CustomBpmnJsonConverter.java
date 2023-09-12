package com.aiurt.modules.editor.language.json.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.constant.FlowModelExtElementConstant;
import com.aiurt.modules.editor.language.json.util.CustomJsonConverterUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExtensionAttribute;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.Process;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverterContext;
import org.flowable.editor.language.json.converter.util.JsonConverterUtil;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * <p>自定义jsonconver 将 BPMN 图转换为 BPMN JSON 格式</p>
 *
 * @author fgw
 */
public class CustomBpmnJsonConverter extends BpmnJsonConverter {

    protected ObjectMapper objectMapper = new ObjectMapper();


    /**
     * 注册自定义的json转换器
     */
    static {
        CustomUserTaskJsonConverter.customFillTypes(convertersToBpmnMap, convertersToJsonMap);
        CustomCallActivityJsonConverter.customFillTypes(convertersToBpmnMap, convertersToJsonMap);
        CustomSubProcessJsonConverter.customFillTypes(convertersToBpmnMap, convertersToJsonMap);
        CustomSequenceFlowJsonConverter.fillTypes(convertersToBpmnMap, convertersToJsonMap);

    }


    @Override
    public ObjectNode convertToJson(BpmnModel model, BpmnJsonConverterContext converterContext) {
        ObjectNode modelNode = super.convertToJson(model, converterContext);
        ObjectNode customPropertiesNode = objectMapper.createObjectNode();
        Process mainProcess = model.getMainProcess();
        if (Objects.isNull(mainProcess)) {
            return modelNode;
        }

        // 流程催办
        Map<String, List<ExtensionElement>> extensionElements = mainProcess.getExtensionElements();
        List<ExtensionElement> extensionElementList = extensionElements.get(FlowModelExtElementConstant.EXT_REMIND);
        if (CollUtil.isNotEmpty(extensionElementList)) {
            ExtensionElement extensionElement = extensionElementList.get(0);
            ObjectNode propertiesNode = objectMapper.createObjectNode();
            propertiesNode.put(FlowModelExtElementConstant.EXT_VALUE, extensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_VALUE));
            customPropertiesNode.putIfAbsent(FlowModelExtElementConstant.EXT_REMIND, propertiesNode);


        }



        // 流程撤回
        List<ExtensionElement> recallExtElementList = extensionElements.get(FlowModelExtElementConstant.EXT_RECALL);
        if (CollUtil.isNotEmpty(recallExtElementList)) {
            ExtensionElement recallExtensionElement = recallExtElementList.get(0);
            String value = recallExtensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_VALUE);
            String recallNode = recallExtensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_RECALL_NODE);
            JsonNode jsonNode = CustomJsonConverterUtil.parseJsonMode(recallNode);
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put(FlowModelExtElementConstant.EXT_VALUE, value);
            objectNode.set(FlowModelExtElementConstant.EXT_RECALL_NODE, jsonNode);
            customPropertiesNode.putIfAbsent(FlowModelExtElementConstant.EXT_RECALL, objectNode);
        }
        modelNode.putIfAbsent(FlowModelExtElementConstant.EXT_CUSTOM_PROPERTIES, customPropertiesNode);
        return modelNode;
    }


    @Override
    public BpmnModel convertToBpmnModel(JsonNode modelNode, BpmnJsonConverterContext converterContext) {
        BpmnModel bpmnModel = super.convertToBpmnModel(modelNode, converterContext);
        Process mainProcess = bpmnModel.getMainProcess();
        // 自定义属性
        JsonNode extensionData = modelNode.get("customProperties");
        JsonNode remindNode = extensionData.get(FlowModelExtElementConstant.EXT_REMIND);
        addExtensionElement(mainProcess, FlowModelExtElementConstant.EXT_REMIND, remindNode);

        JsonNode recallNode = extensionData.get(FlowModelExtElementConstant.EXT_RECALL);
        addExtensionElement(mainProcess, FlowModelExtElementConstant.EXT_RECALL, recallNode);


        //
        return bpmnModel;
    }

    private void addExtensionElement(Process mainProcess, String extensionName, JsonNode extensionData) {
        if (Objects.isNull(extensionData)) {
            return;
        }
        String json = null;
        try {
            json = objectMapper.writeValueAsString(extensionData);
        } catch (JsonProcessingException e) {
        }
        if (StrUtil.isBlank(json)) {
            return;
        }

        if (extensionData.isArray()) {
            JSONArray jsonArray = JSONObject.parseArray(json);
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                ExtensionElement ee = getExtensionElement(extensionName, jsonObject);
                mainProcess.addExtensionElement(ee);
            }
        }else if (extensionData.isObject()) {
            JSONObject jsonObject = JSONObject.parseObject(json);
            ExtensionElement ee = getExtensionElement(extensionName, jsonObject);
            mainProcess.addExtensionElement(ee);
        }
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

    public static void main(String[] args) {
        String v =    "<?xml version='1.0' encoding='UTF-8'?>\n<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:flowable=\"http://flowable.org/bpmn\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" typeLanguage=\"http://www.w3.org/2001/XMLSchema\" expressionLanguage=\"http://www.w3.org/1999/XPath\" targetNamespace=\"http://flowable.org/modeler\" exporter=\"Flowable Open Source Modeler\">\n  <process id=\"condition\" name=\"流转条件\" isExecutable=\"true\">\n    <extensionElements>\n      <flowable:remind xmlns:flowable=\"http://flowable.org/bpmn\" value=\"true\"/>\n      <flowable:recall xmlns:flowable=\"http://flowable.org/bpmn\" node=\"[{&#34;nodeName&#34;:&#34;节点名称&#34;,&#34;nodeId&#34;:&#34;节点id&#34;}]\" value=\"true\"/>\n    </extensionElements>\n    <startEvent id=\"startEvent1\"/>\n    <sequenceFlow id=\"Flow_0de5mmm\" sourceRef=\"startEvent1\" targetRef=\"Activity_0pr7mdx\"/>\n    <userTask id=\"Activity_0pr7mdx\">\n      <extensionElements>\n        <flowable:preNodeAction xmlns:flowable=\"http://flowable.org/bpmn\"/>\n        <flowable:postNodeAction xmlns:flowable=\"http://flowable.org/bpmn\"/>\n        <flowable:autoselect xmlns:flowable=\"http://flowable.org/bpmn\" value=\"true\"/>\n        <flowable:userassignee xmlns:flowable=\"http://flowable.org/bpmn\" name=\"\" value=\"{}\"/>\n        <flowable:carboncopy xmlns:flowable=\"http://flowable.org/bpmn\" name=\"\" value=\"{}\"/>\n      </extensionElements>\n    </userTask>\n    <sequenceFlow id=\"Flow_15s2azd\" sourceRef=\"Activity_0pr7mdx\" targetRef=\"Activity_0s03zyp\"/>\n    <userTask id=\"Activity_0s03zyp\">\n      <extensionElements>\n        <flowable:preNodeAction xmlns:flowable=\"http://flowable.org/bpmn\"/>\n        <flowable:postNodeAction xmlns:flowable=\"http://flowable.org/bpmn\"/>\n        <flowable:autoselect xmlns:flowable=\"http://flowable.org/bpmn\" value=\"true\"/>\n        <flowable:userassignee xmlns:flowable=\"http://flowable.org/bpmn\" name=\"\" value=\"{}\"/>\n        <flowable:carboncopy xmlns:flowable=\"http://flowable.org/bpmn\" name=\"\" value=\"{}\"/>\n      </extensionElements>\n    </userTask>\n    <endEvent id=\"Event_09crgw2\"/>\n    <sequenceFlow id=\"Flow_0h91xcw\" sourceRef=\"Activity_0s03zyp\" targetRef=\"Event_09crgw2\"/>\n  </process>\n  <bpmndi:BPMNDiagram id=\"BPMNDiagram_condition\">\n    <bpmndi:BPMNPlane bpmnElement=\"condition\" id=\"BPMNPlane_condition\">\n      <bpmndi:BPMNShape bpmnElement=\"startEvent1\" id=\"BPMNShape_startEvent1\">\n        <omgdc:Bounds height=\"30.0\" width=\"30.0\" x=\"100.0\" y=\"163.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_0pr7mdx\" id=\"BPMNShape_Activity_0pr7mdx\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"180.0\" y=\"138.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_0s03zyp\" id=\"BPMNShape_Activity_0s03zyp\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"330.0\" y=\"138.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Event_09crgw2\" id=\"BPMNShape_Event_09crgw2\">\n        <omgdc:Bounds height=\"36.0\" width=\"36.0\" x=\"482.0\" y=\"160.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_15s2azd\" id=\"BPMNEdge_Flow_15s2azd\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"279.9499999999581\" y=\"178.0\"/>\n        <omgdi:waypoint x=\"329.9999999999364\" y=\"178.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_0de5mmm\" id=\"BPMNEdge_Flow_0de5mmm\" flowable:sourceDockerX=\"15.0\" flowable:sourceDockerY=\"15.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"129.9499986183554\" y=\"178.0\"/>\n        <omgdi:waypoint x=\"180.0\" y=\"178.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_0h91xcw\" id=\"BPMNEdge_Flow_0h91xcw\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"18.0\" flowable:targetDockerY=\"18.0\">\n        <omgdi:waypoint x=\"429.95000000000005\" y=\"178.0\"/>\n        <omgdi:waypoint x=\"482.0\" y=\"178.0\"/>\n      </bpmndi:BPMNEdge>\n    </bpmndi:BPMNPlane>\n  </bpmndi:BPMNDiagram>\n</definitions>";
        System.out.println(v);
    }
}
