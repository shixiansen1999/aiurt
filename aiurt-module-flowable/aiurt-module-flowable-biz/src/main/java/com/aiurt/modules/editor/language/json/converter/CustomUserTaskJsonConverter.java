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

            // 选人类型， initiator是为：流程发起人, data
            addCustomAttributeForPrefix(elementNode, userTask, "flowable","userType");
            // 角色
            addCustomAttributeForPrefix(elementNode, userTask, "flowable","role");
            // 部门
            addCustomAttributeForPrefix(elementNode, userTask, "flowable","dept");
            // 指定人员
            addCustomAttributeForPrefix(elementNode, userTask, "flowable","user");

            // 动态人员
            addCustomAttributeForPrefix(elementNode, userTask, "flowable", "dynamicPerson");
            // 人员类型: fixed ,dynim
            addCustomAttributeForPrefix(elementNode, userTask, "flowable", "dataType");

            // 表单页面 类型
            addCustomAttributeForPrefix(elementNode, userTask, "flowable","formType");
            // 表单url
            addCustomAttributeForPrefix(elementNode, userTask,"flowable", "formUrl");
            // 业务处理
            addCustomAttributeForPrefix(elementNode, userTask, "flowable", "service");
            // 流程变量
            addCustomAttributeForPrefix(elementNode, userTask,"flowable", "formtaskVariables");

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
        String va = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:bioc=\"http://bpmn.io/schema/bpmn/biocolor/1.0\" xmlns:flowable=\"http://flowable.org/bpmn\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"http://www.flowable.org/processdef\">  <process id=\"process_9qkuldld\" name=\"name_lygad5aa\">    <startEvent id=\"startNode1\" name=\"开始\">      <outgoing>Flow_06a2bjk</outgoing>    </startEvent>    <sequenceFlow id=\"Flow_06a2bjk\" sourceRef=\"startNode1\" targetRef=\"Activity_0g34vod\" />    <userTask id=\"Activity_0g34vod\" name=\"修改提交\" flowable:formType=\"1\" flowable:formUrl=\"/test/\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:dataType=\"dynamic\" flowable:dynamicPerson=\"startUserName\">      <documentation>修改提交</documentation>      <extensionElements>        <flowable:formOperation id=\"1665571540369\" label=\"提交\" type=\"agree\" showOrder=\"1\" />        <flowable:formOperation id=\"1665571540369\" label=\"保存\" type=\"save\" showOrder=\"2\" />      </extensionElements>      <incoming>Flow_06a2bjk</incoming>      <incoming>Flow_16ume73</incoming>      <incoming>Flow_0kkvgyh</incoming>      <outgoing>Flow_00ddq31</outgoing>    </userTask>    <sequenceFlow id=\"Flow_00ddq31\" name=\"提交未审核\" sourceRef=\"Activity_0g34vod\" targetRef=\"Activity_1nrgpvo\" flowable:transferType=\"2\">      <documentation>提交未审核</documentation>      <extensionElements>        <flowable:service name=\"bdWorkTicketServiceImpl.updateState\" />        <flowable:property name=\"已提交待审核\" value=\"2\" />      </extensionElements>    </sequenceFlow>    <userTask id=\"Activity_1nrgpvo\" name=\"审核\" flowable:formType=\"1\" flowable:formUrl=\"/test/test/\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:userType=\"candidateRole\" flowable:role=\"f6817f48af4fb3af11b9e8bf182f618b\">      <documentation>审核</documentation>      <extensionElements>        <flowable:formOperation id=\"1665571714537\" label=\"提交\" type=\"agree\" showOrder=\"1\" />        <flowable:formOperation id=\"1665571714537\" label=\"驳回\" type=\"rejectToStart\" showOrder=\"2\" />      </extensionElements>      <incoming>Flow_00ddq31</incoming>      <outgoing>Flow_1ufzwef</outgoing>      <outgoing>Flow_16ume73</outgoing>    </userTask>    <sequenceFlow id=\"Flow_1ufzwef\" name=\"审核待签发\" sourceRef=\"Activity_1nrgpvo\" targetRef=\"Activity_16lbh3r\" flowable:transferType=\"2\">      <documentation>审核待签发</documentation>      <extensionElements>        <flowable:service name=\"bdWorkTicketServiceImpl.updateState\" />        <flowable:property name=\"已审核待签发\" value=\"3\" />      </extensionElements>    </sequenceFlow>    <userTask id=\"Activity_16lbh3r\" name=\"签发\" flowable:formType=\"1\" flowable:formUrl=\"/test/test\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:userType=\"candidateRole\" flowable:role=\"f6817f48af4fb3af11b9e8bf182f618b\">      <documentation>签发</documentation>      <extensionElements>        <flowable:formOperation id=\"1665571833985\" label=\"提交\" type=\"agree\" showOrder=\"1\" />        <flowable:formOperation id=\"1665571833985\" label=\"驳回\" type=\"rejectToStart\" showOrder=\"2\" />      </extensionElements>      <incoming>Flow_1ufzwef</incoming>      <outgoing>Flow_0u8xzhi</outgoing>      <outgoing>Flow_0kkvgyh</outgoing>    </userTask>    <sequenceFlow id=\"Flow_0u8xzhi\" name=\"签发待归档\" sourceRef=\"Activity_16lbh3r\" targetRef=\"Activity_038kw2s\" flowable:transferType=\"2\">      <documentation>签发待归档</documentation>      <extensionElements>        <flowable:service name=\"bdWorkTicketServiceImpl.updateState\" />        <flowable:property name=\"已签发待归档\" value=\"6\" />      </extensionElements>    </sequenceFlow>    <userTask id=\"Activity_038kw2s\" name=\"归档\" flowable:formType=\"1\" flowable:formUrl=\"/test/test\" flowable:userType=\"candidateRole\" flowable:role=\"f6817f48af4fb3af11b9e8bf182f618b\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\">      <extensionElements>        <flowable:formOperation id=\"1665571998257\" label=\"提交\" type=\"agree\" showOrder=\"1\" />      </extensionElements>      <incoming>Flow_0u8xzhi</incoming>      <outgoing>Flow_0kev6rm</outgoing>    </userTask>    <sequenceFlow id=\"Flow_0kev6rm\" name=\"归档待完结\" sourceRef=\"Activity_038kw2s\" targetRef=\"Activity_091cusm\" flowable:transferType=\"2\">      <documentation>归档待完结</documentation>      <extensionElements>        <flowable:property name=\"已归档待完结\" value=\"7\" />        <flowable:service name=\"bdWorkTicketServiceImpl.updateState\" />      </extensionElements>    </sequenceFlow>    <endEvent id=\"Event_1f4ix00\">      <incoming>Flow_0hh8l5e</incoming>    </endEvent>    <sequenceFlow id=\"Flow_0hh8l5e\" name=\"完结\" sourceRef=\"Activity_091cusm\" targetRef=\"Event_1f4ix00\" flowable:transferType=\"2\">      <extensionElements>        <flowable:property name=\"已完结\" value=\"8\" />        <flowable:service name=\"bdWorkTicketServiceImpl.updateState\" />      </extensionElements>    </sequenceFlow>    <userTask id=\"Activity_091cusm\" name=\"完结\" flowable:formType=\"1\" flowable:formUrl=\"/test/test\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:userType=\"candidateRole\" flowable:role=\"f6817f48af4fb3af11b9e8bf182f618b\">      <documentation>完结</documentation>      <extensionElements>        <flowable:formOperation id=\"1665572068641\" label=\"提交\" type=\"agree\" showOrder=\"1\" />      </extensionElements>      <incoming>Flow_0kev6rm</incoming>      <outgoing>Flow_0hh8l5e</outgoing>    </userTask>    <sequenceFlow id=\"Flow_16ume73\" name=\"审核驳回\" sourceRef=\"Activity_1nrgpvo\" targetRef=\"Activity_0g34vod\" flowable:transferType=\"0\">      <extensionElements>        <flowable:customCondition type=\"operation\" operationType=\"rejectToStart\" />        <flowable:property name=\"审核人驳回\" value=\"11\" />        <flowable:service name=\"bdWorkTicketServiceImpl.updateState\" />      </extensionElements>      <conditionExpression xsi:type=\"tFormalExpression\">${operationType}=='rejectToStart'</conditionExpression>    </sequenceFlow>    <sequenceFlow id=\"Flow_0kkvgyh\" name=\"签发驳回\" sourceRef=\"Activity_16lbh3r\" targetRef=\"Activity_0g34vod\" flowable:transferType=\"0\">      <documentation>签发驳回</documentation>      <extensionElements>        <flowable:customCondition type=\"operation\" operationType=\"rejectToStart\" />        <flowable:property name=\"签发人驳回\" value=\"12\" />        <flowable:service name=\"bdWorkTicketServiceImpl.updateState\" />      </extensionElements>      <conditionExpression xsi:type=\"tFormalExpression\">${operationType}=='rejectToStart'</conditionExpression>    </sequenceFlow>  </process>  <bpmndi:BPMNDiagram id=\"BPMNDiagram_flow\">    <bpmndi:BPMNPlane id=\"BPMNPlane_flow\" bpmnElement=\"process_9qkuldld\">      <bpmndi:BPMNEdge id=\"Flow_06a2bjk_di\" bpmnElement=\"Flow_06a2bjk\">        <di:waypoint x=\"-405\" y=\"80\" />        <di:waypoint x=\"-350\" y=\"80\" />      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_00ddq31_di\" bpmnElement=\"Flow_00ddq31\">        <di:waypoint x=\"-250\" y=\"80\" />        <di:waypoint x=\"-190\" y=\"80\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"-246\" y=\"62\" width=\"56\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_1ufzwef_di\" bpmnElement=\"Flow_1ufzwef\">        <di:waypoint x=\"-90\" y=\"80\" />        <di:waypoint x=\"-30\" y=\"80\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"-88\" y=\"62\" width=\"57\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_0u8xzhi_di\" bpmnElement=\"Flow_0u8xzhi\">        <di:waypoint x=\"70\" y=\"80\" />        <di:waypoint x=\"130\" y=\"80\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"74\" y=\"62\" width=\"56\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_0kev6rm_di\" bpmnElement=\"Flow_0kev6rm\">        <di:waypoint x=\"230\" y=\"80\" />        <di:waypoint x=\"290\" y=\"80\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"232\" y=\"62\" width=\"57\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_0hh8l5e_di\" bpmnElement=\"Flow_0hh8l5e\">        <di:waypoint x=\"390\" y=\"80\" />        <di:waypoint x=\"452\" y=\"80\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"410\" y=\"62\" width=\"23\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_16ume73_di\" bpmnElement=\"Flow_16ume73\">        <di:waypoint x=\"-140\" y=\"40\" />        <di:waypoint x=\"-140\" y=\"10\" />        <di:waypoint x=\"-300\" y=\"10\" />        <di:waypoint x=\"-300\" y=\"40\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"-242\" y=\"-8\" width=\"45\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_0kkvgyh_di\" bpmnElement=\"Flow_0kkvgyh\">        <di:waypoint x=\"20\" y=\"120\" />        <di:waypoint x=\"20\" y=\"170\" />        <di:waypoint x=\"-310\" y=\"170\" />        <di:waypoint x=\"-310\" y=\"120\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"-166\" y=\"152\" width=\"43\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNShape id=\"BPMNShape_startNode1\" bpmnElement=\"startNode1\" bioc:stroke=\"\">        <omgdc:Bounds x=\"-435\" y=\"65\" width=\"30\" height=\"30\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"-433\" y=\"102\" width=\"23\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_02ifbek_di\" bpmnElement=\"Activity_0g34vod\">        <omgdc:Bounds x=\"-350\" y=\"40\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_0ai5038_di\" bpmnElement=\"Activity_1nrgpvo\">        <omgdc:Bounds x=\"-190\" y=\"40\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_06cojyw_di\" bpmnElement=\"Activity_16lbh3r\">        <omgdc:Bounds x=\"-30\" y=\"40\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_1kvgh6y_di\" bpmnElement=\"Activity_038kw2s\">        <omgdc:Bounds x=\"130\" y=\"40\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Event_1f4ix00_di\" bpmnElement=\"Event_1f4ix00\">        <omgdc:Bounds x=\"452\" y=\"62\" width=\"36\" height=\"36\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_1x1c7no_di\" bpmnElement=\"Activity_091cusm\">        <omgdc:Bounds x=\"290\" y=\"40\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>    </bpmndi:BPMNPlane>  </bpmndi:BPMNDiagram></definitions>";
       // System.out.println(va);

        va = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:bioc=\"http://bpmn.io/schema/bpmn/biocolor/1.0\" xmlns:flowable=\"http://flowable.org/bpmn\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"http://www.flowable.org/processdef\">\n" +
                "  <process id=\"process_9qkuldld\" name=\"name_lygad5aa\">\n" +
                "    <startEvent id=\"startNode1\" name=\"开始\">\n" +
                "      <outgoing>Flow_06a2bjk</outgoing>\n" +
                "    </startEvent>\n" +
                "    <sequenceFlow id=\"Flow_06a2bjk\" sourceRef=\"startNode1\" targetRef=\"Activity_0g34vod\" />\n" +
                "    <userTask id=\"Activity_0g34vod\" name=\"修改提交\" flowable:formType=\"1\" flowable:formUrl=\"/test/\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:dataType=\"dynamic\" flowable:dynamicPerson=\"startUserName\">\n" +
                "      <documentation>修改提交</documentation>\n" +
                "      <extensionElements>\n" +
                "        <flowable:formOperation id=\"1665571540369\" label=\"提交\" type=\"agree\" showOrder=\"1\" />\n" +
                "        <flowable:formOperation id=\"1665571540369\" label=\"保存\" type=\"save\" showOrder=\"2\" />\n" +
                "      </extensionElements>\n" +
                "      <incoming>Flow_06a2bjk</incoming>\n" +
                "      <incoming>Flow_16ume73</incoming>\n" +
                "      <incoming>Flow_0kkvgyh</incoming>\n" +
                "      <outgoing>Flow_00ddq31</outgoing>\n" +
                "    </userTask>\n" +
                "    <sequenceFlow id=\"Flow_00ddq31\" name=\"提交未审核\" sourceRef=\"Activity_0g34vod\" targetRef=\"Activity_1nrgpvo\" flowable:transferType=\"2\">\n" +
                "      <documentation>提交未审核</documentation>\n" +
                "      <extensionElements>\n" +
                "        <flowable:service name=\"bdWorkTicketServiceImpl.updateState\" />\n" +
                "        <flowable:property name=\"已提交待审核\" value=\"2\" />\n" +
                "      </extensionElements>\n" +
                "    </sequenceFlow>\n" +
                "    <userTask id=\"Activity_1nrgpvo\" name=\"审核\" flowable:formType=\"1\" flowable:formUrl=\"/test/test/\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:userType=\"candidateRole\" flowable:role=\"f6817f48af4fb3af11b9e8bf182f618b\">\n" +
                "      <documentation>审核</documentation>\n" +
                "      <extensionElements>\n" +
                "        <flowable:formOperation id=\"1665571714537\" label=\"提交\" type=\"agree\" showOrder=\"1\" />\n" +
                "        <flowable:formOperation id=\"1665571714537\" label=\"驳回\" type=\"rejectToStart\" showOrder=\"2\" />\n" +
                "      </extensionElements>\n" +
                "      <incoming>Flow_00ddq31</incoming>\n" +
                "      <outgoing>Flow_1ufzwef</outgoing>\n" +
                "      <outgoing>Flow_16ume73</outgoing>\n" +
                "    </userTask>\n" +
                "    <sequenceFlow id=\"Flow_1ufzwef\" name=\"审核待签发\" sourceRef=\"Activity_1nrgpvo\" targetRef=\"Activity_16lbh3r\" flowable:transferType=\"2\">\n" +
                "      <documentation>审核待签发</documentation>\n" +
                "      <extensionElements>\n" +
                "        <flowable:service name=\"bdWorkTicketServiceImpl.updateState\" />\n" +
                "        <flowable:property name=\"已审核待签发\" value=\"3\" />\n" +
                "      </extensionElements>\n" +
                "    </sequenceFlow>\n" +
                "    <userTask id=\"Activity_16lbh3r\" name=\"签发\" flowable:formType=\"1\" flowable:formUrl=\"/test/test\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:userType=\"candidateRole\" flowable:role=\"f6817f48af4fb3af11b9e8bf182f618b\">\n" +
                "      <documentation>签发</documentation>\n" +
                "      <extensionElements>\n" +
                "        <flowable:formOperation id=\"1665571833985\" label=\"提交\" type=\"agree\" showOrder=\"1\" />\n" +
                "        <flowable:formOperation id=\"1665571833985\" label=\"驳回\" type=\"rejectToStart\" showOrder=\"2\" />\n" +
                "      </extensionElements>\n" +
                "      <incoming>Flow_1ufzwef</incoming>\n" +
                "      <outgoing>Flow_0u8xzhi</outgoing>\n" +
                "      <outgoing>Flow_0kkvgyh</outgoing>\n" +
                "    </userTask>\n" +
                "    <sequenceFlow id=\"Flow_0u8xzhi\" name=\"签发待归档\" sourceRef=\"Activity_16lbh3r\" targetRef=\"Activity_038kw2s\" flowable:transferType=\"2\">\n" +
                "      <documentation>签发待归档</documentation>\n" +
                "      <extensionElements>\n" +
                "        <flowable:service name=\"bdWorkTicketServiceImpl.updateState\" />\n" +
                "        <flowable:property name=\"已签发待归档\" value=\"6\" />\n" +
                "      </extensionElements>\n" +
                "    </sequenceFlow>\n" +
                "    <userTask id=\"Activity_038kw2s\" name=\"归档\" flowable:formType=\"1\" flowable:formUrl=\"/test/test\" flowable:userType=\"candidateRole\" flowable:role=\"f6817f48af4fb3af11b9e8bf182f618b\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\">\n" +
                "      <extensionElements>\n" +
                "        <flowable:formOperation id=\"1665571998257\" label=\"提交\" type=\"agree\" showOrder=\"1\" />\n" +
                "      </extensionElements>\n" +
                "      <incoming>Flow_0u8xzhi</incoming>\n" +
                "      <outgoing>Flow_0kev6rm</outgoing>\n" +
                "    </userTask>\n" +
                "    <sequenceFlow id=\"Flow_0kev6rm\" name=\"归档待完结\" sourceRef=\"Activity_038kw2s\" targetRef=\"Activity_091cusm\" flowable:transferType=\"2\">\n" +
                "      <documentation>归档待完结</documentation>\n" +
                "      <extensionElements>\n" +
                "        <flowable:property name=\"已归档待完结\" value=\"7\" />\n" +
                "        <flowable:service name=\"bdWorkTicketServiceImpl.updateState\" />\n" +
                "      </extensionElements>\n" +
                "    </sequenceFlow>\n" +
                "    <endEvent id=\"Event_1f4ix00\">\n" +
                "      <incoming>Flow_0hh8l5e</incoming>\n" +
                "    </endEvent>\n" +
                "    <sequenceFlow id=\"Flow_0hh8l5e\" name=\"完结\" sourceRef=\"Activity_091cusm\" targetRef=\"Event_1f4ix00\" flowable:transferType=\"2\">\n" +
                "      <extensionElements>\n" +
                "        <flowable:property name=\"已完结\" value=\"8\" />\n" +
                "        <flowable:service name=\"bdWorkTicketServiceImpl.updateState\" />\n" +
                "      </extensionElements>\n" +
                "    </sequenceFlow>\n" +
                "    <userTask id=\"Activity_091cusm\" name=\"完结\" flowable:formType=\"1\" flowable:formUrl=\"/test/test\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\" flowable:userType=\"candidateRole\" flowable:role=\"f6817f48af4fb3af11b9e8bf182f618b\">\n" +
                "      <documentation>完结</documentation>\n" +
                "      <extensionElements>\n" +
                "        <flowable:formOperation id=\"1665572068641\" label=\"提交\" type=\"agree\" showOrder=\"1\" />\n" +
                "      </extensionElements>\n" +
                "      <incoming>Flow_0kev6rm</incoming>\n" +
                "      <outgoing>Flow_0hh8l5e</outgoing>\n" +
                "    </userTask>\n" +
                "    <sequenceFlow id=\"Flow_16ume73\" name=\"审核驳回\" sourceRef=\"Activity_1nrgpvo\" targetRef=\"Activity_0g34vod\" flowable:transferType=\"0\">\n" +
                "      <extensionElements>\n" +
                "        <flowable:customCondition type=\"operation\" operationType=\"rejectToStart\" />\n" +
                "        <flowable:property name=\"审核人驳回\" value=\"11\" />\n" +
                "        <flowable:service name=\"bdWorkTicketServiceImpl.updateState\" />\n" +
                "      </extensionElements>\n" +
                "      <conditionExpression xsi:type=\"tFormalExpression\">${operationType}=='rejectToStart'</conditionExpression>\n" +
                "    </sequenceFlow>\n" +
                "    <sequenceFlow id=\"Flow_0kkvgyh\" name=\"签发驳回\" sourceRef=\"Activity_16lbh3r\" targetRef=\"Activity_0g34vod\" flowable:transferType=\"0\">\n" +
                "      <documentation>签发驳回</documentation>\n" +
                "      <extensionElements>\n" +
                "        <flowable:customCondition type=\"operation\" operationType=\"rejectToStart\" />\n" +
                "        <flowable:property name=\"签发人驳回\" value=\"12\" />\n" +
                "        <flowable:service name=\"bdWorkTicketServiceImpl.updateState\" />\n" +
                "      </extensionElements>\n" +
                "      <conditionExpression xsi:type=\"tFormalExpression\">${operationType}=='rejectToStart'</conditionExpression>\n" +
                "    </sequenceFlow>\n" +
                "  </process>\n" +
                "  <bpmndi:BPMNDiagram id=\"BPMNDiagram_flow\">\n" +
                "    <bpmndi:BPMNPlane id=\"BPMNPlane_flow\" bpmnElement=\"process_9qkuldld\">\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_06a2bjk_di\" bpmnElement=\"Flow_06a2bjk\">\n" +
                "        <di:waypoint x=\"-405\" y=\"80\" />\n" +
                "        <di:waypoint x=\"-350\" y=\"80\" />\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_00ddq31_di\" bpmnElement=\"Flow_00ddq31\">\n" +
                "        <di:waypoint x=\"-250\" y=\"80\" />\n" +
                "        <di:waypoint x=\"-190\" y=\"80\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"-246\" y=\"62\" width=\"56\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_1ufzwef_di\" bpmnElement=\"Flow_1ufzwef\">\n" +
                "        <di:waypoint x=\"-90\" y=\"80\" />\n" +
                "        <di:waypoint x=\"-30\" y=\"80\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"-88\" y=\"62\" width=\"57\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_0u8xzhi_di\" bpmnElement=\"Flow_0u8xzhi\">\n" +
                "        <di:waypoint x=\"70\" y=\"80\" />\n" +
                "        <di:waypoint x=\"130\" y=\"80\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"74\" y=\"62\" width=\"56\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_0kev6rm_di\" bpmnElement=\"Flow_0kev6rm\">\n" +
                "        <di:waypoint x=\"230\" y=\"80\" />\n" +
                "        <di:waypoint x=\"290\" y=\"80\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"232\" y=\"62\" width=\"57\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_0hh8l5e_di\" bpmnElement=\"Flow_0hh8l5e\">\n" +
                "        <di:waypoint x=\"390\" y=\"80\" />\n" +
                "        <di:waypoint x=\"452\" y=\"80\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"410\" y=\"62\" width=\"23\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_16ume73_di\" bpmnElement=\"Flow_16ume73\">\n" +
                "        <di:waypoint x=\"-140\" y=\"40\" />\n" +
                "        <di:waypoint x=\"-140\" y=\"10\" />\n" +
                "        <di:waypoint x=\"-300\" y=\"10\" />\n" +
                "        <di:waypoint x=\"-300\" y=\"40\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"-242\" y=\"-8\" width=\"45\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_0kkvgyh_di\" bpmnElement=\"Flow_0kkvgyh\">\n" +
                "        <di:waypoint x=\"20\" y=\"120\" />\n" +
                "        <di:waypoint x=\"20\" y=\"170\" />\n" +
                "        <di:waypoint x=\"-310\" y=\"170\" />\n" +
                "        <di:waypoint x=\"-310\" y=\"120\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"-166\" y=\"152\" width=\"43\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNShape id=\"BPMNShape_startNode1\" bpmnElement=\"startNode1\" bioc:stroke=\"\">\n" +
                "        <omgdc:Bounds x=\"-435\" y=\"65\" width=\"30\" height=\"30\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"-433\" y=\"102\" width=\"23\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_02ifbek_di\" bpmnElement=\"Activity_0g34vod\">\n" +
                "        <omgdc:Bounds x=\"-350\" y=\"40\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_0ai5038_di\" bpmnElement=\"Activity_1nrgpvo\">\n" +
                "        <omgdc:Bounds x=\"-190\" y=\"40\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_06cojyw_di\" bpmnElement=\"Activity_16lbh3r\">\n" +
                "        <omgdc:Bounds x=\"-30\" y=\"40\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_1kvgh6y_di\" bpmnElement=\"Activity_038kw2s\">\n" +
                "        <omgdc:Bounds x=\"130\" y=\"40\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Event_1f4ix00_di\" bpmnElement=\"Event_1f4ix00\">\n" +
                "        <omgdc:Bounds x=\"452\" y=\"62\" width=\"36\" height=\"36\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_1x1c7no_di\" bpmnElement=\"Activity_091cusm\">\n" +
                "        <omgdc:Bounds x=\"290\" y=\"40\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "    </bpmndi:BPMNPlane>\n" +
                "  </bpmndi:BPMNDiagram>\n" +
                "</definitions>\n";

        //System.out.println(va.replaceAll("\r|\n", ""));

        String c = "<?xml version='1.0' encoding='UTF-8'?>\n<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:flowable=\"http://flowable.org/bpmn\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" typeLanguage=\"http://www.w3.org/2001/XMLSchema\" expressionLanguage=\"http://www.w3.org/1999/XPath\" targetNamespace=\"http://flowable.org/modeler\" exporter=\"Flowable Open Source Modeler\" exporterVersion=\"6.7.2\">\n  <process id=\"bd_work_ticket2\" name=\"name_lygad5aa\" isExecutable=\"true\">\n    <startEvent id=\"startNode1\" name=\"开始\"/>\n    <sequenceFlow id=\"Flow_06a2bjk\" sourceRef=\"startNode1\" targetRef=\"Activity_0g34vod\"/>\n    <userTask id=\"Activity_0g34vod\" name=\"修改提交\" xmlns:flowable=\"http://flowable.org/bpmn\" flowable:dynamicPerson=\"startUserName\" flowable:formType=\"1\" flowable:formUrl=\"/test/\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\">\n      <documentation>修改提交</documentation>\n      <extensionElements>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"1\" id=\"1665571540369\" label=\"提交\" type=\"agree\"/>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"2\" id=\"1665571540369\" label=\"保存\" type=\"save\"/>\n      </extensionElements>\n    </userTask>\n    <sequenceFlow id=\"Flow_00ddq31\" name=\"提交未审核\" sourceRef=\"Activity_0g34vod\" targetRef=\"Activity_1nrgpvo\">\n      <documentation>提交未审核</documentation>\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" name=\"已提交待审核\" value=\"2\"/>\n        <flowable:service xmlns:flowable=\"http://flowable.org/bpmn\" name=\"bdWorkTicketServiceImpl.updateState\"/>\n      </extensionElements>\n    </sequenceFlow>\n    <userTask id=\"Activity_1nrgpvo\" name=\"审核\" xmlns:flowable=\"http://flowable.org/bpmn\" flowable:userType=\"candidateRole\" flowable:role=\"f6817f48af4fb3af11b9e8bf182f618b\" flowable:formType=\"1\" flowable:formUrl=\"/test/test/\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\">\n      <documentation>审核</documentation>\n      <extensionElements>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"1\" id=\"1665571714537\" label=\"提交\" type=\"agree\"/>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"2\" id=\"1665571714537\" label=\"驳回\" type=\"rejectToStart\"/>\n      </extensionElements>\n    </userTask>\n    <sequenceFlow id=\"Flow_1ufzwef\" name=\"审核待签发\" sourceRef=\"Activity_1nrgpvo\" targetRef=\"Activity_16lbh3r\">\n      <documentation>审核待签发</documentation>\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" name=\"已审核待签发\" value=\"3\"/>\n        <flowable:service xmlns:flowable=\"http://flowable.org/bpmn\" name=\"bdWorkTicketServiceImpl.updateState\"/>\n      </extensionElements>\n    </sequenceFlow>\n    <userTask id=\"Activity_16lbh3r\" name=\"签发\" xmlns:flowable=\"http://flowable.org/bpmn\" flowable:userType=\"candidateRole\" flowable:role=\"f6817f48af4fb3af11b9e8bf182f618b\" flowable:formType=\"1\" flowable:formUrl=\"/test/test\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\">\n      <documentation>签发</documentation>\n      <extensionElements>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"1\" id=\"1665571833985\" label=\"提交\" type=\"agree\"/>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"2\" id=\"1665571833985\" label=\"驳回\" type=\"rejectToStart\"/>\n      </extensionElements>\n    </userTask>\n    <sequenceFlow id=\"Flow_0u8xzhi\" name=\"签发待归档\" sourceRef=\"Activity_16lbh3r\" targetRef=\"Activity_038kw2s\">\n      <documentation>签发待归档</documentation>\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" name=\"已签发待归档\" value=\"6\"/>\n        <flowable:service xmlns:flowable=\"http://flowable.org/bpmn\" name=\"bdWorkTicketServiceImpl.updateState\"/>\n      </extensionElements>\n    </sequenceFlow>\n    <userTask id=\"Activity_038kw2s\" name=\"归档\" xmlns:flowable=\"http://flowable.org/bpmn\" flowable:userType=\"candidateRole\" flowable:role=\"f6817f48af4fb3af11b9e8bf182f618b\" flowable:formType=\"1\" flowable:formUrl=\"/test/test\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\">\n      <extensionElements>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"1\" id=\"1665571998257\" label=\"提交\" type=\"agree\"/>\n      </extensionElements>\n    </userTask>\n    <sequenceFlow id=\"Flow_0kev6rm\" name=\"归档待完结\" sourceRef=\"Activity_038kw2s\" targetRef=\"Activity_091cusm\">\n      <documentation>归档待完结</documentation>\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" name=\"已归档待完结\" value=\"7\"/>\n        <flowable:service xmlns:flowable=\"http://flowable.org/bpmn\" name=\"bdWorkTicketServiceImpl.updateState\"/>\n      </extensionElements>\n    </sequenceFlow>\n    <endEvent id=\"Event_1f4ix00\"/>\n    <sequenceFlow id=\"Flow_0hh8l5e\" name=\"完结\" sourceRef=\"Activity_091cusm\" targetRef=\"Event_1f4ix00\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" name=\"已完结\" value=\"8\"/>\n        <flowable:service xmlns:flowable=\"http://flowable.org/bpmn\" name=\"bdWorkTicketServiceImpl.updateState\"/>\n      </extensionElements>\n    </sequenceFlow>\n    <userTask id=\"Activity_091cusm\" name=\"完结\" xmlns:flowable=\"http://flowable.org/bpmn\" flowable:userType=\"candidateRole\" flowable:role=\"f6817f48af4fb3af11b9e8bf182f618b\" flowable:formType=\"1\" flowable:formUrl=\"/test/test\" flowable:service=\"bdWorkTicketServiceImpl.addOrUpdate\">\n      <documentation>完结</documentation>\n      <extensionElements>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"1\" id=\"1665572068641\" label=\"提交\" type=\"agree\"/>\n      </extensionElements>\n    </userTask>\n    <sequenceFlow id=\"Flow_16ume73\" name=\"审核驳回\" sourceRef=\"Activity_1nrgpvo\" targetRef=\"Activity_0g34vod\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" name=\"审核人驳回\" value=\"11\"/>\n        <flowable:customCondition xmlns:flowable=\"http://flowable.org/bpmn\" operationType=\"rejectToStart\" type=\"operation\"/>\n        <flowable:service xmlns:flowable=\"http://flowable.org/bpmn\" name=\"bdWorkTicketServiceImpl.updateState\"/>\n      </extensionElements>\n      <conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[${operationType}=='rejectToStart']]></conditionExpression>\n    </sequenceFlow>\n    <sequenceFlow id=\"Flow_0kkvgyh\" name=\"签发驳回\" sourceRef=\"Activity_16lbh3r\" targetRef=\"Activity_0g34vod\">\n      <documentation>签发驳回</documentation>\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" name=\"签发人驳回\" value=\"12\"/>\n        <flowable:customCondition xmlns:flowable=\"http://flowable.org/bpmn\" operationType=\"rejectToStart\" type=\"operation\"/>\n        <flowable:service xmlns:flowable=\"http://flowable.org/bpmn\" name=\"bdWorkTicketServiceImpl.updateState\"/>\n      </extensionElements>\n      <conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[${operationType}=='rejectToStart']]></conditionExpression>\n    </sequenceFlow>\n  </process>\n  <bpmndi:BPMNDiagram id=\"BPMNDiagram_bd_work_ticket2\">\n    <bpmndi:BPMNPlane bpmnElement=\"bd_work_ticket2\" id=\"BPMNPlane_bd_work_ticket2\">\n      <bpmndi:BPMNShape bpmnElement=\"startNode1\" id=\"BPMNShape_startNode1\">\n        <omgdc:Bounds height=\"30.0\" width=\"30.0\" x=\"-435.0\" y=\"65.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_0g34vod\" id=\"BPMNShape_Activity_0g34vod\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"-350.0\" y=\"40.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_1nrgpvo\" id=\"BPMNShape_Activity_1nrgpvo\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"-190.0\" y=\"40.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_16lbh3r\" id=\"BPMNShape_Activity_16lbh3r\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"-30.0\" y=\"40.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_038kw2s\" id=\"BPMNShape_Activity_038kw2s\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"130.0\" y=\"40.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Event_1f4ix00\" id=\"BPMNShape_Event_1f4ix00\">\n        <omgdc:Bounds height=\"36.0\" width=\"36.0\" x=\"452.0\" y=\"62.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_091cusm\" id=\"BPMNShape_Activity_091cusm\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"290.0\" y=\"40.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_0u8xzhi\" id=\"BPMNEdge_Flow_0u8xzhi\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"69.9499999999471\" y=\"80.0\"/>\n        <omgdi:waypoint x=\"130.0\" y=\"80.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_16ume73\" id=\"BPMNEdge_Flow_16ume73\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"-140.0\" y=\"40.0\"/>\n        <omgdi:waypoint x=\"-140.0\" y=\"10.0\"/>\n        <omgdi:waypoint x=\"-300.0\" y=\"10.0\"/>\n        <omgdi:waypoint x=\"-300.0\" y=\"40.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_00ddq31\" id=\"BPMNEdge_Flow_00ddq31\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"-250.0500000000529\" y=\"80.0\"/>\n        <omgdi:waypoint x=\"-190.0\" y=\"80.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_0kev6rm\" id=\"BPMNEdge_Flow_0kev6rm\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"229.9499999999471\" y=\"80.0\"/>\n        <omgdi:waypoint x=\"290.0\" y=\"80.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_1ufzwef\" id=\"BPMNEdge_Flow_1ufzwef\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"-90.05000000000742\" y=\"80.0\"/>\n        <omgdi:waypoint x=\"-30.0\" y=\"80.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_0kkvgyh\" id=\"BPMNEdge_Flow_0kkvgyh\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"20.0\" y=\"119.95\"/>\n        <omgdi:waypoint x=\"20.0\" y=\"170.0\"/>\n        <omgdi:waypoint x=\"-310.0\" y=\"170.0\"/>\n        <omgdi:waypoint x=\"-304.44444444444446\" y=\"119.95\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_06a2bjk\" id=\"BPMNEdge_Flow_06a2bjk\" flowable:sourceDockerX=\"15.0\" flowable:sourceDockerY=\"15.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"-405.05000126895317\" y=\"80.0\"/>\n        <omgdi:waypoint x=\"-350.00000000000216\" y=\"80.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_0hh8l5e\" id=\"BPMNEdge_Flow_0hh8l5e\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"18.0\" flowable:targetDockerY=\"18.0\">\n        <omgdi:waypoint x=\"389.9499999999922\" y=\"80.0\"/>\n        <omgdi:waypoint x=\"452.0\" y=\"80.0\"/>\n      </bpmndi:BPMNEdge>\n    </bpmndi:BPMNPlane>\n  </bpmndi:BPMNDiagram>\n</definitions>";
        System.out.println(c);
    }



}
