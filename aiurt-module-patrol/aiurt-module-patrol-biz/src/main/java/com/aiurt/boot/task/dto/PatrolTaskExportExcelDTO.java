package com.aiurt.boot.task.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * 导出excel的DTO对象
 * @author 华宜威
 * @date 2023-07-05 11:40:24
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatrolTaskExportExcelDTO {

    /**线路名称*/
    @Excel(name = "lineName", width = 15)
    private String lineName;
    /**站点名称*/
    @Excel(name = "stationName", width = 15)
    private String stationName;
    /**任务名称*/
    @Excel(name = "taskName", width = 15)
    private String taskName;
    /**巡视人*/
    @Excel(name = "patrolUserNameString", width = 15)
    private String patrolUserNameString;
    /**组织机构*/
    @Excel(name = "orgName", width = 15)
    private String orgName;
    /**任务领取方式，1 个人领取、2常规指派、3 手工下发*/
    private Integer source;
    /**任务领取方式翻译*/
    private String sourceString;

    /**任务状态*/
    private Integer status;
    /**任务状态翻译*/
    private String statusString;

    /**作废状态：0未作废、1已作废*/
    private Integer discardStatus;
    /**是否作废*/
    private String discardStatusString;

    /**漏检状态：0未漏检，1已漏检*/
    private Integer omitStatus;
    /**是否漏检*/
    private String omitStatusString;

    /**标准工时：单位秒*/
    private Integer standardDuration;

    /**巡视工时：单位秒*/
    private Integer duration;
    /**巡视工时时间转化，转成1天2时5分9秒这种形式*/
    private String durationString;

    /**执行任务时的wifi连接时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date wifiConnectTime;

    /**任务提交时间*/
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    /**实际巡视时长*/
    private Integer actualDuration;
    /**实际巡视时长时间转化，转成1天2时5分9秒这种形式*/
    private String actualDurationString;

    /**任务编号*/
    private String taskCode;
}
