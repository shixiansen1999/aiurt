package com.aiurt.common.result;

import lombok.Data;

/**
 * @Author zwl
 * @Date 2022/10/25
 */
@Data
public class LogResult {

    /**
     * 故障code
     */
    private String faultCodes;

    /**
     * 故障内容
     */
    private  String  faultContent;

    /**
     * 巡检ids
     */
    private  String  patrolIds;

    /**
     * 巡检内容
     */
    private  String  patrolContent;

    /**
     * 检修code
     */
    private  String  repairCode;

    /**
     * 检修内容
     */
    private  String  repairContent;
}
