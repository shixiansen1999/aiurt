package com.aiurt.boot.constant;

/**
 * @author wgp
 * @Title:
 * @Description:
 * @date 2023/2/149:41
 */
public class EsConstant {
    /**
     * 故障知识库es索引
     */
    public static final String FAULT_KNOWLEDGE_INDEX = "fault_knowledge_base";
    /**
     * 故障现象字段S
     */
    public static final String FAULT_PHENOMENON = "fault_phenomenon";
    /**
     * 故障原因字段
     */
    public static final String FAULT_REASON = "fault_reason";
    /**
     * 解决方案字段
     */
    public static final String SOLUTION = "solution";
    /**
     * 排查方法字段
     */
    public static final String METHOD = "method";
    /**
     * 故障现象分类字段
     */
    public static final String KNOWLEDGE_BASE_TYPE_CODE = "knowledge_base_type_code";

    /**
     * 设备分类字段
     */
    public static final String DEVICE_TYPE_CODE = "device_type_code";
    /**
     * 设备组件字段
     */
    public static final String MATERIAL_CODE = "material_code";

    /**
     * 词语提示输出显示最大条数
     */
    public static final Integer TEN = 8;

    /**
     * fault_knowledge_suggest是一个存储标识
     */
    public static final String FAULT_KNOWLEDGE_SUGGEST = "fault_knowledge_suggest";


    public static final Integer FAULT_KNOWLEDGE_PAGE_SIZE_10 = 10;

    public static final Integer FAULT_KNOWLEDGE_PAGE_NO_1 = 1;

    /**
     * 文件数据索引
     */
    public static final String FILE_DATA_INDEX = "file_data";
}
