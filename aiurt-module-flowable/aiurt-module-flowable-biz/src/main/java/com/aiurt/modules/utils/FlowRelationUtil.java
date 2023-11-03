package com.aiurt.modules.utils;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.ExtensionElement;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author:wgp
 * @create: 2023-07-26 16:20
 * @Description:
 */
@Slf4j
public class FlowRelationUtil {
    private static final Pattern PATTERN = Pattern.compile("\\[(\\d+)\\]");
    /**
     * 定义一个全局的 ObjectMapper 对象，确保在整个类的生命周期内重复使用
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 替换输入字符串中的占位符模式为指定的值。
     *
     * @param input        输入字符串，包含占位符模式，例如 "[1]"、"[2]"、"[3]" 等。
     * @param replacements 用于替换占位符的映射表。键为占位符模式中的数字部分，值为要替换的字符串。
     * @return 替换后的字符串。
     */
    public static String replacePlaceholders(String input, Map<String, String> replacements) {
        if (StrUtil.isEmpty(input) || MapUtil.isEmpty(replacements)) {
            return input;
        }
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            String placeholder = entry.getKey();
            String replacement = entry.getValue();
            input = input.replace(placeholder, replacement);
        }
        return input;
    }

    /**
     * 替换输入字符串中的逻辑运算符"or"和"and"为指定的替代字符串，如果替代字符串为空，则使用默认的"||"和"&&"替代。
     *
     * @param input          输入字符串，包含逻辑运算符"or"和"and"。
     * @param orReplacement  替代字符串，用于替换"or"，如果为空则使用默认的"||"替代。
     * @param andReplacement 替代字符串，用于替换"and"，如果为空则使用默认的"&&"替代。
     * @return 替换后的字符串。
     */
    public static String replaceOperators(String input, String orReplacement, String andReplacement) {
        if (StrUtil.isEmpty(input)) {
            return input;
        }
        return input.replace("or", StrUtil.isNotBlank(orReplacement) ? orReplacement : "||").replace("and", StrUtil.isNotEmpty(andReplacement) ? andReplacement : "&&");
    }

    /**
     * 根据给定的类类型和扩展元素，创建一个包含字段信息的 ObjectNode 对象。
     *
     * @param clazz            类类型，用于获取字段信息。
     * @param extensionElement 扩展元素，用于获取字段值。
     * @return 创建的 ObjectNode 对象，包含字段名和对应的字段值。
     */
    public static <T> ObjectNode createObjectNodeFromFields(Class<T> clazz, ExtensionElement extensionElement) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        Field[] fields = clazz.getDeclaredFields();
        Arrays.stream(fields)
                .filter(field -> !StrUtil.equals("serialVersionUID", field.getName()))
                .forEach(field -> objectNode.put(field.getName(), extensionElement.getAttributeValue(null, field.getName())));
        return objectNode;
    }

    /**
     * 将 ObjectNode 对象转换为指定类类型的对象。
     *
     * @param objectNode ObjectNode 对象，包含要转换的数据。
     * @param clazz      要转换成的类类型。
     * @param <T>        转换后的对象类型。
     * @return 转换后的对象，如果转换失败返回null。
     */
    public static <T> T parseJsonToObject(ObjectNode objectNode, Class<T> clazz) {
        try {
            String json = objectMapper.writeValueAsString(objectNode);
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("转换 ObjectNode 到对象时发生 JSON 处理异常。", e);
        }
        return null;
    }

    /**
     * 将 ArrayNode 对象转换为指定类类型的对象列表。
     *
     * @param arrayNode ArrayNode 对象，包含要转换的数据列表。
     * @param clazz     要转换成的类类型。
     * @param <T>       转换后的对象类型。
     * @return 转换后的对象列表，如果转换失败返回空列表。
     */
    public static <T> List<T> parseJsonToList(ArrayNode arrayNode, Class<T> clazz) {
        try {
            String json = objectMapper.writeValueAsString(arrayNode);
            return JSONObject.parseArray(json, clazz);
        } catch (JsonProcessingException e) {
            log.error("转换 ArrayNode 到对象列表时发生 JSON 处理异常。", e);
        }
        return Collections.emptyList();
    }

}
