package com.aiurt.modules.editor.language.json.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.common.constant.FlowModelAttConstant;
import com.aiurt.modules.common.constant.FlowModelExtElementConstant;
import com.aiurt.modules.common.enums.MultiApprovalRuleEnum;
import com.aiurt.modules.modeler.entity.ActOperationEntity;
import com.aiurt.modules.modeler.entity.ActUserTypeEntity;
import com.aiurt.modules.modeler.entity.AutoSelectEntity;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import liquibase.pro.packaged.F;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverterContext;
import org.flowable.editor.language.json.converter.UserTaskJsonConverter;
import org.flowable.editor.language.json.converter.util.JsonConverterUtil;
import org.intellij.lang.annotations.Flow;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 扩展任务节点属性解析器
 * @author fgw
 */
@Slf4j
public class CustomUserTaskJsonConverter  extends UserTaskJsonConverter {

    /**
     * <flowable:formOperation></flowable:formOperation>
     * 表单操作按钮 {"formOperation":{"id":"","label":"","type":"","showOrder":""}
     */
    public static final String FORM_OPERATION = "formOperation";
    /**
     * 部门岗位集合
     */
    public static final String DEPT_POST_LIST = "deptPostList";

    /**
     * 多人审批规则
     */
    public static final String MULTI_APPROVAL_RULE = "multiApprovalRule";

    /**
     * 候选用户
     */
    public static final String USER_CANDIDATE_GROUPS = "userCandidateGroups";

    /**
     * 变量
     */
    private static final String FORM_VARIABLE = "formVariable";

    /**
     * 表格
     */
    private static final String FORM = "form";

    private static final String SERIAL_VERSION_UID = "serialVersionUID";

    /**
     * 注入自定义CustomUserTaskJsonConverter
     */
    static void customFillTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap, Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        fillJsonTypes(convertersToBpmnMap);
        fillBpmnTypes(convertersToJsonMap);
    }

    public static void fillJsonTypes(Map<String, Class<? extends BaseBpmnJsonConverter>> convertersToBpmnMap) {
        convertersToBpmnMap.put(STENCIL_TASK_USER, CustomUserTaskJsonConverter.class);
    }

    public static void fillBpmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseBpmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(UserTask.class, CustomUserTaskJsonConverter.class);
    }

    /**
     * //重写UserTask Element和Json转换方法
     * @param propertiesNode
     * @param baseElement
     * @param converterContext
     */
    @Override
    protected void convertElementToJson(ObjectNode propertiesNode, BaseElement baseElement, BpmnJsonConverterContext converterContext) {
        super.convertElementToJson(propertiesNode, baseElement, converterContext);
        if (baseElement instanceof UserTask){
            UserTask userTask = (UserTask) baseElement;
            // usetask属性修改
            Map<String, List<ExtensionAttribute>> attributes = baseElement.getAttributes();
            log.info("处理自定义属性:{}",JSON.toJSONString(attributes));
            attributes.forEach((key,list)->{
                ExtensionAttribute extensionAttribute = list.get(0);
                ObjectNode objectNode = super.objectMapper.createObjectNode();
                objectNode.put(extensionAttribute.getName(), extensionAttribute.getValue());
                propertiesNode.put(extensionAttribute.getName(),  extensionAttribute.getValue());
            });

            Map<String, List<ExtensionElement>> extensionElements = baseElement.getExtensionElements();

            // 自定义属性:操作按钮
            List<ExtensionElement> formOperationElements = extensionElements.get(FORM_OPERATION);
            if (CollUtil.isNotEmpty(formOperationElements)) {
                ArrayNode arrayNode = convertExtensionElementsToJson(formOperationElements, ActOperationEntity.class);
                propertiesNode.set(FORM_OPERATION, arrayNode);
            }

            // 多人审批规则
            List<ExtensionElement> userTypeElements = extensionElements.get(MULTI_APPROVAL_RULE);
            if (CollUtil.isNotEmpty(userTypeElements)) {

                // 判断是否第一个节点
                ExtensionElement extensionElement = userTypeElements.get(0);

                ObjectNode objectNode = super.objectMapper.createObjectNode();
                Field[] fields = ActUserTypeEntity.class.getDeclaredFields();
                Arrays.stream(fields).filter(field -> !StrUtil.equalsAnyIgnoreCase(SERIAL_VERSION_UID, field.getName())).forEach(field -> {
                    objectNode.put(field.getName(), extensionElement.getAttributeValue(null, field.getName()));
                });

                propertiesNode.set(MULTI_APPROVAL_RULE, objectNode);

                // 根据选人构造多实例配置；
                String multiApprovalRule = extensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_USER_VALUE);

                // 第一个节点不构造多实例配置；
                boolean isFirstUserTask = isFirstUserTask(userTask);

                if (!isFirstUserTask) {
                    propertiesNode.put(PROPERTY_MULTIINSTANCE_COLLECTION, "assigneeList_usertask_"+baseElement.getId());
                    propertiesNode.put(PROPERTY_MULTIINSTANCE_VARIABLE, "assignee");
                    propertiesNode.putNull(PROPERTY_MULTIINSTANCE_VARIABLE_AGGREGATIONS);

                    switch (multiApprovalRule) {
                        // 任意会签
                        case "taskMultiInstanceType-1":
                            propertiesNode.put(PROPERTY_MULTIINSTANCE_TYPE, "Parallel");
                            propertiesNode.put(PROPERTY_MULTIINSTANCE_CONDITION, "${nrOfCompletedInstances >= 1}");
                            break;
                        // 并行
                        case "taskMultiInstanceType-2":
                            propertiesNode.put(PROPERTY_MULTIINSTANCE_TYPE, "Parallel");
                            propertiesNode.put(PROPERTY_MULTIINSTANCE_CONDITION, "${nrOfCompletedInstances == nrOfInstances}");
                            break;
                        default:
                            propertiesNode.put(PROPERTY_MULTIINSTANCE_TYPE, "Sequential");
                    }
                }

                // 流程选人的
                ObjectNode assignmentNode = objectMapper.createObjectNode();
                ObjectNode assignmentValuesNode = objectMapper.createObjectNode();
                assignmentValuesNode.put("type", "static");
                // $INITIATOR 表示流程发起人
                assignmentValuesNode.put(PROPERTY_USERTASK_ASSIGNEE,isFirstUserTask ? "$INITIATOR": "${assignee}");
                assignmentValuesNode.put("initiatorCanCompleteTask", true);
                assignmentNode.set("assignment", assignmentValuesNode);
                propertiesNode.set(PROPERTY_USERTASK_ASSIGNMENT, assignmentNode);
            }

            // 选人将 flowable:userassignee 属性转换为 JSON 格式
            List<ExtensionElement> userAssigneeElements = extensionElements.get(FlowModelExtElementConstant.EXT_USER_ASSIGNEE);
            buildJsonElement(propertiesNode, userAssigneeElements, FlowModelExtElementConstant.EXT_USER_ASSIGNEE);

            // 抄送人
            List<ExtensionElement> carbonCopyElements = extensionElements.get(FlowModelExtElementConstant.EXT_CARBON_COPY);
            buildJsonElement(propertiesNode, carbonCopyElements, FlowModelExtElementConstant.EXT_CARBON_COPY);

            // 自动选人
            List<ExtensionElement> autoSelectElements = extensionElements.get(FlowModelExtElementConstant.EXT_AUTO_SELECT);
            if (CollUtil.isNotEmpty(autoSelectElements)) {
                ExtensionElement extensionElement = autoSelectElements.get(0);

                ObjectNode objectNode = super.objectMapper.createObjectNode();
                Field[] fields = AutoSelectEntity.class.getDeclaredFields();
                Arrays.stream(fields).filter(field -> !StrUtil.equalsAnyIgnoreCase(SERIAL_VERSION_UID, field.getName())).forEach(field -> {
                    objectNode.put(field.getName(), extensionElement.getAttributeValue(null, field.getName()));
                });

                propertiesNode.set(FlowModelExtElementConstant.EXT_AUTO_SELECT, objectNode);
            }
        }
    }

    /**
     *
     * @param userTask
     * @return
     */
    private boolean isFirstUserTask(UserTask userTask) {
        List<SequenceFlow> incomingFlows = userTask.getIncomingFlows();
        for (int i = 0; i < incomingFlows.size(); i++) {
            SequenceFlow sequenceFlow = incomingFlows.get(i);

            String sourceRef = sequenceFlow.getSourceRef();
            if (StrUtil.containsIgnoreCase(sourceRef, "Event")) {
                return true;
            }
        }
        return false;
    }

    private void buildJsonElement(ObjectNode propertiesNode, List<ExtensionElement> extensionElementList, String elementName) {
        if (CollUtil.isNotEmpty(extensionElementList)) {
            ExtensionElement extensionElement = extensionElementList.get(0);
            String name = extensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_USER_NAME);
            String value = extensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_USER_VALUE);
            String alias = extensionElement.getAttributeValue(null, FlowModelExtElementConstant.EXT_USER_ALIAS);
            JsonNode jsonNode = parseUserAssigneeValue(value);
            ObjectNode objectNode = objectMapper.createObjectNode();
            objectNode.put(FlowModelExtElementConstant.EXT_USER_NAME, StrUtil.isNotBlank(name) ? name : "");
            objectNode.set(FlowModelExtElementConstant.EXT_USER_VALUE, jsonNode);
            objectNode.put(FlowModelExtElementConstant.EXT_USER_ALIAS, alias);
            propertiesNode.set(elementName, objectNode);
        }
    }

    /**
     *  重写Json和UserTask Element转换方法
     * @param elementNode
     * @param modelNode
     * @param shapeMap
     * @param converterContex
     * @return
     */
    @Override
    protected FlowElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, Map<String, JsonNode> shapeMap, BpmnJsonConverterContext converterContex) {
        FlowElement flowElement = super.convertJsonToElement(elementNode, modelNode, shapeMap, converterContex);
        try {
            this.addExtansionPropertiesElement(flowElement, elementNode);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
        }
        return flowElement;
    }

    private void addExtansionPropertiesElement(FlowElement flowElement, JsonNode elementNode) throws JsonProcessingException {
        if (flowElement instanceof UserTask) {
            UserTask userTask = (UserTask) flowElement;

            // 自定义属性:操作按钮
            addExtensionElementToUserTask(userTask, FORM_OPERATION, JsonConverterUtil.getProperty(FORM_OPERATION, elementNode));
            // 多人审批规则
            addExtensionElementToUserTask(userTask, MULTI_APPROVAL_RULE, JsonConverterUtil.getProperty(MULTI_APPROVAL_RULE, elementNode));
            // 选人
            addExtensionElementToUserTask(userTask, FlowModelExtElementConstant.EXT_USER_ASSIGNEE,
                    JsonConverterUtil.getProperty(FlowModelExtElementConstant.EXT_USER_ASSIGNEE, elementNode));

            // 抄送人
            addExtensionElementToUserTask(userTask, FlowModelExtElementConstant.EXT_CARBON_COPY,
                    JsonConverterUtil.getProperty(FlowModelExtElementConstant.EXT_CARBON_COPY, elementNode));

            // 1.0选人
            // 选人类型， initiator是为：流程发起人, data
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.USER_TYPE);
            // 角色
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.ROLE);
            // 部门
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.DEPT);
            // 指定人员
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.USER);
            // 动态人员
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.DYNAMIC_PERSON);
            // 人员类型: fixed ,dynim
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.DATA_TYPE);
            // 表单页面 类型
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.FORM_TYPE);
            // 表单设计器
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.FORM_DYNAMIC_URL);
            // 表单url
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.FORM_URL);
            // 业务处理
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.SERVICE);
            // 流程变量
            addCustomAttributeForPrefix(elementNode, userTask, FlowModelAttConstant.FLOWABLE, FlowModelAttConstant.FORM_TASK_VARIABLES);


            addCustomAttributeForPrefix(elementNode, userTask,"flowable", "formtaskVariables");
        }
    }

    public static void main(String[] args) {
        String v = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:flowable=\"http://flowable.org/bpmn\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"http://flowable.org/test\" exporter=\"Flowable Open Source Modeler\" exporterVersion=\"6.7.2\">\n" +
                "  <process id=\"test0001\" name=\"test0001\" isExecutable=\"true\">\n" +
                "    <startEvent id=\"startEvent1\">\n" +
                "      <outgoing>Flow_1dwhrr1</outgoing>\n" +
                "    </startEvent>\n" +
                "    <sequenceFlow id=\"Flow_1dwhrr1\" sourceRef=\"startEvent1\" targetRef=\"Activity_0jnxa6u\" />\n" +
                "    <endEvent id=\"Event_118lroh\">\n" +
                "      <incoming>Flow_0m4y72z</incoming>\n" +
                "    </endEvent>\n" +
                "    <sequenceFlow id=\"Flow_0m4y72z\" sourceRef=\"Activity_0jnxa6u\" targetRef=\"Event_118lroh\" />\n" +
                "    <userTask id=\"Activity_0jnxa6u\">\n" +
                "      <extensionElements>\n" +
                "        <flowable:carboncopy value=\"{}\" />\n" +
                "        <flowable:userassignee value=\"{}\" />\n" +
                "        <flowable:autoselect value=\"false\" />\n" +
                "        <flowable:multiApprovalRule value=\"taskMultiInstanceType-3\" />\n" +
                "      </extensionElements>\n" +
                "      <incoming>Flow_1dwhrr1</incoming>\n" +
                "      <outgoing>Flow_0m4y72z</outgoing>\n" +
                "    </userTask>\n" +
                "  </process>\n" +
                "  <bpmndi:BPMNDiagram id=\"BPMNDiagram_test0001\">\n" +
                "    <bpmndi:BPMNPlane id=\"BPMNPlane_test0001\" bpmnElement=\"test0001\">\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_1dwhrr1_di\" bpmnElement=\"Flow_1dwhrr1\">\n" +
                "        <omgdi:waypoint x=\"130\" y=\"178\" />\n" +
                "        <omgdi:waypoint x=\"180\" y=\"178\" />\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_0m4y72z_di\" bpmnElement=\"Flow_0m4y72z\">\n" +
                "        <omgdi:waypoint x=\"280\" y=\"178\" />\n" +
                "        <omgdi:waypoint x=\"332\" y=\"178\" />\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNShape id=\"BPMNShape_startEvent1\" bpmnElement=\"startEvent1\">\n" +
                "        <omgdc:Bounds x=\"100\" y=\"163\" width=\"30\" height=\"30\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Event_118lroh_di\" bpmnElement=\"Event_118lroh\">\n" +
                "        <omgdc:Bounds x=\"332\" y=\"160\" width=\"36\" height=\"36\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_1tw11re_di\" bpmnElement=\"Activity_0jnxa6u\">\n" +
                "        <omgdc:Bounds x=\"180\" y=\"138\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "    </bpmndi:BPMNPlane>\n" +
                "  </bpmndi:BPMNDiagram>\n" +
                "</definitions>\n";

        System.out.println(v.replaceAll("\n"," "));

        String v2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:flowable=\"http://flowable.org/bpmn\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"http://flowable.org/test\" exporter=\"Flowable Open Source Modeler\" exporterVersion=\"6.7.2\">   <process id=\"test0001\" name=\"test0001\" isExecutable=\"true\">     <startEvent id=\"startEvent1\">       <outgoing>Flow_1dwhrr1</outgoing>     </startEvent>     <sequenceFlow id=\"Flow_1dwhrr1\" sourceRef=\"startEvent1\" targetRef=\"Activity_0jnxa6u\" />     <endEvent id=\"Event_118lroh\">       <incoming>Flow_0m4y72z</incoming>     </endEvent>     <sequenceFlow id=\"Flow_0m4y72z\" sourceRef=\"Activity_0jnxa6u\" targetRef=\"Event_118lroh\" />     <userTask id=\"Activity_0jnxa6u\">       <extensionElements>         <flowable:carboncopy value=\"{}\" />         <flowable:userassignee value=\"{}\" />         <flowable:autoselect value=\"false\" />         <flowable:multiApprovalRule value=\"taskMultiInstanceType-3\" />       </extensionElements>       <incoming>Flow_1dwhrr1</incoming>       <outgoing>Flow_0m4y72z</outgoing>     </userTask>   </process>   <bpmndi:BPMNDiagram id=\"BPMNDiagram_test0001\">     <bpmndi:BPMNPlane id=\"BPMNPlane_test0001\" bpmnElement=\"test0001\">       <bpmndi:BPMNEdge id=\"Flow_1dwhrr1_di\" bpmnElement=\"Flow_1dwhrr1\">         <omgdi:waypoint x=\"130\" y=\"178\" />         <omgdi:waypoint x=\"180\" y=\"178\" />       </bpmndi:BPMNEdge>       <bpmndi:BPMNEdge id=\"Flow_0m4y72z_di\" bpmnElement=\"Flow_0m4y72z\">         <omgdi:waypoint x=\"280\" y=\"178\" />         <omgdi:waypoint x=\"332\" y=\"178\" />       </bpmndi:BPMNEdge>       <bpmndi:BPMNShape id=\"BPMNShape_startEvent1\" bpmnElement=\"startEvent1\">         <omgdc:Bounds x=\"100\" y=\"163\" width=\"30\" height=\"30\" />       </bpmndi:BPMNShape>       <bpmndi:BPMNShape id=\"Event_118lroh_di\" bpmnElement=\"Event_118lroh\">         <omgdc:Bounds x=\"332\" y=\"160\" width=\"36\" height=\"36\" />       </bpmndi:BPMNShape>       <bpmndi:BPMNShape id=\"Activity_1tw11re_di\" bpmnElement=\"Activity_0jnxa6u\">         <omgdc:Bounds x=\"180\" y=\"138\" width=\"100\" height=\"80\" />       </bpmndi:BPMNShape>     </bpmndi:BPMNPlane>   </bpmndi:BPMNDiagram> </definitions> ";


        String v3 =   "<?xml version='1.0' encoding='UTF-8'?>\n<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:flowable=\"http://flowable.org/bpmn\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" typeLanguage=\"http://www.w3.org/2001/XMLSchema\" expressionLanguage=\"http://www.w3.org/1999/XPath\" targetNamespace=\"http://flowable.org/modeler\" exporter=\"Flowable Open Source Modeler\" exporterVersion=\"6.7.2\">\n  <process id=\"test0001\" name=\"test0001\" isExecutable=\"true\">\n    <startEvent id=\"startEvent1\"/>\n    <sequenceFlow id=\"Flow_1dwhrr1\" sourceRef=\"startEvent1\" targetRef=\"Activity_0jnxa6u\"/>\n    <endEvent id=\"Event_118lroh\"/>\n    <sequenceFlow id=\"Flow_0m4y72z\" sourceRef=\"Activity_0jnxa6u\" targetRef=\"Event_118lroh\"/>\n    <userTask id=\"Activity_0jnxa6u\" flowable:assignee=\"${assignee}\">\n      <extensionElements>\n        <modeler:initiator-can-complete xmlns:modeler=\"http://flowable.org/modeler\"><![CDATA[true]]></modeler:initiator-can-complete>\n        <flowable:multiApprovalRule xmlns:flowable=\"http://flowable.org/bpmn\" value=\"taskMultiInstanceType-1\"/>\n        <flowable:userassignee xmlns:flowable=\"http://flowable.org/bpmn\" name=\"\" value=\"{}\"/>\n        <flowable:carboncopy xmlns:flowable=\"http://flowable.org/bpmn\" name=\"\" value=\"{}\"/>\n      </extensionElements>\n      <multiInstanceLoopCharacteristics isSequential=\"false\" flowable:collection=\"assigneeList_usertask_Activity_0jnxa6u\" flowable:elementVariable=\"assignee\">\n        <extensionElements/>\n        <completionCondition>${nrOfCompletedInstances >= 1}</completionCondition>\n      </multiInstanceLoopCharacteristics>\n    </userTask>\n  </process>\n  <bpmndi:BPMNDiagram id=\"BPMNDiagram_test0001\">\n    <bpmndi:BPMNPlane bpmnElement=\"test0001\" id=\"BPMNPlane_test0001\">\n      <bpmndi:BPMNShape bpmnElement=\"startEvent1\" id=\"BPMNShape_startEvent1\">\n        <omgdc:Bounds height=\"30.0\" width=\"30.0\" x=\"100.0\" y=\"163.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Event_118lroh\" id=\"BPMNShape_Event_118lroh\">\n        <omgdc:Bounds height=\"36.0\" width=\"36.0\" x=\"332.0\" y=\"160.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_0jnxa6u\" id=\"BPMNShape_Activity_0jnxa6u\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"180.0\" y=\"138.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_1dwhrr1\" id=\"BPMNEdge_Flow_1dwhrr1\" flowable:sourceDockerX=\"15.0\" flowable:sourceDockerY=\"15.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"129.9499986183554\" y=\"178.0\"/>\n        <omgdi:waypoint x=\"180.0\" y=\"178.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_0m4y72z\" id=\"BPMNEdge_Flow_0m4y72z\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"18.0\" flowable:targetDockerY=\"18.0\">\n        <omgdi:waypoint x=\"279.95000000000005\" y=\"178.0\"/>\n        <omgdi:waypoint x=\"332.0\" y=\"178.0\"/>\n      </bpmndi:BPMNEdge>\n    </bpmndi:BPMNPlane>\n  </bpmndi:BPMNDiagram>\n</definitions>";

        System.out.println(v3);
    }


    /**
     * 属性
     * @param elementNode
     * @param userTask
     * @param prefix
     * @param attr
     */
    private void addCustomAttributeForPrefix(JsonNode elementNode, UserTask userTask, String prefix, String attr) {
        String formType = JsonConverterUtil.getPropertyValueAsString(attr, elementNode);
        if (StrUtil.isNotBlank(formType)) {
            ExtensionAttribute attribute = new ExtensionAttribute();
            attribute.setName(attr);
            attribute.setValue(formType);
            attribute.setNamespacePrefix(prefix);
            attribute.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
            userTask.addAttribute(attribute);
        }
    }

    /**
     * 构造属性
     * @param value 属性值
     * @param userTask 任务节点
     * @param prefix 前缀
     * @param attr xml 节点属性
     */
    private void addCustomAttributeForPrefix(String value, UserTask userTask, String prefix, String attr) {
        if (StrUtil.isNotBlank(value)) {
            ExtensionAttribute attribute = new ExtensionAttribute();
            attribute.setName(attr);
            attribute.setValue(value);
            attribute.setNamespacePrefix(prefix);
            attribute.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
            userTask.addAttribute(attribute);
        }
    }

    private void addCustomAttribute(JsonNode elementNode, UserTask userTask, String s) {
        String formType = JsonConverterUtil.getPropertyValueAsString(s, elementNode);
        if (StrUtil.isNotBlank(formType)) {
            ExtensionAttribute attribute = new ExtensionAttribute();
            attribute.setName(s);
            attribute.setValue(formType);
            userTask.addAttribute(attribute);
        }
    }



    /**
     * json 字符转为 JsonNode,json对象非json数组
     * @param value
     * @return
     */
    private JsonNode parseUserAssigneeValue(String value) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(value);
            return jsonNode;
        } catch (IOException e) {
            // ignore exception
        }
        return null;
    }



    /**
     * 构建自定义xml标签<flowable:userTye name="userType" value="taskMultiInstanceType-2">
     * @param extensionName 扩展元素的名称
     * @param jsonObject  扩展元素的数据
     * @return
     */
    @NotNull
    private ExtensionElement getExtensionElement(String extensionName, JSONObject jsonObject) {
        ExtensionElement extensionElement = new ExtensionElement();
        extensionElement.setName(extensionName);
        extensionElement.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
        extensionElement.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
        Set<String> keySet = jsonObject.keySet();
        keySet.stream().forEach(key -> {
            ExtensionAttribute attribute = new ExtensionAttribute();
            attribute.setName(key);
            attribute.setValue(jsonObject.getString(key));
            extensionElement.addAttribute(attribute);
        });
        return extensionElement;
    }

    /**
     * 向用户任务（UserTask）添加扩展元素（ExtensionElement）。
     *
     * @param userTask      用户任务对象，用于添加扩展元素
     * @param extensionName 扩展元素的名称
     * @param extensionData 扩展元素的数据，以 JSON 格式表示
     * @throws JsonProcessingException 如果 JSON 转换过程中出现异常
     */
    private void addExtensionElementToUserTask(UserTask userTask, String extensionName, JsonNode extensionData) throws JsonProcessingException {
        if (Objects.nonNull(extensionData)) {
            String json = objectMapper.writeValueAsString(extensionData);
            log.info("json->{}", json);
            if (extensionData.isArray()) {
                JSONArray jsonArray = JSONObject.parseArray(json);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ExtensionElement ee = getExtensionElement(extensionName, jsonObject);
                    userTask.addExtensionElement(ee);
                }
            } else if (extensionData.isObject()) {
                JSONObject jsonObject = JSONObject.parseObject(json);
                ExtensionElement ee = getExtensionElement(extensionName, jsonObject);
                userTask.addExtensionElement(ee);
            }
        }
    }



    /**
     * 将扩展元素列表转换为 JSON 数组，并根据指定类（clazz）的字段将属性名和属性值添加到 JSON 对象中。
     *
     * @param extensionElements 扩展元素列表，用于生成 JSON 数组
     * @param clazz             指定的类，用于获取字段信息
     * @return 生成的 JSON 数组（ArrayNode），包含扩展元素的属性名和属性值
     */
    private ArrayNode convertExtensionElementsToJson(List<ExtensionElement> extensionElements, Class<?> clazz) {
        ArrayNode arrayNode = super.objectMapper.createArrayNode();
        for (ExtensionElement e : extensionElements) {
            ObjectNode objectNode = super.objectMapper.createObjectNode();
            Field[] fields = clazz.getDeclaredFields();
            Arrays.stream(fields).filter(field -> !StrUtil.equalsAnyIgnoreCase(SERIAL_VERSION_UID, field.getName())).forEach(field -> {
                objectNode.put(field.getName(), e.getAttributeValue(null, field.getName()));
            });
            arrayNode.add(objectNode);
        }
        return arrayNode;
    }
}
