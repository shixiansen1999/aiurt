package com.aiurt.modules.largescream.model;

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
public class FaultScreenModule {
    /**
     * 开始时间
     */
    private Date startDate;
    /**
     * 结束时间
     */
    private Date endDate;
    /**
     * 任务状态
     */
    private Integer status;
    /**
     * 未解决状态
     */
    private Integer unSo;
    /**
     * 当日已解决状态
     */
    private Integer todaySolve;
    /**
     * 当日新增状态
     */
    private Integer todayAdd;
    /**
     * 本周已解决状态
     */
    private Integer weekSolve;
    /**
     * 本周新增状态
     */
    private Integer weekAdd;
    /**
     * 当天开始日期
     */
    private Date todayStartDate;
    /**
     * 当天结束日期
     */
    private Date todayEndDate;
    /**
     * 线路
     */
    private String lineCode;
    /**
     * 组织机构编号
     */
    private List<String> majors;
}
