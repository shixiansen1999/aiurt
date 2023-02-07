package com.aiurt.common.system.base.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author fgw
 */
@Data
public class DynamicTableEntity<T extends DynamicTableDataEntity> implements Serializable {

    private static final long serialVersionUID = -5914141692149276831L;

    /***
     * 表头
     */
    private List<DynamicTableTitleEntity> titleList;

    /**
     * 数据项
     */
    private List<T> records;

    private long current;

    private long size;

    private long total;
}
