package com.aiurt.boot.modules.schedule.vo;

import lombok.Data;

@Data
public class ScheduleRecordVo {
    private String userId;
    private String username;
    private String department;
    private Integer count;
    private Integer act;
}
