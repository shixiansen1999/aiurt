package com.aiurt.modules.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.Activity;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.editor.language.json.converter.util.JsonConverterUtil;

import java.util.List;
import java.util.Map;

/**
 * @author fgw
 */
public class ExtensionPropertiesUtil {


    public static void addExtensionPropertiesElement(JsonNode objectNode, Activity activity, String name) {
        JsonNode expansionNode = JsonConverterUtil.getProperty(name, objectNode);
        if (expansionNode instanceof TextNode){
            if (expansionNode != null && StringUtils.isNotBlank(expansionNode.asText())){
                ExtensionElement extensionElement = new ExtensionElement();
                extensionElement.setName(name);
                extensionElement.setNamespacePrefix(BpmnXMLConstants.FLOWABLE_EXTENSIONS_PREFIX);
                extensionElement.setNamespace(BpmnXMLConstants.FLOWABLE_EXTENSIONS_NAMESPACE);
                extensionElement.setElementText(expansionNode.asText());
                activity.addExtensionElement(extensionElement);
            }
        }
    }

    /**
     * 获取bpmnxml 的属性
     * @param extensionMap extensionMap
     * @param rootName 一级属性名
     * @param childName 二级属性名
     * @return
     */
    public static List<ExtensionElement> getMyExtensionElementList(Map<String, List<ExtensionElement>> extensionMap,
                                                                   String rootName, String childName) {
        List<ExtensionElement> elementList = extensionMap.get(rootName);
        if (CollUtil.isEmpty(elementList)) {
            return null;
        }
        ExtensionElement ee = elementList.get(0);
        Map<String, List<ExtensionElement>> childExtensionMap = ee.getChildElements();
        if (MapUtil.isEmpty(childExtensionMap)) {
            return null;
        }
        List<ExtensionElement> childrenElements = childExtensionMap.get(childName);
        if (CollUtil.isEmpty(childrenElements)) {
            return null;
        }
        return childrenElements;
    }
}
