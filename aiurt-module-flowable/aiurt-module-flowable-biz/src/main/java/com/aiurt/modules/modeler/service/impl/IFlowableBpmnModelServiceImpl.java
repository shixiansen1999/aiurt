package com.aiurt.modules.modeler.service.impl;

import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.aiurt.modules.modeler.service.IFlowableBpmnModelService;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.model.ModelKeyRepresentation;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.util.Date;

@Service
public class IFlowableBpmnModelServiceImpl implements IFlowableBpmnModelService {

    @Autowired
    private ModelService modelService;

    @Override
    public ActCustomModelInfo createInitBpmn(ActCustomModelInfo modelInfo, LoginUser user) {
        ModelRepresentation modelRepresentation = new ModelRepresentation();
        modelRepresentation.setModelType(AbstractModel.MODEL_TYPE_BPMN);
        modelRepresentation.setKey(modelInfo.getModelKey());
        modelRepresentation.setName(modelInfo.getName());
        // modelRepresentation.setTenantId(modelInfo.getAppSn());
        modelRepresentation.setLastUpdated(new Date());
        Model model = creatModel(modelRepresentation, user);

        return null;
    }

    @Override
    public Model creatModel(ModelRepresentation modelRepresentation, LoginUser user) {
        // 替换空格
        modelRepresentation.setKey(modelRepresentation.getKey().replaceAll(" ", ""));
        // 校验
        ModelKeyRepresentation modelKeyInfo = modelService.validateModelKey(null, modelRepresentation.getModelType(), modelRepresentation.getKey());
        if (modelKeyInfo.isKeyAlreadyExists()){
            throw new AiurtBootException("流程模板KEY 不能重复");
        }
        String json = modelService.createModelJson(modelRepresentation);
        Model model = modelService.createModel(modelRepresentation, json, user.getUsername());
        return model;
    }
}
