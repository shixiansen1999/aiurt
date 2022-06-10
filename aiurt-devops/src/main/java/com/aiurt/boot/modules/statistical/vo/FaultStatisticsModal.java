package com.aiurt.boot.modules.statistical.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: niuzeyu
 * @date: 2022年01月21日 19:10
 */
@Data
public class FaultStatisticsModal implements Serializable {
    //故障编号
    private String code;
    //线路编号
    //private String lineCode;
    //系统
    private String systemName;
    //站点名称
    private String stationName;
    //站点编号
    private String stationCode;
    //故障发生时间
    private Date occurrenceTime;
    //指派人id
    private String appointUserId;
    //参与者id
    private String participateIds;
    //问题解决状态
    private String solveStatus;
    //指派状态 0未指派 1已指派未填写 2已指派已填写
    private Integer assignStatus;
    //故障状态 0新报修 1维修中 2维修完成
    private Integer status;
    //维修人
    private String maintainer;

}
