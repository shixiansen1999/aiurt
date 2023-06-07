package com.aiurt.modules.fault.dto;

import lombok.Data;

/**
 * @author
 * @description 大屏班组画像-人员效率统计对象
 */
@Data
public class EfficiencyDTO {
    /**
     * 用户名
     */
    private String username;
    /**
     * 平均响应时间
     */
    private Double responseTime;
    /**
     * 平均解决时间
     */
    private Double resolveTime;
}
