package com.aiurt.modules.personnelportrait.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 人员画像-雷达图绩效计数对象
 * @author:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RadarPerformanceModelDTO {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 绩效分数
     */
    private Double score;
}
