package com.aiurt.modules.modeler.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:wgp
 * @create: 2023-07-28 09:30
 * @Description:
 */
@Data
public class RelationMaps {
    private Map<String, String> numberRelationMap;
    private Map<String, String> numberRelationNameMap;

    public RelationMaps() {
        this.numberRelationMap = new HashMap<>();
        this.numberRelationNameMap = new HashMap<>();
    }

    public void addNumberRelation(String key, String value) {
        numberRelationMap.put(key, value);
    }
    public void addNumberRelationName(String key, String value) {
        numberRelationNameMap.put(key, value);
    }
}
