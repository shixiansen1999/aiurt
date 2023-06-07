package org.jeecg.common.system.vo;

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
public class RadarPerformanceModel {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 绩效分数
     */
    private String score;
}
