package com.aiurt.modules.personnelportrait.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 人员画像-雷达图资质证书计数对象
 * @author:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RadarAptitudeModelDTO {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 证书数量
     */
    private Integer number;
}
