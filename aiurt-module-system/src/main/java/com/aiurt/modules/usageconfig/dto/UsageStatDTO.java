package com.aiurt.modules.usageconfig.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author fgw
 */
@Data
public class UsageStatDTO implements Serializable {
    private static final long serialVersionUID = 4220234948171427211L;

    /**
     * 标题
     */
    private String tileName;

    /**
     * 总数
     */
    private Long total;

    /**
     * 新增数量
     */
    private Long newAddNum;

    private String id;

    private String pid;

    private String tableName;

    private String staCondition;

    private List<UsageStatDTO> children;
}

