package com.aiurt.modules.fault.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author:wgp
 * @create: 2023-06-09 18:52
 * @Description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RadarNumberDTO {
    /**
     * 用户名
     */
    private String username;
    /**
     * 数量
     */
    private Integer number;
}
