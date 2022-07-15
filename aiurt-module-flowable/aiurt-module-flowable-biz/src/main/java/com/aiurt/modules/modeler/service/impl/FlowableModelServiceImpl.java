package com.aiurt.modules.modeler.service.impl;

import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.modules.modeler.service.IFlowableModelService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.model.ModelKeyRepresentation;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: flow
 * @description: 模型实现
 * @author fgw
 * @create 2022-07-12
 */
@Slf4j
@Service
public class FlowableModelServiceImpl implements IFlowableModelService {

    @Autowired
    private ModelService modelService;

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
