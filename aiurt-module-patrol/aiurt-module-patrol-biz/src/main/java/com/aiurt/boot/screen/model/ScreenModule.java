package com.aiurt.boot.screen.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

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
     * 组织机构编号
     */
    private List<String> orgCodes;
//    /**
//     * 线路编号
//     */
//    private List<String> lines;
//    /**
//     * 专业编号
//     */
//    private List<String> majors;
}
