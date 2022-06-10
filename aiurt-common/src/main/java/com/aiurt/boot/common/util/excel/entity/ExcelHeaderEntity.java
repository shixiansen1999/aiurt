package com.aiurt.boot.common.util.excel.entity;

import lombok.Data;

@Data
public class ExcelHeaderEntity {
    /**
     * 字段名称
     */
    private String field;
    /**
     * 字段类写
     */
    private Class aClass;
    /**
     * 字段名称
     */
    private String name;
    /**
     * excel表格宽度
     */
    private Double width;
    /**
     * 时间类型格式
     */
    private String format;
}
