package com.aiurt.boot.common.util.excel.entity;

import lombok.Data;

@Data
public class ExcelImportEntity {
    private String fieldName;
    private int index;
    private String clz;
    private String method;
    private String message;
    private String parttern;
    private String changeMethod;
}
