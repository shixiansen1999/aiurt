package com.aiurt.common.system.base.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 数据导入结果返回实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaseImportResult implements Serializable {

    /**
     * 是否成功， true 是， false ：否
     */
    private Boolean isSucceed;

    /**
     * 错误数据记录数
     */
    private int errorCount;

    /**
     *成功的记录数
     */
    private int successCount;

    /**
     * 总的记录数
     */
    private int totalCount;

    /**
     * 错误文件url
     */
    private String failReportUrl;
}
