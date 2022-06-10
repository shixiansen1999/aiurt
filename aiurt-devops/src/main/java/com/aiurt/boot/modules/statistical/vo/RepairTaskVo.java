package com.aiurt.boot.modules.statistical.vo;

import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @author: niuzeyu
 * @date: 2022年01月25日 10:32
 */
@Data
public class RepairTaskVo {
    private String departName;
    private String taskId;
    private String stationName;
    private Integer weeks;
    private String staffNames;
    private Date submitTime;
    private Integer repairStatus;

    private Date endTime;
    private Integer status;
    private String taskName;
}
