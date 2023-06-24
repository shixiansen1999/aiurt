package com.aiurt.modules.fault.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author:wgp
 * @create: 2023-06-09 18:54
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RadarAptitudeDTO {
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 证书数量
     */
    private Integer number;
}
