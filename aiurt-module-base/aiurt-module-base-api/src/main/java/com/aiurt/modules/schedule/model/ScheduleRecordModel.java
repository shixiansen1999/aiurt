package com.aiurt.modules.schedule.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
public class ScheduleRecordModel {
    private Integer id;
    private String userId;
    private String userName;
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date date;
    private Integer itemId;
    private String itemName;
    private String color;
    private String remark;
}
