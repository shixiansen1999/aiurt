package com.aiurt.modules.faultknowledgebase.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.annotation.ExcelCollection;
import lombok.Data;

import java.util.List;

@Data
public class FaultKnowledgeBaseImportModel {

    /**
     * 线路编号
     */
    private java.lang.String lineCode;
    /**
     * 线路名称
     */
    @Excel(name = "线路名称", width = 15)
    private java.lang.String lineName;
    /**
     * 专业编号
     */
    private java.lang.String majorCode;
    /**
     * 专业名称
     */
    @Excel(name = "专业名称", width = 15)
    private java.lang.String majorName;
    /**
     * 子系统编号
     */
    private java.lang.String systemCode;
    /**
     * 子系统名称
     */
    @Excel(name = "子系统名称", width = 15)
    private java.lang.String systemName;
    /**
     * 故障现象分类编号
     */
    private java.lang.String knowledgeBaseTypeCode;
    /**
     * 故障现象分类名称
     */
    @Excel(name = "故障现象分类", width = 15)
    private java.lang.String knowledgeBaseTypeName;
    /**
     * 故障现象
     */
    @Excel(name = "故障现象", width = 15)
    private java.lang.String faultPhenomenon;
    /**
     * 设备类型名称
     */
    @Excel(name = "设备类型名称", width = 15)
    private String deviceTypeName;
    /**
     * 设备类型编号
     */
    @Excel(name = "设备类型编号", width = 15)
    private String deviceTypeCode;
    /**
     * 排查方法
     */
    @Excel(name = "排查方法", width = 15)
    private java.lang.String method;
    /**
     * 原因及解决方案
     */
    @ExcelCollection(name = "原因及解决方案")
    private List<FaultReasonSolutionImportModel> causeSolutions;
}
