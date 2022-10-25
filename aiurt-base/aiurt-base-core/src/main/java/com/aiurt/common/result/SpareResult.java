package com.aiurt.common.result;

import lombok.Data;

/**
 * @Author WangHongTao
 * @Date 2021/11/9
 * 查询备件结果集
 */
@Data
public class SpareResult {

    /**
     * 原备件编号
     */
    private String oldSparePartCode;

    /**
     * 原备件名称
     */
    private String oldSparePartName;

    /**
     * 原备件数量
     */
    private Integer oldSparePartNum;

    /**
     * 新备件编号
     */
    private String newSparePartCode;

    /**
     * 新备件名称
     */
    private String newSparePartName;

    /**
     * 新备件数量
     */
    private Integer newSparePartNum;
}
