package com.aiurt.boot.common.result;

import lombok.Data;

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
