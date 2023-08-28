package com.aiurt.modules.editor.service;

import com.aiurt.modules.editor.language.json.converter.CustomBpmnJsonConverter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.ui.common.service.exception.InternalServerErrorException;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.service.ConverterContext;
import org.flowable.ui.modeler.service.ModelServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author fgw
 */
public class MyModelServiceImpl extends ModelServiceImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyModelServiceImpl.class);

    protected CustomBpmnJsonConverter bpmnJsonConverter = new CustomBpmnJsonConverter();


    @Override
    public BpmnModel getBpmnModel(AbstractModel model, ConverterContext appConverterContext) {

        try {
            ObjectNode editorJsonNode = (ObjectNode) super.objectMapper.readTree(model.getModelEditorJson());
            return this.bpmnJsonConverter.convertToBpmnModel(editorJsonNode, appConverterContext);

        } catch (Exception e) {
            LOGGER.error("Could not generate BPMN 2.0 model for {}", model.getId(), e);
            throw new InternalServerErrorException("Could not generate BPMN 2.0 model");
        }
    }
}
