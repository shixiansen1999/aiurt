package com.aiurt.boot.modules.worklog.param;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.validation.annotation.Validated;

/**
 * @Author: swsc
 * 工作日志查询参数列表
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Validated
public class WorkLogParam {

    /**
     * 开始时间
     */
    private String dayStart;

    /**
     * 结束时间
     */
    private String dayEnd;

    /**
     * 审核状态 其他-未审核 3-已审核
     */
    private String checkStatus;
}
