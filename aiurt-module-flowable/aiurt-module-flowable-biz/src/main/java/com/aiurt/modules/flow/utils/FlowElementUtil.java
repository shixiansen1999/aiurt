package com.aiurt.modules.flow.utils;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.factory.annotation.Autowired;

public class FlowElementUtil {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ModelService modelService;


    public UserTask getFirstUserTask(String modelKey) {

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().latestVersion().processDefinitionKey(modelKey).singleResult();
        String deploymentId = processDefinition.getDeploymentId();
        Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();

        BpmnModel model = repositoryService.getBpmnModel(processDefinition.getId());

        return null;

    }

}
