package com.aiurt.modules.editor.language.json.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aiurt.modules.utils.ExtensionPropertiesUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.*;
import org.flowable.editor.language.json.converter.BaseBpmnJsonConverter;
import org.flowable.editor.language.json.converter.BpmnJsonConverterContext;
import org.flowable.editor.language.json.converter.UserTaskJsonConverter;
import org.flowable.editor.language.json.converter.util.JsonConverterUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 扩展任务节点属性解析器
 * @author fgw
 */
@Slf4j
public class CustomUserTaskJsonConverter  extends UserTaskJsonConverter {


    /**
     * 操作按钮, [{"formOperation":{"id":"","label":"","type":"","showOrder":""}}]
     */
    public static final String OPERATION_LIST = "operationList";

    /**
     * 表单操作按钮
     */
    public static final String FORM_OPERATION = "formOperation";

    /**
     * 变量
     */
    private static final String FORM_VARIABLE = "formVariable";

    /**
     * 表格
     */
    private static final String FORM = "form";

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
            //  自定义属性:操作按钮
            List<ExtensionElement> formOperationElements = extensionElements.get(FORM_OPERATION);
            if (CollUtil.isNotEmpty(formOperationElements)) {
                ArrayNode arrayNode = super.objectMapper.createArrayNode();
                for (ExtensionElement e : formOperationElements) {
                    ObjectNode objectNode = super.objectMapper.createObjectNode();
                    objectNode.put("id", e.getAttributeValue(null, "id"));
                    objectNode.put("label", e.getAttributeValue(null, "label"));
                    objectNode.put("type", e.getAttributeValue(null, "type"));
                    objectNode.put("showOrder", e.getAttributeValue(null, "showOrder"));
                    arrayNode.add(objectNode);
                }
                propertiesNode.set(FORM_OPERATION, arrayNode);
            }

            // 流程选人, 上级部门以及
            List<ExtensionElement> deptPostElements =
                    ExtensionPropertiesUtil.getMyExtensionElementList(extensionElements, "deptPostList", "deptPost");
            if (CollUtil.isNotEmpty(deptPostElements)) {
                ObjectNode node = super.objectMapper.createObjectNode();
                ArrayNode arrayNode = super.objectMapper.createArrayNode();
                for (ExtensionElement e : deptPostElements) {
                    ObjectNode objectNode = super.objectMapper.createObjectNode();
                    objectNode.put("id", e.getAttributeValue(null, "id"));
                    objectNode.put("type", e.getAttributeValue(null, "type"));
                    objectNode.put("postId", e.getAttributeValue(null, "postId"));
                    objectNode.put("deptPostId", e.getAttributeValue(null, "deptPostId"));
                    arrayNode.add(objectNode);
                }
                node.set("deptPost", arrayNode);
                propertiesNode.set("deptPostList", node);
            }


            List<ExtensionElement> elementCandidateGroupsList = extensionElements.get("userCandidateGroups");
            if (CollUtil.isNotEmpty(elementCandidateGroupsList)) {
                ExtensionElement ee = elementCandidateGroupsList.get(0);
                ObjectNode node = super.objectMapper.createObjectNode();
                node.put("type", ee.getAttributeValue(null, "type"));
                node.put("value", ee.getAttributeValue(null, "value"));
                propertiesNode.set("userCandidateGroups", node);
            }

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
        if (flowElement instanceof UserTask){
            UserTask userTask = (UserTask) flowElement;
            JsonNode expansionNode = JsonConverterUtil.getProperty(FORM_OPERATION, elementNode);
            if (Objects.nonNull(expansionNode)) {
                String json = objectMapper.writeValueAsString(expansionNode);
                log.info("json->{}",json);
                JSONArray jsonArray = JSONObject.parseArray(json);
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    ExtensionElement ee = new ExtensionElement();
                    ee.setName(FORM_OPERATION);
                    ee.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
                    ee.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
                    Set<String> keySet = jsonObject.keySet();
                    keySet.stream().forEach(key-> {
                        ExtensionAttribute attribute = new ExtensionAttribute();
                        attribute.setName(key);
                        attribute.setValue(jsonObject.getString(key));
                        ee.addAttribute(attribute);
                    });
                    userTask.addExtensionElement(ee);
                }
            }

            // 表单页面 类型
            // addCustomAttribute(elementNode, userTask, "formData.formType");
            addCustomAttributeForPrefix(elementNode, userTask, "flowable","formType");
            // 表单url
            // addCustomAttribute(elementNode, userTask, "formData.formUrl");
            addCustomAttributeForPrefix(elementNode, userTask,"flowable", "formUrl");
            // 业务处理
           // addCustomAttribute(elementNode, userTask, "formData.service");
            addCustomAttributeForPrefix(elementNode, userTask, "flowable", "service");

            addCustomAttributeForPrefix(elementNode, userTask,"flowable", "formtaskVariables");
            // 流程变量
            // addCustomAttribute(elementNode, userTask, "flowable.formtaskVariables");

            JsonNode deptPostList = JsonConverterUtil.getProperty("deptPostList", elementNode);

            if (Objects.nonNull(deptPostList)) {
                String json = objectMapper.writeValueAsString(deptPostList);
                log.info("json->{}",json);
                ExtensionElement ee = buildElement(deptPostList, json, "deptPostList", "deptPost");
                userTask.addExtensionElement(ee);
            }

            // 候选用户
            JsonNode userCandidateGroupNode = JsonConverterUtil.getProperty("userCandidateGroups", elementNode);
            if (Objects.nonNull(userCandidateGroupNode)) {
                String json = objectMapper.writeValueAsString(userCandidateGroupNode);

                JSONObject jsonObject = JSONObject.parseObject(json);

                ExtensionElement ee = new ExtensionElement();
                ee.setName("userCandidateGroups");
                ee.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
                ee.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
                Set<String> keySet = jsonObject.keySet();
                keySet.stream().forEach(key-> {
                    ExtensionAttribute attribute = new ExtensionAttribute();
                    attribute.setName(key);
                    attribute.setValue(jsonObject.getString(key));
                    ee.addAttribute(attribute);
                });
                userTask.addExtensionElement(ee);
            }
        }
    }

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
     *  构建xml
     * @param expansionNode nodejson
     * @param json jsonString
     * @param listName 一级名称
     * @param childListName 二级
     * @return
     */
    @NotNull
    private ExtensionElement buildElement(JsonNode expansionNode, String json,String listName, String childListName) {
        ExtensionElement ee = new ExtensionElement();
        ee.setName(listName);
        ee.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
        ee.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
        if (expansionNode instanceof ObjectNode) {
            JSONObject jsonObject = JSONObject.parseObject(json);
            JSONArray operation = jsonObject.getJSONArray(childListName);

            Map<String, List<ExtensionElement>> map = new LinkedHashMap<>();
            List<ExtensionElement> extensionElementList = new ArrayList<>();
            for (int i = 0; i < operation.size(); i++) {
                ExtensionElement child = new ExtensionElement();
                child.setName(childListName);
                child.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
                child.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
                JSONObject object = operation.getJSONObject(i);
                Set<String> keySet = object.keySet();
                keySet.stream().forEach(key->{
                    ExtensionAttribute attribute = new ExtensionAttribute();
                    attribute.setName(key);
                    attribute.setValue(object.getString(key));
                    child.addAttribute(attribute);
                });
                extensionElementList.add(child);
            }
            map.put(childListName, extensionElementList);
            ee.setChildElements(map);
            // 如果是文本
        }else if (expansionNode instanceof TextNode) {
            ee.setElementText(expansionNode.asText());
        }
        return ee;
    }

    public static void main(String[] args) {
        String va = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:flowable=\"http://flowable.org/bpmn\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"http://flowable.org/modeler\" exporter=\"Flowable Open Source Modeler\" exporterVersion=\"6.7.2\">  <process id=\"bd_work_ticket2\" name=\"第二种工作票\" isExecutable=\"true\">    <startEvent id=\"startEvent1\" />    <sequenceFlow id=\"Flow_0spc0yf\" sourceRef=\"startEvent1\" targetRef=\"Activity_0zi21v6\" />    <userTask xmlns:flowable=\"http://flowable.org/bpmn\" id=\"Activity_0zi21v6\" name=\"修改提交\" flowable:formType=\"1\" flowable:formUrl=\"/test/vue\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:assignee=\"${startUserName}\">      <extensionElements>        <flowable:formOperation id=\"1665472664181\" label=\"提交\" type=\"1\" showOrder=\"1\" />        <flowable:formOperation id=\"1665472664181\" label=\"保存\" type=\"6\" showOrder=\"2\" />      </extensionElements>      <incoming>Flow_0s1rk8f</incoming>      <incoming>Flow_13t7f6e</incoming>    </userTask>    <sequenceFlow id=\"Flow_0dstjw4\" name=\"提交\" sourceRef=\"Activity_0zi21v6\" targetRef=\"Activity_08cdns0\" flowable:transferType=\"2\" >      <documentation>已提交待审核</documentation>      <extensionElements>        <flowable:property name=\"已提交待审核\" value=\"2\" />\t\t<flowable:service  name=\"BdWorkTicketServiceImpl.updateState\"/>      </extensionElements>    </sequenceFlow>    <userTask id=\"Activity_08cdns0\" name=\"审核\" flowable:formType=\"1\" flowable:formUrl=\"/test\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:assignee=\"admin\">      <extensionElements>        <flowable:formOperation id=\"1665472702468\" label=\"提交\" type=\"1\" showOrder=\"1\" />        <flowable:formOperation id=\"1665472702468\" label=\"驳回\" type=\"3\" showOrder=\"2\" />      </extensionElements>      <outgoing>Flow_0s1rk8f</outgoing>    </userTask>    <sequenceFlow id=\"Flow_1cbbs8u\" name=\"审核待签发\" sourceRef=\"Activity_08cdns0\" targetRef=\"Activity_0skgv56\" flowable:transferType=\"2\" >      <documentation>已审核待签发</documentation>      <extensionElements>        <flowable:property name=\"已审核待签发\" value=\"3\" />\t\t<flowable:service  name=\"BdWorkTicketServiceImpl.updateState\"/>      </extensionElements>    </sequenceFlow>    <userTask id=\"Activity_0skgv56\" name=\"签发\" flowable:formType=\"1\" flowable:formUrl=\"/test\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:assignee=\"admin\">      <extensionElements>        <flowable:formOperation id=\"1665472789695\" label=\"提交\" type=\"1\" showOrder=\"1\" />        <flowable:formOperation id=\"1665472789695\" label=\"驳回\" type=\"3\" showOrder=\"2\" />      </extensionElements>      <outgoing>Flow_13t7f6e</outgoing>    </userTask>    <sequenceFlow id=\"Flow_11j25sb\" name=\"签发待归档\" sourceRef=\"Activity_0skgv56\" targetRef=\"Activity_1kzyhgs\" flowable:transferType=\"2\" >      <documentation>已签发待归档</documentation>      <extensionElements>        <flowable:property name=\"签发待归档\" value=\"6\" />\t\t<flowable:service  name=\"BdWorkTicketServiceImpl.updateState\"/>      </extensionElements>    </sequenceFlow>    <userTask id=\"Activity_1kzyhgs\" name=\"归档\" flowable:formType=\"1\" flowable:formUrl=\"/test\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:assignee=\"admin\">      <extensionElements>        <flowable:formOperation id=\"1665472806261\" label=\"提交\" type=\"1\" showOrder=\"1\" />      </extensionElements>    </userTask>    <sequenceFlow id=\"Flow_1uh9amj\" name=\"归档待完结\" sourceRef=\"Activity_1kzyhgs\" targetRef=\"Activity_1q5553k\" flowable:transferType=\"0\" >      <documentation>已归档待完结</documentation>      <extensionElements>        <flowable:property name=\"已归档待完结\" value=\"7\" />\t\t<flowable:service  name=\"BdWorkTicketServiceImpl.updateState\"/>      </extensionElements>    </sequenceFlow>    <endEvent id=\"Event_1wv5rwj\" />    <sequenceFlow id=\"Flow_020n32j\" name=\"已完结\" sourceRef=\"Activity_1q5553k\" targetRef=\"Event_1wv5rwj\" >      <extensionElements>        <flowable:property name=\"已完结\" value=\"8\" />\t\t<flowable:service  name=\"BdWorkTicketServiceImpl.updateState\"/>      </extensionElements>    </sequenceFlow>    <userTask id=\"Activity_1q5553k\" name=\"完结\" flowable:formType=\"1\" flowable:formUrl=\"/test\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:assignee=\"admin\">      <extensionElements>        <flowable:formOperation id=\"1665472817653\" label=\"提交\" type=\"1\" showOrder=\"1\" />      </extensionElements>    </userTask>    <sequenceFlow id=\"Flow_0s1rk8f\" name=\"审核驳回\" sourceRef=\"Activity_08cdns0\" targetRef=\"Activity_0zi21v6\" flowable:transferType=\"0\" >      <documentation>审核驳回</documentation>      <extensionElements>        <flowable:property name=\"审核人驳回\" value=\"11\" />\t\t<flowable:service  name=\"BdWorkTicketServiceImpl.updateState\"/>      </extensionElements>      <conditionExpression xsi:type=\"tFormalExpression\">${operationType}=='rejectToStart'</conditionExpression>    </sequenceFlow>    <sequenceFlow id=\"Flow_13t7f6e\" name=\"签发人驳回\" sourceRef=\"Activity_0skgv56\" targetRef=\"Activity_0zi21v6\" flowable:transferType=\"0\" >      <documentation>签发人驳回</documentation>      <extensionElements>        <flowable:property name=\"签发人驳回\" value=\"12\" />\t\t<flowable:service  name=\"BdWorkTicketServiceImpl.updateState\"/>      </extensionElements>      <conditionExpression xsi:type=\"tFormalExpression\">${operationType}=='rejectToStart'</conditionExpression>    </sequenceFlow>  </process>  <bpmndi:BPMNDiagram id=\"BPMNDiagram_bd_work_ticket2\">    <bpmndi:BPMNPlane id=\"BPMNPlane_bd_work_ticket2\" bpmnElement=\"bd_work_ticket2\">      <bpmndi:BPMNEdge id=\"BPMNEdge_Flow_020n32j\" bpmnElement=\"Flow_020n32j\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"18.0\" flowable:targetDockerY=\"18.0\">        <omgdi:waypoint x=\"579.9499999999922\" y=\"90\" />        <omgdi:waypoint x=\"642\" y=\"90\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"594\" y=\"72\" width=\"34\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"BPMNEdge_Flow_1uh9amj\" bpmnElement=\"Flow_1uh9amj\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">        <omgdi:waypoint x=\"419.95000000000005\" y=\"90\" />        <omgdi:waypoint x=\"480\" y=\"90\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"422\" y=\"72\" width=\"56\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"BPMNEdge_Flow_11j25sb\" bpmnElement=\"Flow_11j25sb\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">        <omgdi:waypoint x=\"259.95000000000005\" y=\"90\" />        <omgdi:waypoint x=\"319.9999999999376\" y=\"90\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"262\" y=\"72\" width=\"55\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"BPMNEdge_Flow_1cbbs8u\" bpmnElement=\"Flow_1cbbs8u\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">        <omgdi:waypoint x=\"99.9499999999471\" y=\"90\" />        <omgdi:waypoint x=\"159.99999999998312\" y=\"90\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"102\" y=\"72\" width=\"56\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"BPMNEdge_Flow_0dstjw4\" bpmnElement=\"Flow_0dstjw4\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">        <omgdi:waypoint x=\"-60.050000000052904\" y=\"90\" />        <omgdi:waypoint x=\"-1.6896706256375182e-11\" y=\"90\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"-41\" y=\"72\" width=\"22\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"BPMNEdge_Flow_0spc0yf\" bpmnElement=\"Flow_0spc0yf\" flowable:sourceDockerX=\"15.0\" flowable:sourceDockerY=\"15.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">        <omgdi:waypoint x=\"-215.05000126895308\" y=\"90\" />        <omgdi:waypoint x=\"-160.0000000000022\" y=\"90\" />      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_0s1rk8f_di\" bpmnElement=\"Flow_0s1rk8f\">        <omgdi:waypoint x=\"50\" y=\"50\" />        <omgdi:waypoint x=\"50\" y=\"30\" />        <omgdi:waypoint x=\"-110\" y=\"30\" />        <omgdi:waypoint x=\"-110\" y=\"50\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"-51\" y=\"12\" width=\"44\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_13t7f6e_di\" bpmnElement=\"Flow_13t7f6e\">        <omgdi:waypoint x=\"210\" y=\"130\" />        <omgdi:waypoint x=\"210\" y=\"170\" />        <omgdi:waypoint x=\"-110\" y=\"170\" />        <omgdi:waypoint x=\"-110\" y=\"130\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"24\" y=\"152\" width=\"54\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNShape id=\"BPMNShape_startEvent1\" bpmnElement=\"startEvent1\">        <omgdc:Bounds x=\"-245\" y=\"75\" width=\"30\" height=\"30\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"BPMNShape_Activity_0zi21v6\" bpmnElement=\"Activity_0zi21v6\">        <omgdc:Bounds x=\"-160\" y=\"50\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"BPMNShape_Activity_08cdns0\" bpmnElement=\"Activity_08cdns0\">        <omgdc:Bounds x=\"0\" y=\"50\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"BPMNShape_Activity_0skgv56\" bpmnElement=\"Activity_0skgv56\">        <omgdc:Bounds x=\"160\" y=\"50\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"BPMNShape_Activity_1kzyhgs\" bpmnElement=\"Activity_1kzyhgs\">        <omgdc:Bounds x=\"320\" y=\"50\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"BPMNShape_Event_1wv5rwj\" bpmnElement=\"Event_1wv5rwj\">        <omgdc:Bounds x=\"642\" y=\"72\" width=\"36\" height=\"36\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"BPMNShape_Activity_1q5553k\" bpmnElement=\"Activity_1q5553k\">        <omgdc:Bounds x=\"480\" y=\"50\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>    </bpmndi:BPMNPlane>  </bpmndi:BPMNDiagram></definitions>";
       // System.out.println(va);

        va = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:flowable=\"http://flowable.org/bpmn\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"http://flowable.org/modeler\" exporter=\"Flowable Open Source Modeler\" exporterVersion=\"6.7.2\">\n" +
                "  <process id=\"bd_work_ticket2\" name=\"第二种工作票\" isExecutable=\"true\">\n" +
                "    <startEvent id=\"startEvent1\" />\n" +
                "    <sequenceFlow id=\"Flow_0spc0yf\" sourceRef=\"startEvent1\" targetRef=\"Activity_0zi21v6\" />\n" +
                "    <userTask xmlns:flowable=\"http://flowable.org/bpmn\" id=\"Activity_0zi21v6\" name=\"修改提交\" flowable:formType=\"1\" flowable:formUrl=\"/test/vue\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:assignee=\"${startUserName}\">\n" +
                "      <extensionElements>\n" +
                "        <flowable:formOperation id=\"1665472664181\" label=\"提交\" type=\"1\" showOrder=\"1\" />\n" +
                "        <flowable:formOperation id=\"1665472664181\" label=\"保存\" type=\"6\" showOrder=\"2\" />\n" +
                "      </extensionElements>\n" +
                "      <incoming>Flow_0s1rk8f</incoming>\n" +
                "      <incoming>Flow_13t7f6e</incoming>\n" +
                "    </userTask>\n" +
                "    <sequenceFlow id=\"Flow_0dstjw4\" name=\"提交\" sourceRef=\"Activity_0zi21v6\" targetRef=\"Activity_08cdns0\" flowable:transferType=\"2\" >\n" +
                "      <documentation>已提交待审核</documentation>\n" +
                "      <extensionElements>\n" +
                "        <flowable:property name=\"已提交待审核\" value=\"2\" />\n" +
                "\t\t<flowable:service  name=\"BdWorkTicketServiceImpl.updateState\"/>\n" +
                "      </extensionElements>\n" +
                "    </sequenceFlow>\n" +
                "    <userTask id=\"Activity_08cdns0\" name=\"审核\" flowable:formType=\"1\" flowable:formUrl=\"/test\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:assignee=\"admin\">\n" +
                "      <extensionElements>\n" +
                "        <flowable:formOperation id=\"1665472702468\" label=\"提交\" type=\"1\" showOrder=\"1\" />\n" +
                "        <flowable:formOperation id=\"1665472702468\" label=\"驳回\" type=\"3\" showOrder=\"2\" />\n" +
                "      </extensionElements>\n" +
                "      <outgoing>Flow_0s1rk8f</outgoing>\n" +
                "    </userTask>\n" +
                "    <sequenceFlow id=\"Flow_1cbbs8u\" name=\"审核待签发\" sourceRef=\"Activity_08cdns0\" targetRef=\"Activity_0skgv56\" flowable:transferType=\"2\" >\n" +
                "      <documentation>已审核待签发</documentation>\n" +
                "      <extensionElements>\n" +
                "        <flowable:property name=\"已审核待签发\" value=\"3\" />\n" +
                "\t\t<flowable:service  name=\"BdWorkTicketServiceImpl.updateState\"/>\n" +
                "      </extensionElements>\n" +
                "    </sequenceFlow>\n" +
                "    <userTask id=\"Activity_0skgv56\" name=\"签发\" flowable:formType=\"1\" flowable:formUrl=\"/test\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:assignee=\"admin\">\n" +
                "      <extensionElements>\n" +
                "        <flowable:formOperation id=\"1665472789695\" label=\"提交\" type=\"1\" showOrder=\"1\" />\n" +
                "        <flowable:formOperation id=\"1665472789695\" label=\"驳回\" type=\"3\" showOrder=\"2\" />\n" +
                "      </extensionElements>\n" +
                "      <outgoing>Flow_13t7f6e</outgoing>\n" +
                "    </userTask>\n" +
                "    <sequenceFlow id=\"Flow_11j25sb\" name=\"签发待归档\" sourceRef=\"Activity_0skgv56\" targetRef=\"Activity_1kzyhgs\" flowable:transferType=\"2\" >\n" +
                "      <documentation>已签发待归档</documentation>\n" +
                "      <extensionElements>\n" +
                "        <flowable:property name=\"签发待归档\" value=\"6\" />\n" +
                "\t\t<flowable:service  name=\"BdWorkTicketServiceImpl.updateState\"/>\n" +
                "      </extensionElements>\n" +
                "    </sequenceFlow>\n" +
                "    <userTask id=\"Activity_1kzyhgs\" name=\"归档\" flowable:formType=\"1\" flowable:formUrl=\"/test\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:assignee=\"admin\">\n" +
                "      <extensionElements>\n" +
                "        <flowable:formOperation id=\"1665472806261\" label=\"提交\" type=\"1\" showOrder=\"1\" />\n" +
                "      </extensionElements>\n" +
                "    </userTask>\n" +
                "    <sequenceFlow id=\"Flow_1uh9amj\" name=\"归档待完结\" sourceRef=\"Activity_1kzyhgs\" targetRef=\"Activity_1q5553k\" flowable:transferType=\"0\" >\n" +
                "      <documentation>已归档待完结</documentation>\n" +
                "      <extensionElements>\n" +
                "        <flowable:property name=\"已归档待完结\" value=\"7\" />\n" +
                "\t\t<flowable:service  name=\"BdWorkTicketServiceImpl.updateState\"/>\n" +
                "      </extensionElements>\n" +
                "    </sequenceFlow>\n" +
                "    <endEvent id=\"Event_1wv5rwj\" />\n" +
                "    <sequenceFlow id=\"Flow_020n32j\" name=\"已完结\" sourceRef=\"Activity_1q5553k\" targetRef=\"Event_1wv5rwj\" >\n" +
                "      <extensionElements>\n" +
                "        <flowable:property name=\"已完结\" value=\"8\" />\n" +
                "\t\t<flowable:service  name=\"BdWorkTicketServiceImpl.updateState\"/>\n" +
                "      </extensionElements>\n" +
                "    </sequenceFlow>\n" +
                "    <userTask id=\"Activity_1q5553k\" name=\"完结\" flowable:formType=\"1\" flowable:formUrl=\"/test\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:assignee=\"admin\">\n" +
                "      <extensionElements>\n" +
                "        <flowable:formOperation id=\"1665472817653\" label=\"提交\" type=\"1\" showOrder=\"1\" />\n" +
                "      </extensionElements>\n" +
                "    </userTask>\n" +
                "    <sequenceFlow id=\"Flow_0s1rk8f\" name=\"审核驳回\" sourceRef=\"Activity_08cdns0\" targetRef=\"Activity_0zi21v6\" flowable:transferType=\"0\" >\n" +
                "      <documentation>审核驳回</documentation>\n" +
                "      <extensionElements>\n" +
                "        <flowable:property name=\"审核人驳回\" value=\"11\" />\n" +
                "\t\t<flowable:service  name=\"BdWorkTicketServiceImpl.updateState\"/>\n" +
                "      </extensionElements>\n" +
                "      <conditionExpression xsi:type=\"tFormalExpression\">${operationType}=='rejectToStart'</conditionExpression>\n" +
                "    </sequenceFlow>\n" +
                "    <sequenceFlow id=\"Flow_13t7f6e\" name=\"签发人驳回\" sourceRef=\"Activity_0skgv56\" targetRef=\"Activity_0zi21v6\" flowable:transferType=\"0\" >\n" +
                "      <documentation>签发人驳回</documentation>\n" +
                "      <extensionElements>\n" +
                "        <flowable:property name=\"签发人驳回\" value=\"12\" />\n" +
                "\t\t<flowable:service  name=\"BdWorkTicketServiceImpl.updateState\"/>\n" +
                "      </extensionElements>\n" +
                "      <conditionExpression xsi:type=\"tFormalExpression\">${operationType}=='rejectToStart'</conditionExpression>\n" +
                "    </sequenceFlow>\n" +
                "  </process>\n" +
                "  <bpmndi:BPMNDiagram id=\"BPMNDiagram_bd_work_ticket2\">\n" +
                "    <bpmndi:BPMNPlane id=\"BPMNPlane_bd_work_ticket2\" bpmnElement=\"bd_work_ticket2\">\n" +
                "      <bpmndi:BPMNEdge id=\"BPMNEdge_Flow_020n32j\" bpmnElement=\"Flow_020n32j\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"18.0\" flowable:targetDockerY=\"18.0\">\n" +
                "        <omgdi:waypoint x=\"579.9499999999922\" y=\"90\" />\n" +
                "        <omgdi:waypoint x=\"642\" y=\"90\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"594\" y=\"72\" width=\"34\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"BPMNEdge_Flow_1uh9amj\" bpmnElement=\"Flow_1uh9amj\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n" +
                "        <omgdi:waypoint x=\"419.95000000000005\" y=\"90\" />\n" +
                "        <omgdi:waypoint x=\"480\" y=\"90\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"422\" y=\"72\" width=\"56\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"BPMNEdge_Flow_11j25sb\" bpmnElement=\"Flow_11j25sb\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n" +
                "        <omgdi:waypoint x=\"259.95000000000005\" y=\"90\" />\n" +
                "        <omgdi:waypoint x=\"319.9999999999376\" y=\"90\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"262\" y=\"72\" width=\"55\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"BPMNEdge_Flow_1cbbs8u\" bpmnElement=\"Flow_1cbbs8u\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n" +
                "        <omgdi:waypoint x=\"99.9499999999471\" y=\"90\" />\n" +
                "        <omgdi:waypoint x=\"159.99999999998312\" y=\"90\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"102\" y=\"72\" width=\"56\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"BPMNEdge_Flow_0dstjw4\" bpmnElement=\"Flow_0dstjw4\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n" +
                "        <omgdi:waypoint x=\"-60.050000000052904\" y=\"90\" />\n" +
                "        <omgdi:waypoint x=\"-1.6896706256375182e-11\" y=\"90\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"-41\" y=\"72\" width=\"22\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"BPMNEdge_Flow_0spc0yf\" bpmnElement=\"Flow_0spc0yf\" flowable:sourceDockerX=\"15.0\" flowable:sourceDockerY=\"15.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n" +
                "        <omgdi:waypoint x=\"-215.05000126895308\" y=\"90\" />\n" +
                "        <omgdi:waypoint x=\"-160.0000000000022\" y=\"90\" />\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_0s1rk8f_di\" bpmnElement=\"Flow_0s1rk8f\">\n" +
                "        <omgdi:waypoint x=\"50\" y=\"50\" />\n" +
                "        <omgdi:waypoint x=\"50\" y=\"30\" />\n" +
                "        <omgdi:waypoint x=\"-110\" y=\"30\" />\n" +
                "        <omgdi:waypoint x=\"-110\" y=\"50\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"-51\" y=\"12\" width=\"44\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_13t7f6e_di\" bpmnElement=\"Flow_13t7f6e\">\n" +
                "        <omgdi:waypoint x=\"210\" y=\"130\" />\n" +
                "        <omgdi:waypoint x=\"210\" y=\"170\" />\n" +
                "        <omgdi:waypoint x=\"-110\" y=\"170\" />\n" +
                "        <omgdi:waypoint x=\"-110\" y=\"130\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"24\" y=\"152\" width=\"54\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNShape id=\"BPMNShape_startEvent1\" bpmnElement=\"startEvent1\">\n" +
                "        <omgdc:Bounds x=\"-245\" y=\"75\" width=\"30\" height=\"30\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"BPMNShape_Activity_0zi21v6\" bpmnElement=\"Activity_0zi21v6\">\n" +
                "        <omgdc:Bounds x=\"-160\" y=\"50\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"BPMNShape_Activity_08cdns0\" bpmnElement=\"Activity_08cdns0\">\n" +
                "        <omgdc:Bounds x=\"0\" y=\"50\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"BPMNShape_Activity_0skgv56\" bpmnElement=\"Activity_0skgv56\">\n" +
                "        <omgdc:Bounds x=\"160\" y=\"50\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"BPMNShape_Activity_1kzyhgs\" bpmnElement=\"Activity_1kzyhgs\">\n" +
                "        <omgdc:Bounds x=\"320\" y=\"50\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"BPMNShape_Event_1wv5rwj\" bpmnElement=\"Event_1wv5rwj\">\n" +
                "        <omgdc:Bounds x=\"642\" y=\"72\" width=\"36\" height=\"36\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"BPMNShape_Activity_1q5553k\" bpmnElement=\"Activity_1q5553k\">\n" +
                "        <omgdc:Bounds x=\"480\" y=\"50\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "    </bpmndi:BPMNPlane>\n" +
                "  </bpmndi:BPMNDiagram>\n" +
                "</definitions>\n";

        System.out.println(va.replaceAll("\r|\n", ""));

        String c = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:flowable=\"http://flowable.org/bpmn\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"http://flowable.org/test\" exporter=\"Flowable Open Source Modeler\" exporterVersion=\"6.7.2\">  <process id=\"bd_work_titck\" name=\"第一种工作票\" isExecutable=\"true\">    <startEvent id=\"startEvent1\">      <outgoing>Flow_1j4jqzj</outgoing>    </startEvent>    <sequenceFlow id=\"Flow_1j4jqzj\" sourceRef=\"startEvent1\" targetRef=\"Activity_0xj0hpi\" />    <userTask id=\"Activity_0xj0hpi\" name=\"修改提交\" flowable:assignee=\"${startUserName}\">\t  <extensionElements>\t\t  <flowable:formOperation id=\"6\" label=\"保存\" type=\"save\" showOrder=\"0\" />\t\t  <flowable:formOperation id=\"1\" label=\"同意\" type=\"agree\" showOrder=\"1\" />      </extensionElements>      <incoming>Flow_1j4jqzj</incoming>      <incoming>Flow_0iw7o99</incoming>      <incoming>Flow_0l0wfdy</incoming>      <incoming>Flow_16st22k</incoming>      <outgoing>Flow_119xxaw</outgoing>    </userTask>    <sequenceFlow id=\"Flow_119xxaw\" name=\"已提交待审核\" sourceRef=\"Activity_0xj0hpi\" targetRef=\"Activity_1lg7rxi\" >\t<extensionElements>\t\t <flowable:property name=\"latestApprovalStatus\" value=\"2\" service=\"BdWorkTicketServiceImpl.updateState\"/>\t\t </extensionElements>\t  </sequenceFlow>    <userTask id=\"Activity_1lg7rxi\" name=\"审核\" flowable:assignee=\"admin\">\t   <extensionElements>\t\t  <flowable:formOperation id=\"2\" label=\"驳回\" type=\"reject\" showOrder=\"0\" />\t\t  <flowable:formOperation id=\"1\" label=\"同意\" type=\"agree\" showOrder=\"1\" />      </extensionElements>      <incoming>Flow_119xxaw</incoming>      <outgoing>Flow_1vx9uuw</outgoing>      <outgoing>Flow_0iw7o99</outgoing>    </userTask>    <sequenceFlow id=\"Flow_1vx9uuw\" name=\"已审核待提交\" sourceRef=\"Activity_1lg7rxi\" targetRef=\"Activity_11pm9xw\">\t<extensionElements>\t\t<flowable:property name=\"latestApprovalStatus\" value=\"3\" service=\"BdWorkTicketServiceImpl.updateState\"/>\t\t</extensionElements>\t</sequenceFlow>    <userTask id=\"Activity_11pm9xw\" name=\"签发\" flowable:assignee=\"admin\">\t  <extensionElements>\t\t  <flowable:formOperation id=\"2\" label=\"驳回\" type=\"reject\" showOrder=\"0\" />\t\t  <flowable:formOperation id=\"1\" label=\"同意\" type=\"agree\" showOrder=\"1\" />      </extensionElements>      <incoming>Flow_1vx9uuw</incoming>      <outgoing>Flow_0zh9qc6</outgoing>      <outgoing>Flow_0l0wfdy</outgoing>    </userTask>    <sequenceFlow id=\"Flow_0zh9qc6\" name=\"已签发待确认\" sourceRef=\"Activity_11pm9xw\" targetRef=\"Activity_1196s41\">\t<extensionElements>\t\t<flowable:property name=\"latestApprovalStatus\" value=\"4\" service=\"BdWorkTicketServiceImpl.updateState\"/>\t\t</extensionElements>\t</sequenceFlow>    <userTask id=\"Activity_1196s41\" name=\"确认\" flowable:assignee=\"admin\">\t  <extensionElements>\t\t  <flowable:formOperation id=\"2\" label=\"驳回\" type=\"reject\" showOrder=\"0\" />\t\t  <flowable:formOperation id=\"1\" label=\"同意\" type=\"agree\" showOrder=\"1\" />      </extensionElements>      <incoming>Flow_0zh9qc6</incoming>      <outgoing>Flow_127ow4d</outgoing>      <outgoing>Flow_16st22k</outgoing>    </userTask>    <userTask id=\"Activity_0mpx0ju\" name=\"归档\" flowable:assignee=\"admin\">\t  <extensionElements>\t\t  <flowable:formOperation id=\"1\" label=\"同意\" type=\"agree\" showOrder=\"1\" />      </extensionElements>      <incoming>Flow_127ow4d</incoming>      <outgoing>Flow_106rblw</outgoing>    </userTask>    <sequenceFlow id=\"Flow_127ow4d\" name=\"已确认待归档\" sourceRef=\"Activity_1196s41\" targetRef=\"Activity_0mpx0ju\">\t<extensionElements>\t\t<flowable:property name=\"latestApprovalStatus\" value=\"5\" service=\"BdWorkTicketServiceImpl.updateState\"/>\t\t</extensionElements>      <incoming>Flow_0xjt9ld</incoming>\t  </sequenceFlow>    <endEvent id=\"Event_1utosrp\" />    <sequenceFlow id=\"Flow_0xjt9ld\" name=\"已完结\" sourceRef=\"Activity_02dfl1o\" targetRef=\"Event_1utosrp\">\t<extensionElements>\t\t<flowable:property name=\"latestApprovalStatus\" value=\"6\" service=\"BdWorkTicketServiceImpl.updateState\"/>\t\t</extensionElements>\t</sequenceFlow>    <sequenceFlow id=\"Flow_0iw7o99\" name=\"驳回\" sourceRef=\"Activity_1lg7rxi\" targetRef=\"Activity_0xj0hpi\">\t\t<extensionElements>\t\t\t<flowable:customCondition type=\"operation\" operationType=\"refuse\" />\t\t\t<flowable:property name=\"latestApprovalStatus\" value=\"7\" service=\"BdWorkTicketServiceImpl.updateState\"/>\t\t </extensionElements>\t\t <conditionExpression xsi:type=\"tFormalExpression\">${operationType == 'refuse'}</conditionExpression>    </sequenceFlow>    <sequenceFlow id=\"Flow_0l0wfdy\" name=\"驳回\" sourceRef=\"Activity_11pm9xw\" targetRef=\"Activity_0xj0hpi\">\t\t<extensionElements>\t\t\t<flowable:customCondition type=\"operation\" operationType=\"refuse\" />\t\t\t <flowable:property name=\"latestApprovalStatus\" value=\"7\" service=\"BdWorkTicketServiceImpl.updateState\"/>\t\t </extensionElements>\t\t <conditionExpression xsi:type=\"tFormalExpression\">${operationType == 'refuse'}</conditionExpression>\t</sequenceFlow>    <sequenceFlow id=\"Flow_16st22k\" name=\"确认驳回\" sourceRef=\"Activity_1196s41\" targetRef=\"Activity_0xj0hpi\">      <extensionElements>\t\t\t<flowable:customCondition type=\"operation\" operationType=\"refuse\" />\t\t\t <flowable:property name=\"latestApprovalStatus\" value=\"7\" service=\"BdWorkTicketServiceImpl.updateState\"/>\t\t </extensionElements>\t\t <conditionExpression xsi:type=\"tFormalExpression\">${operationType == 'refuse'}</conditionExpression>    </sequenceFlow>  </process>  <bpmndi:BPMNDiagram id=\"BPMNDiagram_bd_work_titck\">    <bpmndi:BPMNPlane id=\"BPMNPlane_bd_work_titck\" bpmnElement=\"bd_work_titck\">      <bpmndi:BPMNEdge id=\"Flow_1j4jqzj_di\" bpmnElement=\"Flow_1j4jqzj\">        <omgdi:waypoint x=\"-265\" y=\"-150\" />        <omgdi:waypoint x=\"-200\" y=\"-150\" />      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_119xxaw_di\" bpmnElement=\"Flow_119xxaw\">        <omgdi:waypoint x=\"-100\" y=\"-150\" />        <omgdi:waypoint x=\"-30\" y=\"-150\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"-98\" y=\"-168\" width=\"67\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_1vx9uuw_di\" bpmnElement=\"Flow_1vx9uuw\">        <omgdi:waypoint x=\"70\" y=\"-150\" />        <omgdi:waypoint x=\"140\" y=\"-150\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"72\" y=\"-168\" width=\"67\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_0zh9qc6_di\" bpmnElement=\"Flow_0zh9qc6\">        <omgdi:waypoint x=\"240\" y=\"-150\" />        <omgdi:waypoint x=\"310\" y=\"-150\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"242\" y=\"-168\" width=\"67\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_127ow4d_di\" bpmnElement=\"Flow_127ow4d\">        <omgdi:waypoint x=\"410\" y=\"-150\" />        <omgdi:waypoint x=\"480\" y=\"-150\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"412\" y=\"-168\" width=\"67\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_106rblw_di\" bpmnElement=\"Flow_106rblw\">        <omgdi:waypoint x=\"580\" y=\"-150\" />        <omgdi:waypoint x=\"650\" y=\"-150\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"582\" y=\"-168\" width=\"67\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_0xjt9ld_di\" bpmnElement=\"Flow_0xjt9ld\">        <omgdi:waypoint x=\"750\" y=\"-150\" />        <omgdi:waypoint x=\"822\" y=\"-150\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"770\" y=\"-168\" width=\"34\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_0iw7o99_di\" bpmnElement=\"Flow_0iw7o99\">        <omgdi:waypoint x=\"20\" y=\"-110\" />        <omgdi:waypoint x=\"20\" y=\"-60\" />        <omgdi:waypoint x=\"-150\" y=\"-60\" />        <omgdi:waypoint x=\"-150\" y=\"-110\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"-75\" y=\"-78\" width=\"21\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_0l0wfdy_di\" bpmnElement=\"Flow_0l0wfdy\">        <omgdi:waypoint x=\"190\" y=\"-190\" />        <omgdi:waypoint x=\"190\" y=\"-260\" />        <omgdi:waypoint x=\"-160\" y=\"-260\" />        <omgdi:waypoint x=\"-160\" y=\"-190\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"5\" y=\"-278\" width=\"21\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_16st22k_di\" bpmnElement=\"Flow_16st22k\">        <omgdi:waypoint x=\"360\" y=\"-110\" />        <omgdi:waypoint x=\"360\" y=\"-20\" />        <omgdi:waypoint x=\"-150\" y=\"-20\" />        <omgdi:waypoint x=\"-150\" y=\"-110\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"84\" y=\"-38\" width=\"43\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNShape id=\"BPMNShape_startEvent1\" bpmnElement=\"startEvent1\">        <omgdc:Bounds x=\"-295\" y=\"-165\" width=\"30\" height=\"30\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_0artbcb_di\" bpmnElement=\"Activity_0xj0hpi\">        <omgdc:Bounds x=\"-200\" y=\"-190\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_0xp7sct_di\" bpmnElement=\"Activity_11pm9xw\">        <omgdc:Bounds x=\"140\" y=\"-190\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_1nunoz9_di\" bpmnElement=\"Activity_1196s41\">        <omgdc:Bounds x=\"310\" y=\"-190\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_0mpx0ju_di\" bpmnElement=\"Activity_0mpx0ju\">        <omgdc:Bounds x=\"480\" y=\"-190\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_1dwzdv9_di\" bpmnElement=\"Activity_02dfl1o\">        <omgdc:Bounds x=\"650\" y=\"-190\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Event_1utosrp_di\" bpmnElement=\"Event_1utosrp\">        <omgdc:Bounds x=\"822\" y=\"-168\" width=\"36\" height=\"36\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_1y7w3s3_di\" bpmnElement=\"Activity_1lg7rxi\">        <omgdc:Bounds x=\"-30\" y=\"-190\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>    </bpmndi:BPMNPlane>  </bpmndi:BPMNDiagram></definitions>";
        System.out.println("<?xml version='1.0' encoding='UTF-8'?>\n<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:flowable=\"http://flowable.org/bpmn\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" typeLanguage=\"http://www.w3.org/2001/XMLSchema\" expressionLanguage=\"http://www.w3.org/1999/XPath\" targetNamespace=\"http://flowable.org/modeler\" exporter=\"Flowable Open Source Modeler\" exporterVersion=\"6.7.2\">\n  <process id=\"bd_work_titck\" name=\"第一种工作票\" isExecutable=\"true\">\n    <startEvent id=\"startEvent1\"/>\n    <sequenceFlow id=\"Flow_1j4jqzj\" sourceRef=\"startEvent1\" targetRef=\"Activity_0xj0hpi\"/>\n    <userTask id=\"Activity_0xj0hpi\" name=\"修改提交\" flowable:assignee=\"${startUserName}\">\n      <extensionElements>\n        <modeler:initiator-can-complete xmlns:modeler=\"http://flowable.org/modeler\"><![CDATA[false]]></modeler:initiator-can-complete>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"0\" id=\"6\" label=\"保存\" type=\"save\"/>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"1\" id=\"1\" label=\"同意\" type=\"agree\"/>\n      </extensionElements>\n    </userTask>\n    <sequenceFlow id=\"Flow_119xxaw\" name=\"已提交待审核\" sourceRef=\"Activity_0xj0hpi\" targetRef=\"Activity_1lg7rxi\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" service=\"BdWorkTicketServiceImpl.updateState\" name=\"latestApprovalStatus\" value=\"2\"/>\n      </extensionElements>\n    </sequenceFlow>\n    <userTask id=\"Activity_1lg7rxi\" name=\"审核\" flowable:assignee=\"admin\">\n      <extensionElements>\n        <modeler:initiator-can-complete xmlns:modeler=\"http://flowable.org/modeler\"><![CDATA[false]]></modeler:initiator-can-complete>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"0\" id=\"2\" label=\"驳回\" type=\"reject\"/>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"1\" id=\"1\" label=\"同意\" type=\"agree\"/>\n      </extensionElements>\n    </userTask>\n    <sequenceFlow id=\"Flow_1vx9uuw\" name=\"已审核待提交\" sourceRef=\"Activity_1lg7rxi\" targetRef=\"Activity_11pm9xw\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" service=\"BdWorkTicketServiceImpl.updateState\" name=\"latestApprovalStatus\" value=\"3\"/>\n      </extensionElements>\n    </sequenceFlow>\n    <userTask id=\"Activity_11pm9xw\" name=\"签发\" flowable:assignee=\"admin\">\n      <extensionElements>\n        <modeler:initiator-can-complete xmlns:modeler=\"http://flowable.org/modeler\"><![CDATA[false]]></modeler:initiator-can-complete>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"0\" id=\"2\" label=\"驳回\" type=\"reject\"/>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"1\" id=\"1\" label=\"同意\" type=\"agree\"/>\n      </extensionElements>\n    </userTask>\n    <sequenceFlow id=\"Flow_0zh9qc6\" name=\"已签发待确认\" sourceRef=\"Activity_11pm9xw\" targetRef=\"Activity_1196s41\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" service=\"BdWorkTicketServiceImpl.updateState\" name=\"latestApprovalStatus\" value=\"4\"/>\n      </extensionElements>\n    </sequenceFlow>\n    <userTask id=\"Activity_1196s41\" name=\"确认\" flowable:assignee=\"admin\">\n      <extensionElements>\n        <modeler:initiator-can-complete xmlns:modeler=\"http://flowable.org/modeler\"><![CDATA[false]]></modeler:initiator-can-complete>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"0\" id=\"2\" label=\"驳回\" type=\"reject\"/>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"1\" id=\"1\" label=\"同意\" type=\"agree\"/>\n      </extensionElements>\n    </userTask>\n    <userTask id=\"Activity_0mpx0ju\" name=\"归档\" flowable:assignee=\"admin\">\n      <extensionElements>\n        <modeler:initiator-can-complete xmlns:modeler=\"http://flowable.org/modeler\"><![CDATA[false]]></modeler:initiator-can-complete>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"1\" id=\"1\" label=\"同意\" type=\"agree\"/>\n      </extensionElements>\n    </userTask>\n    <sequenceFlow id=\"Flow_127ow4d\" name=\"已确认待归档\" sourceRef=\"Activity_1196s41\" targetRef=\"Activity_0mpx0ju\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" service=\"BdWorkTicketServiceImpl.updateState\" name=\"latestApprovalStatus\" value=\"5\"/>\n      </extensionElements>\n    </sequenceFlow>\n    <endEvent id=\"Event_1utosrp\"/>\n    <sequenceFlow id=\"Flow_0xjt9ld\" name=\"已完结\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" service=\"BdWorkTicketServiceImpl.updateState\" name=\"latestApprovalStatus\" value=\"6\"/>\n        <EDITOR_RESOURCEID><![CDATA[Flow_0xjt9ld]]></EDITOR_RESOURCEID>\n      </extensionElements>\n    </sequenceFlow>\n    <sequenceFlow id=\"Flow_0iw7o99\" name=\"驳回\" sourceRef=\"Activity_1lg7rxi\" targetRef=\"Activity_0xj0hpi\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" service=\"BdWorkTicketServiceImpl.updateState\" name=\"latestApprovalStatus\" value=\"7\"/>\n        <flowable:customCondition xmlns:flowable=\"http://flowable.org/bpmn\" operationType=\"refuse\" type=\"operation\"/>\n      </extensionElements>\n      <conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[${operationType == 'refuse'}]]></conditionExpression>\n    </sequenceFlow>\n    <sequenceFlow id=\"Flow_0l0wfdy\" name=\"驳回\" sourceRef=\"Activity_11pm9xw\" targetRef=\"Activity_0xj0hpi\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" service=\"BdWorkTicketServiceImpl.updateState\" name=\"latestApprovalStatus\" value=\"7\"/>\n        <flowable:customCondition xmlns:flowable=\"http://flowable.org/bpmn\" operationType=\"refuse\" type=\"operation\"/>\n      </extensionElements>\n      <conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[${operationType == 'refuse'}]]></conditionExpression>\n    </sequenceFlow>\n    <sequenceFlow id=\"Flow_16st22k\" name=\"确认驳回\" sourceRef=\"Activity_1196s41\" targetRef=\"Activity_0xj0hpi\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" service=\"BdWorkTicketServiceImpl.updateState\" name=\"latestApprovalStatus\" value=\"7\"/>\n        <flowable:customCondition xmlns:flowable=\"http://flowable.org/bpmn\" operationType=\"refuse\" type=\"operation\"/>\n      </extensionElements>\n      <conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[${operationType == 'refuse'}]]></conditionExpression>\n    </sequenceFlow>\n  </process>\n  <bpmndi:BPMNDiagram id=\"BPMNDiagram_bd_work_titck\">\n    <bpmndi:BPMNPlane bpmnElement=\"bd_work_titck\" id=\"BPMNPlane_bd_work_titck\">\n      <bpmndi:BPMNShape bpmnElement=\"startEvent1\" id=\"BPMNShape_startEvent1\">\n        <omgdc:Bounds height=\"30.0\" width=\"30.0\" x=\"-295.0\" y=\"-165.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_0xj0hpi\" id=\"BPMNShape_Activity_0xj0hpi\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"-200.0\" y=\"-190.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_1lg7rxi\" id=\"BPMNShape_Activity_1lg7rxi\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"-30.0\" y=\"-190.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_11pm9xw\" id=\"BPMNShape_Activity_11pm9xw\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"140.0\" y=\"-190.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_1196s41\" id=\"BPMNShape_Activity_1196s41\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"310.0\" y=\"-190.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_0mpx0ju\" id=\"BPMNShape_Activity_0mpx0ju\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"480.0\" y=\"-190.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Event_1utosrp\" id=\"BPMNShape_Event_1utosrp\">\n        <omgdc:Bounds height=\"36.0\" width=\"36.0\" x=\"822.0\" y=\"-168.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_0iw7o99\" id=\"BPMNEdge_Flow_0iw7o99\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"20.0\" y=\"-110.04999999999998\"/>\n        <omgdi:waypoint x=\"20.0\" y=\"-60.0\"/>\n        <omgdi:waypoint x=\"-150.0\" y=\"-60.0\"/>\n        <omgdi:waypoint x=\"-150.0\" y=\"-110.04999999999998\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_16st22k\" id=\"BPMNEdge_Flow_16st22k\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"360.0\" y=\"-110.04999999999998\"/>\n        <omgdi:waypoint x=\"360.0\" y=\"-20.0\"/>\n        <omgdi:waypoint x=\"-150.0\" y=\"-20.0\"/>\n        <omgdi:waypoint x=\"-150.0\" y=\"-110.04999999999998\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_1j4jqzj\" id=\"BPMNEdge_Flow_1j4jqzj\" flowable:sourceDockerX=\"15.0\" flowable:sourceDockerY=\"15.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"-265.05000108130884\" y=\"-150.0\"/>\n        <omgdi:waypoint x=\"-200.00000000000978\" y=\"-150.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_1vx9uuw\" id=\"BPMNEdge_Flow_1vx9uuw\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"69.95\" y=\"-150.0\"/>\n        <omgdi:waypoint x=\"139.99999999993562\" y=\"-150.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_127ow4d\" id=\"BPMNEdge_Flow_127ow4d\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"409.95000000000005\" y=\"-150.0\"/>\n        <omgdi:waypoint x=\"479.9999999999356\" y=\"-150.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_0zh9qc6\" id=\"BPMNEdge_Flow_0zh9qc6\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"239.95000000000002\" y=\"-150.0\"/>\n        <omgdi:waypoint x=\"309.99999999993565\" y=\"-150.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_119xxaw\" id=\"BPMNEdge_Flow_119xxaw\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"-100.04999999999998\" y=\"-150.0\"/>\n        <omgdi:waypoint x=\"-30.000000000064375\" y=\"-150.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_0l0wfdy\" id=\"BPMNEdge_Flow_0l0wfdy\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"190.0\" y=\"-190.0\"/>\n        <omgdi:waypoint x=\"190.0\" y=\"-260.0\"/>\n        <omgdi:waypoint x=\"-160.0\" y=\"-260.0\"/>\n        <omgdi:waypoint x=\"-153.63636363636363\" y=\"-190.0\"/>\n      </bpmndi:BPMNEdge>\n    </bpmndi:BPMNPlane>\n  </bpmndi:BPMNDiagram>\n</definitions>");
    }



}
