package com.aiurt.modules.fault.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author:wgp
 * @create: 2023-06-09 18:55
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RadarPerformanceDTO {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 绩效分数
     */
    private Double score;
}
