package com.aiurt.boot.common.result;

import lombok.Data;

/**
 * 查询备件结果集
 */
@Data
public class SpareResult {

    private String oldSparePartCode;

    private Integer oldSparePartNum;

    private String newSparePartCode;

    private Integer newSparePartNum;
}
