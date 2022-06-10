package com.aiurt.boot.modules.repairManage.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author qian
 * @version 1.0
 * @date 2021/11/19 17:29
 */
@Data
public class TimeVO {
    @NotNull(message = "开始时间不能为空")
    private String startTime;
    @NotNull(message = "结束时间不能为空")
    private String endTime;
}
