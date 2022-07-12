package com.aiurt.modules.modeler.service.impl;

import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.modeler.dto.ModelInfoVo;
import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.aiurt.modules.modeler.enums.ModelFormStatusEnum;
import com.aiurt.modules.modeler.service.IFlowableBpmnService;
import com.aiurt.modules.modeler.service.IFlowableModelService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.ui.common.util.XmlUtil;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.model.ModelKeyRepresentation;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.flowable.ui.modeler.service.ConverterContext;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * @program: flow
 * @description: 流程引擎服务实现
 * @author: fgw
 * @create: 2022-07-10 17:51
 **/
@Slf4j
@Service
public class FlowableBpmnServiceImpl implements IFlowableBpmnService {

    private static final String BPMN_EXTENSION = ".bpmn";
    private static final String BPMN20_XML_EXTENSION = ".bpmn20.xml";

    @Autowired
    private ModelService modelService;

    @Autowired
    private IFlowableModelService flowableModelService;

    @Autowired
    protected BpmnXMLConverter bpmnXMLConverter;

    @Autowired
    protected BpmnJsonConverter bpmnJsonConverter;

    @Override
    public Model createInitBpmn(ActCustomModelInfo modelInfo, LoginUser user) {
        ModelRepresentation modelRepresentation = new ModelRepresentation();
        modelRepresentation.setModelType(AbstractModel.MODEL_TYPE_BPMN);
        modelRepresentation.setKey(modelInfo.getModelKey());
        modelRepresentation.setName(modelInfo.getName());
        modelRepresentation.setTenantId("test");
        modelRepresentation.setLastUpdated(new Date());
        Model model = flowableModelService.creatModel(modelRepresentation, user);
        return model;
    }

    /**
     *
     * @param modelId 流程模型id
     * @return
     */
    @Override
    public ModelInfoVo loadBpmnXmlByModelId(String modelId) {
        Model model = modelService.getModel(modelId);
        byte[] bpmnXML = modelService.getBpmnXML(model);
        String streamStr = new String(bpmnXML);
        ModelInfoVo modelInfoVo = new ModelInfoVo();
        modelInfoVo.setModelId(modelId);
        modelInfoVo.setModelName(model.getName());
        modelInfoVo.setModelKey(model.getKey());
        modelInfoVo.setFileName(model.getName());
        modelInfoVo.setModelXml(streamStr);
        return modelInfoVo;
    }

    /**
     *
     * @param modelId     模型ID
     * @param fileName    文件名称
     * @param modelStream 模型文件流
     * @param user        登录用户
     * @return
     */
    @Override
    public String importBpmnModel(String modelId, String fileName, ByteArrayInputStream modelStream, LoginUser user) {

        Model processModel = modelService.getModel(modelId);
        if (StringUtils.isBlank(fileName)) {
            fileName = processModel.getKey() + BPMN_EXTENSION;
        }

        XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
        InputStreamReader xmlIn = new InputStreamReader(modelStream, StandardCharsets.UTF_8);
        XMLStreamReader xtr = null;
        try {
            xtr = xif.createXMLStreamReader(xmlIn);

        } catch (XMLStreamException e) {
          throw new AiurtBootException("");
        }
        BpmnModel bpmnModel = bpmnXMLConverter.convertToBpmnModel(xtr);
        // 设置xml的processId
        bpmnModel.getMainProcess().setId(processModel.getKey());
        // 默认值
        bpmnModel.setTargetNamespace(BaseBpmnJsonConverter.NAMESPACE);

        if (CollectionUtils.isEmpty(bpmnModel.getProcesses())) {
            throw new AiurtBootException("No process found in definition " + fileName);
        }

        if (bpmnModel.getLocationMap().size() == 0) {
            throw new AiurtBootException( "No required BPMN DI information found in definition " + fileName);
        }
/*
        ConverterContext converterContext = new ConverterContext(modelService, objectMapper);
        List<AbstractModel> decisionTables = modelService.getModelsByModelType(AbstractModel.MODEL_TYPE_DECISION_TABLE);
        decisionTables.forEach(abstractModel -> {
            Model model = (Model) abstractModel;
            converterContext.addDecisionTableModel(model);
        });
        ObjectNode modelNode = bpmnJsonConverter.convertToJson(bpmnModel, converterContext);
       // this.setProcessPropertiesToKey(modelNode, processModel.getKey());
        AbstractModel savedModel = modelService.saveModel(modelId, processModel.getName(), processModel.getKey(),
                processModel.getDescription(), modelNode.toString(), false,
                null, user.getUserNo());
        LambdaQueryWrapper<ActCustomModelInfo> modelInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        modelInfoLambdaQueryWrapper.eq(ModelInfo::getModelId, savedModel.getId());
        ModelInfo modelInfo = modelInfoService.getOne(modelInfoLambdaQueryWrapper);
        modelInfo.setStatus(ModelFormStatusEnum.DFB.getStatus());
        modelInfo.setExtendStatus(ModelFormStatusEnum.DFB.getStatus());*/
        return null;
    }
}
