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
     * 文件数据索引
     */
    public static final String FILE_DATA_INDEX = "file_data";
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
     * 规范知识库内容
     */
    public static final String ATTACHMENT_CONTENT = "attachment.content";
    /**
     * 规范知识库文档格式
     */
    public static final String FORMAT = "format";
    /**
     * 规范知识库文档类型id
     */
    public static final String TYPE_ID = "type_id";
    /**
     * 规范知识库文档名称
     */
    public static final String NAME = "name";

    /**
     * 规程规范与知识库文档名称
     */
    public static final String ATTACHMENT_NAME = "name";

    /**
     * fault_knowledge_suggest是一个存储标识
     */
    public static final String FAULT_KNOWLEDGE_SUGGEST = "fault_knowledge_suggest";

    /**
     * document_manage_suggest是一个存储标识
     */
    public static final String DOCUMENT_MANAGE_SUGGEST = "document_manage_suggest";

    /**
     * 词语补全后缀
     */
    public static final String SUGGEST_SUFFIX = ".suggest";
    /**
     * 高亮词语前缀
     */
    public static final String HIGH_LIGHT_PRE_TAGS = "<b style='color:red'>";
    /**
     * 高亮词语前缀
     */
    public static final String HIGH_LIGHT_POST_TAGS = "</b>";
    /**
     * 高亮词语前缀
     */
    public static final String SORT_ORDER_ASC = "asc";
    /**
     * 词语提示输出显示最大条数
     */
    public static final Integer TEN = 8;
    /**
     * 动态词库对应的字典code
     */
    public static final String IK_DICT_CODE = "ik_dict_code";
    /**
     * id字段名称
     */
    public static final String ID = "id";

    public static final Integer FAULT_KNOWLEDGE_PAGE_SIZE_10 = 10;

    public static final Integer FAULT_KNOWLEDGE_PAGE_NO_1 = 1;


}
