package com.aiurt.modules.worklog.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author cgkj0
 * @version 1.0
 * @date 2022/7/20
 * @desc
 */
@Data
public class WorkLogJobDTO implements Serializable {

    private String orgId;
    private String fromUser;
    private String content;
}