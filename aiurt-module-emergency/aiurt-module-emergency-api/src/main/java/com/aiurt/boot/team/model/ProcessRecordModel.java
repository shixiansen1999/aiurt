package com.aiurt.boot.team.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author lkj
 */
@Data
public class ProcessRecordModel implements Serializable {
    /**序号*/
    private String sort;
    /**时间*/
    private String trainingTime;
    /**是否次日： 0是 1否*/
    private Integer nextDay;
    /**训练内容*/
    private String trainingContent;
}
