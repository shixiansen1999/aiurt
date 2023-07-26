package com.aiurt.modules.schedule.vo;

import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

@Data
public class ScheduleRecordVo {
    private String userId;
    @Excel(name = "姓名",width = 15)
    private String username;
    @Excel(name = "班组",width = 25)
    private String department;
    @Excel(name = "计划夜班次数",width = 15)
    private Long count;
    @Excel(name = "实际夜班次数",width = 15)
    private Long act;
}
