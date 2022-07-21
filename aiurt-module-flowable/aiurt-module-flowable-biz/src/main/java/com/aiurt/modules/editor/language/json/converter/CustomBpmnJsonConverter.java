package com.aiurt.modules.editor.language.json.converter;

import org.flowable.editor.language.json.converter.BpmnJsonConverter;

/**
 * @author fgw
 */
public class CustomBpmnJsonConverter extends BpmnJsonConverter {

    static {
        CustomUserTaskJsonConverter.customFillTypes(convertersToBpmnMap, convertersToJsonMap);
        CustomCallActivityJsonConverter.customFillTypes(convertersToBpmnMap, convertersToJsonMap);
        CustomSubProcessJsonConverter.customFillTypes(convertersToBpmnMap, convertersToJsonMap);
    }
}
