package com.aiurt.modules.train.task.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainExperienceDTO {
    /**
     * 记录ID
     */
    private String id;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 培训名称
     */
    private String taskName;
    /**
     * 培训开始时间
     */
    private Date startTime;
    /**
     * 培训结束时间
     */
    private Date endTime;

}
