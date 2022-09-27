package com.aiurt.modules.editor.language.json.converter;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
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
import org.apache.commons.lang3.StringUtils;
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
                "      <bpmndi:BPMNEdge id=\"Flow_1ida4o2_di\" bpmnElement=\"Flow_1ida4o2\">\n" +
                "        <di:waypoint x=\"870\" y=\"240\" />\n" +
                "        <di:waypoint x=\"930\" y=\"240\" />\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_1hkslp1_di\" bpmnElement=\"Flow_1hkslp1\">\n" +
                "        <di:waypoint x=\"710\" y=\"240\" />\n" +
                "        <di:waypoint x=\"770\" y=\"240\" />\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_14dbf29_di\" bpmnElement=\"Flow_14dbf29\">\n" +
                "        <di:waypoint x=\"550\" y=\"240\" />\n" +
                "        <di:waypoint x=\"610\" y=\"240\" />\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_0tqr84n_di\" bpmnElement=\"Flow_0tqr84n\">\n" +
                "        <di:waypoint x=\"390\" y=\"240\" />\n" +
                "        <di:waypoint x=\"450\" y=\"240\" />\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNEdge id=\"Flow_1xwyk4z_di\" bpmnElement=\"Flow_1xwyk4z\">\n" +
                "        <di:waypoint x=\"238\" y=\"240\" />\n" +
                "        <di:waypoint x=\"290\" y=\"240\" />\n" +
                "      </bpmndi:BPMNEdge>\n" +
                "      <bpmndi:BPMNShape id=\"Event_0z2kuz1_di\" bpmnElement=\"Event_0z2kuz1\">\n" +
                "        <dc:Bounds x=\"202\" y=\"222\" width=\"36\" height=\"36\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_0mv4jsh_di\" bpmnElement=\"Activity_0mv4jsh\">\n" +
                "        <dc:Bounds x=\"290\" y=\"200\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_011h7xn_di\" bpmnElement=\"Activity_011h7xn\">\n" +
                "        <dc:Bounds x=\"450\" y=\"200\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_1f4tw7v_di\" bpmnElement=\"Activity_1f4tw7v\">\n" +
                "        <dc:Bounds x=\"610\" y=\"200\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_0w57vsj_di\" bpmnElement=\"Activity_0w57vsj\">\n" +
                "        <dc:Bounds x=\"770\" y=\"200\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Event_0ek55dj_di\" bpmnElement=\"Event_0ek55dj\">\n" +
                "        <dc:Bounds x=\"1002\" y=\"352\" width=\"36\" height=\"36\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "      <bpmndi:BPMNShape id=\"Activity_0ugsf7v_di\" bpmnElement=\"Activity_0ugsf7v\">\n" +
                "        <dc:Bounds x=\"930\" y=\"200\" width=\"100\" height=\"80\" />\n" +
                "      </bpmndi:BPMNShape>\n" +
                "    </bpmndi:BPMNPlane>\n" +
                "  </bpmndi:BPMNDiagram>\n" +
                "</bpmn2:definitions>";

        System.out.println(va.replaceAll("\r|\n", ""));

        String c = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><bpmn2:definitions xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:bpmn2=\"http://www.omg.org/spec/BPMN/20100524/MODEL\" xmlns:bpmndi=\"http://www.omg.org/spec/BPMN/20100524/DI\" xmlns:dc=\"http://www.omg.org/spec/DD/20100524/DC\" xmlns:di=\"http://www.omg.org/spec/DD/20100524/DI\" xmlns:flowable=\"http://flowable.org/bpmn\" id=\"diagram_test\" targetNamespace=\"http://flowable.org/bpmn\" xsi:schemaLocation=\"http://www.omg.org/spec/BPMN/20100524/MODEL BPMN20.xsd\">  <bpmn2:process id=\"zidingyi_code\" name=\"测试\" isExecutable=\"true\">    <bpmn2:startEvent id=\"Event_0z2kuz1\">      <bpmn2:outgoing>Flow_1xwyk4z</bpmn2:outgoing>    </bpmn2:startEvent>    <bpmn2:userTask id=\"Activity_0mv4jsh\" name=\"发起人\" flowable:formKey=\"{&#34;routerName&#34;:&#34;/test/test&#34;,&#34;readOnly&#34;:true,&#34;groupType&#34;:&#34;ASSIGNEE&#34;}\">      <bpmn2:extensionElements>        <flowable:variableList />        <flowable:copyItemList />\t  <flowable:formOperation id=\"1659428417427\" label=\"保存\" type=\"save\" showOrder=\"0\" />\t  <flowable:formOperation id=\"1659428436977\" label=\"拒绝\" type=\"refuse\" showOrder=\"0\" />             </bpmn2:extensionElements>      <bpmn2:incoming>Flow_1xwyk4z</bpmn2:incoming>      <bpmn2:outgoing>Flow_0tqr84n</bpmn2:outgoing>    </bpmn2:userTask>    <bpmn2:sequenceFlow id=\"Flow_1xwyk4z\" sourceRef=\"Event_0z2kuz1\" targetRef=\"Activity_0mv4jsh\" />    <bpmn2:userTask id=\"Activity_011h7xn\" name=\"上级领导\" flowable:formKey=\"{&#34;routerName&#34;:&#34;/test/test&#34;,&#34;readOnly&#34;:true,&#34;groupType&#34;:&#34;ASSIGNEE&#34;}\" flowable:assignee=\"${appointedAssignee}\">      <bpmn2:extensionElements>        <flowable:copyItemList />                  <flowable:formOperation id=\"1659428455100\" label=\"拒绝\" type=\"refuse\" showOrder=\"0\" />               <flowable:variableList>          <flowable:formVariable id=\"1554381425178841088\" />        </flowable:variableList>      </bpmn2:extensionElements>      <bpmn2:incoming>Flow_0tqr84n</bpmn2:incoming>      <bpmn2:outgoing>Flow_14dbf29</bpmn2:outgoing>    </bpmn2:userTask>    <bpmn2:sequenceFlow id=\"Flow_0tqr84n\" sourceRef=\"Activity_0mv4jsh\" targetRef=\"Activity_011h7xn\" />    <bpmn2:userTask id=\"Activity_1f4tw7v\" name=\"上级部门\" flowable:formKey=\"{&#34;routerName&#34;:&#34;/test/test&#34;,&#34;readOnly&#34;:true,&#34;groupType&#34;:&#34;UP_DEPT_POST_LEADER&#34;}\">      <bpmn2:extensionElements>        <flowable:variableList>          <flowable:formVariable id=\"1554381111751086080\" />          <flowable:formVariable id=\"1554381111923052545\" />          <flowable:formVariable id=\"1554381425178841088\" />        </flowable:variableList>        <flowable:copyItemList />                 <flowable:formOperation id=\"1659428597640\" label=\"同意\" type=\"agree\" showOrder=\"0\" />             </bpmn2:extensionElements>      <bpmn2:incoming>Flow_14dbf29</bpmn2:incoming>      <bpmn2:outgoing>Flow_1hkslp1</bpmn2:outgoing>    </bpmn2:userTask>    <bpmn2:sequenceFlow id=\"Flow_14dbf29\" sourceRef=\"Activity_011h7xn\" targetRef=\"Activity_1f4tw7v\" />    <bpmn2:userTask id=\"Activity_0w57vsj\" name=\"部门\" flowable:formKey=\"{&#34;routerName&#34;:&#34;/test/test&#34;,&#34;readOnly&#34;:true,&#34;groupType&#34;:&#34;USERS&#34;}\" flowable:candidateUsers=\"admin,leaderHR,userA\">      <bpmn2:extensionElements>        \t  <flowable:formOperation id=\"1659428572985\" label=\"驳回到起点\" type=\"rejectToStart\" showOrder=\"0\" />\t  <flowable:formOperation id=\"1659428576592\" label=\"同意\" type=\"agree\" showOrder=\"0\" />               <flowable:variableList />        <flowable:copyItemList />        <flowable:userCandidateGroups type=\"USERS\" value=\"admin,leaderHR,userA\" />      </bpmn2:extensionElements>      <bpmn2:incoming>Flow_1hkslp1</bpmn2:incoming>      <bpmn2:outgoing>Flow_1ida4o2</bpmn2:outgoing>    </bpmn2:userTask>    <bpmn2:sequenceFlow id=\"Flow_1hkslp1\" sourceRef=\"Activity_1f4tw7v\" targetRef=\"Activity_0w57vsj\" />    <bpmn2:endEvent id=\"Event_0ek55dj\">      <bpmn2:incoming>Flow_1wv8xvg</bpmn2:incoming>    </bpmn2:endEvent>    <bpmn2:userTask id=\"Activity_0ugsf7v\" name=\"真部门\" flowable:formKey=\"{&#34;routerName&#34;:&#34;/test/test&#34;,&#34;readOnly&#34;:true,&#34;groupType&#34;:&#34;DEPT&#34;}\" flowable:candidateGroups=\"1440963592970047488,1440963642542526464\">      <bpmn2:extensionElements>                <flowable:formOperation id=\"1659430005896\" label=\"拒绝\" type=\"refuse\" showOrder=\"0\" />          <flowable:formOperation id=\"1659430010386\" label=\"转办\" type=\"transfer\" showOrder=\"0\" />                <flowable:variableList />        <flowable:copyItemList />        <flowable:userCandidateGroups type=\"DEPT\" value=\"1440963592970047488,1440963642542526464\" />      </bpmn2:extensionElements>      <bpmn2:incoming>Flow_1ida4o2</bpmn2:incoming>      <bpmn2:outgoing>Flow_1wv8xvg</bpmn2:outgoing>    </bpmn2:userTask>    <bpmn2:sequenceFlow id=\"Flow_1ida4o2\" sourceRef=\"Activity_0w57vsj\" targetRef=\"Activity_0ugsf7v\" />    <bpmn2:sequenceFlow id=\"Flow_1wv8xvg\" sourceRef=\"Activity_0ugsf7v\" targetRef=\"Event_0ek55dj\" />  </bpmn2:process>  <bpmndi:BPMNDiagram id=\"BPMNDiagram_1\">    <bpmndi:BPMNPlane id=\"BPMNPlane_1\" bpmnElement=\"test\">      <bpmndi:BPMNEdge id=\"Flow_1wv8xvg_di\" bpmnElement=\"Flow_1wv8xvg\">        <di:waypoint x=\"980\" y=\"280\" />        <di:waypoint x=\"980\" y=\"316\" />        <di:waypoint x=\"1020\" y=\"316\" />        <di:waypoint x=\"1020\" y=\"352\" />      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_1ida4o2_di\" bpmnElement=\"Flow_1ida4o2\">        <di:waypoint x=\"870\" y=\"240\" />        <di:waypoint x=\"930\" y=\"240\" />      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_1hkslp1_di\" bpmnElement=\"Flow_1hkslp1\">        <di:waypoint x=\"710\" y=\"240\" />        <di:waypoint x=\"770\" y=\"240\" />      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_14dbf29_di\" bpmnElement=\"Flow_14dbf29\">        <di:waypoint x=\"550\" y=\"240\" />        <di:waypoint x=\"610\" y=\"240\" />      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_0tqr84n_di\" bpmnElement=\"Flow_0tqr84n\">        <di:waypoint x=\"390\" y=\"240\" />        <di:waypoint x=\"450\" y=\"240\" />      </bpmndi:BPMNEdge>      <bpmndi:BPMNEdge id=\"Flow_1xwyk4z_di\" bpmnElement=\"Flow_1xwyk4z\">        <di:waypoint x=\"238\" y=\"240\" />        <di:waypoint x=\"290\" y=\"240\" />      </bpmndi:BPMNEdge>      <bpmndi:BPMNShape id=\"Event_0z2kuz1_di\" bpmnElement=\"Event_0z2kuz1\">        <dc:Bounds x=\"202\" y=\"222\" width=\"36\" height=\"36\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_0mv4jsh_di\" bpmnElement=\"Activity_0mv4jsh\">        <dc:Bounds x=\"290\" y=\"200\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_011h7xn_di\" bpmnElement=\"Activity_011h7xn\">        <dc:Bounds x=\"450\" y=\"200\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_1f4tw7v_di\" bpmnElement=\"Activity_1f4tw7v\">        <dc:Bounds x=\"610\" y=\"200\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_0w57vsj_di\" bpmnElement=\"Activity_0w57vsj\">        <dc:Bounds x=\"770\" y=\"200\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Event_0ek55dj_di\" bpmnElement=\"Event_0ek55dj\">        <dc:Bounds x=\"1002\" y=\"352\" width=\"36\" height=\"36\" />      </bpmndi:BPMNShape>      <bpmndi:BPMNShape id=\"Activity_0ugsf7v_di\" bpmnElement=\"Activity_0ugsf7v\">        <dc:Bounds x=\"930\" y=\"200\" width=\"100\" height=\"80\" />      </bpmndi:BPMNShape>    </bpmndi:BPMNPlane>  </bpmndi:BPMNDiagram></bpmn2:definitions>";
    }



}
