package com.aiurt.boot.core.common.model;

import lombok.Data;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * 高亮对象封装
 */
@Data
public class HighLight {
    private String preTag = "";
    private String postTag = "";
    private List<String> highLightList = null;
    private HighlightBuilder highlightBuilder = null;

    public HighLight() {
        highLightList = new ArrayList<>();
    }

    public HighLight field(String fieldValue) {
        highLightList.add(fieldValue);
        return this;
    }
}
