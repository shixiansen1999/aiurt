package com.aiurt.modules.modeler.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtErrorEnum;
import com.aiurt.modules.constants.FlowConstant;
import com.aiurt.modules.editor.language.json.converter.CustomBpmnJsonConverter;
import com.aiurt.modules.flow.service.FlowApiService;
import com.aiurt.modules.manage.entity.ActCustomVersion;
import com.aiurt.modules.manage.service.IActCustomVersionService;
import com.aiurt.modules.modeler.dto.ModelInfoVo;
import com.aiurt.modules.modeler.dto.TaskInfoVo;
import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.enums.ModelFormStatusEnum;
import com.aiurt.modules.modeler.service.IActCustomModelInfoService;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.modeler.service.IFlowableBpmnService;
import com.aiurt.modules.modeler.service.IFlowableModelService;
import com.aiurt.modules.utils.BaseFlowIdentityExtHelper;
import com.aiurt.modules.utils.FlowCustomExtFactory;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.bpmn.model.Process;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.editor.language.json.converter.util.CollectionUtils;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.ui.common.util.XmlUtil;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.flowable.ui.modeler.service.ConverterContext;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * bpmn xml和BpmnModel 转换器
     */
    @Autowired
    protected BpmnXMLConverter bpmnXMLConverter;

    /**
     * bpmn json和BpmnModel 转换器
     */
    @Autowired
    protected CustomBpmnJsonConverter bpmnJsonConverter;

    @Autowired
    @Lazy
    private IActCustomModelInfoService modelInfoService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private IActCustomVersionService versionService;

    @Autowired
    private FlowCustomExtFactory flowCustomExtFactory;

    @Autowired
    private FlowApiService flowApiService;

    @Autowired
    private IActCustomTaskExtService taskExtService;

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

        // 获取model
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
            log.error(e.getMessage());
          throw new AiurtBootException("");
        }
        // 实现将bpmn xml转换成BpmnModel内存模型对象
        BpmnModel bpmnModel = bpmnXMLConverter.convertToBpmnModel(xtr);
        // 设置xml的processId 也就是modelkey
        bpmnModel.getMainProcess().setId(processModel.getKey());
        // 默认值
        bpmnModel.setTargetNamespace(BaseBpmnJsonConverter.NAMESPACE);

        if (CollectionUtils.isEmpty(bpmnModel.getProcesses())) {
            throw new AiurtBootException("BPMN模型没有配置流程:" + fileName);
        }

        if (bpmnModel.getLocationMap().size() == 0) {
            throw new AiurtBootException( "No required BPMN DI information found in definition " + fileName);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        ConverterContext converterContext = new ConverterContext(modelService, objectMapper);
        //
        List<AbstractModel> decisionTables = modelService.getModelsByModelType(AbstractModel.MODEL_TYPE_DECISION_TABLE);
        decisionTables.forEach(abstractModel -> {
            Model model = (Model) abstractModel;
            converterContext.addDecisionTableModel(model);
        });

        // 设置模板json格式
        ObjectNode modelNode = bpmnJsonConverter.convertToJson(bpmnModel, converterContext);

        AbstractModel savedModel = modelService.saveModel(modelId, processModel.getName(), processModel.getKey(),
                processModel.getDescription(), modelNode.toString(), false,
                null, user.getUsername());

        // 更新act_customl_info
        LambdaQueryWrapper<ActCustomModelInfo> modelInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        modelInfoLambdaQueryWrapper.eq(ActCustomModelInfo::getModelId, savedModel.getId());
        ActCustomModelInfo modelInfo = modelInfoService.getOne(modelInfoLambdaQueryWrapper);
        modelInfo.setStatus(ModelFormStatusEnum.DFB.getStatus());
        modelInfo.setExtendStatus(ModelFormStatusEnum.DFB.getStatus());
        modelInfoService.updateById(modelInfo);
        return "保存成功";
    }

    /**
     * 部署流程
     * @param modelId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishBpmn(String modelId) {

        LambdaQueryWrapper<ActCustomModelInfo> modelInfoLambdaQueryWrapper = new LambdaQueryWrapper<>();
        modelInfoLambdaQueryWrapper.eq(ActCustomModelInfo::getModelId, modelId);
        ActCustomModelInfo modelInfo = modelInfoService.getOne(modelInfoLambdaQueryWrapper);

        if (Objects.isNull(modelInfo)) {
            throw new AiurtBootException(AiurtErrorEnum.FLOW_MODEL_NOT_FOUND.getCode(), AiurtErrorEnum.FLOW_MODEL_NOT_FOUND.getMessage());
        }

        Model model = modelService.getModel(modelId);
        if (Objects.isNull(model)) {
            throw new AiurtBootException(AiurtErrorEnum.FLOW_MODEL_NOT_FOUND.getCode(), AiurtErrorEnum.FLOW_MODEL_NOT_FOUND.getMessage());
        }

        // 转为bpmnModel 内存模型; 通过model中的editjson转为bpmnl， 流程标准模型
        BpmnModel bpmnModel = modelService.getBpmnModel(model);
        // todo 校验

        // 增加监听器

        // 选人属性
        Collection<FlowElement> elementList = bpmnModel.getMainProcess().getFlowElements();

        // list->map
        Map<String, FlowElement> elementMap =
                elementList.stream().filter(e -> e instanceof UserTask).collect(Collectors.toMap(FlowElement::getId, c -> c));
        // todo
        List<ActCustomTaskExt> actCustomTaskExtList = buildTaskExtList(bpmnModel);
        if (CollUtil.isNotEmpty(actCustomTaskExtList)) {
            BaseFlowIdentityExtHelper flowIdentityExtHelper = flowCustomExtFactory.getFlowIdentityExtHelper();
            for (ActCustomTaskExt t : actCustomTaskExtList) {
                UserTask userTask = (UserTask) elementMap.get(t.getTaskId());
                if (StrUtil.equals(t.getGroupType(), FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER)) {
                    userTask.setCandidateGroups(
                            CollUtil.newArrayList("${" + FlowConstant.GROUP_TYPE_UP_DEPT_POST_LEADER_VAR + "}"));
                    Assert.notNull(flowIdentityExtHelper);
                    flowApiService.addTaskCreateListener(userTask, flowIdentityExtHelper.getUpDeptPostLeaderListener());
                } else if (StrUtil.equals(t.getGroupType(), FlowConstant.GROUP_TYPE_DEPT_POST_LEADER)) {
                    userTask.setCandidateGroups(
                            CollUtil.newArrayList("${" + FlowConstant.GROUP_TYPE_DEPT_POST_LEADER_VAR + "}"));
                    Assert.notNull(flowIdentityExtHelper);
                    flowApiService.addTaskCreateListener(userTask, flowIdentityExtHelper.getDeptPostLeaderListener());
                } else if (StrUtil.equals(t.getGroupType(), FlowConstant.GROUP_TYPE_POST)) {
                    // todo 没有岗位暂时不处理
                    /*Assert.notNull(t.getDeptPostListJson());
                    List<FlowTaskPostCandidateGroup> groupDataList =
                            JSON.parseArray(t.getDeptPostListJson(), FlowTaskPostCandidateGroup.class);
                    List<String> candidateGroupList =
                            FlowTaskPostCandidateGroup.buildCandidateGroupList(groupDataList);
                    userTask.setCandidateGroups(candidateGroupList);*/
                }
            }
        }

        // 部署流程
        Deployment deploy = repositoryService.createDeployment()
                .name(model.getName())
                .key(model.getKey())
                .category(modelInfo.getClassifyCode())
                .tenantId(model.getTenantId())
                .addBpmnModel(model.getKey() + BPMN_EXTENSION, bpmnModel)
                .deploy();

        // 查询流程定义
        ProcessDefinition definition = repositoryService.createProcessDefinitionQuery().deploymentId(deploy.getId()).singleResult();


        //  todo 保存其他的属性

        // 增加一个版本, 流程定义
        List<ProcessDefinition> definitionList = repositoryService.createProcessDefinitionQuery().processDefinitionKey(model.getKey())
                .orderByProcessDefinitionVersion().desc().list();

        if (CollectionUtils.isNotEmpty(definitionList)) {
            // 设置其他版本为非主版本
            LambdaUpdateWrapper<ActCustomVersion> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ActCustomVersion::getModelId, modelId).set(ActCustomVersion::getMainVersion, "0");
            versionService.update(updateWrapper);

            ProcessDefinition processDefinition = definitionList.get(0);
            ActCustomVersion actCustomVersion = ActCustomVersion.builder()
                    .deployId(processDefinition.getDeploymentId())
                    .processDefinitionId(processDefinition.getId())
                    .mainVersion("1")
                    .modelId(modelId)
                    .deployTime(new Date())
                    .status(1)
                    .version(definitionList.size())
                    .build();
            versionService.save(actCustomVersion);
        }

        // 已发布
        modelInfo.setStatus(ModelFormStatusEnum.YFB.getStatus());
        modelInfoService.updateById(modelInfo);
        if (CollUtil.isNotEmpty(actCustomTaskExtList)) {
            actCustomTaskExtList.forEach(t -> t.setProcessDefinitionId(definition.getId()));
            taskExtService.saveBatch(actCustomTaskExtList);
        }
    }

    /**
     * 根据流程定义id获取标准流程模型
     * @param processDefinitionId
     * @return
     */
    @Override
    public BpmnModel getBpmnModelByDefinitionId(String processDefinitionId) {
        return repositoryService.getBpmnModel(processDefinitionId);
    }

    private List<ActCustomTaskExt> buildTaskExtList(BpmnModel bpmnModel) {
        List<ActCustomTaskExt> flowTaskExtList = new LinkedList<>();
        List<Process> processList = bpmnModel.getProcesses();
        for (Process process : processList) {
            for (FlowElement element : process.getFlowElements()) {
                if (element instanceof UserTask) {
                    ActCustomTaskExt flowTaskExt = this.buildTaskExt((UserTask) element);
                    flowTaskExtList.add(flowTaskExt);
                }
            }
        }
        return flowTaskExtList;
    }

    /**
     * 构建属性
     * @param userTask
     * @return
     */
    private ActCustomTaskExt buildTaskExt(UserTask userTask) {
        ActCustomTaskExt flowTaskExt = new ActCustomTaskExt();
        flowTaskExt.setTaskId(userTask.getId());

         //属性
        Map<String, List<ExtensionAttribute>> taskAttributeMap = userTask.getAttributes();
        // 处理表单属性
        JSONObject form = new JSONObject();
        JSONObject variable = new JSONObject();
        taskAttributeMap.forEach((key,list)->{
            ExtensionAttribute extensionAttribute = list.get(0);
            if (StrUtil.startWith(extensionAttribute.getName(), "formData")) {
                form.put(extensionAttribute.getName().replaceAll("formData\\.", ""), extensionAttribute.getValue());
            }else if (StrUtil.startWith(extensionAttribute.getName(), "flowable")) {
                variable.put(extensionAttribute.getName().replaceAll("flowable\\.", ""), extensionAttribute.getValue());
            }
        });

        if (variable.size()>0) {
            flowTaskExt.setVariableListJson(JSONObject.toJSONString(variable));
        }
        if (variable.size()>0){
            flowTaskExt.setFormJson(JSONObject.toJSONString(form));
        }

        Map<String, List<ExtensionElement>> extensionMap = userTask.getExtensionElements();
        if (MapUtil.isNotEmpty(extensionMap)) {
            List<JSONObject> operationList = this.buildOperationListExtensionElement(extensionMap);
            if (CollUtil.isNotEmpty(operationList)) {
                flowTaskExt.setOperationListJson(JSON.toJSONString(operationList));
            }
            // todo 多实例
            /*JSONObject assigneeListObject = this.buildAssigneeListExtensionElement(extensionMap);
            if (assigneeListObject != null) {
                flowTaskExt.setAssigneeListJson(JSON.toJSONString(assigneeListObject));
            }*/
            List<JSONObject> deptPostList = this.buildDeptPostListExtensionElement(extensionMap);
            if (deptPostList != null) {
                flowTaskExt.setDeptPostListJson(JSON.toJSONString(deptPostList));
            }
            // todo 抄送
           /* List<JSONObject> copyList = this.buildCopyListExtensionElement(extensionMap);
            if (copyList != null) {
                flowTaskExt.setCopyListJson(JSON.toJSONString(copyList));
            }*/
            JSONObject candidateGroupObject = this.buildUserCandidateGroupsExtensionElement(extensionMap);
            if (candidateGroupObject != null) {
                String type = candidateGroupObject.getString("type");
                String value = candidateGroupObject.getString("value");
                switch (type) {
                    case "DEPT":
                        flowTaskExt.setDeptIds(value);
                        break;
                    case "ROLE":
                        flowTaskExt.setRoleIds(value);
                        break;
                    case "USERS":
                        flowTaskExt.setCandidateUsernames(value);
                        break;
                    default:
                        break;
                }
            }
        }
        return flowTaskExt;
    }


    /**
     * 构建表单按钮属性
     * @param extensionMap
     * @return
     */
    private List<JSONObject> buildOperationListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        List<ExtensionElement> formOperationElements = extensionMap.get("formOperation");
        if (CollUtil.isNotEmpty(formOperationElements)) {
            List<JSONObject> list = new ArrayList<>();
            for (ExtensionElement e : formOperationElements) {
                JSONObject json = new JSONObject();
                json.put("id", e.getAttributeValue(null, "id"));
                json.put("label", e.getAttributeValue(null, "label"));
                json.put("type", e.getAttributeValue(null, "type"));
                json.put("showOrder", e.getAttributeValue(null, "showOrder"));
                list.add(json);
            }
            return list;
        }
        return null;
    }

    /**
     * 构建变量
     * @param extensionMap
     * @return
     */
    private List<JSONObject> buildVariableListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        return null;
    }

    /**
     *  岗位
     * @param extensionMap
     * @return
     */
    private List<JSONObject> buildDeptPostListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        return null;
    }

    /**
     *
     * @param extensionMap
     * @return
     */
    private JSONObject buildUserCandidateGroupsExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        return null;
    }

}
