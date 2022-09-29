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
           /* List<ExtensionElement> formOperationElements =
                    ExtensionPropertiesUtil.getMyExtensionElementList(extensionElements, OPERATION_LIST, FORM_OPERATION);*/
            List<ExtensionElement> formOperationElements = extensionElements.get(FORM_OPERATION);
            if (CollUtil.isNotEmpty(formOperationElements)) {
                // ObjectNode node = super.objectMapper.createObjectNode();
                ArrayNode arrayNode = super.objectMapper.createArrayNode();
                for (ExtensionElement e : formOperationElements) {
                    ObjectNode objectNode = super.objectMapper.createObjectNode();
                    objectNode.put("id", e.getAttributeValue(null, "id"));
                    objectNode.put("label", e.getAttributeValue(null, "label"));
                    objectNode.put("type", e.getAttributeValue(null, "type"));
                    objectNode.put("showOrder", e.getAttributeValue(null, "showOrder"));
                    String multiSignAssignee = e.getAttributeValue(null, "multiSignAssignee");
                    arrayNode.add(objectNode);
                }
                propertiesNode.set(FORM_OPERATION, arrayNode);
            }

            // 流程变量
            /*List<ExtensionElement> variableElements = ExtensionPropertiesUtil.getMyExtensionElementList(extensionElements, "variableList", "formVariable");
            if (CollUtil.isNotEmpty(variableElements)) {
                ObjectNode node = super.objectMapper.createObjectNode();
                ArrayNode arrayNode = super.objectMapper.createArrayNode();
                for (ExtensionElement e : variableElements) {
                    ObjectNode objectNode = super.objectMapper.createObjectNode();
                    objectNode.put("id", e.getAttributeValue(null, "id"));
                    arrayNode.add(objectNode);
                }
                node.set(FORM_VARIABLE, arrayNode);
                propertiesNode.set("variableList", node);
            }*/

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
            addCustomAttibute(elementNode, userTask, "formData.formType");
            // 表单url
            addCustomAttibute(elementNode, userTask, "formData.formUrl");
            // 业务处理
            addCustomAttibute(elementNode, userTask, "formData.service");
            // 流程变量
            addCustomAttibute(elementNode, userTask, "flowable.formtaskVariables");

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

    private void addCustomAttibute(JsonNode elementNode, UserTask userTask, String s) {
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
        String va = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<bpmn2:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:flowable=\"http://flowable.org/bpmn\" id=\"diagram_test\" targetNamespace=\"http://flowable.org/bpmn\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">\n  <bpmn2:process id=\"zidingyi_code\" name=\"测试\" isExecutable=\"true\">\n    <bpmn2:startEvent id=\"Event_0z2kuz1\">\n      <bpmn2:outgoing>Flow_1xwyk4z</bpmn2:outgoing>\n    </bpmn2:startEvent>\n    <bpmn2:userTask id=\"Activity_0mv4jsh\" name=\"发起人\" flowable:formKey=\"{&#34;routerName&#34;:&#34;/test/test&#34;,&#34;readOnly&#34;:true,&#34;groupType&#34;:&#34;ASSIGNEE&#34;}\">\n      <bpmn2:extensionElements>\n        <flowable:variableList />\n        <flowable:copyItemList />\n        <flowable:operationList>\n          <flowable:formOperation id=\"1659428417427\" label=\"保存\" type=\"save\" showOrder=\"0\" />\n          <flowable:formOperation id=\"1659428436977\" label=\"拒绝\" type=\"refuse\" showOrder=\"0\" />\n        </flowable:operationList>\n      </bpmn2:extensionElements>\n      <bpmn2:incoming>Flow_1xwyk4z</bpmn2:incoming>\n      <bpmn2:outgoing>Flow_0tqr84n</bpmn2:outgoing>\n    </bpmn2:userTask>\n    <bpmn2:sequenceFlow id=\"Flow_1xwyk4z\" sourceRef=\"Event_0z2kuz1\" targetRef=\"Activity_0mv4jsh\" />\n    <bpmn2:userTask id=\"Activity_011h7xn\" name=\"上级领导\" flowable:formKey=\"{&#34;routerName&#34;:&#34;/test/test&#34;,&#34;readOnly&#34;:true,&#34;groupType&#34;:&#34;ASSIGNEE&#34;}\" flowable:assignee=\"${appointedAssignee}\">\n      <bpmn2:extensionElements>\n        <flowable:copyItemList />\n        <flowable:operationList>\n          <flowable:formOperation id=\"1659428455100\" label=\"拒绝\" type=\"refuse\" showOrder=\"0\" />\n        </flowable:operationList>\n        <flowable:variableList>\n          <flowable:formVariable id=\"1554381425178841088\" />\n        </flowable:variableList>\n      </bpmn2:extensionElements>\n      <bpmn2:incoming>Flow_0tqr84n</bpmn2:incoming>\n      <bpmn2:outgoing>Flow_14dbf29</bpmn2:outgoing>\n    </bpmn2:userTask>\n    <bpmn2:sequenceFlow id=\"Flow_0tqr84n\" sourceRef=\"Activity_0mv4jsh\" targetRef=\"Activity_011h7xn\" />\n    <bpmn2:userTask id=\"Activity_1f4tw7v\" name=\"上级部门\" flowable:formKey=\"{&#34;routerName&#34;:&#34;/test/test&#34;,&#34;readOnly&#34;:true,&#34;groupType&#34;:&#34;UP_DEPT_POST_LEADER&#34;}\">\n      <bpmn2:extensionElements>\n        <flowable:variableList>\n          <flowable:formVariable id=\"1554381111751086080\" />\n          <flowable:formVariable id=\"1554381111923052545\" />\n          <flowable:formVariable id=\"1554381425178841088\" />\n        </flowable:variableList>\n        <flowable:copyItemList />\n        <flowable:operationList>\n          <flowable:formOperation id=\"1659428597640\" label=\"同意\" type=\"agree\" showOrder=\"0\" />\n        </flowable:operationList>\n      </bpmn2:extensionElements>\n      <bpmn2:incoming>Flow_14dbf29</bpmn2:incoming>\n      <bpmn2:outgoing>Flow_1hkslp1</bpmn2:outgoing>\n    </bpmn2:userTask>\n    <bpmn2:sequenceFlow id=\"Flow_14dbf29\" sourceRef=\"Activity_011h7xn\" targetRef=\"Activity_1f4tw7v\" />\n    <bpmn2:userTask id=\"Activity_0w57vsj\" name=\"部门\" flowable:formKey=\"{&#34;routerName&#34;:&#34;/test/test&#34;,&#34;readOnly&#34;:true,&#34;groupType&#34;:&#34;USERS&#34;}\" flowable:candidateUsers=\"admin,leaderHR,userA\">\n      <bpmn2:extensionElements>\n        <flowable:operationList>\n          <flowable:formOperation id=\"1659428572985\" label=\"驳回到起点\" type=\"rejectToStart\" showOrder=\"0\" />\n          <flowable:formOperation id=\"1659428576592\" label=\"同意\" type=\"agree\" showOrder=\"0\" />\n        </flowable:operationList>\n        <flowable:variableList />\n        <flowable:copyItemList />\n        <flowable:userCandidateGroups type=\"USERS\" value=\"admin,leaderHR,userA\" />\n      </bpmn2:extensionElements>\n      <bpmn2:incoming>Flow_1hkslp1</bpmn2:incoming>\n      <bpmn2:outgoing>Flow_1ida4o2</bpmn2:outgoing>\n    </bpmn2:userTask>\n    <bpmn2:sequenceFlow id=\"Flow_1hkslp1\" sourceRef=\"Activity_1f4tw7v\" targetRef=\"Activity_0w57vsj\" />\n    <bpmn2:endEvent id=\"Event_0ek55dj\">\n      <bpmn2:incoming>Flow_1wv8xvg</bpmn2:incoming>\n    </bpmn2:endEvent>\n    <bpmn2:userTask id=\"Activity_0ugsf7v\" name=\"真部门\" flowable:formKey=\"{&#34;routerName&#34;:&#34;/test/test&#34;,&#34;readOnly&#34;:true,&#34;groupType&#34;:&#34;DEPT&#34;}\" flowable:candidateGroups=\"1440963592970047488,1440963642542526464\">\n      <bpmn2:extensionElements>\n        <flowable:operationList>\n          <flowable:formOperation id=\"1659430005896\" label=\"拒绝\" type=\"refuse\" showOrder=\"0\" />\n          <flowable:formOperation id=\"1659430010386\" label=\"转办\" type=\"transfer\" showOrder=\"0\" />\n        </flowable:operationList>\n        <flowable:variableList />\n        <flowable:copyItemList />\n        <flowable:userCandidateGroups type=\"DEPT\" value=\"1440963592970047488,1440963642542526464\" />\n      </bpmn2:extensionElements>\n      <bpmn2:incoming>Flow_1ida4o2</bpmn2:incoming>\n      <bpmn2:outgoing>Flow_1wv8xvg</bpmn2:outgoing>\n    </bpmn2:userTask>\n    <bpmn2:sequenceFlow id=\"Flow_1ida4o2\" sourceRef=\"Activity_0w57vsj\" targetRef=\"Activity_0ugsf7v\" />\n    <bpmn2:sequenceFlow id=\"Flow_1wv8xvg\" sourceRef=\"Activity_0ugsf7v\" targetRef=\"Event_0ek55dj\" />\n  </bpmn2:process>\n  <bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">\n    <bpmndi:BPMNPlane id=\"BPMNPlane_1\" bpmnElement=\"test\">\n      <bpmndi:BPMNEdge id=\"Flow_1wv8xvg_di\" bpmnElement=\"Flow_1wv8xvg\">\n        <di:waypoint x=\"980\" y=\"280\" />\n        <di:waypoint x=\"980\" y=\"316\" />\n        <di:waypoint x=\"1020\" y=\"316\" />\n        <di:waypoint x=\"1020\" y=\"352\" />\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge id=\"Flow_1ida4o2_di\" bpmnElement=\"Flow_1ida4o2\">\n        <di:waypoint x=\"870\" y=\"240\" />\n        <di:waypoint x=\"930\" y=\"240\" />\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge id=\"Flow_1hkslp1_di\" bpmnElement=\"Flow_1hkslp1\">\n        <di:waypoint x=\"710\" y=\"240\" />\n        <di:waypoint x=\"770\" y=\"240\" />\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge id=\"Flow_14dbf29_di\" bpmnElement=\"Flow_14dbf29\">\n        <di:waypoint x=\"550\" y=\"240\" />\n        <di:waypoint x=\"610\" y=\"240\" />\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge id=\"Flow_0tqr84n_di\" bpmnElement=\"Flow_0tqr84n\">\n        <di:waypoint x=\"390\" y=\"240\" />\n        <di:waypoint x=\"450\" y=\"240\" />\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge id=\"Flow_1xwyk4z_di\" bpmnElement=\"Flow_1xwyk4z\">\n        <di:waypoint x=\"238\" y=\"240\" />\n        <di:waypoint x=\"290\" y=\"240\" />\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNShape id=\"Event_0z2kuz1_di\" bpmnElement=\"Event_0z2kuz1\">\n        <dc:Bounds x=\"202\" y=\"222\" width=\"36\" height=\"36\" />\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape id=\"Activity_0mv4jsh_di\" bpmnElement=\"Activity_0mv4jsh\">\n        <dc:Bounds x=\"290\" y=\"200\" width=\"100\" height=\"80\" />\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape id=\"Activity_011h7xn_di\" bpmnElement=\"Activity_011h7xn\">\n        <dc:Bounds x=\"450\" y=\"200\" width=\"100\" height=\"80\" />\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape id=\"Activity_1f4tw7v_di\" bpmnElement=\"Activity_1f4tw7v\">\n        <dc:Bounds x=\"610\" y=\"200\" width=\"100\" height=\"80\" />\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape id=\"Activity_0w57vsj_di\" bpmnElement=\"Activity_0w57vsj\">\n        <dc:Bounds x=\"770\" y=\"200\" width=\"100\" height=\"80\" />\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape id=\"Event_0ek55dj_di\" bpmnElement=\"Event_0ek55dj\">\n        <dc:Bounds x=\"1002\" y=\"352\" width=\"36\" height=\"36\" />\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape id=\"Activity_0ugsf7v_di\" bpmnElement=\"Activity_0ugsf7v\">\n        <dc:Bounds x=\"930\" y=\"200\" width=\"100\" height=\"80\" />\n      </bpmndi:BPMNShape>\n    </bpmndi:BPMNPlane>\n  </bpmndi:BPMNDiagram>\n</bpmn2:definitions>\n";
        System.out.println(va);

        va = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<bpmn2:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:flowable=\"http://flowable.org/bpmn\" id=\"diagram_test\" targetNamespace=\"http://flowable.org/bpmn\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">\n" +
                "  <bpmn2:process id=\"zidingyi_code\" name=\"测试\" isExecutable=\"true\">\n" +
                "    <bpmn2:startEvent id=\"Event_0z2kuz1\">\n" +
                "      <bpmn2:outgoing>Flow_1xwyk4z</bpmn2:outgoing>\n" +
                "    </bpmn2:startEvent>\n" +
                "    <bpmn2:userTask id=\"Activity_0mv4jsh\" name=\"发起人\" flowable:formKey=\"{&#34;routerName&#34;:&#34;/test/test&#34;,&#34;readOnly&#34;:true,&#34;groupType&#34;:&#34;ASSIGNEE&#34;}\">\n" +
                "      <bpmn2:extensionElements>\n" +
                "        <flowable:variableList />\n" +
                "        <flowable:copyItemList />\n" +
                "\t  <flowable:formOperation id=\"1659428417427\" label=\"保存\" type=\"save\" showOrder=\"0\" />\n" +
                "\t  <flowable:formOperation id=\"1659428436977\" label=\"拒绝\" type=\"refuse\" showOrder=\"0\" />\n" +
                "       \n" +
                "      </bpmn2:extensionElements>\n" +
                "      <bpmn2:incoming>Flow_1xwyk4z</bpmn2:incoming>\n" +
                "      <bpmn2:outgoing>Flow_0tqr84n</bpmn2:outgoing>\n" +
                "    </bpmn2:userTask>\n" +
                "    <bpmn2:sequenceFlow id=\"Flow_1xwyk4z\" sourceRef=\"Event_0z2kuz1\" targetRef=\"Activity_0mv4jsh\" />\n" +
                "    <bpmn2:userTask id=\"Activity_011h7xn\" name=\"上级领导\" flowable:formKey=\"{&#34;routerName&#34;:&#34;/test/test&#34;,&#34;readOnly&#34;:true,&#34;groupType&#34;:&#34;ASSIGNEE&#34;}\" flowable:assignee=\"${appointedAssignee}\">\n" +
                "      <bpmn2:extensionElements>\n" +
                "        <flowable:copyItemList />\n" +
                "        \n" +
                "          <flowable:formOperation id=\"1659428455100\" label=\"拒绝\" type=\"refuse\" showOrder=\"0\" />\n" +
                "       \n" +
                "        <flowable:variableList>\n" +
                "          <flowable:formVariable id=\"1554381425178841088\" />\n" +
                "        </flowable:variableList>\n" +
                "      </bpmn2:extensionElements>\n" +
                "      <bpmn2:incoming>Flow_0tqr84n</bpmn2:incoming>\n" +
                "      <bpmn2:outgoing>Flow_14dbf29</bpmn2:outgoing>\n" +
                "    </bpmn2:userTask>\n" +
                "    <bpmn2:sequenceFlow id=\"Flow_0tqr84n\" sourceRef=\"Activity_0mv4jsh\" targetRef=\"Activity_011h7xn\" />\n" +
                "    <bpmn2:userTask id=\"Activity_1f4tw7v\" name=\"上级部门\" flowable:formKey=\"{&#34;routerName&#34;:&#34;/test/test&#34;,&#34;readOnly&#34;:true,&#34;groupType&#34;:&#34;UP_DEPT_POST_LEADER&#34;}\">\n" +
                "      <bpmn2:extensionElements>\n" +
                "        <flowable:variableList>\n" +
                "          <flowable:formVariable id=\"1554381111751086080\" />\n" +
                "          <flowable:formVariable id=\"1554381111923052545\" />\n" +
                "          <flowable:formVariable id=\"1554381425178841088\" />\n" +
                "        </flowable:variableList>\n" +
                "        <flowable:copyItemList />\n" +
                "       \n" +
                "          <flowable:formOperation id=\"1659428597640\" label=\"同意\" type=\"agree\" showOrder=\"0\" />\n" +
                "       \n" +
                "      </bpmn2:extensionElements>\n" +
                "      <bpmn2:incoming>Flow_14dbf29</bpmn2:incoming>\n" +
                "      <bpmn2:outgoing>Flow_1hkslp1</bpmn2:outgoing>\n" +
                "    </bpmn2:userTask>\n" +
                "    <bpmn2:sequenceFlow id=\"Flow_14dbf29\" sourceRef=\"Activity_011h7xn\" targetRef=\"Activity_1f4tw7v\" />\n" +
                "    <bpmn2:userTask id=\"Activity_0w57vsj\" name=\"部门\" flowable:formKey=\"{&#34;routerName&#34;:&#34;/test/test&#34;,&#34;readOnly&#34;:true,&#34;groupType&#34;:&#34;USERS&#34;}\" flowable:candidateUsers=\"admin,leaderHR,userA\">\n" +
                "      <bpmn2:extensionElements>\n" +
                "        \n" +
                "\t  <flowable:formOperation id=\"1659428572985\" label=\"驳回到起点\" type=\"rejectToStart\" showOrder=\"0\" />\n" +
                "\t  <flowable:formOperation id=\"1659428576592\" label=\"同意\" type=\"agree\" showOrder=\"0\" />\n" +
                "       \n" +
                "        <flowable:variableList />\n" +
                "        <flowable:copyItemList />\n" +
                "        <flowable:userCandidateGroups type=\"USERS\" value=\"admin,leaderHR,userA\" />\n" +
                "      </bpmn2:extensionElements>\n" +
                "      <bpmn2:incoming>Flow_1hkslp1</bpmn2:incoming>\n" +
                "      <bpmn2:outgoing>Flow_1ida4o2</bpmn2:outgoing>\n" +
                "    </bpmn2:userTask>\n" +
                "    <bpmn2:sequenceFlow id=\"Flow_1hkslp1\" sourceRef=\"Activity_1f4tw7v\" targetRef=\"Activity_0w57vsj\" />\n" +
                "    <bpmn2:endEvent id=\"Event_0ek55dj\">\n" +
                "      <bpmn2:incoming>Flow_1wv8xvg</bpmn2:incoming>\n" +
                "    </bpmn2:endEvent>\n" +
                "    <bpmn2:userTask id=\"Activity_0ugsf7v\" name=\"真部门\" flowable:formKey=\"{&#34;routerName&#34;:&#34;/test/test&#34;,&#34;readOnly&#34;:true,&#34;groupType&#34;:&#34;DEPT&#34;}\" flowable:candidateGroups=\"1440963592970047488,1440963642542526464\">\n" +
                "      <bpmn2:extensionElements>\n" +
                "      \n" +
                "          <flowable:formOperation id=\"1659430005896\" label=\"拒绝\" type=\"refuse\" showOrder=\"0\" />\n" +
                "          <flowable:formOperation id=\"1659430010386\" label=\"转办\" type=\"transfer\" showOrder=\"0\" />\n" +
                "        \n" +
                "        <flowable:variableList />\n" +
                "        <flowable:copyItemList />\n" +
                "        <flowable:userCandidateGroups type=\"DEPT\" value=\"1440963592970047488,1440963642542526464\" />\n" +
                "      </bpmn2:extensionElements>\n" +
                "      <bpmn2:incoming>Flow_1ida4o2</bpmn2:incoming>\n" +
                "      <bpmn2:outgoing>Flow_1wv8xvg</bpmn2:outgoing>\n" +
                "    </bpmn2:userTask>\n" +
                "    <bpmn2:sequenceFlow id=\"Flow_1ida4o2\" sourceRef=\"Activity_0w57vsj\" targetRef=\"Activity_0ugsf7v\" />\n" +
                "    <bpmn2:sequenceFlow id=\"Flow_1wv8xvg\" sourceRef=\"Activity_0ugsf7v\" targetRef=\"Event_0ek55dj\" />\n" +
                "  </bpmn2:process>\n" +
                "  <bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">\n" +
                "    <bpmndi:BPMNPlane id=\"BPMNPlane_1\" bpmnElement=\"test\">\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_1wv8xvg_di\" bpmnElement=\"Flow_1wv8xvg\">\n" +
                "        <di:waypoint x=\"980\" y=\"280\" />\n" +
                "        <di:waypoint x=\"980\" y=\"316\" />\n" +
                "        <di:waypoint x=\"1020\" y=\"316\" />\n" +
                "        <di:waypoint x=\"1020\" y=\"352\" />\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_106rblw_di\" bpmnElement=\"Flow_106rblw\">\n" +
                "        <omgdi:waypoint x=\"580\" y=\"-150\" />\n" +
                "        <omgdi:waypoint x=\"650\" y=\"-150\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"582\" y=\"-168\" width=\"67\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_0xjt9ld_di\" bpmnElement=\"Flow_0xjt9ld\">\n" +
                "        <omgdi:waypoint x=\"750\" y=\"-150\" />\n" +
                "        <omgdi:waypoint x=\"822\" y=\"-150\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"770\" y=\"-168\" width=\"34\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_0iw7o99_di\" bpmnElement=\"Flow_0iw7o99\">\n" +
                "        <omgdi:waypoint x=\"20\" y=\"-110\" />\n" +
                "        <omgdi:waypoint x=\"20\" y=\"-60\" />\n" +
                "        <omgdi:waypoint x=\"-150\" y=\"-60\" />\n" +
                "        <omgdi:waypoint x=\"-150\" y=\"-110\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"-75\" y=\"-78\" width=\"21\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_0l0wfdy_di\" bpmnElement=\"Flow_0l0wfdy\">\n" +
                "        <omgdi:waypoint x=\"190\" y=\"-190\" />\n" +
                "        <omgdi:waypoint x=\"190\" y=\"-260\" />\n" +
                "        <omgdi:waypoint x=\"-160\" y=\"-260\" />\n" +
                "        <omgdi:waypoint x=\"-160\" y=\"-190\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"5\" y=\"-278\" width=\"21\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_16st22k_di\" bpmnElement=\"Flow_16st22k\">\n" +
                "        <omgdi:waypoint x=\"360\" y=\"-110\" />\n" +
                "        <omgdi:waypoint x=\"360\" y=\"-20\" />\n" +
                "        <omgdi:waypoint x=\"-150\" y=\"-20\" />\n" +
                "        <omgdi:waypoint x=\"-150\" y=\"-110\" />\n" +
                "        <bpmndi:BPMNLabel>\n" +
                "          <omgdc:Bounds x=\"84\" y=\"-38\" width=\"43\" height=\"14\" />\n" +
                "        </bpmndi:BPMNLabel>\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNShape id=\"BPMNShape_startEvent1\" bpmnElement=\"startEvent1\">\n" +
                "        <omgdc:Bounds x=\"-295\" y=\"-165\" width=\"30\" height=\"30\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_0artbcb_di\" bpmnElement=\"Activity_0xj0hpi\">\n" +
                "        <omgdc:Bounds x=\"-200\" y=\"-190\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_0xp7sct_di\" bpmnElement=\"Activity_11pm9xw\">\n" +
                "        <omgdc:Bounds x=\"140\" y=\"-190\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_1nunoz9_di\" bpmnElement=\"Activity_1196s41\">\n" +
                "        <omgdc:Bounds x=\"310\" y=\"-190\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_0mpx0ju_di\" bpmnElement=\"Activity_0mpx0ju\">\n" +
                "        <omgdc:Bounds x=\"480\" y=\"-190\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_1dwzdv9_di\" bpmnElement=\"Activity_02dfl1o\">\n" +
                "        <omgdc:Bounds x=\"650\" y=\"-190\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Event_1utosrp_di\" bpmnElement=\"Event_1utosrp\">\n" +
                "        <omgdc:Bounds x=\"822\" y=\"-168\" width=\"36\" height=\"36\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_1y7w3s3_di\" bpmnElement=\"Activity_1lg7rxi\">\n" +
                "        <omgdc:Bounds x=\"-30\" y=\"-190\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "    </bpmndi:BPMNPlane>\n" +
                "  </bpmndi:BPMNDiagram>\n" +
                "</definitions>\n";

        System.out.println(va.replaceAll("\r|\n", ""));

        String c = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:flowable=\"http://flowable.org/bpmn\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" targetNamespace=\"http://flowable.org/test\" exporter=\"Flowable Open Source Modeler\" exporterVersion=\"6.7.2\">  <process id=\"bd_work_titck\" name=\"第一种工作票\" isExecutable=\"true\">    <startEvent id=\"startEvent1\">      <outgoing>Flow_1j4jqzj</outgoing>    </startEvent>    <sequenceFlow id=\"Flow_1j4jqzj\" sourceRef=\"startEvent1\" targetRef=\"Activity_0xj0hpi\" />    <userTask id=\"Activity_0xj0hpi\" name=\"修改提交\" flowable:assignee=\"${startUserName}\">\t  <extensionElements>\t\t  <flowable:formOperation id=\"6\" label=\"保存\" type=\"save\" showOrder=\"0\" />\t\t  <flowable:formOperation id=\"1\" label=\"同意\" type=\"agree\" showOrder=\"1\" />      </extensionElements>      <incoming>Flow_1j4jqzj</incoming>      <incoming>Flow_0iw7o99</incoming>      <incoming>Flow_0l0wfdy</incoming>      <incoming>Flow_16st22k</incoming>      <outgoing>Flow_119xxaw</outgoing>    </userTask>    <sequenceFlow id=\"Flow_119xxaw\" name=\"已提交待审核\" sourceRef=\"Activity_0xj0hpi\" targetRef=\"Activity_1lg7rxi\" >\t<extensionElements>\t\t <flowable:property name=\"latestApprovalStatus\" value=\"2\" service=\"BdWorkTicketServiceImpl.updateState\"/>\t\t </extensionElements>\t  </sequenceFlow>    <userTask id=\"Activity_1lg7rxi\" name=\"审核\" flowable:assignee=\"admin\">\t   <extensionElements>\t\t  <flowable:formOperation id=\"2\" label=\"驳回\" type=\"reject\" showOrder=\"0\" />\t\t  <flowable:formOperation id=\"1\" label=\"同意\" type=\"agree\" showOrder=\"1\" />      </extensionElements>      <incoming>Flow_119xxaw</incoming>      <outgoing>Flow_1vx9uuw</outgoing>      <outgoing>Flow_0iw7o99</outgoing>    </userTask>    <sequenceFlow id=\"Flow_1vx9uuw\" name=\"已审核待提交\" sourceRef=\"Activity_1lg7rxi\" targetRef=\"Activity_11pm9xw\">\t<extensionElements>\t\t<flowable:property name=\"latestApprovalStatus\" value=\"3\" service=\"BdWorkTicketServiceImpl.updateState\"/>\t\t</extensionElements>\t</sequenceFlow>    <userTask id=\"Activity_11pm9xw\" name=\"签发\" flowable:assignee=\"admin\">\t  <extensionElements>\t\t  <flowable:formOperation id=\"2\" label=\"驳回\" type=\"reject\" showOrder=\"0\" />\t\t  <flowable:formOperation id=\"1\" label=\"同意\" type=\"agree\" showOrder=\"1\" />      </extensionElements>      <incoming>Flow_1vx9uuw</incoming>      <outgoing>Flow_0zh9qc6</outgoing>      <outgoing>Flow_0l0wfdy</outgoing>    </userTask>    <sequenceFlow id=\"Flow_0zh9qc6\" name=\"已签发待确认\" sourceRef=\"Activity_11pm9xw\" targetRef=\"Activity_1196s41\">\t<extensionElements>\t\t<flowable:property name=\"latestApprovalStatus\" value=\"4\" service=\"BdWorkTicketServiceImpl.updateState\"/>\t\t</extensionElements>\t</sequenceFlow>    <userTask id=\"Activity_1196s41\" name=\"确认\" flowable:assignee=\"admin\">\t  <extensionElements>\t\t  <flowable:formOperation id=\"2\" label=\"驳回\" type=\"reject\" showOrder=\"0\" />\t\t  <flowable:formOperation id=\"1\" label=\"同意\" type=\"agree\" showOrder=\"1\" />      </extensionElements>      <incoming>Flow_0zh9qc6</incoming>      <outgoing>Flow_127ow4d</outgoing>      <outgoing>Flow_16st22k</outgoing>    </userTask>    <userTask id=\"Activity_0mpx0ju\" name=\"归档\" flowable:assignee=\"admin\">\t  <extensionElements>\t\t  <flowable:formOperation id=\"1\" label=\"同意\" type=\"agree\" showOrder=\"1\" />      </extensionElements>      <incoming>Flow_127ow4d</incoming>      <outgoing>Flow_106rblw</outgoing>    </userTask>    <sequenceFlow id=\"Flow_127ow4d\" name=\"已确认待归档\" sourceRef=\"Activity_1196s41\" targetRef=\"Activity_0mpx0ju\">\t<extensionElements>\t\t<flowable:property name=\"latestApprovalStatus\" value=\"5\" service=\"BdWorkTicketServiceImpl.updateState\"/>\t\t</extensionElements>      <incoming>Flow_0xjt9ld</incoming>\t  </sequenceFlow>    <endEvent id=\"Event_1utosrp\" />    <sequenceFlow id=\"Flow_0xjt9ld\" name=\"已完结\" sourceRef=\"Activity_02dfl1o\" targetRef=\"Event_1utosrp\">\t<extensionElements>\t\t<flowable:property name=\"latestApprovalStatus\" value=\"6\" service=\"BdWorkTicketServiceImpl.updateState\"/>\t\t</extensionElements>\t</sequenceFlow>    <sequenceFlow id=\"Flow_0iw7o99\" name=\"驳回\" sourceRef=\"Activity_1lg7rxi\" targetRef=\"Activity_0xj0hpi\">\t\t<extensionElements>\t\t\t<flowable:customCondition type=\"operation\" operationType=\"refuse\" />\t\t\t<flowable:property name=\"latestApprovalStatus\" value=\"7\" service=\"BdWorkTicketServiceImpl.updateState\"/>\t\t </extensionElements>\t\t <conditionExpression xsi:type=\"tFormalExpression\">${operationType == 'refuse'}</conditionExpression>    </sequenceFlow>    <sequenceFlow id=\"Flow_0l0wfdy\" name=\"驳回\" sourceRef=\"Activity_11pm9xw\" targetRef=\"Activity_0xj0hpi\">\t\t<extensionElements>\t\t\t<flowable:customCondition type=\"operation\" operationType=\"refuse\" />\t\t\t <flowable:property name=\"latestApprovalStatus\" value=\"7\" service=\"BdWorkTicketServiceImpl.updateState\"/>\t\t </extensionElements>\t\t <conditionExpression xsi:type=\"tFormalExpression\">${operationType == 'refuse'}</conditionExpression>\t</sequenceFlow>    <sequenceFlow id=\"Flow_16st22k\" name=\"确认驳回\" sourceRef=\"Activity_1196s41\" targetRef=\"Activity_0xj0hpi\">      <extensionElements>\t\t\t<flowable:customCondition type=\"operation\" operationType=\"refuse\" />\t\t\t <flowable:property name=\"latestApprovalStatus\" value=\"7\" service=\"BdWorkTicketServiceImpl.updateState\"/>\t\t </extensionElements>\t\t <conditionExpression xsi:type=\"tFormalExpression\">${operationType == 'refuse'}</conditionExpression>    </sequenceFlow>  </process>  <bpmndi:BPMNDiagram id=\"BPMNDiagram_bd_work_titck\">    <bpmndi:BPMNPlane id=\"BPMNPlane_bd_work_titck\" bpmnElement=\"bd_work_titck\">      <bpmndi:BPMNEdge id=\"Flow_1j4jqzj_di\" bpmnElement=\"Flow_1j4jqzj\">        <omgdi:waypoint x=\"-265\" y=\"-150\" />        <omgdi:waypoint x=\"-200\" y=\"-150\" />      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_119xxaw_di\" bpmnElement=\"Flow_119xxaw\">        <omgdi:waypoint x=\"-100\" y=\"-150\" />        <omgdi:waypoint x=\"-30\" y=\"-150\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"-98\" y=\"-168\" width=\"67\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_1vx9uuw_di\" bpmnElement=\"Flow_1vx9uuw\">        <omgdi:waypoint x=\"70\" y=\"-150\" />        <omgdi:waypoint x=\"140\" y=\"-150\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"72\" y=\"-168\" width=\"67\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_0zh9qc6_di\" bpmnElement=\"Flow_0zh9qc6\">        <omgdi:waypoint x=\"240\" y=\"-150\" />        <omgdi:waypoint x=\"310\" y=\"-150\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"242\" y=\"-168\" width=\"67\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_127ow4d_di\" bpmnElement=\"Flow_127ow4d\">        <omgdi:waypoint x=\"410\" y=\"-150\" />        <omgdi:waypoint x=\"480\" y=\"-150\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"412\" y=\"-168\" width=\"67\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_106rblw_di\" bpmnElement=\"Flow_106rblw\">        <omgdi:waypoint x=\"580\" y=\"-150\" />        <omgdi:waypoint x=\"650\" y=\"-150\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"582\" y=\"-168\" width=\"67\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_0xjt9ld_di\" bpmnElement=\"Flow_0xjt9ld\">        <omgdi:waypoint x=\"750\" y=\"-150\" />        <omgdi:waypoint x=\"822\" y=\"-150\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"770\" y=\"-168\" width=\"34\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_0iw7o99_di\" bpmnElement=\"Flow_0iw7o99\">        <omgdi:waypoint x=\"20\" y=\"-110\" />        <omgdi:waypoint x=\"20\" y=\"-60\" />        <omgdi:waypoint x=\"-150\" y=\"-60\" />        <omgdi:waypoint x=\"-150\" y=\"-110\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"-75\" y=\"-78\" width=\"21\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_0l0wfdy_di\" bpmnElement=\"Flow_0l0wfdy\">        <omgdi:waypoint x=\"190\" y=\"-190\" />        <omgdi:waypoint x=\"190\" y=\"-260\" />        <omgdi:waypoint x=\"-160\" y=\"-260\" />        <omgdi:waypoint x=\"-160\" y=\"-190\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"5\" y=\"-278\" width=\"21\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_16st22k_di\" bpmnElement=\"Flow_16st22k\">        <omgdi:waypoint x=\"360\" y=\"-110\" />        <omgdi:waypoint x=\"360\" y=\"-20\" />        <omgdi:waypoint x=\"-150\" y=\"-20\" />        <omgdi:waypoint x=\"-150\" y=\"-110\" />        <bpmndi:BPMNLabel>          <omgdc:Bounds x=\"84\" y=\"-38\" width=\"43\" height=\"14\" />        </bpmndi:BPMNLabel>      </bpmndi:BPMNEdge>      <bpmndi:BPMNShape id=\"BPMNShape_startEvent1\" bpmnElement=\"startEvent1\">        <omgdc:Bounds x=\"-295\" y=\"-165\" width=\"30\" height=\"30\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_0artbcb_di\" bpmnElement=\"Activity_0xj0hpi\">        <omgdc:Bounds x=\"-200\" y=\"-190\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_0xp7sct_di\" bpmnElement=\"Activity_11pm9xw\">        <omgdc:Bounds x=\"140\" y=\"-190\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_1nunoz9_di\" bpmnElement=\"Activity_1196s41\">        <omgdc:Bounds x=\"310\" y=\"-190\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_0mpx0ju_di\" bpmnElement=\"Activity_0mpx0ju\">        <omgdc:Bounds x=\"480\" y=\"-190\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_1dwzdv9_di\" bpmnElement=\"Activity_02dfl1o\">        <omgdc:Bounds x=\"650\" y=\"-190\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Event_1utosrp_di\" bpmnElement=\"Event_1utosrp\">        <omgdc:Bounds x=\"822\" y=\"-168\" width=\"36\" height=\"36\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_1y7w3s3_di\" bpmnElement=\"Activity_1lg7rxi\">        <omgdc:Bounds x=\"-30\" y=\"-190\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>    </bpmndi:BPMNPlane>  </bpmndi:BPMNDiagram></definitions>";
        System.out.println("<?xml version='1.0' encoding='UTF-8'?>\n<definitions xmlns=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:flowable=\"http://flowable.org/bpmn\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:omgdc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:omgdi=\"http://www.omg.org/spec/DD/20100524/DI\" typeLanguage=\"http://www.w3.org/2001/XMLSchema\" expressionLanguage=\"http://www.w3.org/1999/XPath\" targetNamespace=\"http://flowable.org/modeler\" exporter=\"Flowable Open Source Modeler\" exporterVersion=\"6.7.2\">\n  <process id=\"bd_work_titck\" name=\"第一种工作票\" isExecutable=\"true\">\n    <startEvent id=\"startEvent1\"/>\n    <sequenceFlow id=\"Flow_1j4jqzj\" sourceRef=\"startEvent1\" targetRef=\"Activity_0xj0hpi\"/>\n    <userTask id=\"Activity_0xj0hpi\" name=\"修改提交\" flowable:assignee=\"${startUserName}\">\n      <extensionElements>\n        <modeler:initiator-can-complete xmlns:modeler=\"http://flowable.org/modeler\"><![CDATA[false]]></modeler:initiator-can-complete>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"0\" id=\"6\" label=\"保存\" type=\"save\"/>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"1\" id=\"1\" label=\"同意\" type=\"agree\"/>\n      </extensionElements>\n    </userTask>\n    <sequenceFlow id=\"Flow_119xxaw\" name=\"已提交待审核\" sourceRef=\"Activity_0xj0hpi\" targetRef=\"Activity_1lg7rxi\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" service=\"BdWorkTicketServiceImpl.updateState\" name=\"latestApprovalStatus\" value=\"2\"/>\n      </extensionElements>\n    </sequenceFlow>\n    <userTask id=\"Activity_1lg7rxi\" name=\"审核\" flowable:assignee=\"admin\">\n      <extensionElements>\n        <modeler:initiator-can-complete xmlns:modeler=\"http://flowable.org/modeler\"><![CDATA[false]]></modeler:initiator-can-complete>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"0\" id=\"2\" label=\"驳回\" type=\"reject\"/>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"1\" id=\"1\" label=\"同意\" type=\"agree\"/>\n      </extensionElements>\n    </userTask>\n    <sequenceFlow id=\"Flow_1vx9uuw\" name=\"已审核待提交\" sourceRef=\"Activity_1lg7rxi\" targetRef=\"Activity_11pm9xw\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" service=\"BdWorkTicketServiceImpl.updateState\" name=\"latestApprovalStatus\" value=\"3\"/>\n      </extensionElements>\n    </sequenceFlow>\n    <userTask id=\"Activity_11pm9xw\" name=\"签发\" flowable:assignee=\"admin\">\n      <extensionElements>\n        <modeler:initiator-can-complete xmlns:modeler=\"http://flowable.org/modeler\"><![CDATA[false]]></modeler:initiator-can-complete>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"0\" id=\"2\" label=\"驳回\" type=\"reject\"/>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"1\" id=\"1\" label=\"同意\" type=\"agree\"/>\n      </extensionElements>\n    </userTask>\n    <sequenceFlow id=\"Flow_0zh9qc6\" name=\"已签发待确认\" sourceRef=\"Activity_11pm9xw\" targetRef=\"Activity_1196s41\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" service=\"BdWorkTicketServiceImpl.updateState\" name=\"latestApprovalStatus\" value=\"4\"/>\n      </extensionElements>\n    </sequenceFlow>\n    <userTask id=\"Activity_1196s41\" name=\"确认\" flowable:assignee=\"admin\">\n      <extensionElements>\n        <modeler:initiator-can-complete xmlns:modeler=\"http://flowable.org/modeler\"><![CDATA[false]]></modeler:initiator-can-complete>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"0\" id=\"2\" label=\"驳回\" type=\"reject\"/>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"1\" id=\"1\" label=\"同意\" type=\"agree\"/>\n      </extensionElements>\n    </userTask>\n    <userTask id=\"Activity_0mpx0ju\" name=\"归档\" flowable:assignee=\"admin\">\n      <extensionElements>\n        <modeler:initiator-can-complete xmlns:modeler=\"http://flowable.org/modeler\"><![CDATA[false]]></modeler:initiator-can-complete>\n        <flowable:formOperation xmlns:flowable=\"http://flowable.org/bpmn\" showOrder=\"1\" id=\"1\" label=\"同意\" type=\"agree\"/>\n      </extensionElements>\n    </userTask>\n    <sequenceFlow id=\"Flow_127ow4d\" name=\"已确认待归档\" sourceRef=\"Activity_1196s41\" targetRef=\"Activity_0mpx0ju\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" service=\"BdWorkTicketServiceImpl.updateState\" name=\"latestApprovalStatus\" value=\"5\"/>\n      </extensionElements>\n    </sequenceFlow>\n    <endEvent id=\"Event_1utosrp\"/>\n    <sequenceFlow id=\"Flow_0xjt9ld\" name=\"已完结\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" service=\"BdWorkTicketServiceImpl.updateState\" name=\"latestApprovalStatus\" value=\"6\"/>\n        <EDITOR_RESOURCEID><![CDATA[Flow_0xjt9ld]]></EDITOR_RESOURCEID>\n      </extensionElements>\n    </sequenceFlow>\n    <sequenceFlow id=\"Flow_0iw7o99\" name=\"驳回\" sourceRef=\"Activity_1lg7rxi\" targetRef=\"Activity_0xj0hpi\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" service=\"BdWorkTicketServiceImpl.updateState\" name=\"latestApprovalStatus\" value=\"7\"/>\n        <flowable:customCondition xmlns:flowable=\"http://flowable.org/bpmn\" operationType=\"refuse\" type=\"operation\"/>\n      </extensionElements>\n      <conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[${operationType == 'refuse'}]]></conditionExpression>\n    </sequenceFlow>\n    <sequenceFlow id=\"Flow_0l0wfdy\" name=\"驳回\" sourceRef=\"Activity_11pm9xw\" targetRef=\"Activity_0xj0hpi\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" service=\"BdWorkTicketServiceImpl.updateState\" name=\"latestApprovalStatus\" value=\"7\"/>\n        <flowable:customCondition xmlns:flowable=\"http://flowable.org/bpmn\" operationType=\"refuse\" type=\"operation\"/>\n      </extensionElements>\n      <conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[${operationType == 'refuse'}]]></conditionExpression>\n    </sequenceFlow>\n    <sequenceFlow id=\"Flow_16st22k\" name=\"确认驳回\" sourceRef=\"Activity_1196s41\" targetRef=\"Activity_0xj0hpi\">\n      <extensionElements>\n        <flowable:property xmlns:flowable=\"http://flowable.org/bpmn\" service=\"BdWorkTicketServiceImpl.updateState\" name=\"latestApprovalStatus\" value=\"7\"/>\n        <flowable:customCondition xmlns:flowable=\"http://flowable.org/bpmn\" operationType=\"refuse\" type=\"operation\"/>\n      </extensionElements>\n      <conditionExpression xsi:type=\"tFormalExpression\"><![CDATA[${operationType == 'refuse'}]]></conditionExpression>\n    </sequenceFlow>\n  </process>\n  <bpmndi:BPMNDiagram id=\"BPMNDiagram_bd_work_titck\">\n    <bpmndi:BPMNPlane bpmnElement=\"bd_work_titck\" id=\"BPMNPlane_bd_work_titck\">\n      <bpmndi:BPMNShape bpmnElement=\"startEvent1\" id=\"BPMNShape_startEvent1\">\n        <omgdc:Bounds height=\"30.0\" width=\"30.0\" x=\"-295.0\" y=\"-165.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_0xj0hpi\" id=\"BPMNShape_Activity_0xj0hpi\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"-200.0\" y=\"-190.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_1lg7rxi\" id=\"BPMNShape_Activity_1lg7rxi\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"-30.0\" y=\"-190.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_11pm9xw\" id=\"BPMNShape_Activity_11pm9xw\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"140.0\" y=\"-190.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_1196s41\" id=\"BPMNShape_Activity_1196s41\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"310.0\" y=\"-190.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Activity_0mpx0ju\" id=\"BPMNShape_Activity_0mpx0ju\">\n        <omgdc:Bounds height=\"80.0\" width=\"100.0\" x=\"480.0\" y=\"-190.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNShape bpmnElement=\"Event_1utosrp\" id=\"BPMNShape_Event_1utosrp\">\n        <omgdc:Bounds height=\"36.0\" width=\"36.0\" x=\"822.0\" y=\"-168.0\"/>\n      </bpmndi:BPMNShape>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_0iw7o99\" id=\"BPMNEdge_Flow_0iw7o99\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"20.0\" y=\"-110.04999999999998\"/>\n        <omgdi:waypoint x=\"20.0\" y=\"-60.0\"/>\n        <omgdi:waypoint x=\"-150.0\" y=\"-60.0\"/>\n        <omgdi:waypoint x=\"-150.0\" y=\"-110.04999999999998\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_16st22k\" id=\"BPMNEdge_Flow_16st22k\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"360.0\" y=\"-110.04999999999998\"/>\n        <omgdi:waypoint x=\"360.0\" y=\"-20.0\"/>\n        <omgdi:waypoint x=\"-150.0\" y=\"-20.0\"/>\n        <omgdi:waypoint x=\"-150.0\" y=\"-110.04999999999998\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_1j4jqzj\" id=\"BPMNEdge_Flow_1j4jqzj\" flowable:sourceDockerX=\"15.0\" flowable:sourceDockerY=\"15.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"-265.05000108130884\" y=\"-150.0\"/>\n        <omgdi:waypoint x=\"-200.00000000000978\" y=\"-150.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_1vx9uuw\" id=\"BPMNEdge_Flow_1vx9uuw\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"69.95\" y=\"-150.0\"/>\n        <omgdi:waypoint x=\"139.99999999993562\" y=\"-150.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_127ow4d\" id=\"BPMNEdge_Flow_127ow4d\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"409.95000000000005\" y=\"-150.0\"/>\n        <omgdi:waypoint x=\"479.9999999999356\" y=\"-150.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_0zh9qc6\" id=\"BPMNEdge_Flow_0zh9qc6\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"239.95000000000002\" y=\"-150.0\"/>\n        <omgdi:waypoint x=\"309.99999999993565\" y=\"-150.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_119xxaw\" id=\"BPMNEdge_Flow_119xxaw\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"-100.04999999999998\" y=\"-150.0\"/>\n        <omgdi:waypoint x=\"-30.000000000064375\" y=\"-150.0\"/>\n      </bpmndi:BPMNEdge>\n      <bpmndi:BPMNEdge bpmnElement=\"Flow_0l0wfdy\" id=\"BPMNEdge_Flow_0l0wfdy\" flowable:sourceDockerX=\"50.0\" flowable:sourceDockerY=\"40.0\" flowable:targetDockerX=\"50.0\" flowable:targetDockerY=\"40.0\">\n        <omgdi:waypoint x=\"190.0\" y=\"-190.0\"/>\n        <omgdi:waypoint x=\"190.0\" y=\"-260.0\"/>\n        <omgdi:waypoint x=\"-160.0\" y=\"-260.0\"/>\n        <omgdi:waypoint x=\"-153.63636363636363\" y=\"-190.0\"/>\n      </bpmndi:BPMNEdge>\n    </bpmndi:BPMNPlane>\n  </bpmndi:BPMNDiagram>\n</definitions>");
    }



}
