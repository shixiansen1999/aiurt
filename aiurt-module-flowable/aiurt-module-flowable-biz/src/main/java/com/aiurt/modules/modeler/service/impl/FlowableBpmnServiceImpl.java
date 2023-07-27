package com.aiurt.modules.modeler.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtErrorEnum;
import com.aiurt.modules.common.constant.FlowModelAttConstant;
import com.aiurt.modules.common.constant.FlowModelExtElementConstant;
import com.aiurt.modules.editor.language.json.converter.CustomBpmnJsonConverter;
import com.aiurt.modules.manage.entity.ActCustomVersion;
import com.aiurt.modules.manage.service.IActCustomVersionService;
import com.aiurt.modules.modeler.dto.FlowUserAttributeModel;
import com.aiurt.modules.modeler.dto.FlowUserModel;
import com.aiurt.modules.modeler.dto.ModelInfoVo;
import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.entity.ActOperationEntity;
import com.aiurt.modules.modeler.enums.ModelFormStatusEnum;
import com.aiurt.modules.modeler.service.IActCustomModelInfoService;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.modeler.service.IFlowableBpmnService;
import com.aiurt.modules.modeler.service.IFlowableModelService;
import com.aiurt.modules.user.entity.ActCustomUser;
import com.aiurt.modules.user.service.IActCustomUserService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
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
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
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
    private IActCustomTaskExtService taskExtService;

    @Autowired
    private IActCustomUserService userService;

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
        ModelInfoVo modelInfoVo = null;
        try {
            Model model = modelService.getModel(modelId);
            byte[] bpmnXML = modelService.getBpmnXML(model);
            String streamStr = new String(bpmnXML);
            modelInfoVo = new ModelInfoVo();
            modelInfoVo.setModelId(modelId);
            modelInfoVo.setModelName(model.getName());
            modelInfoVo.setModelKey(model.getKey());
            modelInfoVo.setFileName(model.getName());
            if (StrUtil.isNotBlank(streamStr)) {
                streamStr = StrUtil.replaceIgnoreCase(streamStr, "&quot;", "&#34;");
            }
            modelInfoVo.setModelXml(streamStr);
        } catch (Exception e) {
            throw new AiurtBootException("系统中不存在该流程模型，请刷新尝试！");
        }
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

        // 构建属性
        List<ActCustomTaskExt> taskExtList = new ArrayList<>();
        List<ActCustomUser> userList = new ArrayList<>();
        buildTaskExtList(bpmnModel, taskExtList, userList);

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

            modelInfo.setExtendStatus(actCustomVersion.getVersion());
        }

        // 已发布
        modelInfo.setStatus(ModelFormStatusEnum.YFB.getStatus());

        modelInfoService.updateById(modelInfo);
        if (CollUtil.isNotEmpty(taskExtList)) {
            taskExtList.forEach(t -> t.setProcessDefinitionId(definition.getId()));
            taskExtService.saveBatch(taskExtList);
        }
        if (CollUtil.isNotEmpty(userList)) {
            userList.forEach(t->t.setProcessDefinitionId(definition.getId()));
            userService.saveBatch(userList);
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

    private void buildTaskExtList(BpmnModel bpmnModel, List<ActCustomTaskExt> taskExtList, List<ActCustomUser> userList) {
        List<Process> processList = bpmnModel.getProcesses();
        for (Process process : processList) {
            for (FlowElement element : process.getFlowElements()) {
                // 用户任务
                if (element instanceof UserTask) {
                    this.buildUserTaskExt((UserTask) element, taskExtList, userList);
                }else if (element instanceof SequenceFlow) {
                    ActCustomTaskExt flowTaskExt = this.buildTaskExt((SequenceFlow) element);
                }
            }
        }

    }

    private ActCustomTaskExt buildTaskExt(SequenceFlow element) {
        ActCustomTaskExt taskExt = new ActCustomTaskExt();
        String id = element.getId();

        String conditionExpression = element.getConditionExpression();
        taskExt.setTaskId(id);
        taskExt.setConditionExpression(conditionExpression);
        return taskExt;
    }

    /**
     * 构建属性
     * @param userTask
     * @param taskExtList
     * @param userList
     * @return
     */
    private void buildUserTaskExt(UserTask userTask, List<ActCustomTaskExt> taskExtList, List<ActCustomUser> userList) {
        ActCustomTaskExt flowTaskExt = new ActCustomTaskExt();
        flowTaskExt.setTaskId(userTask.getId());

        // 属性
        Map<String, List<ExtensionAttribute>> taskAttributeMap = userTask.getAttributes();
        // 处理表单属性
        JSONObject form = new JSONObject();
        JSONObject variable = new JSONObject();
        String groupType = null;

        // 属性名与操作的映射表,该接口定义了一个 accept 方法，用于接受两个输入参数，并执行相关的操作。
        // 由于 BiConsumer 是一个函数式接口，可以通过 lambda 表达式来实现它的方法
        Map<String, BiConsumer<ExtensionAttribute, ActCustomTaskExt>> attributeHandlers = new HashMap<>();
        attributeHandlers.put(FlowModelAttConstant.FORM_TYPE, (attr, ext) -> form.put(attr.getName(), attr.getValue()));
        attributeHandlers.put(FlowModelAttConstant.FORM_URL, (attr, ext) -> form.put(attr.getName(), attr.getValue()));
        attributeHandlers.put(FlowModelAttConstant.SERVICE, (attr, ext) -> form.put(attr.getName(), attr.getValue()));
        attributeHandlers.put(FlowModelAttConstant.FORM_DYNAMIC_URL, (attr, ext) -> form.put(attr.getName(), attr.getValue()));
        attributeHandlers.put(FlowModelAttConstant.FORM_TASK_VARIABLES, (attr, ext) -> variable.put(attr.getName(), attr.getValue()));
        attributeHandlers.put(FlowModelAttConstant.ROLE, (attr, ext) -> ext.setRoleIds(attr.getValue()));
        attributeHandlers.put(FlowModelAttConstant.DEPT, (attr, ext) -> ext.setDeptIds(attr.getValue()));
        attributeHandlers.put(FlowModelAttConstant.USER, (attr, ext) -> ext.setCandidateUsernames(attr.getValue()));
        attributeHandlers.put(FlowModelAttConstant.DYNAMIC_PERSON, (attr, ext) -> {
            ext.setDynamicVariable(attr.getValue());
            ext.setGroupType("dynamic");
        });
        attributeHandlers.put(FlowModelAttConstant.USER_TYPE, (attr, ext) -> {
            if (StrUtil.isNotBlank(attr.getValue())) {
                ext.setGroupType(attr.getValue());
            }
        });

        for (List<ExtensionAttribute> attrList : taskAttributeMap.values()) {
            ExtensionAttribute attr = attrList.get(0);
            BiConsumer<ExtensionAttribute, ActCustomTaskExt> handler = attributeHandlers.get(attr.getName());
            if (handler != null) {
                handler.accept(attr, flowTaskExt);
            }
        }

        if (variable.size() > 0) {
            flowTaskExt.setVariableListJson(variable.toJSONString());
        }
        if (form.size() > 0) {
            flowTaskExt.setFormJson(form.toJSONString());
        }

        Map<String, List<ExtensionElement>> extensionMap = userTask.getExtensionElements();
        if (MapUtil.isNotEmpty(extensionMap)) {
            // 按钮
            List<JSONObject> operationList = this.buildOperationListExtensionElement(extensionMap);
            Optional.ofNullable(operationList).ifPresent(list -> flowTaskExt.setOperationListJson(JSON.toJSONString(list)));

            // 办理人
            addUser(userTask,extensionMap, userList, FlowModelExtElementConstant.EXT_USER_ASSIGNEE, "0");
            // 抄送人
            addUser(userTask, extensionMap, userList, FlowModelExtElementConstant.EXT_CARBON_COPY, "1");
        }

        taskExtList.add(flowTaskExt);
    }

    /**
     * 构造办理人，抄送人
     * @param userTask
     * @param extensionMap
     * @param userList
     * @param extUserAssignee
     * @param s
     */
    private void addUser(UserTask userTask, Map<String, List<ExtensionElement>> extensionMap, List<ActCustomUser> userList, String extUserAssignee, String s) {
        Optional.ofNullable(extensionMap.get(extUserAssignee))
                .ifPresent(elementList -> {
                    ExtensionElement extensionElement = elementList.get(0);
                    Optional.ofNullable(extensionElement).ifPresent(element -> {
                        Map<String, List<ExtensionAttribute>> elementAttributeMap = element.getAttributes();
                        ExtensionAttribute extensionAttribute = CollUtil.getFirst(elementAttributeMap.getOrDefault(FlowModelExtElementConstant.EXT_USER_VALUE, Collections.emptyList()));
                        Optional.ofNullable(extensionAttribute).ifPresent(attr -> {
                            // json
                            String value = attr.getValue();
                            FlowUserModel flowUserModel = JSON.parseObject(value, FlowUserModel.class);
                            Optional.ofNullable(flowUserModel).ifPresent(model -> {
                                ActCustomUser customUser = new ActCustomUser();
                                customUser.setTaskId(userTask.getId());
                                customUser.setType(s);

                                List<FlowUserAttributeModel> user = model.getUser();
                                Optional.ofNullable(user).ifPresent(users -> customUser.setUserName(users.stream().map(FlowUserAttributeModel::getValue).collect(Collectors.joining(","))));

                                List<FlowUserAttributeModel> org = model.getOrg();
                                Optional.ofNullable(org).ifPresent(orgs -> customUser.setOrgId(orgs.stream().map(FlowUserAttributeModel::getValue).collect(Collectors.joining(","))));

                                List<FlowUserAttributeModel> role = model.getRole();
                                Optional.ofNullable(role).ifPresent(roles -> customUser.setRoleCode(roles.stream().map(FlowUserAttributeModel::getValue).collect(Collectors.joining(","))));

                                List<FlowUserAttributeModel> post = model.getPost();
                                Optional.ofNullable(post).ifPresent(posts -> customUser.setPost(posts.stream().map(FlowUserAttributeModel::getValue).collect(Collectors.joining(","))));

                                userList.add(customUser);
                            });
                        });
                    });
                });
    }


    /**
     * 构建表单按钮属性
     * @param extensionMap
     * @return
     */
    private List<JSONObject> buildOperationListExtensionElement(Map<String, List<ExtensionElement>> extensionMap) {
        List<ExtensionElement> formOperationElements = extensionMap.get(FlowModelAttConstant.FORM_OPERATION);
        if (CollUtil.isNotEmpty(formOperationElements)) {
            List<JSONObject> list = new ArrayList<>();
            for (ExtensionElement e : formOperationElements) {
                JSONObject json = new JSONObject();
                Class clazz = ActOperationEntity.class;
                Field[] fields = clazz.getDeclaredFields();
                Arrays.stream(fields).filter(field -> !StrUtil.equals("serialVersionUID", field.getName())).forEach(field -> {
                    json.put(field.getName(), e.getAttributeValue(null, field.getName()));
                });
                list.add(json);
            }
            return list;
        }
        return null;
    }

    /**
     * 根据流程标识
     *
     * @param modelKey
     * @return
     */
    @Override
    public ModelInfoVo loadBpmnXmlByModelKey(String modelKey) {

        ActCustomModelInfo one = modelInfoService.getOne(new LambdaQueryWrapper<ActCustomModelInfo>().eq(ActCustomModelInfo::getModelKey, modelKey).last("limit 1"));

        if (Objects.isNull(one)) {
            throw new AiurtBootException("系统中不存在该流程模型，请刷新尝试！");
        }

        String modelId = one.getModelId();
        if (StrUtil.isBlank(modelId)) {
            throw new AiurtBootException("系统中不存在该流程模型，请刷新尝试！");
        }
        ModelInfoVo modelInfoVo = null;
        try {
            Model model = modelService.getModel(modelId);
            byte[] bpmnXML = modelService.getBpmnXML(model);
            String streamStr = new String(bpmnXML);
            modelInfoVo = new ModelInfoVo();
            modelInfoVo.setModelId(modelId);
            modelInfoVo.setModelName(model.getName());
            modelInfoVo.setModelKey(model.getKey());
            modelInfoVo.setFileName(model.getName());
            modelInfoVo.setModelXml(streamStr);
        } catch (Exception e) {
            throw new AiurtBootException("系统中不存在该流程模型，请刷新尝试！");
        }
        return modelInfoVo;
    }
}
