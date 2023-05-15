package com.aiurt.boot.statistics.dto;

import com.aiurt.boot.constant.PatrolConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class IndexCountDTO {
    private static final long serialVersionUID = 1L;

    /**
     * 开始时间
     */
    private Date startDate;

    /**
     * 结束时间
     */
    private Date endDate;

    /**
     * 权限拼接SQl
     */
    private String jointSQL;
    /**
     * 任务已完成状态
     */
    private Integer status = PatrolConstant.TASK_COMPLETE;
    /**
     * 任务异常状态
     */
    private Integer abnormal = PatrolConstant.TASK_ABNORMAL;
    /**
     * 任务漏巡状态
     */
    private Integer omitStatus = PatrolConstant.OMIT_STATUS;
    /**
     * 任务已作废状态
     */
    private Integer discardStatus = PatrolConstant.TASK_DISCARD;

    public IndexCountDTO(Date startDate, Date endDate, String jointSQL) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.jointSQL = jointSQL;
    }
}
