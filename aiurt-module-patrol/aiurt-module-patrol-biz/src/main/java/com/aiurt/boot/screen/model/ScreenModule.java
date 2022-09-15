package com.aiurt.boot.screen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class ScreenModule {
    /**
     * 结束时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 任务状态
     */
    private Integer status;
    /**
     * 漏检状态
     */
    private Integer omit;
    /**
     * 异常状态
     */
    private Integer abnormal;
    /**
     * 当天日期
     */
    private Date today;
    /**
     * 线路编号
     */
    private String lineCode;
}
