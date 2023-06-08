package org.jeecg.common.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 人员画像-雷达图计数对象
 * @author:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RadarModel {
    /**
     * 当前值
     */
//    private int total;
    private Double currentValue = 0.0;
    /**
     * 最大值
     */
    private Double maxValue = 0.0;
    /**
     * 最小值
     */
    private Double minValue = 0.0;
}
