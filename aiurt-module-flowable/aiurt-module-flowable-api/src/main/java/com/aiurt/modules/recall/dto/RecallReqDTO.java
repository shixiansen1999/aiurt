package com.aiurt.modules.recall.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author fgw
 */
@Data
public class RecallReqDTO implements Serializable {

    private String processInstanceId;

    private String taskId;

    private String reason;
}
