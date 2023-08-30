package com.aiurt.modules.editor.language.json.converter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.Process;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverterContext;

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
        JsonNode jsonNode = modelNode.get(EDITOR_SHAPE_PROPERTIES);
        ObjectNode propertiesNode = objectMapper.createObjectNode();

        return modelNode;
    }


    @Override
    public BpmnModel convertToBpmnModel(JsonNode modelNode, BpmnJsonConverterContext converterContext) {
        BpmnModel bpmnModel = super.convertToBpmnModel(modelNode, converterContext);
        Process mainProcess = bpmnModel.getMainProcess();
        // 自定义属性
        // BpmnJsonConverterUtil.c

        // mainProcess.setExtensionElements();
        return bpmnModel;
    }
}
