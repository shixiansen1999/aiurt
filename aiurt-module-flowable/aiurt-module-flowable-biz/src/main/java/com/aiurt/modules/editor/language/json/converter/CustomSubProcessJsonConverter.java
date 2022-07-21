package com.aiurt.modules.editor.language.json.converter;

import com.fasterxml.jackson.databind.JsonNode;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverterContext;
import org.flowable.editor.language.json.converter.BpmnJsonConverterUtil;
import org.flowable.editor.language.json.converter.SubProcessJsonConverter;

import java.util.Map;

/**
 * @author fgw
 */
public class CustomSubProcessJsonConverter extends SubProcessJsonConverter {

    public static void customFillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap,
                                       Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {

        fillJsonTypes(convertersToBpmnMap);
        fillBpmnTypes(convertersToJsonMap);
    }

    public static void fillJsonTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
        convertersToBpmnMap.put(STENCIL_SUB_PROCESS, CustomSubProcessJsonConverter.class);
        convertersToBpmnMap.put(STENCIL_COLLAPSED_SUB_PROCESS, CustomSubProcessJsonConverter.class);
    }

    public static void fillBpmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(SubProcess.class, CustomSubProcessJsonConverter.class);
        convertersToJsonMap.put(Transaction.class, CustomSubProcessJsonConverter.class);
    }

    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap,
                                               BpmnJsonConverterContext converterContext) {
        FlowElement flowElement = super.convertJsonToElement(elementNode, modelNode, shapeMap, converterContext);
        GraphicInfo graphicInfo = model.getGraphicInfo(BpmnJsonConverterUtil.getElementId(elementNode));
        if (STENCIL_COLLAPSED_SUB_PROCESS.equals(BpmnJsonConverterUtil.getStencilId(elementNode))){
            graphicInfo.setExpanded(false); //default is null!
        } else {
            graphicInfo.setExpanded(true);
        }
        return flowElement;
    }
}
