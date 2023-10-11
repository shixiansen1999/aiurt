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
        //超时审批提醒
        List<ExtensionElement> timeoutExtElementList = extensionElements.get(FlowModelExtElementConstant.EXT_TIMEOUT_REMINDER);
        if (CollUtil.isNotEmpty(timeoutExtElementList)) {
            ExtensionElement timeoutExtElement = timeoutExtElementList.get(0);
            String value = timeoutExtElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_VALUE);
            String timeoutRemindNode = timeoutExtElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_LIST);
            JsonNode jsonNode = CustomJsonConverterUtil.parseJsonMode(timeoutRemindNode);
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put(FlowModelExtElementConstant.EXT_VALUE, value);
            objectNode.set(FlowModelExtElementConstant.EXT_LIST, jsonNode);
            customPropertiesNode.putIfAbsent(FlowModelExtElementConstant.EXT_TIMEOUT_REMINDER, objectNode);
        }


        // 审批去重
        List<ExtensionElement> duplicateRuleElementList = extensionElements.get(FlowModelExtElementConstant.EXT_ASSIGN_DUPLICATE_RULE);
        if (CollUtil.isNotEmpty(duplicateRuleElementList)) {
            ExtensionElement extensionElement = duplicateRuleElementList.get(0);
            String value = extensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_VALUE);
            String rule = extensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_RULE);
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put(FlowModelExtElementConstant.EXT_VALUE, value);
            objectNode.put(FlowModelExtElementConstant.EXT_RULE, rule);
            customPropertiesNode.putIfAbsent(FlowModelExtElementConstant.EXT_ASSIGN_DUPLICATE_RULE, objectNode);
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
        if (Objects.isNull(extensionData)) {
            return bpmnModel;
        }
        JsonNode remindNode = extensionData.get(FlowModelExtElementConstant.EXT_REMIND);
        addExtensionElement(mainProcess, FlowModelExtElementConstant.EXT_REMIND, remindNode);

        JsonNode recallNode = extensionData.get(FlowModelExtElementConstant.EXT_RECALL);
        addExtensionElement(mainProcess, FlowModelExtElementConstant.EXT_RECALL, recallNode);

        JsonNode duplicateNode = extensionData.get(FlowModelExtElementConstant.EXT_ASSIGN_DUPLICATE_RULE);
        addExtensionElement(mainProcess, FlowModelExtElementConstant.EXT_ASSIGN_DUPLICATE_RULE, duplicateNode);

        JsonNode timeoutRemindNode = extensionData.get(FlowModelExtElementConstant.EXT_TIMEOUT_REMINDER);
        addExtensionElement(mainProcess, FlowModelExtElementConstant.EXT_TIMEOUT_REMINDER, timeoutRemindNode);


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
}
