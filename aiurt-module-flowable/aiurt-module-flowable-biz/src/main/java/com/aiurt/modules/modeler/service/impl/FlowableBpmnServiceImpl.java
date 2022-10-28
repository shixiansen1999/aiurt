package com.aiurt.modules.modeler.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.common.exception.AiurtBootException;
import com.aiurt.common.exception.AiurtErrorEnum;
import com.aiurt.modules.common.constant.FlowModelAttConstant;
import com.aiurt.modules.editor.language.json.converter.CustomBpmnJsonConverter;
import com.aiurt.modules.manage.entity.ActCustomVersion;
import com.aiurt.modules.manage.service.IActCustomVersionService;
import com.aiurt.modules.modeler.dto.ModelInfoVo;
import com.aiurt.modules.modeler.entity.ActCustomModelInfo;
import com.aiurt.modules.modeler.entity.ActCustomTaskExt;
import com.aiurt.modules.modeler.enums.ModelFormStatusEnum;
import com.aiurt.modules.modeler.service.IActCustomModelInfoService;
import com.aiurt.modules.modeler.service.IActCustomTaskExtService;
import com.aiurt.modules.modeler.service.IFlowableBpmnService;
import com.aiurt.modules.modeler.service.IFlowableModelService;
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
        // todo 校验

        // 增加监听器

        // 选人属性
        Collection<FlowElement> elementList = bpmnModel.getMainProcess().getFlowElements();

        // list->map
        Map<String, FlowElement> elementMap =
                elementList.stream().filter(e -> e instanceof UserTask).collect(Collectors.toMap(FlowElement::getId, c -> c));

        List<ActCustomTaskExt> actCustomTaskExtList = buildTaskExtList(bpmnModel);

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
            // 页面信息, 比如业务操作接口, 前端url, 前端类型
            if (StrUtil.equalsAnyIgnoreCase(extensionAttribute.getName(), FlowModelAttConstant.FORM_TYPE,
                    FlowModelAttConstant.FORM_URL, FlowModelAttConstant.SERVICE, FlowModelAttConstant.FORM_DYNAMIC_URL)) {
                form.put(extensionAttribute.getName(), extensionAttribute.getValue());
                // 流程变量
            }else if (StrUtil.equalsAnyIgnoreCase(extensionAttribute.getName(), FlowModelAttConstant.FORM_TASK_VARIABLES)) {
                variable.put(extensionAttribute.getName(), extensionAttribute.getValue());
                // 角色
            } else if (StrUtil.equalsIgnoreCase(extensionAttribute.getName(), FlowModelAttConstant.ROLE)) {
                flowTaskExt.setRoleIds(extensionAttribute.getValue());
                // 部门
            } else if (StrUtil.equalsIgnoreCase(extensionAttribute.getName(), FlowModelAttConstant.DEPT)) {
                flowTaskExt.setDeptIds(extensionAttribute.getValue());
                // 用户id
            } else if (StrUtil.equalsIgnoreCase(extensionAttribute.getName(), FlowModelAttConstant.USER)) {
                flowTaskExt.setCandidateUsernames(extensionAttribute.getValue());
                // 动态
            } else if (StrUtil.equalsIgnoreCase(extensionAttribute.getName(), FlowModelAttConstant.DYNAMIC_PERSON)) {
                flowTaskExt.setDynamicVariable(extensionAttribute.getValue());
                flowTaskExt.setGroupType("dynamic");
            }

            // 设置
            if (StrUtil.equalsIgnoreCase(extensionAttribute.getName(), FlowModelAttConstant.USER_TYPE)) {
                if (StrUtil.isNotBlank(extensionAttribute.getValue())) {
                    flowTaskExt.setGroupType(extensionAttribute.getValue());
                }
            }
        });

        if (variable.size()>0) {
            flowTaskExt.setVariableListJson(JSONObject.toJSONString(variable));
        }
        if (form.size()>0){
            flowTaskExt.setFormJson(JSONObject.toJSONString(form));
        }

        Map<String, List<ExtensionElement>> extensionMap = userTask.getExtensionElements();
        if (MapUtil.isNotEmpty(extensionMap)) {
            List<JSONObject> operationList = this.buildOperationListExtensionElement(extensionMap);
            if (CollUtil.isNotEmpty(operationList)) {
                flowTaskExt.setOperationListJson(JSON.toJSONString(operationList));
            }
            // todo 多实例抄送
        }
        return flowTaskExt;
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
                json.put(FlowModelAttConstant.ID, e.getAttributeValue(null, FlowModelAttConstant.ID));
                json.put(FlowModelAttConstant.LABEL, e.getAttributeValue(null, FlowModelAttConstant.LABEL));
                json.put(FlowModelAttConstant.TYPE, e.getAttributeValue(null, FlowModelAttConstant.TYPE));
                json.put(FlowModelAttConstant.SHOW_ORDER, e.getAttributeValue(null, FlowModelAttConstant.SHOW_ORDER));
                list.add(json);
            }
            return list;
        }
        return null;
    }

}
