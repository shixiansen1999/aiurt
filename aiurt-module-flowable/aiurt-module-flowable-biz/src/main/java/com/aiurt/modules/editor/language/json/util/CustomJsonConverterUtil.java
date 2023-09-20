package com.aiurt.modules.editor.language.json.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author fgw
 */
public  class CustomJsonConverterUtil {

    private static final Logger log = LoggerFactory.getLogger(CustomJsonConverterUtil.class);

    /**
     * json String转换为JsonNode
     * @param jsonValue json String
     * @return jsonNode
     */
    public static JsonNode parseJsonMode(String jsonValue) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonValue);
            return jsonNode;
        } catch (IOException e) {
            // ignore exception
            log.error(e.getMessage(), e);
        }
        return null;
    }

}
