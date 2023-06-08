package org.jeecg.common.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 人员画像-雷达图故障处理次数计数对象
 * @author:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RadarNumberModel {
//    /**
//     * 用户ID
//     */
//    private String userId;
    /**
     * 用户名
     */
    private String username;
    /**
     * 数量
     */
    private Integer number;
}
