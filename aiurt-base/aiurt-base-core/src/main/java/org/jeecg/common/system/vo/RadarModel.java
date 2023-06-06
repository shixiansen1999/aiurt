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
    private int currentValue;
    /**
     * 最大值
     */
    private int maxValue;
    /**
     * 最小值
     */
    private int minValue;
}
