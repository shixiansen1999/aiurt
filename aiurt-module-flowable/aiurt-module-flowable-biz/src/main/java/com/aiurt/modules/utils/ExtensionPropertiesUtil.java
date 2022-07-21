package com.aiurt.modules.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.constants.BpmnXMLConstants;
import org.flowable.bpmn.model.Activity;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.editor.language.json.converter.util.JsonConverterUtil;

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
}
